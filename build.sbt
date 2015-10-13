name := """AkkaTyped"""

scalaVersion := "2.11.7"

val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion withSources(),
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion withSources(),
  "com.typesafe.akka" %% "akka-typed-experimental" % akkaVersion withSources(),
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
