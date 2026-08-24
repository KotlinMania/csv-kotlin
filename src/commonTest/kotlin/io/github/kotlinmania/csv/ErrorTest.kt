// port-lint: tests error.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ErrorTest {
    @Test
    fun testIoError() {
        val err = CsvError.from(RuntimeException("disk error"))
        assertTrue(err.isIoError())
        assertNull(err.position())
        assertTrue(err.toString().contains("disk error"))
        assertEquals(err.toString(), err.fmt())
    }

    @Test
    fun testUtf8Error() {
        val uErr = newUtf8Error(field = 2, validUpTo = 10)
        assertEquals(2, uErr.field())
        assertEquals(10, uErr.validUpTo())
        assertTrue(uErr.toString().contains("field 2"))
        assertTrue(uErr.toString().contains("10"))
        assertEquals(uErr, newUtf8Error(field = 2, validUpTo = 10))
        assertEquals(uErr.hashCode(), newUtf8Error(field = 2, validUpTo = 10).hashCode())

        val pos = Position(100uL, 5uL, 2uL)
        val kindWithPos = ErrorKind.Utf8(pos = pos, err = uErr)
        val csvErrWithPos = CsvError.new(kindWithPos)
        assertFalse(csvErrWithPos.isIoError())
        assertEquals(pos, csvErrWithPos.position())
        assertTrue(csvErrWithPos.toString().contains("record 2"))
        assertTrue(csvErrWithPos.toString().contains("line 5"))

        val kindNoPos = ErrorKind.Utf8(pos = null, err = uErr)
        val csvErrNoPos = CsvError.new(kindNoPos)
        assertNull(csvErrNoPos.position())
        assertTrue(csvErrNoPos.toString().contains("field 2"))
    }

    @Test
    fun testFromUtf8Error() {
        val rec = ByteRecord.fromStrings(listOf("abc", "def"))
        val uErr = newUtf8Error(field = 1, validUpTo = 0)
        val fromErr = FromUtf8Error.new(rec, uErr)
        assertEquals(rec, fromErr.intoByteRecord())
        assertEquals(uErr, fromErr.utf8Error())
        val (pRec, pErr) = fromErr.intoParts()
        assertEquals(rec, pRec)
        assertEquals(uErr, pErr)
    }

    @Test
    fun testUnequalLengths() {
        val pos = Position(50uL, 3uL, 2uL)
        val kindWithPos = ErrorKind.UnequalLengths(pos = pos, expectedLen = 4uL, len = 2uL)
        val errWithPos = CsvError.new(kindWithPos)
        assertEquals(pos, errWithPos.position())
        assertTrue(errWithPos.toString().contains("found record with 2 fields, but the previous record has 4 fields"))

        val kindNoPos = ErrorKind.UnequalLengths(pos = null, expectedLen = 4uL, len = 2uL)
        val errNoPos = CsvError.new(kindNoPos)
        assertNull(errNoPos.position())
        assertTrue(errNoPos.toString().contains("found record with 2 fields, but the previous record has 4 fields"))
    }

    @Test
    fun testSeekError() {
        val kind = ErrorKind.Seek
        val err = CsvError.new(kind)
        assertNull(err.position())
        assertTrue(err.toString().contains("cannot access headers"))
    }

    @Test
    fun testSerializeAndDeserializeErrors() {
        val serKind = ErrorKind.Serialize("failed to serialize")
        val serErr = CsvError.new(serKind)
        assertTrue(serErr.toString().contains("CSV write error: failed to serialize"))

        val pos = Position(15uL, 2uL, 1uL)
        val deKindWithPos = ErrorKind.Deserialize(pos = pos, message = "invalid field")
        val deErrWithPos = CsvError.new(deKindWithPos)
        assertEquals(pos, deErrWithPos.position())
        assertTrue(deErrWithPos.toString().contains("CSV deserialize error: record 1"))

        val deKindNoPos = ErrorKind.Deserialize(pos = null, message = "missing field")
        val deErrNoPos = CsvError.new(deKindNoPos)
        assertNull(deErrNoPos.position())
        assertTrue(deErrNoPos.toString().contains("CSV deserialize error: missing field"))
    }

    @Test
    fun testIntoInnerError() {
        val cause = RuntimeException("write failed")
        val intoErr = IntoInnerError.new(writer = "mockWriter", error = cause)
        assertEquals(cause, intoErr.error())
        assertEquals(cause, intoErr.intoError())
        assertEquals("mockWriter", intoErr.intoInner())
        assertEquals(intoErr.toString(), intoErr.fmt())
    }
}
