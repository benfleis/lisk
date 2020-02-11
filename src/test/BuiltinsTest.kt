package lispy

import lispy.Expr.*
import lispy.Expr.Boolean.*
import kotlin.test.*
import org.junit.jupiter.api.*

internal class BuiltinsTest {
    private fun getEval(): (String) -> Expr {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        return { it.parseProgram().eval(env) }
    }

    @Test
    fun test_add() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        // basic longs and arities
        assertEquals(LongNumeric(0), "(+)".parseProgram().eval(env))
        assertEquals(LongNumeric(1), "(+ 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(2), "(+ 1 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(6), "(+ 1 2 3)".parseProgram().eval(env))
        assertEquals(LongNumeric(55), "(+ 1 2 3 4 5 6 7 8 9 10)".parseProgram().eval(env))

        // basic doubles
        assertEquals(DoubleNumeric(1.0), "(+ 1.0)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(2.0), "(+ 1.0 1.0)".parseProgram().eval(env))

        // confirm double casting
        assertEquals(DoubleNumeric(2.0), "(+ 1.0 1)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(2.0), "(+ 1 1.0)".parseProgram().eval(env))

        // oops
        assertFails { "(+ a)".parseProgram().eval(env) }
        assertFails { "(+ 1 a)".parseProgram().eval(env) }
        assertFails { "(+ a 1)".parseProgram().eval(env) }
        assertFails { "(+ 1 a 1)".parseProgram().eval(env) }
        assertFails { "(+ 1 1 a)".parseProgram().eval(env) }
    }

    @Test
    fun test_multiply() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        // basic longs and arities
        assertEquals(LongNumeric(1), "(*)".parseProgram().eval(env))
        assertEquals(LongNumeric(1), "(* 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(3), "(* 3 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(6), "(* 1 2 3)".parseProgram().eval(env))
        assertEquals(LongNumeric(3628800), "(* 1 2 3 4 5 6 7 8 9 10)".parseProgram().eval(env))

        // basic doubles
        assertEquals(DoubleNumeric(2.0), "(* 2.0)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(8.0), "(* 2.0 4.0)".parseProgram().eval(env))

        // confirm double casting
        assertEquals(DoubleNumeric(2.0), "(* 1.0 2)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(2.0), "(* 1 2.0)".parseProgram().eval(env))

        // oops
        assertFails { "(* a)".parseProgram().eval(env) }
        assertFails { "(* 1 a)".parseProgram().eval(env) }
        assertFails { "(* a 1)".parseProgram().eval(env) }
        assertFails { "(* 1 a 1)".parseProgram().eval(env) }
        assertFails { "(* 1 1 a)".parseProgram().eval(env) }
    }


    @Test
    fun test_subtract() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        // basic longs and arities
        assertEquals(LongNumeric(0), "(-)".parseProgram().eval(env))
        assertEquals(LongNumeric(-1), "(- 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(0), "(- 1 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(-4), "(- 1 2 3)".parseProgram().eval(env))
        assertEquals(LongNumeric(-53), "(- 1 2 3 4 5 6 7 8 9 10)".parseProgram().eval(env))

        // basic doubles
        assertEquals(DoubleNumeric(-1.0), "(- 1.0)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(0.0), "(- 1.0 1.0)".parseProgram().eval(env))

        // confirm double casting
        assertEquals(DoubleNumeric(0.0), "(- 1.0 1)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(0.0), "(- 1 1.0)".parseProgram().eval(env))

        // oops
        assertFails { "(- a)".parseProgram().eval(env) }
        assertFails { "(- 1 a)".parseProgram().eval(env) }
        assertFails { "(- a 1)".parseProgram().eval(env) }
        assertFails { "(- 1 a 1)".parseProgram().eval(env) }
        assertFails { "(- 1 1 a)".parseProgram().eval(env) }
    }

    @Test
    fun test_divide() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        // basic longs and arities
        assertEquals(LongNumeric(1), "(/)".parseProgram().eval(env))
        assertEquals(LongNumeric(1), "(/ 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(3), "(/ 6 2)".parseProgram().eval(env))
        assertEquals(LongNumeric(3), "(/ 12 2 2)".parseProgram().eval(env))
        assertEquals(LongNumeric(0), "(/ 1 2 3 4 5 6 7 8 9 10)".parseProgram().eval(env))

        // basic doubles
        assertEquals(DoubleNumeric(1.0), "(/ 1.0)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(2.0), "(/ 1.0 0.5)".parseProgram().eval(env))

        // confirm double casting
        assertEquals(DoubleNumeric(0.5), "(/ 0.5 1)".parseProgram().eval(env))
        assertEquals(DoubleNumeric(0.5), "(/ 1 2.0)".parseProgram().eval(env))

        // oops
        assertFails { "(/ a)".parseProgram().eval(env) }
        assertFails { "(/ 1 a)".parseProgram().eval(env) }
        assertFails { "(/ a 1)".parseProgram().eval(env) }
        assertFails { "(/ 1 a 1)".parseProgram().eval(env) }
        assertFails { "(/ 1 1 a)".parseProgram().eval(env) }
    }

    @Test
    fun test_begin() {
        val eval = getEval()

        assertFails { eval("(begin)") }
        // confirm simple
        assertEquals(LongNumeric(1), eval("(begin 1)"))
        // confirm last
        assertEquals(LongNumeric(1), eval("(begin (+ 1 2 3) 1)"))
        // confirm eval ordering
        assertEquals(LongNumeric(1), eval("(begin (define one 2) (define one 1) one)"))

        assertEquals("(begin 1)", "(begin 1)".parseProgram().toCode())
        assertEquals("(begin 1 2 3)", "(begin 1 2 3)".parseProgram().toCode())
    }

    @Test
    fun test_lte() {
        val eval = getEval()

        assertFails { eval("(<=)") }
        assertFails { eval("(<= 1)") }
        assertFails { eval("(<= nil 1)")}
        assertFails { eval("(<= 1 <=)")}

        assertEquals(True, eval("(<= 0 1)"))
        assertEquals(True, eval("(<= 0 1 2)"))
        assertEquals(True, eval("(<= 0 1.0 2)"))
        assertEquals(True, eval("(<= 0 0 1.0 1.0 2 2 2)"))
        assertEquals(True, eval("(<= (- 1) 1.0 3 5.0)"))

        assertEquals(False, eval("(<= 1 0)"))
        assertEquals(False, eval("(<= 0 1 0)"))
        assertEquals(False, eval("(<= 0 1 0.0 2)"))
        assertEquals(False, eval("(<= 1 0.0 0 2)"))
    }
}
