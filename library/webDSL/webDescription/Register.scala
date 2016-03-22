package webDSL.webDescription


import leon.webDSL.webDescription._

import scala.collection.mutable
//import scala.reflect.runtime.universe

/**
  * Created by dupriez on 3/21/16.
  *
  * NEW: the following webpage provides useful info about reflecting at runtime when only knowing the name of the class:
  *   http://www.veebsbraindump.com/2013/03/scala-2-10-runtime-reflection-from-a-class-name/
  *
  * Unworking attempt at automating the unExpr of leon Expr by ProgramEvaluator.
  * All the concrete classes of webDescription would have registered themselves using the method below, and ProgramEvaluator could just take
  * the produced map and do:
  * "val constructorMap = Register.fullNameToConstructorMap.map({case (fullName, constructor) => (lookupCaseClass(program)(fullName), constructor)})"
  * to get a CaseClassDef->Constructor map
  * It compiled, but leon raised errors when evaluating the program provided by the web interface:
  *   "[ Error  ] leon/library/webDSL/webDescription/Register.scala:7:22: error: object runtime is not a member of package reflect
  *        import scala.reflect.runtime.universe
  *   [ Error  ] leon/library/webDSL/webDescription/Register.scala:14:71: error: not found: value universe
  *          val fullNameToConstructorMap : scala.collection.mutable.Map[String, universe.MethodMirror] = mutable.Map()"
  */
object Register {

//  val fullNameToConstructorMap : scala.collection.mutable.Map[String, universe.MethodMirror] = mutable.Map()
//
//  private def getReflectConstructor[T: universe.TypeTag] = {
//    val mirror = universe.runtimeMirror(getClass.getClassLoader)
//    val classs = universe.typeOf[T].typeSymbol.asClass
//    val classMirror = mirror.reflectClass(classs)
//    val constructor = universe.typeOf[T].decl(universe.termNames.CONSTRUCTOR).asMethod
//    val constructorMirror = classMirror.reflectConstructor(constructor)
//    constructorMirror
//  }
//
//  def registerConcreteWebType[yourType: universe.TypeTag](fullClassName: String) = {
//    fullNameToConstructorMap(fullClassName) = getReflectConstructor[yourType]
//  }
//
//  registerConcreteWebType[WebPage]("leon.webDSL.webDescription.WebPage")
//  registerConcreteWebType[TestWebElement2]("leon.webDSL.webDescription.TestWebElement2")
//  registerConcreteWebType[leon.collection.Cons[_]]("leon.collection.Cons")
//  registerConcreteWebType[leon.collection.Nil[_]]("leon.collection.Nil")

}