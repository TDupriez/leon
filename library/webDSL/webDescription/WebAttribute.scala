package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/11/16.
  * WebAttributes are to be seen as keys in a map that points to their corresponding value
  */
sealed trait WebAttribute
sealed trait StringWebAttribute extends WebAttribute

case class TestWebAttribute1(oi: Int) extends WebAttribute

case object Text extends StringWebAttribute

//TODO: create a type of map for StringWebAttributes, which would know the default values for
//TODO: these attributes and allow shorter code when the client unpack webElement to turn them into scalajs-react element

//case class StringWebAttributeValueHolder(text: String)
//object StringWebAttribute {
//  def getArgNumberOfWebAttributeInValueHolder(swa: StringWebAttribute) = {
//    swa match {
//      case Text => 1
//      case _ => throw new RuntimeException("Unknown StringWebAttribute ("+swa+") was provided to the getArgNumberOfWebAttributeInValueHolder method")
//    }
//  }
//}