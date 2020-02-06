package lispy

import kotlin.test.*
import org.junit.jupiter.api.*

internal class ExprTest {
    val emptyEnv = Env(null, mutableMapOf())
    val lit1 = Expr.LongNumeric(1)
    val lit1_0 = Expr.DoubleNumeric(1.0)
    val sym = Expr.Symbol("sym")

    @Test
    fun test_long_numeric_evalIn() {
        assertEquals(lit1, lit1.evalIn(emptyEnv))
        assertEquals("1", lit1.evalIn(emptyEnv).generate())
    }

    @Test
    fun test_double_numeric_evalIn() {
        assertEquals(lit1_0, lit1_0.evalIn(emptyEnv))
        assertEquals("1.0", lit1_0.evalIn(emptyEnv).generate()) // sketchy w/ double/floats; 1.0 should suffice in base2
    }

    @Test
    fun test_symbol_evalIn() {
        assertFails { sym.evalIn(emptyEnv) }
        val symEnv = Env(null, mutableMapOf(Pair("sym", lit1)))
        assertEquals(lit1, sym.evalIn(symEnv))
    }
}
