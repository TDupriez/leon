/* Copyright 2009-2016 EPFL, Lausanne */

package leon
package invariant.engine
import z3.scala._
import purescala.Common._
import purescala.Definitions._
import purescala.Expressions._
import purescala.ExprOps._
import leon.purescala.TypeOps.instantiateType
import purescala.Extractors._
import purescala.Types._
import java.io._
import leon.invariant.templateSolvers.ExtendedUFSolver
import invariant._
import scala.util.control.Breaks._
import solvers._
import scala.concurrent._
import scala.concurrent.duration._
import leon.evaluators.DefaultEvaluator

import invariant.templateSolvers._
import invariant.factories._
import invariant.util._
import invariant.structure._
import FunctionUtils._
import Util._
import PredicateUtil._
import ProgramUtil._
import SolverUtil._

class SpecInstantiator(ctx: InferenceContext, program: Program, ctrTracker: ConstraintTracker) {

  val verbose = false

  protected val disableAxioms = false
  protected val debugAxiomInstantiation = false

  val tru = BooleanLiteral(true)
  val axiomFactory = new AxiomFactory(ctx) //handles instantiation of axiomatic specification

  //the guards of the set of calls that were already processed
  protected var exploredGuards = Set[Variable]()

  def instantiate() = {
    val funcs = ctrTracker.getFuncs

    funcs.foreach((fd) => {
      val formula = ctrTracker.getVC(fd)
      val disjuncts = formula.disjunctsInFormula
      val newguards = disjuncts.keySet.diff(exploredGuards)
      exploredGuards ++= newguards

      val newcalls = formula.getCallsOfGuards(newguards.toSeq).toSet //flatMap(g => disjuncts(g).collect { case c: Call => c })
      instantiateSpecs(formula, newcalls, funcs.toSet)

      if (!disableAxioms) {
        //remove all multiplication if "withmult" is specified
        val relavantCalls = if (ctx.withmult) {
          newcalls.filter(call => !isMultFunctions(call.fi.tfd.fd))
        } else newcalls
        instantiateAxioms(formula, relavantCalls)
      }
    })
  }

  /**
   * This function refines the formula by assuming the specifications/templates for calls in the formula
   * Here, we assume (pre => post ^ template) for each call (templates only for calls with VC)
   * Important: adding templates for 'newcalls' of the previous iterations is empirically more effective
   */
  //a set of calls for which templates or specifications have not been assumed
  private var untemplatedCalls = Map[FunDef, Set[Call]]()
  def getUntempCalls(fd: FunDef) = if (untemplatedCalls.contains(fd)) untemplatedCalls(fd) else Set()
  def resetUntempCalls(fd: FunDef, calls: Set[Call]) = {
    if (untemplatedCalls.contains(fd)) {
      untemplatedCalls -= fd
      untemplatedCalls += (fd -> calls)
    } else {
      untemplatedCalls += (fd -> calls)
    }
  }

  def instantiateSpecs(formula: Formula, calls: Set[Call], funcsWithVC: Set[FunDef]) = {

    //assume specifications
    calls.foreach((call) => {
      //first get the spec for the call if it exists
      val spec = specForCall(call)
      if (spec.isDefined && spec.get != tru) {
        val cdata = formula.callData(call)
        formula.conjoinWithDisjunct(cdata.guard, spec.get, cdata.parents, inSpec = true)
      }
    })

    //try to assume templates for all the current un-templated calls
    var newUntemplatedCalls = Set[Call]()
    getUntempCalls(formula.fd).foreach { call =>
      if (funcsWithVC.contains(call.fi.tfd.fd)) { // add templates of only functions for which there exists a VC
        templateForCall(call) match {
          case Some(temp) =>
            val cdata = formula.callData(call)
            formula.conjoinWithDisjunct(cdata.guard, temp, cdata.parents, inSpec = true)
          case _ =>
            ; // here there is no template for the call
        }
      } else {
        newUntemplatedCalls += call
      }
    }
    resetUntempCalls(formula.fd, newUntemplatedCalls ++ calls)
  }

