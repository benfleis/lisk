package lispy

sealed class Expr {
    // Expression sealed class contains only int literal in v0.
    data class Literal(val int: Int) : Expr()
}

fun Expr.eval(): Expr =
    when (this) {
        is Expr.Literal -> this
    }

