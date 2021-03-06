package sim.base

typealias Bus = List<Value>
typealias MutableBus = List<MutableValue>

@JvmField
val EMPTY_BUS: Bus = listOf()

@JvmField
val ZERO_BUS: Bus = (0..64).map { Value.ZERO }.toList()

@JvmField
val ONE_BUS: Bus = (0..64).map { Value.ONE }.toList()

/** creates a n-bit mutable bus to use */
@JvmOverloads
fun bus(n: Int, name: String = ""): MutableBus =
	(0 until n).map { mut(false, if (name.isNotBlank()) "$name[$it]" else name) }.toList()

/** create bus of some values */
fun bus(vararg values: Value): Bus =
	listOf(*values)

/** create bus of some values */
fun bus(vararg values: Boolean): Bus =
	values.map { it.toValue() }

/** create MutableBus of some values */
fun mutableBus(vararg values: MutableValue): MutableBus =
	listOf(*values)

/** create MutableBus of some values */
fun mutableBus(vararg values: Value): MutableBus =
	values.map { it.toMut() }

/** create MutableBus of some values */
fun mutableBus(vararg values: Boolean): MutableBus =
	values.map { it.toValue().toMut() }


fun MutableBus.asBus() =
	this as Bus

@Suppress("UNCHECKED_CAST")
fun Bus.asMutableBus() =
	this as MutableBus


/** to match MutableBus.set */
fun set(me: MutableValue, other: Value) = me.set(other)

/** to match MutableBus.set */
fun set(me: MutableValue, other: Boolean) = me.set(other)

/** set a value to all of a bus lines */
fun MutableBus.set(value: Value) = this.forEach { it.set(value) }

/** set a bus values to another bus lines */
fun MutableBus.set(other: Bus) = this.zip(other).forEach { (lhs, rhs) -> lhs.set(rhs) }

/** set a const value to another bus lines */
fun MutableBus.set(other: Int) = set(other.toBus(size))

/** set a const value to another bus lines */
fun MutableBus.set(other: Long) = set(other.toBus(size))

/** write a value to all of a bus lines */
fun Value.writeOn(other: MutableBus) = other.set(this)

/** write bus values to another bus lines */
fun Bus.writeOn(other: MutableBus) = other.set(this)


/** slice and create another bus from a bus */
@JvmOverloads
fun <T : Value> List<T>.slice(from: Int = 0, to: Int = -1) =
	this.subList(from, if (to >= 0) to else this.size + to)

/** slice and create another bus from a bus */
@JvmOverloads
fun <T : Value> List<T>.pick(from: Int = 0, len: Int = -1) =
	this.subList(from, if (len >= 0) from + len else this.size - 1)

/** merge some bus to getter */
fun <T : Value> merge(vararg buses: List<T>): List<T> =
	listOf(*buses).flatten()

/** converts a group of values to equivalent int */
@JvmName("constant")
fun Bus.toConst(): Bus =
	this.map { it.toConst() }.toList()

/** converts a group of values to equivalent int */
fun Bus.toMut(): MutableBus =
	this.map { it.toMut() }.toList()

/** converts a group of values to equivalent int */
fun Bus.toInt(): Int =
	this.foldRight(0) { it, acc: Int -> (acc shl 1) or (it.toInt()) }

/** converts a group of values to equivalent long int */
fun Bus.toLong(): Long =
	this.foldRight(0) { it, acc: Long -> (acc shl 1) or (it.toInt().toLong()) }

/** get all values and return list of boolean */
fun Bus.read(): List<Boolean> =
	this.map { it.get() }.toList()

/** converts a long to n-bit list of values */
@JvmOverloads
fun Long.toBus(n: Int = 32): Bus =
	(0 until n).asSequence().map { const((this and (1L shl it)) != 0L, "#$it") }.toList()

/** converts a integer to n-bit list of values */
@JvmOverloads
fun Int.toBus(n: Int = 32): Bus =
	toLong().toBus(n)

internal fun main() {
	println(ONE_BUS)
}
