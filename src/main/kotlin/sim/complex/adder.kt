package sim.complex

import sim.base.*

/** attention: return values are lazy calculated */
fun halfAdder(A: Value, B: Value, res: MutableValue, carry: MutableValue) {
	res.set(xor(A, B))
	carry.set(and(A, B))
}

/** attention: return values are lazy calculated */
fun fullAdder(A: Value, B: Value, C: Value, res: MutableValue, carry: MutableValue) {
	val s1 = mut(false)
	val c1 = mut(false)
	val c2 = mut(false)
	halfAdder(A, B, s1, c1)
	halfAdder(C, s1, res, c2)
	carry.set(or(c1, c2))
}

// test adders
private fun main2() {
	val A = mut(true)
	val B = mut(true)
	val res = mut(false)
	val car = mut(false)
	halfAdder(A, B, res, car)
	println("res: $res    carry: $car")
	A.set(false)
	println("res: $res    carry: $car")
}
