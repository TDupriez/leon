package leon.webDSL.webDescription

import leon.annotation.{ignore, extern}

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler
  * (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed trait WebElement {
  //WebElementID
  var weid: Int = 0
  val sons: leon.collection.List[WebElement]
//  val id : WebElementID = WebElementIDProvider.generateFreshId()
}

//abstract class WebElement(implicit id: WebElemID) {
//  val sons: leon.collection.List[WebElement]
//}

//case class TestWebElement1(sons: leon.collection.List[WebElement]) extends WebElement
//case class TestWebElement2(oi: Int) extends WebElement
case class Div(/*id: Int,*/ sons: leon.collection.List[WebElement]) extends WebElement //{var weid = id}
case class Header(/*id: Int,*/ text: String, level: HeaderLevel) extends WebElement {override val sons = leon.collection.List[WebElement]()/*var weid = id*/}
case class Paragraph(/*id: Int,*/ text: String) extends WebElement {override val sons = leon.collection.List[WebElement]()/*var weid = id*/}

//case class WebElementID(id: Int)
//object WebElementIDProvider {
//  @ignore
//  private var counter = 0
//  @extern
//  def generateFreshId() = {
//    counter = counter + 1
//    WebElementID(counter)
//  }
//  @extern
//  def resetGenerator() = {
//    counter = 0
//  }
//}

//class WebElemID(id: Int)

case class WebElementWithID(we: WebElement, id: Int) extends WebElement{override val sons = leon.collection.List[WebElement]()}