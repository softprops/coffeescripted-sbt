sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

scalacOptions += "-deprecation"

//wait for posterous 2.9 update
//version in Posterous := "0.1.5"

version <<= (sbtVersion) { (sv) =>
  val v = "0.2.0"
  if(sv.startsWith("0.10")) "%s-%s-SNAPSHOT" format(v,sv)
  else if(sv.startsWith("0.11")) v + "-SNAPSHOT"
  else error("unsupported version of sbt: %s" format sv)
}

libraryDependencies += "rhino" % "js" % "1.7R2"

publishMavenStyle := true

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))

seq(ScriptedPlugin.scriptedSettings: _*)

