package coffeescript

import org.mozilla.javascript.{
  Context, Function, JavaScriptException, NativeObject }
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * A Scala / Rhino Coffeescript compiler.
 * @author daggerrz
 * @author doug (to a lesser degree)
 */
abstract class Compiler(src: String) {
  val utf8 = Charset.forName("utf-8")

  /** compiler arguments in addition to `bare` */
  def args: Map[String, Any] = Map.empty[String, Any]

  override def toString = "%s(%s)" format(getClass.getSimpleName, src)

  /**
   * Compiles a string of Coffeescript code to Javascript.
   *
   * @param code the Coffeescript source code
   * @param bare whether the Coffeescript compiler should run in "bare" mode
   * @return Either a compilation error description or
   *   the compiled Javascript code
   */
  def compile(code: String, bare: Boolean): Either[String, String] =
    withContext { ctx =>
      val coffee = scope.get("CoffeeScript", scope).asInstanceOf[NativeObject]
      val compileFunc = coffee.get("compile", scope).asInstanceOf[Function]
      val opts = ctx.evaluateString(scope, jsArgs(bare), null, 1, null)
      try {
        Right(compileFunc.call(
          ctx, scope, coffee, Array(code, opts)).asInstanceOf[String])
      } catch {
        case e : JavaScriptException =>
          Left(e.getValue.toString)
      }
    }

  lazy val scope = withContext { ctx =>
    val scope = ctx.initStandardObjects()
    ctx.evaluateReader(
      scope,
      new InputStreamReader(
        getClass().getResourceAsStream("/%s" format src), utf8
      ), src, 1, null
    )

    scope
  }

  private def withContext[T](f: Context => T): T =
    try {
      val ctx = Context.enter()
      // Do not compile to byte code (max 64kb methods)
      ctx.setOptimizationLevel(-1)
      f(ctx)
    } finally {
      Context.exit()
    }

  private def jsArgs(bare: Boolean) =
    ((List.empty[String] /: (Map("bare" -> bare) ++ args)) {
      (a,e) => e match {
        case (k, v) =>
          "%s:%s".format(k, v match {
            case s: String => "'%s'" format s
            case lit => lit
          }) :: a
        }
    }).mkString("({",",","});")

}

object Vanilla extends Compiler("vanilla/coffee-script.js")

object Iced extends Compiler("iced/coffee-script.js") {
  override def args = Map("runtime" -> "inline")
}
