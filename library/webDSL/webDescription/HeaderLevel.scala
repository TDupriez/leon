package leon.webDSL.webDescription

/**
  * Created by dupriez on 3/22/16.
  */
sealed trait HeaderLevel
case class HLOne() extends HeaderLevel
case class HLTwo() extends HeaderLevel
case class HLThree() extends HeaderLevel
case class HLFour() extends HeaderLevel
case class HLFive() extends HeaderLevel
case class HLSix() extends HeaderLevel