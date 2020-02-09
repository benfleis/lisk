package lispy

import kotlin.test.*
import lispy.Expr.*
import org.junit.jupiter.api.*

internal class ExprTest {
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
}
