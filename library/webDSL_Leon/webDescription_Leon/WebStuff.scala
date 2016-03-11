package webDSL_Leon.webDescription_Leon

/**
  * Created by dupriez on 3/4/16.
  */

sealed trait WebStuff
sealed trait WebAttribute extends WebStuff
case class TestWebAttribute(oi: Int) extends WebAttribute

case class WebElement(attributesAndSons: leon.collection.List[WebStuff]) extends WebStuff