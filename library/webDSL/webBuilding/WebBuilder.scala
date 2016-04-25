package leon.webDSL.webBuilding
import leon.webDSL.webDescription._
import leon.collection._
import leon.annotation._

sealed abstract class ChildNode
case class WebElementWrapper(e: WebElement) extends ChildNode
abstract class PropertySet extends ChildNode
case class PropertyStringSet(tag: String, value: String) extends PropertySet

case class Acceptor[T](tag: String) {
  def :=(v: String) = PropertyStringSet(tag, v)
}

object implicits {
  implicit def toWrapper(e: WebElement): ChildNode = WebElementWrapper(e)
  implicit def toAttribute(e: String): ChildNode = PropertyStringSet("text", e)
  
  def extractElements(e: List[ChildNode], acc: List[WebElement], acc2: List[PropertySet]): (List[WebElement], List[PropertySet]) = e match {
    case Nil() => (acc.reverse, acc2.reverse)
    case Cons(WebElementWrapper(e), t) => extractElements(t, e::acc, acc2)
    case Cons(p:PropertySet, t) => extractElements(t, acc, p::acc2)
  }
  
  def getStringProperty(tag: String, properties: List[PropertySet], default: String): String = {
    properties.flatMap[String] { e => e match {
      case PropertyStringSet(tag2, e) if tag2 == tag=> e :: Nil[String]
      case _ => Nil()
    }}.headOption.getOrElse(default)
  }
  def getAllStringProperty(tag: String, properties: List[PropertySet], default: String): String = {
    properties.foldLeft("") { (acc, e) => e match {
      case PropertyStringSet(tag2, e) if tag2 == tag => acc + e
      case _ => acc
    }}
  }
}

object < {
  import implicits._
  
  def _div(elems: List[ChildNode]): Div = {
    val (children, properties) = extractElements(elems, Nil(), Nil())
    
    Div(children) // TODO: Need to add properties
  }
  def div(): WebElement = _div(Nil[ChildNode]())
  def div(e: ChildNode): WebElement = _div(e :: Nil[ChildNode]())
  def div(e: ChildNode, e2: ChildNode): WebElement = _div(e :: e2 :: Nil[ChildNode]())
  def div(e: ChildNode, e2: ChildNode, e3: ChildNode): WebElement = _div(e :: e2 :: e3 :: Nil[ChildNode]())
  def div(e: ChildNode, e2: ChildNode, e3: ChildNode, e4: ChildNode): WebElement = _div(e :: e2 :: e3 :: e4 :: Nil[ChildNode]())
  
  def _input(elems: List[ChildNode]): Input = {
    val (children, properties) = extractElements(elems, Nil(), Nil())
    
    val tpe = getStringProperty("type", properties, "")
    val placeHolder = getStringProperty("placeHolder", properties, "")
    val value = getStringProperty("value", properties, "")
    
    Input(tpe, placeHolder, value) // TODO: Need to add properties
  }
  def input(): Input = _input(Nil[ChildNode])
  def input(elem1: ChildNode): Input = _input(elem1 :: Nil[ChildNode])
  def input(elem1: ChildNode, elem2: ChildNode): Input = _input(elem1 :: elem2 :: Nil[ChildNode])
  def input(elem1: ChildNode, elem2: ChildNode, elem3: ChildNode): Input = _input(elem1 :: elem2 :: elem3 :: Nil[ChildNode])
  
  def _hn(elems: List[ChildNode], level: HeaderLevel): Header = {
    val (children, properties) = extractElements(elems, Nil(), Nil())
    val text = getAllStringProperty("text", properties, "") // TODO: Support more than just text?
    Header(text, level)
  }
  
  def _h1(elems: List[ChildNode]): Header = _hn(elems, HLOne())
  def _h2(elems: List[ChildNode]): Header = _hn(elems, HLTwo())
  def _h3(elems: List[ChildNode]): Header = _hn(elems, HLThree())
  def _h4(elems: List[ChildNode]): Header = _hn(elems, HLFour())
  def _h5(elems: List[ChildNode]): Header = _hn(elems, HLFive())
  def _h6(elems: List[ChildNode]): Header = _hn(elems, HLSix())
  
  def h1() = _h1(Nil[ChildNode]())
  def h1(elem: ChildNode) = _h1(elem::Nil[ChildNode]())
  def h1(elem: ChildNode, elem2: ChildNode) = _h1(elem::elem2::Nil[ChildNode]())
  def h1(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h1(elem::elem2::elem3::Nil[ChildNode]())

  def h2() = _h2(Nil[ChildNode]())
  def h2(elem: ChildNode) = _h2(elem::Nil[ChildNode]())
  def h2(elem: ChildNode, elem2: ChildNode) = _h2(elem::elem2::Nil[ChildNode]())
  def h2(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h2(elem::elem2::elem3::Nil[ChildNode]())

  def h3() = _h3(Nil[ChildNode]())
  def h3(elem: ChildNode) = _h3(elem::Nil[ChildNode]())
  def h3(elem: ChildNode, elem2: ChildNode) = _h3(elem::elem2::Nil[ChildNode]())
  def h3(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h3(elem::elem2::elem3::Nil[ChildNode]())
  
  def h4() = _h4(Nil[ChildNode]())
  def h4(elem: ChildNode) = _h4(elem::Nil[ChildNode]())
  def h4(elem: ChildNode, elem2: ChildNode) = _h4(elem::elem2::Nil[ChildNode]())
  def h4(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h4(elem::elem2::elem3::Nil[ChildNode]())
  
  def h5() = _h5(Nil[ChildNode]())
  def h5(elem: ChildNode) = _h5(elem::Nil[ChildNode]())
  def h5(elem: ChildNode, elem2: ChildNode) = _h5(elem::elem2::Nil[ChildNode]())
  def h5(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h5(elem::elem2::elem3::Nil[ChildNode]())
  
  def h6() = _h6(Nil[ChildNode]())
  def h6(elem: ChildNode) = _h6(elem::Nil[ChildNode]())
  def h6(elem: ChildNode, elem2: ChildNode) = _h6(elem::elem2::Nil[ChildNode]())
  def h6(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _h6(elem::elem2::elem3::Nil[ChildNode]())
  
  def _p(elems: List[ChildNode]): Paragraph = {
    val (children, properties) = extractElements(elems, Nil(), Nil())
    val text = getAllStringProperty("text", properties, "") // TODO: Support more than just text?
    Paragraph(text)
  }
  
  def p() = _p(Nil[ChildNode]())
  def p(elem: ChildNode) = _p(elem::Nil[ChildNode]())
  def p(elem: ChildNode, elem2: ChildNode) = _p(elem::elem2::Nil[ChildNode]())
  def p(elem: ChildNode, elem2: ChildNode, elem3: ChildNode) = _p(elem::elem2::elem3::Nil[ChildNode]())
}

object ^ {
  val tpe = Acceptor[String]("type")
  val value = Acceptor[String]("value")
  val placeHolder = Acceptor[String]("placeHolder")
  val id = Acceptor[String]("id")
}

object WebBuilder {

}