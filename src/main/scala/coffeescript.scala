package coffeescript

import scala.collection.JavaConversions._

import sbt._
import Keys._
import Project.Initialize

import java.io.File

object CoffeeScript extends Plugin {

  var Coffee = config("coffee") extend(Runtime)

  type Compiler = { def compile(src: String): Either[String, String] }

  val coffee = TaskKey[Seq[File]]("coffee", "Compile coffee sources.")
  val clean = TaskKey[Unit]("clean", "Clean compiled coffee sources.")
  val sources = TaskKey[Seq[File]]("sources", "List of coffee source files")
  val sourceDirectory = SettingKey[File]("source-directory", "Directory containing coffee sources.")
  val coffeeFilter = SettingKey[FileFilter]("coffee-filter", "Filter for selecting coffee sources from default directories.")
  val targetDirectory = SettingKey[File]("target-directory", "Output directory for compiled coffee sources.")
  val bare = SettingKey[Boolean]("bare", "Compile coffee sources without top-level function wrapper.")

  private def javascript(sources: File, coffee: File, targetDir: File) =
    Some(new File(targetDir, IO.relativize(sources, coffee).get.replace(".coffee",".js")))

  private def compile(compiler: Compiler, out: Logger)(pair: (File, File)) =
    try {
      val (coffee, js) = pair
      out.debug("Compiling %s" format coffee)
      compiler.compile(io.Source.fromFile(coffee).mkString).fold({ err =>
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

  private def compileChanged(sources: File, target: File, compiler: Compiler, out: Logger) =
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
          xs map compile(compiler, out)
          out.debug("Compiled %s CoffeeScripts" format xs.size)
          compiled(target)
      }

  private def coffeeCleanTask =
    (streams, targetDirectory) map {
      (out, target) =>
        out.log.info("Cleaning generated JavaScript under " + target)
        IO.delete(target)
    }

  private def coffeeSourceGeneratorTask =
    (streams, sourceDirectory, targetDirectory, bare) map {
      (out, sourceDir, targetDir, bare) =>
        compileChanged(sourceDir, targetDir, compiler(bare), out.log)
    }

  private def coffeeSourcesTask =
    (sourceDirectory, coffeeFilter, defaultExcludes) map {
      (sourceDir, filt, excl) =>
         sourceDir.descendentsExcept(filt,excl).get
    }

  private def compiler(bare: Boolean) = if(bare) Compiler(true) else Compiler()

  def coffeeSettings: Seq[Setting[_]] = inConfig(Coffee)(Seq(
    sourceDirectory <<= (sourceDirectory in Compile) { _ / "coffee" },
    coffeeFilter :== "*.coffee",
    targetDirectory <<= (resourceManaged in Compile) { _ / "js" },
    sources <<= coffeeSourcesTask,
    bare := false,
    cleanFiles <+= targetDirectory.identity,
    clean <<= coffeeCleanTask,
    coffee <<= coffeeSourceGeneratorTask,
    resourceGenerators in Compile <+= coffee.identity
  )) ++ Seq(
    coffee <<= (coffee in Coffee).identity,
    watchSources <++= (sources in Coffee).identity
  )

}
