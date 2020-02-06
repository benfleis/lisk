package lispy

import lispy.Expr.*

/**
 * builtin procedures, made generally available in base environment.
 */
fun add2(a: Expr, b: Expr): Expr =
    when {
        a is LongNumeric && b is LongNumeric -> LongNumeric(a.long + b.long)
        a is LongNumeric && b is DoubleNumeric -> DoubleNumeric(a.long.toDouble() + b.double)
        a is DoubleNumeric && b is LongNumeric -> DoubleNumeric(a.double + b.long.toDouble())
        a is DoubleNumeric && b is DoubleNumeric -> DoubleNumeric(a.double + b.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

fun add(vararg args: Expr): Expr = args.asSequence().fold(LongNumeric(0) as Expr, ::add2)


fun Env.registerBuiltins() {
    update("+", Expr.Procedure(::add))
}
