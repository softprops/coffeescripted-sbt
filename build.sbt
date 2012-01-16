sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

version <<= (sbtVersion) { (sv) =>
  val v = "0.2.1"
  if(sv.startsWith("0.11")) v
  else error("unsupported version of sbt: %s" format sv)
}

libraryDependencies += "rhino" % "js" % "1.7R2"

publishMavenStyle := true

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

seq(scriptedSettings: _*)

seq(lsSettings:_*)

(LsKeys.tags in LsKeys.lsync) := Seq("sbt", "coffeescript")

description := "Sbt plugin for compiling CoffeeScript sources"

licenses <<= (version)(v => Seq(
  ("MIT", url("https://github.com/softprops/coffeescripted-sbt/blob/%s/LICENSE" format v))
))

