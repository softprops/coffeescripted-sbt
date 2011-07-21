package coffeescript

import scala.collection.JavaConversions._

import sbt._
import Keys._
import Project.Initialize

import java.io.File

import org.jcoffeescript.{JCoffeeScriptCompiler, Option}

object CoffeeScript extends Plugin {

  var Coffee = config("coffee") extend(Runtime)

  type Compiler = { def compile(src: String): String }

  val coffee = TaskKey[Seq[File]]("coffee", "Compile coffee sources.")
  val coffeeClean = TaskKey[Unit]("coffee-clean", "Clean compiled coffee sources.")
  val coffeeSource = SettingKey[File]("coffee-source", "Directory containing coffee sources.")
  val coffeeTarget = SettingKey[File]("coffee-target", "Output directory for compiled coffee sources.")
  val coffeeBare = SettingKey[Boolean]("coffee-bare", "Compile coffee sources without top-level function wrapper.")

  private def javascript(sources: File, coffee: File, targetDir: File) =
    Some(new File(targetDir, IO.relativize(sources, coffee).get.replace(".coffee",".js")))

  private def compile(compiler: Compiler, out: Logger)(pair: (File, File)) =
    try {
      val (coffee, js) = pair
      out.debug("Compiling %s" format coffee)
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
  override def settings = inConfig(Coffee)(Seq(
    coffeeSource <<= (sourceDirectory in Compile) { _ / "coffee" },
    coffeeTarget <<= (resourceManaged in Compile) { _ / "js" },
    coffeeBare := false,
    cleanFiles <+= coffeeTarget.identity,
    coffeeClean <<= coffeeCleanTask,
    coffee <<= coffeeSourceGeneratorTask,
    resourceGenerators in Compile <+= coffee.identity
  )) ++ Seq(
    coffee <<= (coffee in Coffee).identity
  )

}
