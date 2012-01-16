sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

version <<= (sbtVersion) { (sv) =>
  val v = "0.2.1-SNAPSHOT"
  if(sv.startsWith("0.11")) v
  else error("unsupported version of sbt: %s" format sv)
}

libraryDependencies += "rhino" % "js" % "1.7R2"

publishMavenStyle := true

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))

seq(scriptedSettings: _*)

seq(lsSettings:_*)

(LsKeys.tags in LsKeys.lsync) := Seq("sbt", "coffeescript")

(externalResolvers in LsKeys.lsync) := Seq("less is" at "http://repo.lessis.me")

description := "Sbt plugin for compiling CoffeeScript sources"

licenses <<= (version)(v => Seq(
  ("MIT", url("https://github.com/softprops/coffeescripted-sbt/blob/%s/LICENSE" format v))
))