  def specForCall(call: Call): Option[Expr] = {
    val tfd = call.fi.tfd
    val callee = tfd.fd
    if (callee.hasPostcondition) {
      //get the postcondition without templates
      val rawpost = freshenLocals(callee.getPostWoTemplate)
      val rawspec =
        if (callee.hasPrecondition) {
          val pre = freshenLocals(callee.precondition.get)
          if (ctx.assumepre)
            And(pre, rawpost)
          else
            Implies(pre, rawpost)
        } else {
          rawpost
        }
      // instantiate the post
      val tparamMap = (callee.tparams zip tfd.tps).toMap
      val instSpec =  instantiateType(replace(formalToActual(call), rawspec), tparamMap, Map())
      val inlinedSpec = ExpressionTransformer.normalizeExpr(instSpec, ctx.multOp)
      Some(inlinedSpec)
    } else {
      None
    }
  }

  def templateForCall(call: Call): Option[Expr] = {
    val tfd = call.fi.tfd
    val callee = tfd.fd
    if (callee.hasTemplate) {
      val argmap = formalToActual(call)
      val tparamMap = (callee.tparams zip tfd.tps).toMap
      val tempExpr = instantiateType(replace(argmap, freshenLocals(callee.getTemplate)), tparamMap, Map())
      val template = if (callee.hasPrecondition) {
        val pre = instantiateType(replace(argmap, freshenLocals(callee.precondition.get)), tparamMap, Map())
        if (ctx.assumepre)
          And(pre, tempExpr)
        else
          Implies(pre, tempExpr)
      } else {
        tempExpr
      }
      //TODO: should we freshen locals of template here ??
      Some(ExpressionTransformer.normalizeExpr(template, ctx.multOp))
    } else None
  }

  //axiomatic specification
  protected var axiomRoots = Map[Seq[Call], Variable]() //a mapping from axioms keys (a sequence of calls) to the guards
  def instantiateAxioms(formula: Formula, calls: Set[Call]) = {

    val debugSolver = if (this.debugAxiomInstantiation) {
      val sol = new ExtendedUFSolver(ctx.leonContext, program)
      sol.assertCnstr(formula.toExpr)
      Some(sol)
    } else None

    val inst1 = instantiateUnaryAxioms(formula, calls)
    val inst2 = instantiateBinaryAxioms(formula, calls)
    val axiomInsts = inst1 ++ inst2

    Stats.updateCounterStats(atomNum(createAnd(axiomInsts)), "AxiomBlowup", "VC-refinement")
    if(verbose) ctx.reporter.info("Number of axiom instances: " + axiomInsts.size)

    if (this.debugAxiomInstantiation) {
      println("Instantianting axioms over: " + calls)
      println("Instantiated Axioms: ")
      axiomInsts.foreach((ainst) => {
        println(ainst)
        debugSolver.get.assertCnstr(ainst)
        val res = debugSolver.get.check
        res match {
          case Some(false) =>
            println("adding axiom made formula unsat!!")
          case _ => ;
        }
      })
      debugSolver.get.free
    }
  }

  //this code is similar to assuming specifications
  def instantiateUnaryAxioms(formula: Formula, calls: Set[Call]) = {
    val axioms = calls.collect {
      case call @ _ if axiomFactory.hasUnaryAxiom(call) => {
        val (ant, conseq) = axiomFactory.unaryAxiom(call)
        val axiomInst = Implies(ant, conseq)
        val nnfAxiom = ExpressionTransformer.normalizeExpr(axiomInst, ctx.multOp)
        val cdata = formula.callData(call)
        formula.conjoinWithDisjunct(cdata.guard, nnfAxiom, cdata.parents, inSpec = true)
        axiomInst
      }
    }
    axioms.toSeq
  }

