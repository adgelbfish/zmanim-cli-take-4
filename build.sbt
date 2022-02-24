ThisBuild / version := "0.1.6-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "zmanim-cli"
  )

libraryDependencies += "com.kosherjava" % "zmanim" % "2.3.0"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"
libraryDependencies += "com.typesafe" % "config" % "1.4.1"


enablePlugins(JavaAppPackaging)