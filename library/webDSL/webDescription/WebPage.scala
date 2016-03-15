package leon.webDSL.webDescription

//import boopickle.Default._
/**
  * Created by dupriez on 3/1/16.
  */


case class WebPage(webPageAttributes: leon.collection.List[WebPageAttribute], sons: leon.collection.List[WebElement])
//case class WebPage(leonList: leon.collection.List[Int])

//object WebPage {
//  implicit val pickler: Pickler[WebPage] = generatePickler[WebPage]
//}