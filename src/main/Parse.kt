package lispy

import lispy.Expr.*

fun String.parseProgram(): Expr {
    val programTokens = tokenize().toMutableList()
    if (programTokens.isEmpty()) throw IllegalArgumentException("Empty program")

    // inefficient and awkward, close to direct kotlin translation -- the py version is built on different principles.
    fun parseTokens(tokens: MutableList<String>): Expr  {
        val token = tokens.removeAt(0)
        return when (token) {
            "(" -> {
                val l = mutableListOf<Expr>()
                while (tokens[0] != ")") {
                    l.add(parseTokens(tokens))
                }
                tokens.removeAt(0)  // trailing ")"
                return l.toForm()
            }
            ")" -> throw IllegalArgumentException("Unexpected closing parenthesis")
            else -> token.parseAtom()
        }
    }
    return parseTokens(programTokens)
}

fun MutableList<Expr>.toForm(): Expr {
    val size = this.size
    if (isEmpty()) return List(this)
    val first = this.first()
    if (first is Symbol) {
        when (first.name) {
            "begin" -> {
                if (size < 2) { throw Exception("Begin requires at least 1 arg") }
                return Form.Begin(this.drop(1))
            }
            "if" -> {
                if (size < 3 || size > 4) throw IllegalArgumentException("Incorrect number of args")
                return Form.If(get(1), get(2), if (this.size == 4) get(3) else null)
            }
            "define" -> {
                if (size != 3) throw IllegalArgumentException("define requires 2 args")
                val second = get(1)
                if (second !is Symbol) throw java.lang.IllegalArgumentException("define requires symbol as first arg")
                return Form.Define(second, get(2))
            }
            "quote" -> {
                if (size != 2) throw IllegalArgumentException("quote requires 1 arg")
                return Form.Quote(get(1))
            }
        }
    }
    return List(this)
}

fun String.parseLongNumeric() = LongNumeric(this.toInt())
fun String.parseDoubleNumeric() = DoubleNumeric(this.toDouble())
fun String.parseSymbol() = Symbol(this)

internal val literalConstants = mapOf(
    "nil" to Expr.Nil,
    "#t" to Expr.Boolean.True,
    "#f" to Expr.Boolean.False
)

fun String.parseAtom(): Expr =
    literalConstants.getOrElse(this) {
        try {
            return this.parseLongNumeric()
        } catch (e: NumberFormatException) {
        }
        try {
            return this.parseDoubleNumeric()
        } catch (e: NumberFormatException) {
        }
        return this.parseSymbol()
    }

fun String.tokenize(): List<String> = this
    .replace("(", " ( ")
    .replace(")", " ) ")
    .split(" ", "\n", "\t")
    .filter { it.isNotEmpty() }
