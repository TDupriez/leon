package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  */
sealed trait WebPageAttribute

case class TestWebPageAttribute1(oi: Int) extends WebPageAttribute
