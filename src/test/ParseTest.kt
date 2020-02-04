package lispy

import kotlin.test.*
import org.junit.jupiter.api.Test

class ParseTest {
    @Test
    fun test_parse() {
        assertEquals("1".parseProgram(), Expr.Literal(1))
    }

    // fun Sequence<T>.toArray<T>

    @Test fun test_tokenize() {
        assertEquals(listOf("1"), "1".tokenize().toList())
        assertEquals(listOf("a", "b", "1", "2"), "a b 1 2".tokenize().toList())
        assertEquals(listOf("a", "b"), " a  b ".tokenize().toList())
    }
}

