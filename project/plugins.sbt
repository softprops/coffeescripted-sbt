libraryDependencies <+= (sbtVersion)("org.scala-tools.sbt" %% "scripted-plugin" % _)

resolvers ++= Seq(
  "less is" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")
