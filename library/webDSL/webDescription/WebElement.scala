package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler
  * (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed trait WebElement {
  val id : WebElementID = WebElementIDProvider.generateFreshId()
}

//case class TestWebElement1(sons: leon.collection.List[WebElement]) extends WebElement
//case class TestWebElement2(oi: Int) extends WebElement
case class Div(sons: leon.collection.List[WebElement]) extends WebElement
case class Header(level: HeaderLevel, stringAttributes: leon.lang.Map[StringWebAttribute, String]) extends WebElement
case class Paragraph(stringAttributes: leon.lang.Map[StringWebAttribute, String]) extends WebElement

case class WebElementID(id: Int)
object WebElementIDProvider {
  private var counter = 0
  def generateFreshId() = {
    counter = counter + 1
    WebElementID(counter)
  }
  def resetGenerator() = {
    counter = 0
  }
}