package leon.webDSL.webDescription
/**
  * Created by dupriez on 3/1/16.
  */

case class WebPage(main: WebElement)

// The assumption is that in a WebPageWithIDedWebElements, each WebElement is immediately contained in a WebElementWithID
case class WebPageWithIDedWebElements(main: WebElementWithID)