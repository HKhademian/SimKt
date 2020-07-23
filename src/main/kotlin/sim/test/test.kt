package sim.test

import sim.debugWrite
import java.util.function.Consumer

inline fun <T> measureNanoTime(crossinline task: () -> T): Pair<Long, T> {
	val start = System.nanoTime()
	val res = task()
	val end = System.nanoTime()
	val diff = end - start
	return diff to res
}

inline fun <T> measureTime(crossinline task: () -> T): Pair<Long, T> {
	val start = System.nanoTime()
	val res = task()
	val end = System.nanoTime()
	val diff = end - start
	return diff to res
}

@JvmSynthetic
inline fun tester(msg: String, crossinline task: () -> Any? = {}) {
	println("***** $msg *****")
	val (takes, res) = measureTime(task)
	if (res != null) print(res.debugWrite())
	println("***** takes: $takes ms *****\n")
}

@JvmSynthetic
fun testOn(target: Any?, msg: String = "sim/test", task: (() -> Unit)?) =
	tester(msg) { task?.invoke(); target }

@JvmSynthetic
fun <T> test(init: () -> T, msg: String = "sim/test", task: ((T) -> Unit)? = null): T =
	init().also { testOn(it, msg) { task?.invoke(it) } }


@JvmOverloads
fun testOn(target: Any?, msg: String = "sim/test", task: Runnable?) =
	tester(msg) { task?.run(); target }

@JvmOverloads
fun <T> test(init: () -> T, msg: String = "sim/test", task: Consumer<T>? = null): T =
	init().also { testOn(it, msg, Runnable { task?.accept(it) }) }