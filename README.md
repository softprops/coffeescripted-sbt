# CoffeeScripted Sbt

In the tradition of [CoffeeScript Plugins](https://github.com/rubbish/coffee-script-sbt-plugin), this Sbt plugin compiles [CoffeeScript](http://jashkenas.github.com/coffee-script/) sources into JavaScript.

![CoffeeScript](http://jashkenas.github.com/coffee-script/documentation/images/logo.png) ![Scala](https://github.com/downloads/softprops/coffeescripted-sbt/scala_logo.png)

## Install

For sbt 0.11.+

In your project, define a file for plugin library dependencies, `project/plugins.sbt`

And add the following lines

    addSbtPlugin("me.lessis" % "coffeescripted-sbt" % "0.2.3")
    
If you haven't already, append the sbt community plugin resolver to your plugin definition, and add the following line as well

    resolvers += Resolver.url("sbt-plugin-releases",
      new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
        Resolver.ivyStylePatterns)

In your `build.sbt` file add the following line

    seq(coffeeSettings: _*)

## Settings

    coffee # compiles CoffeeScript source files
    bare(for coffee) # removes function wrapper from generated JavaScript sources
    iced(for coffee) # compiles CoffeeScript use the iced CoffeeScript compiler
    charset(for coffee) # sets the character encoding used to generate files
    exclude-filter(for coffee) # filter for files ignored by plugin
    unmanaged-sources(for coffee) # lists resolved CoffeeScript sources
    clean(for coffee) # deletes compiled JavaScript resources    
    config:source-directory(for coffee) # directory containging CoffeeScript sources
    config:resource-managed(for coffee) # where compiled JavaScript will be copied to

## Customization

By default, generated JavaScript resources are wrapped in an anonymous module.

    (function() {
    
      // your code here
    
    }).call(this);

CoffeeScripted enables `bare`-style JavaScript as well. Bare-style JavaScript is JavaScript that is not defined wrapped in an anonymous function module. You can control this in coffeescripted-sbt by overriding the `bare` setting. To enable bare-style JavaScript, append the following to your build definition.

    (CoffeeKeys.bare in (Compile, CoffeeKeys.coffee)) := true

The enhanced [iced][iced] compiler adds two new asynchronous control primitives `await` and `defer`. To take advantage of them, use the `iced` setting.

    (CoffeeKeys.iced in (Compile, CoffeeKeys.coffee)) := true

Note, Iced CoffeeScript is a superset of CoffeeScript, meaning all of your CoffeeScript will work without change under the `iced` compiler. In iced mode, all `.coffee` and `.iced` files will get compiled using the `iced` compiler. You can revert back to standard CoffeeScript by leaving the `iced` setting as `false`. Only `.coffee` files will be compiled under the default coffee compiler, leaving you free to mix `ice` with your coffee safely.

By default, CoffeeScript sources are resolved under `src/main/coffee` and compiled JavaScript will be copied to `target/scala-2.9.1/resource_managed/main/js`

You can override this behavior by overriding the `resourceManaged` setting scoped to your configuration and the `CoffeeKeys.coffee` task. Below is an example you can append to your build definition. It will copy generated JavaScript to target/:scala-version/your_preference/js

    (resourceManaged in (Compile, CoffeeKeys.coffee)) <<= (crossTarget in Compile)(_ / "your_preference" / "js")

## Props

This was converted into a plugin based on a [gist](https://gist.github.com/1018046) by [zentroupe](https://gist.github.com/zentrope) targeting sbt 0.11.*

This plugin currently uses the v1.2.0 version of the CoffeeScript js compiler and the v1.2.0j version of the [iced][iced] CoffeeScript compiler.

Doug Tangren (softprops) 2011-2012

[iced]: http://maxtaco.github.com/coffee-script/
