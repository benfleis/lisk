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

fun add(env: Env, vararg args: Expr): Expr = args.asSequence().fold(LongNumeric(0) as Expr, ::add2)
fun multiply(env: Env, vararg args: Expr): Expr = args.asSequence().fold(LongNumeric(1) as Expr, ::multiply2)

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

fun Env.registerBuiltinProcedures() {
    update("+", Procedure(::add))
    update("*", Procedure(::multiply))
    update("-", Procedure(::subtract))
    update("/", Procedure(::divide))
}

// Special Forms -- arg eval handled within the form, differs from Procedures above

fun ifThenElse(env: Env, predicate: Expr, consequent: Expr, alternate: Expr?): Expr =
    when (alternate) {
        null -> ifThenElse(env, predicate, consequent, Nil)
        else -> if (predicate.eval(env) != Expr.Boolean.False) { consequent.eval(env) } else { alternate.eval(env) }
    }

// define one 1 -> Nil, and sets Env `one` string to LongNumeric(1)
fun define(env: Env, vararg args: Expr): Nil = Nil.also {
    when (args.size) {
        2 -> env.update((args[0] as Symbol).name, args[1].eval(env))
        else -> throw IllegalArgumentException("Incorrect number of arguments")
    }
}
