package coffeescript

import org.mozilla.javascript.{Context, Function, JavaScriptException, NativeObject}
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * A Scala / Rhino Coffeescript compiler.
 * @author daggerrz
 * @author doug (to a lesser degree)
 */

object Compiler {
  val utf8 = Charset.forName("utf-8")

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
    val opts = ctx.evaluateString(scope, "({bare: %b});".format(bare), null, 1, null)
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
      new InputStreamReader(getClass().getResourceAsStream("/coffee-script.js"), utf8),
      "coffee-script.js", 1, null
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
