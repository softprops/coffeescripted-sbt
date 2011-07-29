# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) Sources into JavaScript.

![CoffeeScript](http://jashkenas.github.com/coffee-script/documentation/images/logo.png) + ![Scala](https://github.com/downloads/softprops/coffeescripted-sbt/scala_logo.png)

## Install

In you project, define a file for plugin library dependencies `project/plugins/build.sbt`

And add the following lines

    resolvers += "less is" at "http://repo.lessis.me"

    libraryDependencies <++= sbtVersion(v => Seq(
       "me.lessis" %% "coffeescripted-sbt" % "0.1.2-%s".format(v),
       "org.jcoffeescript" % "jcoffeescript" % "1.1" from "http://cloud.github.com/downloads/yeungda/jcoffeescript/jcoffeescript-1.1.jar"
    ))

## Settings

    coffee:bare # removes function wrapper from generated JavaScript sources
    coffee:source-directory # Directory containing CoffeeScript Sources
    coffee:target-directory # target directory for generated JavaScript sources. defaults to src/main/js under target/{scala_version}/resource_managed

## Commands

    coffee # compiles any stale *.coffee sources
    coffee:clean # cleans the generated JavaScript files under the coffee:target-directory path
    coffee:sources # returns all CoffeeScript sources

## Props

This was converted into a plugin from based on a [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.10.*

Doug Tangren (softprops) 2011
