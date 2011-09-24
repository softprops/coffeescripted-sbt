libraryDependencies <+= sbtVersion { sv =>
  val v = "0.1.6"
  if(sv.startsWith("0.10")) "me.lessis" %% "coffeescripted-sbt" % "%s-%s-SNAPSHOT".format(v,sv)
  else if(sv.startsWith("0.11")) "me.lessis" % "coffeescripted-sbt" % (v + "-SNAPSHOT")
  else error("unsupported version of sbt: %s" format sv)
}
