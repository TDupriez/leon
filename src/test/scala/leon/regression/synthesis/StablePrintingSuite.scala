/* Copyright 2009-2016 EPFL, Lausanne */

package leon.regression.synthesis

import leon._
import leon.test._

import leon.purescala.Definitions._
import leon.purescala.ScalaPrinter
import leon.purescala.PrinterContext
import leon.purescala.PrinterOptions
import leon.synthesis._
import leon.synthesis.rules._

import scala.collection.mutable.Stack
import scala.io.Source

import java.io.File

class StablePrintingSuite extends LeonRegressionSuite {
  private def forEachFileIn(path : String)(block : File => Unit) {
    val fs = filesInResourceDir(path, _.endsWith(".scala"))

    for(f <- fs) {
      block(f)
    }
  }


  private def testIterativeSynthesis(cat: String, f: File, depth: Int) {

    val decompRules = List[Rule](
      Unification.DecompTrivialClash,
      Unification.OccursCheck, // probably useless
      Disunification.Decomp,
      ADTDual,
      CaseSplit,
      IfSplit,
      UnusedInput,
      EquivalentInputs,
      UnconstrainedOutput,
      OptimisticGround,
      InequalitySplit,
      rules.Assert,
      DetupleInput,
      ADTSplit,
      InnerCaseSplit
    )

    def getChooses(ctx: LeonContext, content: String): (Program, SynthesisSettings, Seq[SourceInfo]) = {
      val opts = SynthesisSettings()
      val pipeline = leon.utils.TemporaryInputPhase andThen 
                     frontends.scalac.ExtractionPhase andThen
                     new leon.utils.PreprocessingPhase

      val (ctx2, program) = pipeline.run(ctx, (List(content), Nil))

      (program, opts, SourceInfo.extractFromProgram(ctx2, program))
    }

    case class Job(content: String, choosesToProcess: Set[Int], rules: List[String]) {
      def info(task: String): String = {
        val r = if (rules.isEmpty) "<init>" else "after "+rules.head

        val indent = "  "* rules.size +" "

        f"${indent+r}%-40s [$task%s]"
      }
    }


    test(cat+": "+f.getName+" - Synthesis <-> Print (depth="+depth+")") {
      val res = Source.fromFile(f).mkString

      val workList = Stack[Job](Job(res, Set(), Nil))

      while(workList.nonEmpty) {
        val reporter = new TestSilentReporter
        val ctx = createLeonContext("--synthesis", "--timeout=120").copy(reporter = reporter)
        val j = workList.pop()

        info(j.info("compilation"))

        val (pgm, opts, chooses) = try {
          getChooses(ctx, j.content)
        } catch {
          case e: Throwable =>
            val contentWithLines = j.content.split("\n").zipWithIndex.map{ case (l, i) => f"${i+1}%4d: $l"}.mkString("\n")
            info("Error while compiling:\n"+contentWithLines)
            for (e <- reporter.lastErrors) {
              info(e)
            }
            info(e.getMessage)
            e.printStackTrace()
            fail("Compilation failed")
        }

        if (j.rules.size < depth) {
          for ((ci, i) <- chooses.zipWithIndex if j.choosesToProcess(i) || j.choosesToProcess.isEmpty) {
            val synthesizer = new Synthesizer(ctx, pgm, ci, opts)
            val sctx = synthesizer.sctx
            try {
              val search = synthesizer.getSearch
              val hctx = new SearchContext(sctx, ci.source, search.g.root, search)
              val problem = ci.problem
              info(j.info("synthesis "+problem.asString(sctx)))
              val apps = decompRules flatMap { _.instantiateOn(hctx, problem)}

              for (a <- apps) {
                a.apply(hctx) match {
                  case RuleClosed(sols) =>
                  case RuleExpanded(sub) =>
                    a.onSuccess(sub.map(Solution.choose)) match {
                      case Some(sol) =>
                        val result = sol.toSimplifiedExpr(ctx, pgm, ci.fd)

                        val newContent = new FileInterface(ctx.reporter).substitute(j.content, ci.source, (indent: Int) => {
                          val p = new ScalaPrinter(PrinterOptions(), Some(pgm))
                          p.pp(result)(PrinterContext(result, List(ci.fd), indent, p))
                          p.toString
                        })

                        workList push Job(newContent, (i to i+sub.size).toSet, a.asString(ctx) :: j.rules)
                      case None =>
                    }
                }
              }
            } finally {
              synthesizer.shutdown()
            }
          }
        }
      }
    }
  }



  forEachFileIn("regression/synthesis/Church/") { f =>
    testIterativeSynthesis("Church", f, 1)
  }

  forEachFileIn("regression/synthesis/List/") { f =>
    testIterativeSynthesis("List", f, 1)
  }
}
