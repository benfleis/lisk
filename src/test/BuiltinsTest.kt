package lispy

import org.junit.jupiter.api.*
import kotlin.test.*

internal class BuiltinsTest {
    @Test
    fun test_add() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

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

        // oops
        assertFails { "(+ a)".parseProgram().evalIn(env) }
        assertFails { "(+ 1 a)".parseProgram().evalIn(env) }
        assertFails { "(+ a 1)".parseProgram().evalIn(env) }
        assertFails { "(+ 1 a 1)".parseProgram().evalIn(env) }
        assertFails { "(+ 1 1 a)".parseProgram().evalIn(env) }
    }

    @Test
    fun test_multiply() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

        // basic longs and arities
        assertEquals(Expr.LongNumeric(1), "(*)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(1), "(* 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(3), "(* 3 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(6), "(* 1 2 3)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(3628800), "(* 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(Expr.DoubleNumeric(2.0), "(* 2.0)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(8.0), "(* 2.0 4.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(Expr.DoubleNumeric(2.0), "(* 1.0 2)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(2.0), "(* 1 2.0)".parseProgram().evalIn(env))

        // oops
        assertFails { "(* a)".parseProgram().evalIn(env) }
        assertFails { "(* 1 a)".parseProgram().evalIn(env) }
        assertFails { "(* a 1)".parseProgram().evalIn(env) }
        assertFails { "(* 1 a 1)".parseProgram().evalIn(env) }
        assertFails { "(* 1 1 a)".parseProgram().evalIn(env) }
    }


    @Test
    fun test_subtract() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

        // basic longs and arities
        assertEquals(Expr.LongNumeric(0), "(-)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(-1), "(- 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(0), "(- 1 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(-4), "(- 1 2 3)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(-53), "(- 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(Expr.DoubleNumeric(-1.0), "(- 1.0)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(0.0), "(- 1.0 1.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(Expr.DoubleNumeric(0.0), "(- 1.0 1)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(0.0), "(- 1 1.0)".parseProgram().evalIn(env))

        // oops
        assertFails { "(- a)".parseProgram().evalIn(env) }
        assertFails { "(- 1 a)".parseProgram().evalIn(env) }
        assertFails { "(- a 1)".parseProgram().evalIn(env) }
        assertFails { "(- 1 a 1)".parseProgram().evalIn(env) }
        assertFails { "(- 1 1 a)".parseProgram().evalIn(env) }
    }

    @Test
    fun test_divide() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

        // basic longs and arities
        assertEquals(Expr.LongNumeric(1), "(/)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(1), "(/ 1)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(3), "(/ 6 2)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(3), "(/ 12 2 2)".parseProgram().evalIn(env))
        assertEquals(Expr.LongNumeric(0), "(/ 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(Expr.DoubleNumeric(1.0), "(/ 1.0)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(2.0), "(/ 1.0 0.5)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(Expr.DoubleNumeric(0.5), "(/ 0.5 1)".parseProgram().evalIn(env))
        assertEquals(Expr.DoubleNumeric(0.5), "(/ 1 2.0)".parseProgram().evalIn(env))

        // oops
        assertFails { "(/ a)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 a)".parseProgram().evalIn(env) }
        assertFails { "(/ a 1)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 a 1)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 1 a)".parseProgram().evalIn(env) }
    }
}
