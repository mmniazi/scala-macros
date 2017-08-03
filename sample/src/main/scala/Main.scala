object Main extends App {

	// Greetings
	Macros.greeting

	// Looks like function?
	val x = 1
	var y = 2
	println("maximum(x, y): " + Macros.maximum(x, y))

	// Look at macro code and predict output then 😉
	def y_++ : Int = {
		val ans = y
		y += 1
		ans
	}

	println(s"maximum(x, y++): ${Macros.maximum(x, y_++)}")
	println(s"y: $y")

	// How can we solve this, temp vars?
	y = 2
	println(s"betterMaximum(x, y++): ${Macros.betterMaximum(x, y_++)}")
	println(s"y: $y")

	// Uncomment this code to see that temp1 and temp2 are embedded in a block.
	// Their definitions are not leaked into the surrounding context, so this code will not compile:
	// betterMaximum(10, 20)
	// println(temp1)

	@Schema("""
	{
		a: String,
		b: Boolean
	}
  """)
	class MySchema

	val schema = MySchema("1", true)

	println(schema)

	@Json("""
	{
		a: "some string",
		b: true
	}
  """)
	object myJson

	println(myJson.a)
	println(myJson.b)
	// We are not making compiler go nuts type information is still there
	println(myJson.c)
}
