import java.util.Date

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.Context

object Macros {

	def greeting = macro greetingMacro
	def greetingMacro(c: Context): c.Expr[Any] = {
		import c.universe._
		val now = new Date().toString
		c.Expr(q"""
       println("compile time:" + $now)
     """)
	}

	def maximum(a: Int, b: Int): Int = macro maximumMacro

	def maximumMacro(c: Context)(a: c.Expr[Int], b: c.Expr[Int]): c.Expr[Int] = {
		import c.universe._
		c.Expr[Int](q"if($a > $b) $a else $b")
	}

	def betterMaximum(a: Int, b: Int): Int = macro betterMaximumMacro

	def betterMaximumMacro(c: Context)(a: c.Expr[Int], b: c.Expr[Int]): c.Expr[Int] = {
		import c.universe._

		c.Expr[Int](q"""
     val temp1 = $a
     val temp2 = $b
     if(temp1 > temp2) temp1 else temp2
     """)
	}

	def schemaMacro(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

		import c.universe._

		// retrieve the schema
		val schema = c.prefix.tree match {
			case Apply(_, List(Literal(Constant(x)))) => x.toString
			case _ => c.abort(c.enclosingPosition, "schema not specified")
		}

		// retrieve the annotate class name
		val className = annottees.map(_.tree) match {
			case List(q"class $name") => name
			case _ => c.abort(c.enclosingPosition, "the annotation can only be used with classes")
		}

		def parseSchema(schema: String) = {
			val trimmed = schema.replaceAll("""[\s\t\r\n]""", "")
			val bracketsRemoved = trimmed.substring(1, trimmed.length - 1)
			val propsArr = bracketsRemoved.split(',')

			val props = propsArr.map(prop => {
				val property = prop.split(':')
				val fieldName = newTermName(property(0))
				val fieldType = newTypeName(property(1))
				q"val $fieldName : $fieldType"
			}).toList

			// rewrite the class definition
			c.Expr(
				q"""
        case class $className(..$props) {

        }
      """
			)
		}

		parseSchema(schema)
	}

	def jsonMacro(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

		import c.universe._

		// retrieve the json
		val json = c.prefix.tree match {
			case Apply(_, List(Literal(Constant(x)))) => x.toString
			case _ => c.abort(c.enclosingPosition, "json not specified")
		}

		// retrieve the annotate class name
		val objectName = annottees.map(_.tree) match {
			case List(q"object $name") => name
			case _ => c.abort(c.enclosingPosition, "the annotation can only be used with objects")
		}

		def parseJson(json: String) = {
			val trimmed = json.replaceAll("""[\s\t\r\n]""", "")
			val bracketsRemoved = trimmed.substring(1, trimmed.length - 1)
			val propsArr = bracketsRemoved.split(',')

			val props = propsArr.map(prop => {
				val _prop = prop.split(':')
				val fieldName = newTermName(_prop(0))
				val fieldValue = _prop(1)
				q"val $fieldName = $fieldValue;"
			}).toList

			// rewrite the class definition
			c.Expr(
				q"""
        object $objectName {
					..$props
        }
      """
			)
		}

		parseJson(json)
	}

}

class Schema(schemaFile: String) extends StaticAnnotation {
	def macroTransform(annottees: Any*) = macro Macros.schemaMacro
}

class Json(schemaFile: String) extends StaticAnnotation {
	def macroTransform(annottees: Any*) = macro Macros.jsonMacro
}
