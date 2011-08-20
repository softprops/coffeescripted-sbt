# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) Sources into JavaScript.

![CoffeeScript](http://jashkenas.github.com/coffee-script/documentation/images/logo.png) + ![Scala](https://github.com/downloads/softprops/coffeescripted-sbt/scala_logo.png)

## Install

In you project, define a file for plugin library dependencies `project/plugins/build.sbt`

And add the following lines

    resolvers += "less is" at "http://repo.lessis.me"

    libraryDependencies <+= sbtVersion(v =>
      "me.lessis" %% "coffeescripted-sbt" % "0.1.4-%s".format(v)
    )

And in your `build.sbt` file add the following line

    seq(coffeescript.CoffeeScript.coffeeSettings: _*)

## Settings

    coffee:bare # removes function wrapper from generated JavaScript sources
    coffee:source-directory # Directory containing CoffeeScript sources
    coffee:filter # FileFilter used for including CoffeeScript sources
    coffee:target-directory # target directory for generated JavaScript sources. defaults to src/main/js under target/{scala_version}/resource_managed

## Tasks

    coffee # compiles any stale *.coffee sources
    coffee:clean # cleans the generated JavaScript files under the coffee:target-directory path
    coffee:sources # returns all CoffeeScript sources

## Props

This was converted into a plugin from based on a [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.10.*

Doug Tangren (softprops) 2011
