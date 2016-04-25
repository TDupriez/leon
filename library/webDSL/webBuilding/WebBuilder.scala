package leon.webDSL.webBuilding
import leon.webDSL.webDescription._
import leon.collection._
import leon.annotation._

case class Acceptor[T](tag: String) {
  def :=(v: String) = WebAttribute(tag, v)
}

object implicits {
  implicit def toAttribute(e: String): WebTree = TextElement(e)
  
  def extractElements(e: List[WebTree], acc: List[WebElement], acc2: List[WebAttribute]): (List[WebElement], List[WebAttribute]) = e match {
    case Nil() => (acc.reverse, acc2.reverse)
    case Cons(e: WebElement, t) => extractElements(t, e::acc, acc2)
    case Cons(p: WebAttribute, t) => extractElements(t, acc, p::acc2)
  }
  
  def getStringProperty(tag: String, properties: List[WebAttribute], default: String): String = {
    properties.flatMap[String] { e => e match {
      case WebAttribute(tag2, e) if tag2 == tag=> e :: Nil[String]
      case _ => Nil()
    }}.headOption.getOrElse(default)
  }
  def getAllStringProperty(tag: String, properties: List[WebAttribute], default: String): String = {
    properties.foldLeft("") { (acc, e) => e match {
      case WebAttribute(tag2, e) if tag2 == tag => acc + e
      case _ => acc
    }}
  }
}

object < {
  import implicits._
  
  def extract(tag: String, elems: List[WebTree]): Element = {
    val (children, properties) = extractElements(elems, Nil(), Nil())
    Element(tag, children, properties)
  }
  
  def _div(elems: List[WebTree]): WebElement = extract("div", elems)
  def div(): WebElement = _div(Nil[WebTree]())
  def div(e: WebTree): WebElement = _div(e :: Nil[WebTree]())
  def div(e: WebTree, e2: WebTree): WebElement = _div(e :: e2 :: Nil[WebTree]())
  def div(e: WebTree, e2: WebTree, e3: WebTree): WebElement = _div(e :: e2 :: e3 :: Nil[WebTree]())
  def div(e: WebTree, e2: WebTree, e3: WebTree, e4: WebTree): WebElement = _div(e :: e2 :: e3 :: e4 :: Nil[WebTree]())
    
  def _input(elems: List[WebTree]): Element = extract("input", elems)
  def input(): Element = _input(Nil[WebTree])
  def input(elem1: WebTree): Element = _input(elem1 :: Nil[WebTree])
  def input(elem1: WebTree, elem2: WebTree): Element = _input(elem1 :: elem2 :: Nil[WebTree])
  def input(elem1: WebTree, elem2: WebTree, elem3: WebTree): Element = _input(elem1 :: elem2 :: elem3 :: Nil[WebTree])
    
  def _h1(elems: List[WebTree]): Element = extract("h1", elems)
  def _h2(elems: List[WebTree]): Element = extract("h2", elems)
  def _h3(elems: List[WebTree]): Element = extract("h3", elems)
  def _h4(elems: List[WebTree]): Element = extract("h4", elems)
  def _h5(elems: List[WebTree]): Element = extract("h5", elems)
  def _h6(elems: List[WebTree]): Element = extract("h6", elems)
  
  def h1() = _h1(Nil[WebTree]())
  def h1(elem: WebTree) = _h1(elem::Nil[WebTree]())
  def h1(elem: WebTree, elem2: WebTree) = _h1(elem::elem2::Nil[WebTree]())
  def h1(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h1(elem::elem2::elem3::Nil[WebTree]())

  def h2() = _h2(Nil[WebTree]())
  def h2(elem: WebTree) = _h2(elem::Nil[WebTree]())
  def h2(elem: WebTree, elem2: WebTree) = _h2(elem::elem2::Nil[WebTree]())
  def h2(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h2(elem::elem2::elem3::Nil[WebTree]())

  def h3() = _h3(Nil[WebTree]())
  def h3(elem: WebTree) = _h3(elem::Nil[WebTree]())
  def h3(elem: WebTree, elem2: WebTree) = _h3(elem::elem2::Nil[WebTree]())
  def h3(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h3(elem::elem2::elem3::Nil[WebTree]())
  
  def h4() = _h4(Nil[WebTree]())
  def h4(elem: WebTree) = _h4(elem::Nil[WebTree]())
  def h4(elem: WebTree, elem2: WebTree) = _h4(elem::elem2::Nil[WebTree]())
  def h4(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h4(elem::elem2::elem3::Nil[WebTree]())
  
  def h5() = _h5(Nil[WebTree]())
  def h5(elem: WebTree) = _h5(elem::Nil[WebTree]())
  def h5(elem: WebTree, elem2: WebTree) = _h5(elem::elem2::Nil[WebTree]())
  def h5(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h5(elem::elem2::elem3::Nil[WebTree]())
  
  def h6() = _h6(Nil[WebTree]())
  def h6(elem: WebTree) = _h6(elem::Nil[WebTree]())
  def h6(elem: WebTree, elem2: WebTree) = _h6(elem::elem2::Nil[WebTree]())
  def h6(elem: WebTree, elem2: WebTree, elem3: WebTree) = _h6(elem::elem2::elem3::Nil[WebTree]())
  
  def _p(elems: List[WebTree]): Element = extract("p", elems)
  def p() = _p(Nil[WebTree]())
  def p(elem: WebTree) = _p(elem::Nil[WebTree]())
  def p(elem: WebTree, elem2: WebTree) = _p(elem::elem2::Nil[WebTree]())
  def p(elem: WebTree, elem2: WebTree, elem3: WebTree) = _p(elem::elem2::elem3::Nil[WebTree]())
}

object ^ {
  val tpe = Acceptor[String]("type")
  val value = Acceptor[String]("value")
  val placeHolder = Acceptor[String]("placeHolder")
  val id = Acceptor[String]("id")
  val className = Acceptor[String]("class")
}