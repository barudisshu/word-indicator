name := "word-indicator"

organization := "info.galudisu"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion          = "2.5.23"
val akkaHttpVersion      = "10.1.3"
val log4j2Version        = "2.9.0"
val scalatestFullVersion = "3.0.3"
val scalaMockVersion     = "3.6.0"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  // -- akka --
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  // --log---
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version,
  "org.apache.logging.log4j" % "log4j-api" % log4j2Version,
  "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
  // -- test --
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.specs2" %% "specs2-core" % "4.3.4" % Test,
  "org.specs2" %% "specs2-mock" % "4.3.4" % Test,
  "org.scalatest" %% "scalatest" % scalatestFullVersion % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % scalaMockVersion % Test
)