package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, MediaRanges, StatusCodes}
import akka.http.scaladsl.model.headers.Accept
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.Logger
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait Csv extends Json {

  implicit val system: ActorSystem

  private val logger = Logger(classOf[Csv])

  def httpRequest(uri: String): HttpRequest = {
    HttpRequest(uri = uri)
      .withHeaders(Accept(MediaRanges.`text/*`))
  }

  implicit def toHttpRequest(url: String) = httpRequest(url)

  val jsonFlow = Flow[Map[String,String]].map(toJson)

  def httpRequestStream(httpRequests: Seq[HttpRequest], parallelism: Int) = {
    Source
      .apply(httpRequests)
      .mapAsync(parallelism) { req =>
        Http().singleRequest(req)
      }
      .flatMapConcat(extractEntityData)
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.toMapAsStrings())
      .map(toNasdaq)
      .map(nasdaqToJson).recover{ throwable =>
      logger.error(throwable.toString)
    }
      .to(Sink.foreach(nasdaq => println(nasdaq.toString)))
  }

  def extractEntityData(httpResponse: HttpResponse):Source[ByteString,_] = {
    httpResponse match {
      case HttpResponse(OK, _, entity, _)  => {
        entity.dataBytes
      }
      case _ =>
        logger.error("Could not retrieve .csv")
        Source.failed(new Exception("Could not retrieve .csv"))
    }
  }

}
