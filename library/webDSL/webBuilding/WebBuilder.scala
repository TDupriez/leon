package leon.webDSL.webBuilding
import leon.webDSL.webDescription._
import leon.collection._
import leon.annotation._

case class Acceptor[T](tag: String) {
  def :=(v: String) = WebAttribute(tag, v)
}

case class CssAcceptor[T](tag: String) {
  def :=(v: String) = WebStyle(tag, v)
}

object implicits {
  implicit def toAttribute(e: String): WebTree = TextElement(e)
  
  def extractElements(e: List[WebTree], acc: List[WebElement], acc2: List[WebAttribute], acc3: List[WebStyle]): (List[WebElement], List[WebAttribute], List[WebStyle]) = e match {
    case Nil() => (acc.reverse, acc2.reverse, acc3.reverse)
    case Cons(e: WebElement, t) => extractElements(t, e::acc, acc2, acc3)
    case Cons(p: WebAttribute, t) => extractElements(t, acc, p::acc2, acc3)
    case Cons(p: WebStyle, t) => extractElements(t, acc, acc2, p::acc3)
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
    val (children, properties, styles) = extractElements(elems, Nil(), Nil(), Nil())
    Element(tag, children, properties, styles)
  }
  
  val div = Element("div", Nil(), Nil(), Nil())
  val input = Element("input", Nil(), Nil(), Nil())
  val label = Element("label", Nil(), Nil(), Nil())
  val span = Element("span", Nil(), Nil(), Nil())
  val h1 = Element("h1", Nil(), Nil(), Nil())
  val h2 = Element("h2", Nil(), Nil(), Nil())
  val h3 = Element("h3", Nil(), Nil(), Nil())
  val h4 = Element("h4", Nil(), Nil(), Nil())
  val h5 = Element("h5", Nil(), Nil(), Nil())
  val h6 = Element("h6", Nil(), Nil(), Nil())
  val p = Element("p", Nil(), Nil(), Nil())
  val table = Element("table", Nil(), Nil(), Nil())
  val tr = Element("tr", Nil(), Nil(), Nil())
  val td = Element("td", Nil(), Nil(), Nil())
  val br = Element("br", Nil(), Nil(), Nil())
  val ul = Element("ul", Nil(), Nil(), Nil())
  val li = Element("li", Nil(), Nil(), Nil())
}

object ^ {
  val name = Acceptor[String]("name")
  val tpe = Acceptor[String]("type")
  val value = Acceptor[String]("value")
  val placeHolder = Acceptor[String]("placeHolder")
  val id = Acceptor[String]("id")
  val className = Acceptor[String]("class")
  val border = CssAcceptor[String]("border")
  val color = CssAcceptor[String]("color")
  val background = CssAcceptor[String]("background")
}