libraryDependencies <++= (sbtVersion) { sv => Seq(
    //"net.databinder" %% "posterous-sbt" % ("0.3.0_sbt" + sv)
    "org.scala-tools.sbt" %% "scripted-plugin" % sv
  )
}
