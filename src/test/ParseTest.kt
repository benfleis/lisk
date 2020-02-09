package lispy

import lispy.Expr.*
import org.junit.jupiter.api.*
import kotlin.test.*

internal class ParseTest {
    @Test
    fun test_parse() {
        todo {
            assertEquals("1".parseProgram(), LongNumeric(1))
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
    fun test_parse_long_numeric() {
        val lit1 = "1".parseLongNumeric()
        assertEquals("1", lit1.toCode())

        // 1.0 should fail to parse as Long
        assertFails { "1.0".parseLongNumeric() }
    }

    @Test
    fun test_parse_double_numeric() {
        val lit1_0 = "1.0".parseDoubleNumeric()
        assertEquals("1.0", lit1_0.toCode())
    }

    @Test
    fun test_parse_list() {
        val list123 = "( 1 2 3 ) ".parseProgram()
        assertEquals("(1 2 3)", list123.toCode())
    }

    @TestFactory
    fun test_programs() = listOf(
        "1" to "1",
        "1.0" to "1.0",
        "Foobar" to "Foobar",
        "()" to "()",
        " (  )\t\n" to "()",
        "1 2 3" to "1",                     // reminder: program is only first expression
        "(1 2 3)" to "(1 2 3)",
        "(+ 1 2)" to "(+ 1 2)",
        "(+ (+ 1 2) (- 4 2))" to "(+ (+ 1 2) (- 4 2))"
        ).map { (input, expected) ->
        DynamicTest.dynamicTest("program | parse | generate : $input -> $expected") {
            assertEquals(expected, input.parseProgram().toCode())
        }
    }
}

