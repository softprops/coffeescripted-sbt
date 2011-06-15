package coffeescript

import scala.collection.JavaConversions._

import sbt._
import Keys._
import Project.Initialize

import java.io.File

import org.jcoffeescript.{JCoffeeScriptCompiler, Option}

object CoffeesScript extends Plugin {

  type Compiler = { def compile(src: String): String }

  val coffeeSource = SettingKey[File]("coffee-source", "Directory containing coffee files.")
  val coffeeTarget = SettingKey[File]("coffee-target", "Output directory for translated coffee scripts.")
  val coffeeBare = SettingKey[Boolean]("coffee-bare", "Compile JavaScript without top-level function wrapper.")
  val coffeeClean = TaskKey[Unit]("coffee-clean", "Clean just the files generated from coffee sources.")
  val coffee = TaskKey[Seq[File]]("coffee", "Compile the coffee sources.")

  private def javascript(sources: File, coffee: File, targetDir: File) =
    new File(targetDir, IO.relativize(sources, coffee).get.replace(".coffee",".js"))

  private def outdated(coffee: File, javascript: File) =
    !javascript.exists || coffee.lastModified > javascript.lastModified

  private def compile(sources: File, target: File, compiler: Compiler, out: Logger)(coffee: File) =
    try {
      out.debug("Compiling %s" format coffee)
      val js = javascript(sources, coffee, target)
      IO.write(
        js,
        compiler.compile(io.Source.fromFile(coffee).mkString)
      )
      out.debug("Wrote to file %s" format js)
      js
    } catch { case e: Exception =>
      throw new RuntimeException(
        "error occured while compiling %s: %s" format(coffee, e.getMessage), e
      )
    }

  private def compileChanged(sources: File, target: File, compiler: Compiler, out: Logger) =
    (for (coffee <- (sources ** "*.coffee").get
      if (outdated(coffee, javascript(sources, coffee, target)))) yield {
        coffee
      }) match {
        case Nil =>
          out.info("No CoffeeScripts to compile")
          (target ** "*.js").get
        case xs =>
          out.info("Compiling %d CoffeeScripts to %s" format(xs.size, target))
          xs map compile(sources, target, compiler, out)
          (target ** "*.js").get
      }

  private def coffeeCleanTask =
    (streams, coffeeTarget) map {
      (out, target) =>
        out.log.info("Cleaning generated JavaScript under " + target)
        IO.delete(target)
    }

  private def coffeeSourceGeneratorTask =
    (streams, coffeeSource, coffeeTarget, coffeeBare) map {
      (out, sourceDir, targetDir, bare) =>
        compileChanged(sourceDir, targetDir, compiler(bare), out.log)
    }

  private def compiler(bare: Boolean) =  new JCoffeeScriptCompiler(if(bare) Option.BARE :: Nil else Nil)

  /** these commands will be automatically added to projects using plugin */
  override def settings = Seq (
    coffeeSource <<= (sourceDirectory in Compile) { _ / "coffee" },
    coffeeTarget <<= (resourceManaged in Compile) { _ / "www" / "js" },
    coffeeBare := false,
    cleanFiles <+= coffeeTarget.identity,
    coffeeClean <<= coffeeCleanTask,
    coffee <<= coffeeSourceGeneratorTask,
    resourceGenerators in Compile <+= coffee.identity
  )

}
