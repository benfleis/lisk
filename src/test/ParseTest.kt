package lispy

import kotlin.test.*
import org.junit.jupiter.api.Test

class ParseTest {
    @Test
    fun test_parse() {
        todo {
            assertEquals("1".parseProgram(), Expr.LongLiteral(1))
        }
    }

    @Test fun test_tokenize() {
        assertEquals(listOf("1"), "1".tokenize().toList())
        assertEquals(listOf("a", "b", "1", "2"), "a b 1 2".tokenize().toList())
        assertEquals(
            listOf("a", "(", "b", ")", "(", "c", ")"),
            " a  (b \n) (c  \n)".tokenize().toList())
    }

    @Test
    fun test_parse_long_literal() {
        val lit1 = "1".parseLongLiteral()
        assertEquals("LongLiteral(long=1)", lit1.toString())

        // 1.0 should fail to parse as Long
        assertFails { "1.0".parseLongLiteral() }
    }

    @Test
    fun test_parse_double_literal() {
        val lit1_0 = "1.0".parseDoubleLiteral()
        assertEquals("DoubleLiteral(double=1.0)", lit1_0.toString())
    }
}

