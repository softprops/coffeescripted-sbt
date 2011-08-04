package coffeescript

import org.mozilla.javascript.{Context, Function, JavaScriptException, NativeObject}
import java.io.InputStreamReader

/**
 * A Scala / Rhino Coffeescript compiler.
 * @author daggerrz
 */
case class Compiler(bare: Boolean = false) {

  /**
   * Compiles a string of Coffeescript code to Javascript.
   *
   * @param code the Coffeescript code
   * @return either a compilation error description or the compiled Javascript code
   */
  def compile(code: String): Either[String, String] = withContext { ctx =>
    val scope = ctx.initStandardObjects()
    ctx.evaluateReader(scope,
      new InputStreamReader(getClass().getResourceAsStream("/coffee-script.js"), "UTF-8"),
     "coffee-script.js", 1, null
    )

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

  private def withContext[T](f: Context => T): T = {
    val ctx = Context.enter()
    try {
      ctx.setOptimizationLevel(-1) // Do not compile to byte code (max 64kb methods)
      f(ctx)
    } finally {
      Context.exit()
    }
  }
}
