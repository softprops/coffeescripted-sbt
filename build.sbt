sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

version <<= sbtVersion("0.1.2-%s-SNAPSHOT" format _)

libraryDependencies ++= Seq(
   "org.jcoffeescript" % "jcoffeescript" % "1.1" from "http://cloud.github.com/downloads/yeungda/jcoffeescript/jcoffeescript-1.1.jar"
)

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
