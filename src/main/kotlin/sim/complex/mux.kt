package sim.complex

import sim.base.*
import sim.tool.println

///** simulated N2One multiplexer */
//private class MuxN(
//	val selectors: List<Value>,
//	override val inputs: List<Value>
//) : MultiInputElement, ValueElement("Mux", {
//	val select = selectors.toInt()
//	inputs[select]
//}) {
//	init {
//		var inputCount = 1 shl selectors.size
//		if (inputs.size != inputCount)
//			throw RuntimeException("inputs does not match")
//	}
//
////	override fun eval(time: Long) =
////		inputs.eval(time)
//}

// hear I just try to implement real mux
// I like to use recursive method to design data-path
// it's important to note, I of these methods are run at startup and compiled to
// simple and/or/... gates
// there is no programming tricks here
// this implementation may has some slower results, ratter than the simulate implement

/** a simple 2 to 1 mux */
fun mux2(selector: Value, inp0: Value, inp1: Value): Value =
	(inp0 and not(selector)) or (inp1 and selector)

/** a simple 2M to M mux */
fun mux2(selector: Value, inp0: Bus, inp1: Bus): Bus =
	inp0.zip(inp1).map { (inp0, inp1) -> mux2(selector, inp0, inp1) }.toList()


/** a simple 2 to 1 mux */
fun mux4(selector0: Value, selector1: Value, inp0: Value, inp1: Value, inp2: Value, inp3: Value): Value =
	mux2(
		selector1,
		mux2(selector0, inp0, inp1),
		mux2(selector0, inp2, inp3)
	)

/** a simple 4M to M mux */
fun mux4(selector0: Value, selector1: Value, inp0: Bus, inp1: Bus, inp2: Bus, inp3: Bus): Bus =
	mux2(
		selector1,
		mux2(selector0, inp0, inp1),
		mux2(selector0, inp2, inp3)
	)

/**
 * a N to 1 mux
 * recursively forms multiplexer connections
 * input lines must be 2^selector lines
 */
fun mux(selector: Bus, input: Bus): Value =
	if (selector.size <= 1) mux2(selector[0], input[0], input[1])
	else mux2(
		selector[selector.size - 1],
		mux(selector.dropLast(1), input.slice(0 until input.size / 2)),
		mux(selector.dropLast(1), input.slice(input.size until input.size))
	)

/**
 * a N*M to M mux,
 * I rewrite recursive part here, to prevent extra recursive
 */
fun mux(selector: Bus, input: List<Bus>): Bus =
	if (selector.size <= 1) mux2(selector[0], input[0], input[1])
	else mux2(
		selector[selector.size - 1],
		mux(selector.dropLast(1), input.slice(0 until (input.size / 2))),
		mux(selector.dropLast(1), input.slice((input.size / 2) until input.size))
	)


/** select between 2^n inputs */
fun mux(selector: List<Value>, vararg inputs: Value): Value =
	mux(selector, listOf(*inputs))

/** select between M*2^n inputs */
fun mux(selector: List<Value>, vararg inputs: Bus): Bus =
	mux(selector, listOf(*inputs))


internal fun main() {

	val A = 74.toBus(16)
	val B = 99.toBus(16)
	val C = 11.toBus(16)
	val D = 49.toBus(16)
	val s1 = mut(false, "S1")
	val s2 = mut(false, "S2")

	val res = mux2(s1, A, B)

	res[0].println()

	println(
		"""
		s: $s1
		A: ${A.toInt()}
		B: ${B.toInt()}
		res: ${res.toInt()}
		
	""".trimIndent()
	)

	// I change just the selector
	// see how result changed, on the fly, in real time
	s1.set(true)

	println(
		"""
		s: $s1
		A: ${A.toInt()}
		B: ${B.toInt()}
		res: ${res.toInt()}
		
	""".trimIndent()
	)

	println(
		"""
		A: ${A.toInt()}
		B: ${B.toInt()}
		C: ${C.toInt()}
		D: ${D.toInt()}
		""".trimIndent()
	)

	val res1 = mux2(s1, A, B)
	res1[2].println() // show path of a mux
	println("s1: $s1 -> res: ${res1.toInt()}")
	// I change just the selector
	// see how result changed, on the fly, in real time
	s1.set(true)
	println("s1: $s1 -> res: ${res1.toInt()}")


	val res2 = mux(listOf(s1, s2), listOf(A, B, C, D))
	res2[5].println()
	println("s2s1: $s2 $s1 -> res: ${res2.toInt()}")
	s2.set(true);s1.reset()
	println("s2s1: $s2 $s1 -> res: ${res2.toInt()}")
}
