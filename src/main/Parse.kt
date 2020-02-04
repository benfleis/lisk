package lispy

fun String.parseProgram(): Expr {
    // currently presumes single numeric string
    return tokenize().first().parseLiteral()
}

fun String.parseLiteral(): Expr.Literal {
    return Expr.Literal(this.toInt())
}

fun String.tokenize(): Sequence<String> {
    return splitToSequence(" ").filter { it.isNotEmpty() }
}