  /**
   * Here, we assume that axioms do not introduce calls.
   * If this does not hold, 'guards' have to be used while instantiating axioms so as
   * to compute correct verification conditions.
   * TODO: Use least common ancestor etc. to avoid axiomatizing calls along different disjuncts
   * TODO: can we avoid axioms like (a <= b ^ x<=y => p <= q), (x <= y ^ a<=b => p <= q), ...
   * TODO: can we have axiomatic specifications relating two different functions ?
   */
  protected var binaryAxiomCalls = Map[FunDef, Set[Call]]() //calls with axioms so far seen
  def getBinaxCalls(fd: FunDef) = if (binaryAxiomCalls.contains(fd)) binaryAxiomCalls(fd) else Set[Call]()
  def appendBinaxCalls(fd: FunDef, calls: Set[Call]) = {
    if (binaryAxiomCalls.contains(fd)) {
      val oldcalls = binaryAxiomCalls(fd)
      binaryAxiomCalls -= fd
      binaryAxiomCalls += (fd -> (oldcalls ++ calls))
    } else {
      binaryAxiomCalls += (fd -> calls)
    }
  }

  def instantiateBinaryAxioms(formula: Formula, calls: Set[Call]) = {

    val newCallsWithAxioms = calls.filter(axiomFactory.hasBinaryAxiom _)

    def isInstantiable(call1: Call, call2: Call): Boolean = {
      //important: check if the two calls refer to the same function
      (call1.fi.tfd.id == call2.fi.tfd.id) && (call1 != call2)
    }

    val product = cross[Call, Call](newCallsWithAxioms, getBinaxCalls(formula.fd), Some(isInstantiable)).flatMap(
      p => Seq((p._1, p._2), (p._2, p._1))) ++
      cross[Call, Call](newCallsWithAxioms, newCallsWithAxioms, Some(isInstantiable)).map(p => (p._1, p._2))

    //ctx.reporter.info("# of pairs with axioms: "+product.size)
    //Stats.updateCumStats(product.size, "Call-pairs-with-axioms")

    val addedAxioms = product.flatMap(pair => {
      //union the parents of the two calls
      val cdata1 = formula.callData(pair._1)
      val cdata2 = formula.callData(pair._2)
      val parents = cdata1.parents ++ cdata2.parents
      val axiomInsts = axiomFactory.binaryAxiom(pair._1, pair._2)

      axiomInsts.foldLeft(Seq[Expr]())((acc, inst) => {
        val (ant, conseq) = inst
        val axiom = Implies(ant, conseq)
        val nnfAxiom = ExpressionTransformer.normalizeExpr(axiom, ctx.multOp)
        val axroot = formula.conjoinWithRoot(nnfAxiom, parents, true)
        //important: here we need to update the axiom roots
        axiomRoots += (Seq(pair._1, pair._2) -> axroot)
        acc :+ axiom
      })
    })
    appendBinaxCalls(formula.fd, newCallsWithAxioms)
    addedAxioms
  }

  /**
   * Note: taking a formula as input may not be necessary. We can store it as a part of the state
   * TODO: can we use transitivity here to optimize ?
   */
  def axiomsForCalls(formula: Formula, calls: Set[Call], model: LazyModel, tmplMap: Map[Identifier,Expr], eval: DefaultEvaluator): Seq[Constraint] = {
    //note: unary axioms need not be instantiated
    //consider only binary axioms
    (for (x <- calls; y <- calls) yield (x, y)).foldLeft(Seq[Constraint]())((acc, pair) => {
      val (c1, c2) = pair
      if (c1 != c2) {
        val axRoot = axiomRoots.get(Seq(c1, c2))
        if (axRoot.isDefined)
          acc ++ formula.pickSatDisjunct(axRoot.get, model, tmplMap, eval)
        else acc
      } else acc
    })
  }
}