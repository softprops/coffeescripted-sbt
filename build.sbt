sbtPlugin := true

name := "coffeescripted-sbt"

organization := "me.lessis"

version := "0.2.4-SNAPSHOT"

libraryDependencies += "org.mozilla" % "rhino" % "1.7R3"

scalacOptions += Opts.compile.deprecation

seq(scriptedSettings: _*)

seq(lsSettings:_*)

(LsKeys.tags in LsKeys.lsync) := Seq("sbt", "coffeescript")

description := "Sbt plugin for compiling CoffeeScript sources"

licenses <<= (version)(v => Seq(
  ("MIT", url("https://github.com/softprops/coffeescripted-sbt/blob/%s/LICENSE" format v))
))

publishTo := Some(Classpaths.sbtPluginReleases)

//publishTo := Some(Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns))

publishArtifact in Test := false

publishMavenStyle := false

pomExtra := (
  <scm>
    <url>git@github.com:softprops/coffeescripted-sbt.git</url>
    <connection>scm:git:git@github.com:softprops/coffeescripted-sbt.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>
)
