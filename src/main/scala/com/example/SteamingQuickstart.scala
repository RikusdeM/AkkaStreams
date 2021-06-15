package com.example

import akka.actor.{ActorLogging, ActorSystem}

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.example.Routes.routes

import scala.io.StdIn

object SteamingQuickstart extends App with Csv {

  implicit val system = ActorSystem("my-system")
  implicit val executionContext = ExecutionContext.global

  val nasdaqCsv = "http://localhost:8000/nasdaq_screener_1623702059061.csv"
  val stream = httpRequestStream(Seq(nasdaqCsv),1)
  stream.run()

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
