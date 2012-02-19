package coffeescript

import org.mozilla.javascript.{Context, Function, JavaScriptException, NativeObject}
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * A Scala / Rhino Coffeescript compiler.
 * @author daggerrz
 * @author doug (to a lesser degree)
 */
abstract class Compiler(src: String) {
  val utf8 = Charset.forName("utf-8")

  def extendedArgs: Map[String, String] = Map.empty[String, String]

  /**
   * Compiles a string of Coffeescript code to Javascript.
   *
   * @param code the Coffeescript source code
   * @param bare whether the Coffeescript compiler should run in "bare" mode
   * @return Either a compilation error description or the compiled Javascript code
   */
  def compile(code: String, bare: Boolean): Either[String, String] = withContext { ctx =>
    val coffee = scope.get("CoffeeScript", scope).asInstanceOf[NativeObject]
    val compileFunc = coffee.get("compile", scope).asInstanceOf[Function]
    val opts = ctx.evaluateString(scope, "({bare: %b%s});".format(
      bare, if(extendedArgs.isEmpty) "" else "," + (extendedArgs.map {
        case (k, v) => "%s:'%s'".format(k, v)
    } mkString(","))), null, 1, null)
    try {
      Right(compileFunc.call(ctx, scope, coffee, Array(code, opts)).asInstanceOf[String])
    } catch {
      case e : JavaScriptException =>
        Left(e.getValue.toString)
    }
  }

  lazy val scope = withContext { ctx =>
    val scope = ctx.initStandardObjects()
    ctx.evaluateReader(
      scope,
      new InputStreamReader(getClass().getResourceAsStream("/%s" format src), utf8),
      src, 1, null
    )

    scope
  }

  private def withContext[T](f: Context => T): T = {
    val ctx = Context.enter()
    try {
      ctx.setOptimizationLevel(-1) // Do not compile to byte code (max 64kb methods)
      f(ctx)
    } catch {
      case e =>
        throw e
    } finally {
      Context.exit()
    }
  }
}

object Vanilla extends Compiler("vanilla/coffee-script.js")

object Iced extends Compiler("iced/coffee-script.js") {
  override def extendedArgs = Map("runtime" -> "inline")
}
