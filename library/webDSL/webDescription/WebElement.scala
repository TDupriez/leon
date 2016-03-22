package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler
  * (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed trait WebElement

//case class TestWebElement1(sons: leon.collection.List[WebElement]) extends WebElement
//case class TestWebElement2(oi: Int) extends WebElement
case class Div(sons: leon.collection.List[WebElement]) extends WebElement
case class Header(level: HeaderLevel, text: String) extends WebElement
case class Paragraph(text: String) extends WebElement