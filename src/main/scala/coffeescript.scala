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
    // possible candidate for community key repo
    val charset = SettingKey[Charset]("charset", "Sets the character encoding used in file IO. Defaults to utf-8")
    // think about changing to filter in the next rel (maybe, I like the way coffee:filter sounds :))
    val filter = SettingKey[FileFilter]("filter", "Filter for selecting coffee sources from default directories.")
    val excludeFilter = SettingKey[FileFilter]("exclude-filter", "Filter for excluding files from default directories.")
  }

  type Compiler = { def compile(src: String): Either[String, String] }

  private def javascript(sources: File, coffee: File, targetDir: File) =
    Some(new File(targetDir, IO.relativize(sources, coffee).get.replace(".coffee",".js")))

  private def compile(compiler: Compiler, charset: Charset, out: Logger)(pair: (File, File)) =
    try {
      val (coffee, js) = pair
      out.debug("Compiling %s" format coffee)
      compiler.compile(io.Source.fromFile(coffee)(io.Codec(charset)).mkString).fold({ err =>
        error(err)
      }, { compiled =>
        IO.write(js, compiled)
        out.debug("Wrote to file %s" format js)
        js
      })
    } catch { case e: Exception =>
      throw new RuntimeException(
        "error occured while compiling %s: %s" format(pair._1, e.getMessage), e
      )
    }

  private def compiled(under: File) = (under ** "*.js").get

  private def compileChanged(sources: File, target: File, compiler: Compiler, charset: Charset, out: Logger) =
    (for (coffee <- (sources ** "*.coffee").get;
          js <- javascript(sources, coffee, target)
      if (coffee newerThan js)) yield {
        (coffee, js)
      }) match {
        case Nil =>
          out.info("No CoffeeScripts to compile")
          compiled(target)
        case xs =>
          out.info("Compiling %d CoffeeScripts to %s" format(xs.size, target))
          xs map compile(compiler, charset, out)
          out.debug("Compiled %s CoffeeScripts" format xs.size)
          compiled(target)
      }

  private def coffeeCleanTask =
    (streams, resourceManaged in coffee) map {
      (out, target) =>
        out.log.info("Cleaning generated JavaScript under " + target)
        IO.delete(target)
    }

  private def coffeeSourceGeneratorTask =
    (streams, sourceDirectory in coffee, resourceManaged in coffee, charset in coffee, bare in coffee) map {
      (out, sourceDir, targetDir, charset, bare) =>
        compileChanged(sourceDir, targetDir, compiler(bare), charset, out.log)
    }

  // move defaultExcludes to excludeFilter in unmanagedSources later
  private def coffeeSourcesTask =
    (sourceDirectory in coffee, filter in coffee, excludeFilter in coffee) map {
      (sourceDir, filt, excl) =>
         sourceDir.descendentsExcept(filt, excl).get
    }

  private def compiler(bare: Boolean) = if(bare) Compiler(true) else Compiler()

  def coffeeSettingsIn(c: Configuration): Seq[Setting[_]] =
    inConfig(c)(coffeeSettings0 ++ Seq(
      resourceGenerators in c <+= (unmanagedSources in coffee).identity,
      sourceDirectory in coffee <<= (sourceDirectory in c) { _ / "coffee" },
      resourceManaged in coffee <<= (resourceManaged in c) { _ / "js" },
      resourceGenerators in c <+= coffee.identity
    )) ++ Seq(
      cleanFiles <+= (resourceManaged in coffee in c).identity,
      watchSources <++= (unmanagedSources in coffee in c).identity
    )

  def coffeeSettings: Seq[Setting[_]] =
    coffeeSettingsIn(Compile) ++ coffeeSettingsIn(Test)

  def coffeeSettings0: Seq[Setting[_]] = Seq(
    bare in coffee := false,
    charset in coffee := Charset.forName("utf-8"),
    filter in coffee := "*.coffee",
    // change to (excludeFilter in Global) when dropping support of sbt 0.10.*
    excludeFilter in coffee := (".*"  - ".") || HiddenFileFilter,
    unmanagedSources in coffee <<= coffeeSourcesTask,
    clean in coffee <<= coffeeCleanTask,
    coffee <<= coffeeSourceGeneratorTask
  )
}
