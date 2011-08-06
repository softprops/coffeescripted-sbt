sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

posterousNotesVersion := "0.1.3"

version <<= (posterousNotesVersion, sbtVersion) ("%s-%s" format(_,_))

libraryDependencies += "rhino" % "js" % "1.7R2"

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
