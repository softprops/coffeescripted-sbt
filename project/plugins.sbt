//resolvers += Resolver.url("scalasbt", new URL(
//  "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(
//  Resolver.ivyStylePatterns)

addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")

libraryDependencies <+= (sbtVersion)("org.scala-sbt" %% "scripted-plugin" % _)

resolvers ++= Seq(
  "coda" at "http://repo.codahale.com",
  //Classpaths.sbtPluginReleases
  Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")
