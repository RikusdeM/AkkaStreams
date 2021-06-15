package com.example

import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class Nasdaq(
    Symbol: String,
    Name: String,
    `Last Sale`: String,
    `Net Change`: Double,
    `% Change`: String,
    `Market Cap`: Double,
    Country: String,
    `IPO Year`: Int,
    Volume: Int,
    Sector: String,
    Industry: String
)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val nasdaqFormat: RootJsonFormat[Nasdaq] = jsonFormat11(
    Nasdaq.apply
  )
}

trait Json extends DefaultJsonProtocol {
  import MyJsonProtocol._
  implicit val executionContext: ExecutionContext

  def toJson(map: Map[String, String])(implicit
      jsWriter: JsonWriter[Map[String, String]]
  ): JsValue = jsWriter.write(map)

  def toNasdaq(map: Map[String, String]):Nasdaq = {
    val nas = Try {
      Nasdaq(
        map("Symbol"),
        map("Name"),
        map("Last Sale"),
        map("Net Change").toDouble,
        map("% Change"),
        map("Market Cap").toDouble,
        map("Country"),
        map("IPO Year").toInt,
        map("Volume").toInt,
        map("Sector"),
        map("Industry")
      )
    }
    nas.getOrElse(Nasdaq("","","",0.0,"",0.0,"",0,0,"",""))
  }


  val nasdaqToJson: Nasdaq => JsValue = (nasdaq: Nasdaq) => nasdaq.toJson
  val jsonToNasdaq: JsValue => Nasdaq = (nasdaqJson: JsValue) =>
    nasdaqJson.convertTo[Nasdaq]

}
