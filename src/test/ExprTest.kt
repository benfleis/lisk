package lispy

import kotlin.test.*
import org.junit.jupiter.api.Test

class ExprTest {
    @Test
    fun test_eval() {
        val lit1 = "1".parseLiteral()
        assertEquals(lit1, lit1.eval())
        assertEquals("Literal(int=1)", lit1.eval().toString())
    }
}
