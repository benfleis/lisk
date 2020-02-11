package lispy

import kotlin.test.*
import lispy.Expr.*
import org.junit.jupiter.api.*

internal class ExprTest {
    private fun getEval(): (String) -> Expr {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        return { it.parseProgram().eval(env) }
    }

    val emptyEnv = Env(null, mutableMapOf())
    val lit1 = LongNumeric(1)
    val lit1_0 = DoubleNumeric(1.0)
    val sym = Symbol("sym")

    @Test
    fun test_long_numeric_evalIn() {
        assertEquals(lit1, lit1.eval(emptyEnv))
        assertEquals("1", lit1.eval(emptyEnv).toCode())
    }

    @Test
    fun test_double_numeric_evalIn() {
        assertEquals(lit1_0, lit1_0.eval(emptyEnv))
        assertEquals("1.0", lit1_0.eval(emptyEnv).toCode()) // sketchy w/ double/floats; 1.0 should suffice in base2
    }

    @Test
    fun test_symbol_evalIn() {
        assertFails { sym.eval(emptyEnv) }
        val symEnv = Env(null, mutableMapOf(Pair("sym", lit1)))
        assertEquals(lit1, sym.eval(symEnv))
    }

    @Test
    fun test_nested_math() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        assertEquals(
            LongNumeric(10),
            "(+ (+ 1 2 (+ 0) (- 3)) (+ 3 4 (+) (- (- 3))))".parseProgram().eval(env))
    }

    @Test
    fun test_lambda_stuff() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        assertEquals(
            LongNumeric(2),
            """(begin 
                (define incr (lambda (a) (+ a 1))) +
                (incr 1))
            """".parseProgram().eval(env))
    }

    @Test
    fun test_fn_recursion() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()

        // define fact up front
        assertEquals(
            Nil,
            "(define fact (lambda (n) (if (<= n 1) 1 (* n (fact (- n 1))))))".parseProgram().eval(env))

        assertEquals(LongNumeric(1), "(fact 1)".parseProgram().eval(env))
        assertEquals(LongNumeric(2), "(fact 2)".parseProgram().eval(env))
        assertEquals(LongNumeric(6), "(fact 3)".parseProgram().eval(env))
        assertEquals(LongNumeric(3628800), "(fact 10)".parseProgram().eval(env))
    }

    @Test
    fun test_if() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        fun String.eval() = this.parseProgram().eval(env)

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
    fun test_set() {
        val eval = getEval()
        val one = 1.toExpr()
        val two = 2.toExpr()

        assertFails { eval("(set!)") }
        assertFails { eval("(set! a)") }
        assertFails { eval("(set! a 1)") }

        eval("(define a 1)")
        eval("(define b 4)")
        assertEquals(one, eval("a"))
        assertEquals(one, eval("(set! a 2)"))
        assertEquals(two, eval("a"))
        assertEquals(two, eval("(set! a b)"))

        assertFails { eval("(set! 1 2)") }
    }

    @Test
    fun test_quote() {
        val env = Env(null, mutableMapOf())
        env.registerBuiltinProcedures()
        val eval = { s: String -> s.parseProgram().eval(env) }

        val plus = Symbol("+")
        val one = 1L.toExpr()
        val two_0 = 2.0.toExpr()

        assertFails { eval("(quote)") }
        assertFails { eval("(quote 1 2)") }
        assertEquals(one, eval("(quote 1)"))
        assertEquals(List(mutableListOf(plus, one, two_0)), eval("(quote (+ 1 2.0))"))
    }
}
