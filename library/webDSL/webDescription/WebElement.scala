package leon.webDSL.webDescription

import leon.annotation.{ignore, extern}

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler
  * (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed trait WebElement// {
//  val id : WebElementID = WebElementIDProvider.generateFreshId()
//}

//case class TestWebElement1(sons: leon.collection.List[WebElement]) extends WebElement
//case class TestWebElement2(oi: Int) extends WebElement
case class Div(sons: leon.collection.List[WebElement]) extends WebElement
case class Header(text: String, level: HeaderLevel) extends WebElement
case class Paragraph(text: String) extends WebElement

case class WebElementID(id: Int)
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