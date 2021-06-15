package com.example

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalacheck._
import Gen._
import Arbitrary.arbitrary
import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.testkit.TestKit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class User(name: String, age: Int)

class SteamsTestSpec() extends Properties("Users") with AnyWordSpecLike {

  implicit val system = ActorSystem("MySpec")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val genUsr = const[User](User("rikus", 32))

  "a reactive stream" should {
    "create a stream" in {

      val users: Gen[User] = for {
        name <- arbitrary[String].suchThat(str => str.length < 20)
        age <- arbitrary[Short].suchThat(a => a > 0 && a < 100)
      } yield {
        User(name, age)
      }

      def alwaysUser[A](a: Gen[A]): A = {
        val usr = a.sample
        if (usr.isDefined) usr.get
        else alwaysUser(a)
      }

      def genUser: Gen[User] = oneOf(genUsr, lzy(users))

      def createABunchOfExamples[A](
          default: A,
          total: Int,
          generator: Gen[A],
          collection: Seq[A]
      ): Seq[A] = {

        if (total > 0) {
          createABunchOfExamples(
            default,
            total - 1,
            generator,
            collection :+ alwaysUser(generator)
          )
        } else {
          collection
        }
      }

      val usersCollection = createABunchOfExamples[User](
        User("Rikus", 32),
        10,
        users,
        Seq.empty[User]
      )

      println(usersCollection)

      val ageDoubler = Flow[User].map(u => u.copy(age = u.age * 2))
      val loggerSink: Sink[User, Future[Done]] =
        Sink.foreach(u => println(u.toString))

     Source(usersCollection)
        .via(ageDoubler)
        .to(loggerSink)
        .run()

    }
  }

}
