package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler
  * (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed abstract class WebTree
sealed abstract class WebElement extends WebTree

case class Element(tag: String, sons: leon.collection.List[WebElement], properties: leon.collection.List[WebAttribute]) extends WebElement {
  def attr(attributeName: String): leon.lang.Option[String] = {
    (properties.find { we => we.attributeName == attributeName }) map (_.attributeValue)
  }
}
case class TextElement(text: String) extends WebElement
case class WebAttribute(attributeName: String, attributeValue: String) extends WebTree 

case class WebElementWithID(we: WebElement, id: Int) extends WebElement