sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
   "org.jcoffeescript" % "jcoffeescript" % "1.1" from "http://cloud.github.com/downloads/yeungda/jcoffeescript/jcoffeescript-1.0.jar"
)