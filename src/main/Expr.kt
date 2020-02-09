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
    // evaluate AST within given Environment, returns result Expr
    abstract fun eval(env: Env): Expr
    // convert AST Expr to code
    abstract fun toCode(): String

    // Atoms
    object Nil : Expr() {
        override fun eval(env: Env) = this
        override fun toCode(): String = "nil"
    }

    sealed class Boolean(val bool: kotlin.Boolean) : Expr() {
        object True : Expr.Boolean(true) {
            override fun eval(env: Env) = this
            override fun toCode(): String = "#t"
        }
        object False : Expr.Boolean(false) {
            override fun eval(env: Env) = this
            override fun toCode(): String = "#f"
        }
    }

    data class LongNumeric(val long: Int) : Expr() {
        override fun eval(env: Env) = this
        override fun toCode(): String = "$long"
    }
    data class DoubleNumeric(val double: Double) : Expr() {
        override fun eval(env: Env) = this
        override fun toCode(): String = "$double"
    }
    data class Symbol(val name: String) : Expr() {
        override fun eval(env: Env): Expr {
            return env.find(name) ?: throw InvalidNameException("Symbol not found: $name")
        }
        override fun toCode(): String = "$name"
    }

    // -> Form?
    data class List(val list: kotlin.collections.List<Expr>): Expr() {
        operator fun plus(expr: Expr): Expr.List = Expr.List(this.list.plus(expr))
        override fun toCode(): String = list.joinToString(prefix = "(", separator = " ", postfix = ")", transform = Expr::toCode)
        override fun eval(env: Env): Expr {
            if (list.isEmpty()) { throw Exception("Eval empty list impossible") }
            val proc = list.first().eval(env)
            return when (proc) {
                is Expr.Callable -> proc.callable.call(env, list.subList(1, list.size).map { it.eval(env) }.toTypedArray())
                else -> throw Exception("Eval list head must be procedure")
            }
        }
    }

    // currently callable via KCallable, which like java uses Array for varargs. Future consideration to use List for
    // natural iteration.
    // Q: should this be an Expr?
    // A: (for now) yes, as it still sits naturally in the resolved tree structure.
    // Alternative idea: List gets eval'd into a bound procedure. How does curry'ing fit into that?
    data class Callable(val callable: KCallable<Expr>): Expr() {
        override fun toCode(): String = "<procedure>"
        override fun eval(env: Env): Expr = throw IllegalCallerException("Procedure can only be eval'd from List")
    }

    sealed class Form : Expr() {
        data class Begin(val args: kotlin.collections.List<Expr>): Expr() {
            override fun toCode(): String {
                return args.joinToString("", "(begin", ")", transform = { " " + it.toCode() })
            }
            override fun eval(env: Env): Expr = args.fold(Nil as Expr, { _, expr -> expr.eval(env) })
        }

        data class If(val predicate: Expr, val consequent: Expr, val alternate: Expr?) : Form() {
            override fun toCode(): String {
                return "(if (${predicate.toCode()}) (${consequent.toCode()})" +
                        if (alternate == null) ")" else " (${alternate.toCode()}))"
            }

            override fun eval(env: Env): Expr {
                return if (predicate.eval(env) != Expr.Boolean.False) {
                    consequent.eval(env)
                } else {
                    alternate?.eval(env) ?: Nil
                }
            }
        }

        data class Define(val symbol: Symbol, val value: Expr) : Form() {
            override fun toCode(): String = "(define ${symbol.name} $value)"
            override fun eval(env: Env): Expr {
                env.update(symbol.name, value.eval(env))
                return Nil
            }
        }

        data class Quote(val expr: Expr): Expr() {
            override fun toCode(): String = "(quote ${expr.toCode()}"
            override fun eval(env: Env): Expr = expr
        }
    }
}
