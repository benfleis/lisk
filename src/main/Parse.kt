package lispy

fun String.parseProgram(): Expr {
    return tokenize().first().parseLongLiteral()
}

fun String.parseLongLiteral() = Expr.LongLiteral(this.toInt())
fun String.parseDoubleLiteral() = Expr.DoubleLiteral(this.toDouble())
fun String.parseSymbol() = Expr.Symbol(this)

fun String.tokenize(): Sequence<String> = this
    .replace("(", " ( ")
    .replace(")", " ) ")
    .splitToSequence(" ", "\n", "\t")
    .filter { it.isNotEmpty() }
