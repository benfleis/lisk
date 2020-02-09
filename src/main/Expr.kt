package lispy

import javax.naming.InvalidNameException
import kotlin.reflect.KCallable

// Playing fast and loose here -- MutableMap is how things get "used in practice", e.g., with set! and define
// operations, but for testing and other contexts immutable maps may be useful.
// As such, `update` does _not_ return a new env but instead provides a mutable-like API, even when inelegant.
// Callers can make elegant in other ways, so that the mutable reality is not forgotten.
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
    abstract fun evalIn(env: Env): Expr

    // Atoms
    object Nil : Expr() {
        override fun evalIn(env: Env) = this
        override fun generate(): String = "nil"
    }

    sealed class Boolean(val bool: kotlin.Boolean) : Expr() {
        object True : Expr.Boolean(true) {
            override fun evalIn(env: Env) = this
            override fun generate(): String = "#t"
        }
        object False : Expr.Boolean(false) {
            override fun evalIn(env: Env) = this
            override fun generate(): String = "#f"
        }
    }

    data class LongNumeric(val long: Int) : Expr() {
        override fun evalIn(env: Env) = this
        override fun generate(): String = "$long"
    }
    data class DoubleNumeric(val double: Double) : Expr() {
        override fun evalIn(env: Env) = this
        override fun generate(): String = "$double"
    }
    data class Symbol(val name: String) : Expr() {
        override fun evalIn(env: Env): Expr {
            return env.find(name) ?: throw InvalidNameException("Symbol not found: $name")
        }
        override fun generate(): String = "$name"
    }

    // -> Form?
    data class List(val list: kotlin.collections.List<Expr>): Expr() {
        operator fun plus(expr: Expr): Expr.List = Expr.List(this.list.plus(expr))
        override fun generate(): String = list.joinToString(prefix = "(", separator = " ", postfix = ")", transform = Expr::generate)
        override fun evalIn(env: Env): Expr {
            if (list.isEmpty()) { throw Exception("Eval empty list impossible") }
            val proc = list.first().evalIn(env)
            return when (proc) {
                is Expr.Procedure -> proc.callable.call(env, list.subList(1, list.size).map { it.evalIn(env) }.toTypedArray())
                else -> throw Exception("Eval list head must be procedure")
            }
        }
    }

    // currently callable via KCallable, which like java uses Array for varargs. Future consideration to use List for
    // natural iteration.
    // Q: should this be an Expr?
    // A: (for now) yes, as it still sits naturally in the resolved tree structure.
    // Alternative idea: List gets eval'd into a bound procedure. How does curry'ing fit into that?
    data class Procedure(val callable: KCallable<Expr>): Expr() {
        override fun generate(): String = "<procedure>"
        override fun evalIn(env: Env): Expr = throw IllegalCallerException("Procedure can only be eval'd from List")
    }

    sealed class Form : Expr() {
        data class Begin(val args: kotlin.collections.List<Expr>): Expr() {
            override fun generate(): String {
                return args.joinToString("", "(begin", ")", transform = { " " + it.generate() })
            }
            override fun evalIn(env: Env): Expr = args.fold(Nil as Expr, { _, expr -> expr.evalIn(env) })
        }

        data class If(val predicate: Expr, val consequent: Expr, val alternate: Expr?) : Form() {
            override fun generate(): String {
                return "(if (${predicate.generate()}) (${consequent.generate()})" +
                        if (alternate == null) ")" else " (${alternate.generate()}))"
            }

            override fun evalIn(env: Env): Expr {
                return if (predicate.evalIn(env) != Expr.Boolean.False) {
                    consequent.evalIn(env)
                } else {
                    alternate?.evalIn(env) ?: Nil
                }
            }
        }

        data class Define(val symbol: Symbol, val value: Expr) : Form() {
            override fun generate(): String = "(define ${symbol.name} $value)"
            override fun evalIn(env: Env): Expr {
                env.update(symbol.name, value.evalIn(env))
                return Nil
            }
        }

        data class Quote(val expr: Expr): Expr() {
            override fun generate(): String = "(quote ${expr.generate()}"
            override fun evalIn(env: Env): Expr = expr
        }
    }
}
