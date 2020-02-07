package lispy

import javax.naming.InvalidNameException
import kotlin.reflect.KCallable

data class Env(val outer: Env?, val map: MutableMap<String, Expr>) {
    fun find(K: String): Expr? = map.get(K) ?: outer?.find(K)
    fun update(K: String, V: Expr) = map.put(K, V)
    // fun push() = Env(this, mutableMapOf())
    // fun pop() = outer
}

// Debating whether to make all Exprs bind to an Env -- I don't imagine re-using an expr in a new env, so it simplifies
// eval considerably to bind at create time.
sealed class Expr {
    abstract fun generate(): String

    // Atoms
    object Nil : Expr() {
        override fun generate(): String = "nil"
    }

    sealed class Boolean(val bool: kotlin.Boolean) : Expr() {
        object True : Expr.Boolean(true) { override fun generate(): String = "#t" }
        object False : Expr.Boolean(false) { override fun generate(): String = "#f" }
    }

    data class LongNumeric(val long: Int) : Expr() {
        override fun generate(): String = "$long"
    }
    data class DoubleNumeric(val double: Double) : Expr() {
        override fun generate(): String = "$double"
    }
    data class Symbol(val name: String) : Expr() {
        override fun generate(): String = "$name"
    }

    data class List(val list: kotlin.collections.List<Expr>): Expr() {
        operator fun plus(expr: Expr): Expr.List = Expr.List(this.list.plus(expr))
        override fun generate(): String = list.joinToString(prefix = "(", separator = " ", postfix = ")", transform = Expr::generate)
    }

    // currently callable via KCallable, which like java uses Array for varargs. Future consideration to use List for
    // natural iteration.
    // Q: should this be an Expr?
    // A: (for now) yes, as it still sits naturally in the resolved tree structure.
    // Alternative idea: List gets eval'd into a bound procedure. How does curry'ing fit into that?
    data class Procedure(val callable: KCallable<Expr>): Expr() {
        override fun generate(): String = "<procedure>"
    }
}

// operator fun Env.plus(K: String, V: Expr) = Env(this.outer, this + Pair(K, V))

fun Expr.evalIn(env: Env): Expr =
    when (this) {
        is Expr.LongNumeric -> this
        is Expr.DoubleNumeric -> this
        is Expr.Nil -> this
        is Expr.Boolean -> this
        is Expr.Symbol -> env.find(name) ?: throw InvalidNameException("Symbol not found: $name")
        is Expr.List -> this.evalIn(env)
        // uneval-able Exprs below all throw exception, and should only occur due to bug / internal error
        is Expr.Procedure -> throw Exception("Eval procedure impossible")
    }

fun Expr.List.evalIn(env: Env): Expr {
    if (this.list.isEmpty()) { throw Exception("Eval empty list impossible") }
    val proc = this.list.first().evalIn(env)
    return when (proc) {
        is Expr.Procedure -> proc.callable.call(env, list.subList(1, list.size).map { it.evalIn(env) }.toTypedArray())
        else -> throw Exception("Eval list head must be procedure")
    }
}
