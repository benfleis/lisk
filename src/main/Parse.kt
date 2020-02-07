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
                return List(l)
            }
            ")" -> throw IllegalArgumentException("Unexpected closing parenthesis")
            else -> token.parseAtom()
        }
    }
    return parseTokens(programTokens)
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
