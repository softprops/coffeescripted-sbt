package coffeescript

import sbt._
import Keys._
import Project.Initialize

import java.io.File

import org.jcoffeescript.JCoffeeScriptCompiler

object CoffeesScript extends Plugin {

  val coffeeSource = SettingKey[File]("coffee-source", "Directory containing coffee files.")
  val coffeeTarget = SettingKey[File]("coffee-target", "Output directory for translated coffee scripts.")
  val coffeeClean = TaskKey[Unit]("coffee-clean", "Clean just the files generated from coffee sources.")
  val coffee = TaskKey[Unit]("coffee", "Compile the coffee sources.")

  private def compiler = new JCoffeeScriptCompiler()

  private def base(file: File) =
    file.getName.substring(0, file.getName.lastIndexOf("."))

  private def javascript(coffee: File, targetDir: File) =
    new File(targetDir, base(coffee) + ".js")

  private def outdated(coffee: File, javascript: File) =
    !javascript.exists || coffee.lastModified > javascript.lastModified

  private def compile(coffee: File, target: File, out: Logger) =
    try {
      out.info("compiling %s" format coffee)
      IO.write(
        javascript(coffee, target),
        compiler.compile(io.Source.fromFile(coffee).mkString)
      )
      out.info("wrote file to %s" format javascript(coffee, target))
    } catch { case e: Exception =>
      //out.warn("error occured while compiling %s: %s" format(coffee, e))
      throw new RuntimeException(
        "error occured while compiling %s: %s" format(coffee, e.getMessage), e
      )
    }

  private def compileChanged(sources: File, target: File, out: Logger) =
    for (coffee <- (sources * "*").get
      if (outdated(coffee, javascript(coffee, target)))) yield {
        compile(coffee, target, out)
        target
      }

  private def coffeeCleanTask: Initialize[Task[Unit]] =
    (streams, coffeeTarget) map {
      (out, target) =>
        out.log.info("Cleaning " + target)
        IO.delete(target)
    }

  private def coffeeSourceGeneratorTask: Initialize[Task[Seq[File]]] =
    (streams, coffeeSource, coffeeTarget) map {
      (out, sourceDir, targetDir) =>
        compileChanged(sourceDir, targetDir, out.log)
    }

  private def coffeeTask: Initialize[Task[Unit]] =
    (streams, coffeeSource, coffeeTarget) map {
      (out, sourceDir, targetDir) =>
        compileChanged(sourceDir, targetDir, out.log)
    }

  /** these commands will be automatically added to projects using plugin */
  override def settings = Seq (
    coffeeSource <<= (sourceDirectory in Compile) { _ / "coffee" },
    coffeeTarget <<= (resourceManaged in Compile) { _ / "www" / "js" },
    cleanFiles <+= (coffeeTarget) { t => t },
    coffeeClean <<= coffeeCleanTask,
    coffee <<= coffeeTask,
    resourceGenerators in Compile <+= coffeeSourceGeneratorTask
  )

}
