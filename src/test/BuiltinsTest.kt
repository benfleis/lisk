package lispy

import lispy.Expr.*
import kotlin.test.*
import org.junit.jupiter.api.*

internal class BuiltinsTest {
    @Test
    fun test_add() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()

        // basic longs and arities
        assertEquals(LongNumeric(0), "(+)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(1), "(+ 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(2), "(+ 1 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(6), "(+ 1 2 3)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(55), "(+ 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(DoubleNumeric(1.0), "(+ 1.0)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(2.0), "(+ 1.0 1.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(DoubleNumeric(2.0), "(+ 1.0 1)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(2.0), "(+ 1 1.0)".parseProgram().evalIn(env))

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
        assertEquals(LongNumeric(1), "(*)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(1), "(* 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(3), "(* 3 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(6), "(* 1 2 3)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(3628800), "(* 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(DoubleNumeric(2.0), "(* 2.0)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(8.0), "(* 2.0 4.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(DoubleNumeric(2.0), "(* 1.0 2)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(2.0), "(* 1 2.0)".parseProgram().evalIn(env))

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
        assertEquals(LongNumeric(0), "(-)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(-1), "(- 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(0), "(- 1 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(-4), "(- 1 2 3)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(-53), "(- 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(DoubleNumeric(-1.0), "(- 1.0)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(0.0), "(- 1.0 1.0)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(DoubleNumeric(0.0), "(- 1.0 1)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(0.0), "(- 1 1.0)".parseProgram().evalIn(env))

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
        assertEquals(LongNumeric(1), "(/)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(1), "(/ 1)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(3), "(/ 6 2)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(3), "(/ 12 2 2)".parseProgram().evalIn(env))
        assertEquals(LongNumeric(0), "(/ 1 2 3 4 5 6 7 8 9 10)".parseProgram().evalIn(env))

        // basic doubles
        assertEquals(DoubleNumeric(1.0), "(/ 1.0)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(2.0), "(/ 1.0 0.5)".parseProgram().evalIn(env))

        // confirm double casting
        assertEquals(DoubleNumeric(0.5), "(/ 0.5 1)".parseProgram().evalIn(env))
        assertEquals(DoubleNumeric(0.5), "(/ 1 2.0)".parseProgram().evalIn(env))

        // oops
        assertFails { "(/ a)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 a)".parseProgram().evalIn(env) }
        assertFails { "(/ a 1)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 a 1)".parseProgram().evalIn(env) }
        assertFails { "(/ 1 1 a)".parseProgram().evalIn(env) }
    }

    @Test
    fun test_if() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltins()
        fun String.eval() = this.parseProgram().evalIn(env)

        assertEquals(Expr.Boolean.True, "(if #t #t #f)".eval())
        assertEquals(Expr.Boolean.False, "(if #f #t #f)".eval())
        assertEquals(LongNumeric(2), "(if 1 2 3)".eval())
        assertEquals(Expr.Boolean.True, "(if (if #t #t) #t #f)".eval())
        assertEquals(Expr.Boolean.True, "(if (if #f #f) #t #f)".eval())

        assertEquals(LongNumeric(3), "(if (if #t #t) (+ 1 2) (+ 3 4))".eval())
        assertEquals(LongNumeric(7), "(if (if #t #f) (+ 1 2) (+ 3 4))".eval())
        assertEquals(LongNumeric(10), "(+ (if #t 1 2) (if #f 3 4) (if #t 5 6))".eval())

        assertFails { "(if)".eval() }
        assertFails { "(if 1)".eval() }
        assertFails { "(if 1 2 3 4)".eval() }
    }
}
