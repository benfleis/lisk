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
    fun test_if() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        fun String.eval() = this.parseProgram().eval(env)

        assertEquals(True, "(if #t #t #f)".eval())
        assertEquals(False, "(if #f #t #f)".eval())
        assertEquals(LongNumeric(2), "(if 1 2 3)".eval())
        assertEquals(True, "(if (if #t #t) #t #f)".eval())
        assertEquals(True, "(if (if #f #f) #t #f)".eval())

        assertEquals(LongNumeric(3), "(if (if #t #t) (+ 1 2) (+ 3 4))".eval())
        assertEquals(LongNumeric(7), "(if (if #t #f) (+ 1 2) (+ 3 4))".eval())
        assertEquals(LongNumeric(10), "(+ (if #t 1 2) (if #f 3 4) (if #t 5 6))".eval())

        assertFails { "(if)".eval() }
        assertFails { "(if 1)".eval() }
        assertFails { "(if 1 2 3 4)".eval() }
    }

    @Test
    fun test_define() {
        val eval = getEval()

        assertFails { eval("one") }
        assertEquals(Nil, eval("(define one 1)"))
        assertEquals(LongNumeric(1), eval("one"))
        assertEquals(Nil, eval("(define one 2)"))
        assertEquals(LongNumeric(2), eval("one"))
        assertEquals(Nil, eval("(define one 1)"))
        assertEquals(LongNumeric(3), eval("(+ one 1 one)"))
    }

    @Test
    fun test_quote() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        val eval = { s: String -> s.parseProgram().eval(env) }

        val plus = Symbol("+")
        val one = 1L.toLongExpr()
        val two_0 = 2.0.toDoubleExpr()

        assertFails { eval("(quote)") }
        assertFails { eval("(quote 1 2)") }
        assertEquals(one, eval("(quote 1)"))
        assertEquals(List(mutableListOf(plus, one, two_0)), eval("(quote (+ 1 2.0))"))
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
