name := "word-indicator"
maintainer := "galudisu@gmail.com"

organization := "info.galudisu"

/* description about this project */
description := "word indicator for analysis"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion          = "2.5.24"
val akkaHttpVersion      = "10.1.3"
val log4j2Version        = "2.9.0"
val scalatestFullVersion = "3.0.3"
val scalaMockVersion     = "3.6.0"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-Xlint"
)

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  // -- akka --
  "com.typesafe.akka" %% "akka-actor"            % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"           % akkaVersion,
  "com.typesafe.akka" %% "akka-remote"           % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"          % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools"    % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  // --log---
  "com.typesafe.akka"          %% "akka-slf4j"      % akkaVersion,
  "org.apache.logging.log4j"   % "log4j-slf4j-impl" % log4j2Version,
  "org.apache.logging.log4j"   % "log4j-api"        % log4j2Version,
  "org.apache.logging.log4j"   % "log4j-core"       % log4j2Version,
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
  // --opencsv--
  "com.opencsv"        % "opencsv"       % "4.6",
  "org.apache.commons" % "commons-lang3" % "3.9",
// -- test --
  "com.typesafe.akka" %% "akka-http-testkit"           % akkaHttpVersion      % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"         % akkaVersion          % Test,
  "org.specs2"        %% "specs2-core"                 % "4.3.4"              % Test,
  "org.specs2"        %% "specs2-mock"                 % "4.3.4"              % Test,
  "org.scalatest"     %% "scalatest"                   % scalatestFullVersion % Test,
  "org.scalamock"     %% "scalamock-scalatest-support" % scalaMockVersion     % Test
)

version in Docker := "latest"
dockerExposedPorts in Docker := Seq(1600)
dockerRepository := Some("ericsson")
dockerBaseImage := "java"
enablePlugins(JavaAppPackaging)
