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

// classic compare(a, b) -> { lt -> -1, eq -> 0, gt -> 1 }
internal fun compare(a: Expr, b: Expr): Int =
    when {
        a is LongNumeric && b is LongNumeric -> when {
            a.long < b.long -> -1
            a.long > b.long -> 1
            else -> 0
        }
        else -> {
            val aDbl = when (a) {
                is LongNumeric -> a.long.toDouble()
                is DoubleNumeric -> a.double
                else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
            }
            val bDbl = when (b) {
                is LongNumeric -> b.long.toDouble()
                is DoubleNumeric -> b.double
                else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
            }
            when {
                aDbl < bDbl -> -1
                aDbl > bDbl -> 1
                else -> 0
            }
        }
    }

internal fun negate(expr: Expr): Expr =
    when (expr) {
        is LongNumeric -> LongNumeric(-expr.long)
        is DoubleNumeric -> DoubleNumeric(-expr.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

internal fun invert(expr: Expr): Expr =
    when (expr) {
        is LongNumeric -> LongNumeric(1 / expr.long)
        is DoubleNumeric -> DoubleNumeric(1.0 / expr.double)
        else -> throw IllegalArgumentException("Procedure received non-Numeric argument")
    }

fun add(env: Env, vararg args: Expr): Expr = args.fold(LongNumeric(0) as Expr, ::add2)
fun multiply(env: Env, vararg args: Expr): Expr = args.fold(LongNumeric(1) as Expr, ::multiply2)

// def: (-) -> 0, (- 1) -> -1, (- 1 1) -> 0, (- 1 1 1) -> -1, ...
// thus 0 and 1 arg are special cases, 2+ means that lfold seeded w/ first arg
// see: https://www.gnu.org/software/mit-scheme/documentation/mit-scheme-ref/Numerical-operations.html
fun subtract(env: Env, vararg args: Expr): Expr = when {
    args.isEmpty() -> LongNumeric(0)
    args.size == 1 -> negate(args[0])
    else -> args.asSequence().drop(1).fold(args[0], ::subtract2)
}

// def: (/) -> 1, (/ 2) -> 1/2, (/ 3 1) -> 3, (/ 30 5 3) -> 2, ...
// thus 0 and 1 arg are special cases, 2+ means that lfold seeded w/ first arg
// see: https://www.gnu.org/software/mit-scheme/documentation/mit-scheme-ref/Numerical-operations.html
fun divide(env: Env, vararg args: Expr): Expr = when {
    args.isEmpty() -> LongNumeric(1)
    args.size == 1 -> invert(args[0])
    else -> args.asSequence().drop(1).fold(args[0], ::divide2)
}

fun lessThanOrEqual(env: Env, vararg args: Expr): Expr.Boolean = when {
    args.size == 2 -> compare(args[0], args[1]) <= 0
    args.size < 2 -> throw IllegalArgumentException("Incorrect number of arguments")
    else -> (args zip args.drop(1)).find { compare(it.first, it.second) > 0 } == null
}.toExpr()

fun Env.registerBuiltinProcedures() {
    update("+", Callable(::add))
    update("*", Callable(::multiply))
    update("-", Callable(::subtract))
    update("/", Callable(::divide))
    update("<=", Callable(::lessThanOrEqual))
}
