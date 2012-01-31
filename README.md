# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) Sources into JavaScript.

![CoffeeScript](http://jashkenas.github.com/coffee-script/documentation/images/logo.png) + ![Scala](https://github.com/downloads/softprops/coffeescripted-sbt/scala_logo.png)

## Install

For sbt 0.11.+

In your project, define a file for plugin library dependencies, `project/plugins.sbt`

And add the following lines

    addSbtPlugin("me.lessis" % "coffeescripted-sbt" % "0.2.1")

In your `build.sbt` file add the following line

    seq(coffeeSettings: _*)

## Settings
    coffee # compiles CoffeeScript source files
    bare(for coffee) # removes function wrapper from generated JavaScript sources
    iced(for coffee) # compiles coffeescript used the [iced][iced] coffeescript compile
    charset(for coffee) # sets the character encoding used to generate files
    exclude-filter(for coffee) # filter for files ignored by plugin
    unmanaged-sources(for coffee) # lists resolved CoffeeScript sources
    clean(for coffee) # deletes compiled javascript resources    
    config:source-directory(for coffee) # directory containging CoffeeScript sources
    config:resource-managed(for coffee) # where compiled javascript will be copied to

## Customization

By default, generated javascript resources are wrapped in annonymous module.

    (function() {
    
      // your code here
    
    }).call(this);

CoffeeScripted enables `bare`-style javascript as well. Bare-style javascript is javascript that is not defined wrapped in an anonymous function module. You can control this in coffeescripted-sbt by overriding the `bare` setting. To enable bare-style javascript, append the following to your build definition.

    (CoffeeKeys.bare in (Compile, CoffeeKeys.coffee)) := true

The enhanged [iced][iced] compiler adds two new asyncronous control primatives `await` and `defer`. To take advanage of them use the `iced` setting

    (CoffeeKeys.iced in (Compile, CoffeeKeys.coffee)) := true

By default, CoffeeScript sources are resolved under `src/main/coffee` and compiled javascript will by copied to `target/scala-2.9.1/resource_managed/main/js`

You can override this behavior by overriding the `resourceManaged` setting scoped to your configration and the `CoffeeKeys.coffee` task. Below is an example you can append to your build definition which will copy generated javascript to target/:scala-version/your_preference/js

    (resourceManaged in (Compile, CoffeeKeys.coffee)) <<= (crossTarget in Compile)(_ / "your_preference" / "js")

## Props

This was converted into a plugin based on a [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.11.*

This plugin currently uses the v1.2.0 version of the coffeescript js compiler and the v1.2.0j version of the [iced][iced] coffeescript compiler.

Doug Tangren (softprops) 2011-2012

[iced]: http://maxtaco.github.com/coffee-script/
