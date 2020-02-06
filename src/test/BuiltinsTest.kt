package lispy

import org.junit.jupiter.api.*
import kotlin.test.*

internal class BuiltinsTest {
    @Test
    fun test_add() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

        val expr = "(+)".parseProgram()
        val result = expr.evalIn(env)

        // basic longs and arities
        assertEquals(Expr.LongNumeric(0), "(+)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(1), "(+ 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(2), "(+ 1 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(6), "(+ 1 2 3)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(55), "(+ 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(Expr.DoubleNumeric(1.0), "(+ 1.0)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(2.0), "(+ 1.0 1.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(Expr.DoubleNumeric(2.0), "(+ 1.0 1)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(2.0), "(+ 1 1.0)".parseProgram().evalIn(env))
    }

}
