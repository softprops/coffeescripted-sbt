package coffeescript

import sbt._
import sbt.Keys._
import sbt.Project.Initialize
import scala.collection.JavaConversions._
import java.nio.charset.Charset
import java.io.File

object Plugin extends sbt.Plugin {
  import CoffeeKeys._

  object CoffeeKeys {
    val coffee = TaskKey[Seq[File]]("coffee", "Compile coffee sources.")
    val bare = SettingKey[Boolean]("bare", "Compile coffee sources without top-level function wrapper.")
    val iced = SettingKey[Boolean]("iced", """When true, The coffee command will compile vanilla CoffeeScript and "Iced" CoffeeScript sources """)
    // possible candidate for community key repo
    val charset = SettingKey[Charset]("charset", "Sets the character encoding used in file IO. Defaults to utf-8")
    // think about changing to filter in the next rel (maybe, I like the way coffee:filter sounds :))
    val filter = SettingKey[FileFilter]("filter", "Filter for selecting coffee sources from default directories.")
    val excludeFilter = SettingKey[FileFilter]("exclude-filter", "Filter for excluding files from default directories.")
  }

  type Compiler = { def compile(src: String): Either[String, String] }

  private def javascript(sources: File, coffee: File, targetDir: File) =
    Some(new File(targetDir, IO.relativize(sources, coffee).get.replace(".coffee",".js").replace(".iced", ".js")))

  private def compileSources(bare: Boolean, charset: Charset, iced: Boolean, log: Logger)(pair: (File, File)) =
    try {
      val (coffee, js) = pair
      log.debug("Compiling %s" format coffee)
      val compiler = if(iced) Iced else Vanilla
      compiler.compile(io.Source.fromFile(coffee)(io.Codec(charset)).mkString, bare).fold({ err =>
        sys.error(err)
      }, { compiled =>
        IO.write(js, compiled)
        log.debug("Wrote to file %s" format js)
        js
      })
    } catch { case e: Exception =>
      throw new RuntimeException(
        "error occured while compiling %s with %s: %s" format(
          pair._1, if(iced) Iced else Vanilla, e.getMessage), e
      )
    }

  private def compiled(under: File) = (under ** "*.js").get

  private def compileChanged(
    sources: File, target: File, incl: FileFilter, excl: FileFilter,
    bare: Boolean, charset: Charset, iced: Boolean, log: Logger) =
    (for (coffee <- sources.descendentsExcept(incl, excl).get;
          js <- javascript(sources, coffee, target)
      if (coffee newerThan js)) yield {
        (coffee, js)
      }) match {
        case Nil =>
          log.debug("No CoffeeScripts to compile")
          compiled(target)
        case xs =>
          log.info("Compiling %d CoffeeScripts to %s" format(xs.size, target))
          xs map compileSources(bare, charset, iced, log)
          log.debug("Compiled %s CoffeeScripts" format xs.size)
          compiled(target)
      }

  private def coffeeCleanTask =
    (streams, resourceManaged in coffee) map {
      (out, target) =>
        out.log.info("Cleaning generated JavaScript under " + target)
        IO.delete(target)
    }

  private def coffeeCompilerTask =
    (streams, sourceDirectory in coffee, resourceManaged in coffee,
     filter in coffee, excludeFilter in coffee, charset in coffee, bare in coffee, iced in coffee) map {
      (out, sourceDir, targetDir, incl, excl, charset, bare, iced) =>
        compileChanged(sourceDir, targetDir, incl, excl, bare, charset, iced, out.log)
    }

  // move defaultExcludes to excludeFilter in unmanagedSources later
  private def coffeeSourcesTask =
    (sourceDirectory in coffee, filter in coffee, excludeFilter in coffee) map {
      (sourceDir, filt, excl) =>
         sourceDir.descendentsExcept(filt, excl).get
    }

  def coffeeSettingsIn(c: Configuration): Seq[Setting[_]] =
    inConfig(c)(coffeeSettings0 ++ Seq(
      sourceDirectory in coffee <<= (sourceDirectory in c) { _ / "coffee" },
      resourceManaged in coffee <<= (resourceManaged in c) { _ / "js" },
      cleanFiles in coffee <<= (resourceManaged in coffee)(_ :: Nil),
      watchSources in coffee <<= (unmanagedSources in coffee)
    )) ++ Seq(
      cleanFiles <+= (resourceManaged in coffee in c),
      watchSources <++= (unmanagedSources in coffee in c),
      resourceGenerators in c <+= coffee in c,
      compile in c <<= (compile in c).dependsOn(coffee in c)
    )

  def coffeeSettings: Seq[Setting[_]] =
    coffeeSettingsIn(Compile) ++ coffeeSettingsIn(Test)

  def coffeeSettings0: Seq[Setting[_]] = Seq(
    bare in coffee := false,
    iced in coffee := false,
    charset in coffee := Charset.forName("utf-8"),
    filter in coffee <<= (iced in coffee)((iced) => if(iced) "*.coffee" || "*.iced" else "*.coffee"),
    // change to (excludeFilter in Global) when dropping support of sbt 0.10.*
    excludeFilter in coffee := (".*" - ".") || HiddenFileFilter,
    unmanagedSources in coffee <<= coffeeSourcesTask,
    clean in coffee <<= coffeeCleanTask,
    coffee <<= coffeeCompilerTask
  )
}
