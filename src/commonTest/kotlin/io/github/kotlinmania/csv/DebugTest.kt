// port-lint: tests csv/src/debug.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DebugTest {
    @Test
    fun bytesPrintsAsciiVerbatim() {
        val s = Bytes("hello".encodeToByteArray().toUByteArray()).toString()
        assertEquals("\"hello\"", s)
    }

    @Test
    fun bytesEscapesEmbeddedNulAsBackslashZero() {
        val s = Bytes(ubyteArrayOf(0x61u, 0x00u, 0x62u)).toString()
        assertEquals("\"a\\0b\"", s)
    }

    @Test
    fun bytesEscapesControlCharsAsHex() {
        val s = Bytes(ubyteArrayOf(0x01u, 0x7Fu)).toString()
        assertEquals("\"\\x01\\x7f\"", s)
    }

    @Test
    fun bytesPreservesTabNewlineCarriageReturnAsEscapes() {
        val s = Bytes(ubyteArrayOf(0x09u, 0x0Au, 0x0Du)).toString()
        assertEquals("\"\\t\\n\\r\"", s)
    }

    @Test
    fun bytesEscapesBackslashAndDoubleQuote() {
        val s = Bytes("a\\b\"c".encodeToByteArray().toUByteArray()).toString()
        assertEquals("\"a\\\\b\\\"c\"", s)
    }

    @Test
    fun bytesEscapesInvalidLeadingByteAsHex() {
        // 0xFF is never a valid UTF-8 lead byte.
        val s = Bytes(ubyteArrayOf(0x61u, 0xFFu, 0x62u)).toString()
        assertEquals("\"a\\xffb\"", s)
    }

    @Test
    fun bytesDecodesMultiByteUtf8() {
        // U+00E9 (é) encodes as 0xC3 0xA9; the result keeps the printable form.
        val s = Bytes(ubyteArrayOf(0xC3u, 0xA9u)).toString()
        assertEquals("\"é\"", s)
    }

    @Test
    fun utf8DecodeReturnsNullForEmpty() {
        assertNull(utf8Decode(ubyteArrayOf()))
    }

    @Test
    fun utf8DecodeOkForAscii() {
        val r = utf8Decode(ubyteArrayOf(0x41u))
        assertTrue(r is Utf8DecodeResult.Ok)
        assertEquals('A', r.ch)
    }

    @Test
    fun utf8DecodeErrForInvalidLeadByte() {
        val r = utf8Decode(ubyteArrayOf(0x80u))
        assertTrue(r is Utf8DecodeResult.Err)
        assertEquals(0x80u.toUByte(), r.byte)
    }

    @Test
    fun utf8DecodeErrForTruncatedMultiByteSequence() {
        val r = utf8Decode(ubyteArrayOf(0xE2u, 0x82u))
        assertTrue(r is Utf8DecodeResult.Err)
        assertEquals(0xE2u.toUByte(), r.byte)
    }

    @Test
    fun utf8DecodeErrForOverlongEncoding() {
        val r = utf8Decode(ubyteArrayOf(0xC0u, 0x80u))
        assertTrue(r is Utf8DecodeResult.Err)
        assertEquals(0xC0u.toUByte(), r.byte)
    }

    @Test
    fun utf8DecodeOkForFourByteScalarValue() {
        val r = utf8Decode(ubyteArrayOf(0xF0u, 0x9Fu, 0x98u, 0x80u))
        assertTrue(r is Utf8DecodeResult.Ok)
        assertTrue(r.ch.isHighSurrogate())
    }
}
