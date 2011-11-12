# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) Sources into JavaScript.

![CoffeeScript](http://jashkenas.github.com/coffee-script/documentation/images/logo.png) + ![Scala](https://github.com/downloads/softprops/coffeescripted-sbt/scala_logo.png)

## Install

For sbt 0.11.+

In your project, define a file for plugin library dependencies, `project/plugins.sbt`

And add the following lines

    resolvers += "less is" at "http://repo.lessis.me"

    addSbtPlugin("me.lessis" % "coffeescripted-sbt" % "0.2.0")

In your `build.sbt` file add the following line

    seq(coffeeSettings: _*)

## Settings
    coffee # compiles CoffeeScript source files
    bare(for coffee) # removes function wrapper from generated JavaScript sources
    charset(for coffee) # sets the character encoding used to generate files
    exclude-filter(for coffee) # filter for files ignored by plugin
    unmanaged-sources(for coffee) # lists resolved CoffeeScript sources
    clean(for coffee) # deletes compiled javascript resources    
    config:source-directory(for coffee) # directory containging CoffeeScript sources
    config:resource-managed(for coffee) # where compiled javascript will be copied to

## Customization

By default, generate javascript resources are wrapped in annonymous module.

    (function() {
      // your code here
    }).call(this);

CoffeeScripted enables `bare`-style javascript as well. You can controll this with the `bare` setting.
To enable it, append the following to your build definition.

    (CoffeeKeys.bare in (Compile, CoffeeKeys.coffee)) := true

By default, CoffeeScript sources are resolved under `src/main/coffee` and compiled javascript will by copied to `target/scala-2.9.1/resource_managed/main/js`

You can override this behavior by appending the following to your build definition.

    (resourceManaged in (Compile, CoffeeKeys.coffee)) <<= (crossTarget in Compile)(_ / "your_preference" / "js")

## Props


This was converted into a plugin based on a [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.11.*

Doug Tangren (softprops) 2011
