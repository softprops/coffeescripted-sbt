# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) Sources into JavaScript.

## Install

In you project, define a file for plugin library dependencies `project/plugins/build.sbt`

And add the following line

    libraryDependencies += "me.lessis" %% "coffeescripted-sbt" % "0.1.0-SNAPSHOT"

## Settings

    coffee-source # directory containing CoffeeScript sources. defaults to src/main/coffeescript
    coffee-target # target directory for generated JavaScript sources. defaults to src/main/www/js

## Commands

    coffee-clean # cleans the generated JavaScript files under the coffee-target path
    coffee # compiles any stale *.coffee sources

This plugin also piggybacks on the `compile` task, generating new JavaScript sources from stale CoffeeScript files and appends the `coffee-target` to Sbt's `cleanFiles` task which will remove generated JavaScript sources on a `clean`.

## Props

This was converted into a plugin from based on a  [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.10.0

Doug Tangren (softprops) 2011
