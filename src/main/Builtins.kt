package lispy

import lispy.Expr.*

/**
 * builtin procedures, made generally available in base environment.
 */
internal fun add2(a: Expr, b: Expr): Expr =
    when {
        a is LongNumeric && b is LongNumeric -> LongNumeric(a.long + b.long)
        a is LongNumeric && b is DoubleNumeric -> DoubleNumeric(a.long.toDouble() + b.double)
        a is DoubleNumeric && b is LongNumeric -> DoubleNumeric(a.double + b.long.toDouble())
        a is DoubleNumeric && b is DoubleNumeric -> DoubleNumeric(a.double + b.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal fun subtract2(a: Expr, b: Expr): Expr =
    when {
        a is LongNumeric && b is LongNumeric -> LongNumeric(a.long - b.long)
        a is LongNumeric && b is DoubleNumeric -> DoubleNumeric(a.long.toDouble() - b.double)
        a is DoubleNumeric && b is LongNumeric -> DoubleNumeric(a.double - b.long.toDouble())
        a is DoubleNumeric && b is DoubleNumeric -> DoubleNumeric(a.double - b.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal fun multiply2(a: Expr, b: Expr): Expr =
    when {
        a is LongNumeric && b is LongNumeric -> LongNumeric(a.long * b.long)
        a is LongNumeric && b is DoubleNumeric -> DoubleNumeric(a.long.toDouble() * b.double)
        a is DoubleNumeric && b is LongNumeric -> DoubleNumeric(a.double * b.long.toDouble())
        a is DoubleNumeric && b is DoubleNumeric -> DoubleNumeric(a.double * b.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal fun divide2(a: Expr, b: Expr): Expr =
    when {
        a is LongNumeric && b is LongNumeric -> LongNumeric(a.long / b.long)
        a is LongNumeric && b is DoubleNumeric -> DoubleNumeric(a.long.toDouble() / b.double)
        a is DoubleNumeric && b is LongNumeric -> DoubleNumeric(a.double / b.long.toDouble())
        a is DoubleNumeric && b is DoubleNumeric -> DoubleNumeric(a.double / b.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal inline fun negate(expr: Expr): Expr =
    when (expr) {
        is LongNumeric -> LongNumeric(-expr.long)
        is DoubleNumeric -> DoubleNumeric(-expr.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal inline fun invert(expr: Expr): Expr =
    when (expr) {
        is LongNumeric -> LongNumeric(1 / expr.long)
        is DoubleNumeric -> DoubleNumeric(1.0 / expr.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

fun add(vararg args: Expr): Expr = args.asSequence().fold(LongNumeric(0) as Expr, ::add2)
fun multiply(vararg args: Expr): Expr = args.asSequence().fold(LongNumeric(1) as Expr, ::multiply2)

// def: (-) -> 0, (- 1) -> -1, (- 1 1) -> 0, (- 1 1 1) -> -1, ...
// thus 0 and 1 arg are special cases, 2+ means that lfold seeded w/ first arg
// see: https://www.gnu.org/software/mit-scheme/documentation/mit-scheme-ref/Numerical-operations.html
fun subtract(vararg args: Expr): Expr = when {
    args.isEmpty() -> LongNumeric(0)
    args.size == 1 -> negate(args[0])
    else -> args.asSequence().drop(1).fold(args[0], ::subtract2)
}

// def: (/) -> 1, (/ 2) -> 1/2, (/ 3 1) -> 3, (/ 30 5 3) -> 2, ...
// thus 0 and 1 arg are special cases, 2+ means that lfold seeded w/ first arg
// see: https://www.gnu.org/software/mit-scheme/documentation/mit-scheme-ref/Numerical-operations.html
fun divide(vararg args: Expr): Expr = when {
    args.isEmpty() -> LongNumeric(1)
    args.size == 1 -> invert(args[0])
    else -> args.asSequence().drop(1).fold(args[0], ::divide2)
}


fun Env.registerBuiltins() {
    update("+", Expr.Procedure(::add))
    update("*", Expr.Procedure(::multiply))
    update("-", Expr.Procedure(::subtract))
    update("/", Expr.Procedure(::divide))
}
