package com.example

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import spray.json._
import scala.concurrent.ExecutionContext

class JsonSpec extends AnyWordSpecLike with Matchers with Json {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.global

  "a json parser" should {
    "parse the jsonString to an object instance" in {
      val nasdaqJson =
        """{"Volume":1879652,"Country":"United States","Market Cap":28963165850.00,"IPO Year":2018,"Symbol":"ZS","Net Change":3.64,"% Change":"1.752%","Sector":"Technology","Name":"Zscaler Inc. Common Stock","Industry":"EDP Services","Last Sale":"$211.43"}
          |""".stripMargin

      val nasdaqJSValue: JsValue = nasdaqJson.parseJson
      val nasdaq: Nasdaq = jsonToNasdaq(nasdaqJSValue)
      println(nasdaq.toString)
    }
  }
}
