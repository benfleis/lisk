package lispy

import kotlin.test.*
import org.junit.jupiter.api.Test

class ExprTest {
    val emptyEnv = Env(null, mutableMapOf())
    val lit1 = Expr.LongLiteral(1)
    val lit1_0 = Expr.DoubleLiteral(1.0)
    val sym = Expr.Symbol("sym")

    @Test
    fun test_long_literal_evalIn() {
        assertEquals(lit1, lit1.evalIn(emptyEnv))
        assertEquals("LongLiteral(long=1)", lit1.evalIn(emptyEnv).toString())
    }

    @Test
    fun test_double_literal_evalIn() {
        assertEquals(lit1_0, lit1_0.evalIn(emptyEnv))
        assertEquals("DoubleLiteral(double=1.0)", lit1_0.evalIn(emptyEnv).toString())
    }

    @Test
    fun test_symbol_evalIn() {
        assertFails { sym.evalIn(emptyEnv) }
        val symEnv = Env(null, mutableMapOf(Pair("sym", lit1)))
        assertEquals(lit1, sym.evalIn(symEnv))
    }
}
