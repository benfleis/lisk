package lispy

import javax.naming.InvalidNameException

data class Env(val outer: Env?, val map: MutableMap<String, Expr>) {
    fun find(K: String): Expr? = map.get(K) ?: outer?.find(K)
    /*
    fun update(K: String, V: Expr) = map.put(K, V)
    fun push() = Env(this, mutableMapOf())
    fun pop() = outer
    */
}

sealed class Expr() {
    // Atoms
    data class LongLiteral(val long: Int) : Expr()
    data class DoubleLiteral(val double: Double) : Expr()
    data class Symbol(val name: String): Expr()
    // List
/*
    data class List(val list: Sequence<Expr>): Expr()
    data class Procedure(val callable: Any)
*/
}

// operator fun Env.plus(K: String, V: Expr) = Env(this.outer, this + Pair(K, V))

fun Expr.evalIn(env: Env): Expr =
    when (this) {
        is Expr.LongLiteral -> this
        is Expr.DoubleLiteral -> this
        is Expr.Symbol -> env.find(name) ?: throw InvalidNameException("Symbol not found: $name")
    }

