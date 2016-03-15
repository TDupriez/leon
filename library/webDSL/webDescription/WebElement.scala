package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  *
  * All new subclasses of WebElement must also be registered in the pickler (see shared/src/main/scala/shared/Picklers (the "webElementPickler" implicit val))
  */
sealed trait WebElement

case class TestWebElement1(sons: leon.collection.List[WebElement]) extends WebElement
