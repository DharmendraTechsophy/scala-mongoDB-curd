name := "scala"

version := "0.1"

scalaVersion := "2.13.5"
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "1.0.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-slf4j"%"2.5.31",
  "org.reactivemongo" %% "reactivemongo-scalafix" % "1.0.3"
)
