name := "streamstest"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
val AlpakkaVersion = "2.0.2"

resolvers ++= Seq(
  Resolver.mavenCentral,
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.apache.kafka" % "kafka-clients" % "2.6.0",
  "org.testcontainers" % "kafka" % "1.14.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",

  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.1" % "test",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)
