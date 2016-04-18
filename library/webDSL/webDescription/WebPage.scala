package leon.webDSL.webDescription
/**
  * Created by dupriez on 3/1/16.
  */


case class WebPage(webPageAttributes: leon.collection.List[WebPageAttribute], sons: leon.collection.List[WebElement])

case class WebPageWithIDedWebElements(webPageAttributes: leon.collection.List[WebPageAttribute], sons: leon.collection.List[WebElementWithID])