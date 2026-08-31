// port-lint: tests csv/src/byte_record.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ByteRecordTest {
    private fun b(s: String): ByteArray = s.encodeToByteArray()

    @Test
    fun record1() {
        val rec = ByteRecord.new()
        rec.pushField(b("foo"))

        assertEquals(1, rec.len())
        assertEquals("foo", rec.get(0)?.decodeToString())
        assertNull(rec.get(1))
        assertNull(rec.get(2))
    }

    @Test
    fun record2() {
        val rec = ByteRecord.new()
        rec.pushField(b("foo"))
        rec.pushField(b("quux"))

        assertEquals(2, rec.len())
        assertEquals("foo", rec.get(0)?.decodeToString())
        assertEquals("quux", rec.get(1)?.decodeToString())
        assertNull(rec.get(2))
        assertNull(rec.get(3))
    }

    @Test
    fun emptyRecord() {
        val rec = ByteRecord.new()

        assertEquals(0, rec.len())
        assertNull(rec.get(0))
        assertNull(rec.get(1))
    }

    @Test
    fun trimWhitespaceOnly() {
        val rec = ByteRecord.from(listOf(byteArrayOf(0x20, 0x09, 0x0A, 0x0D, 0x0C)))
        rec.trim()
        assertEquals("", rec.get(0)?.decodeToString())
    }

    @Test
    fun trimFront() {
        val rec = ByteRecord.from(listOf(b(" abc")))
        rec.trim()
        assertEquals("abc", rec.get(0)?.decodeToString())

        val rec2 = ByteRecord.from(listOf(b(" abc"), b("  xyz")))
        rec2.trim()
        assertEquals("abc", rec2.get(0)?.decodeToString())
        assertEquals("xyz", rec2.get(1)?.decodeToString())
    }

    @Test
    fun trimBack() {
        val rec = ByteRecord.from(listOf(b("abc ")))
        rec.trim()
        assertEquals("abc", rec.get(0)?.decodeToString())

        val rec2 = ByteRecord.from(listOf(b("abc "), b("xyz  ")))
        rec2.trim()
        assertEquals("abc", rec2.get(0)?.decodeToString())
        assertEquals("xyz", rec2.get(1)?.decodeToString())
    }

    @Test
    fun trimBoth() {
        val rec = ByteRecord.from(listOf(b(" abc ")))
        rec.trim()
        assertEquals("abc", rec.get(0)?.decodeToString())

        val rec2 = ByteRecord.from(listOf(b(" abc "), b("  xyz  ")))
        rec2.trim()
        assertEquals("abc", rec2.get(0)?.decodeToString())
        assertEquals("xyz", rec2.get(1)?.decodeToString())
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords1() {
        val rec = ByteRecord.from(listOf(b("")))
        rec.trim()
        assertEquals("", rec.get(0)?.decodeToString())
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords2() {
        val rec = ByteRecord.from(listOf(b(""), b("")))
        rec.trim()
        assertEquals("", rec.get(0)?.decodeToString())
        assertEquals("", rec.get(1)?.decodeToString())
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords3() {
        val rec = ByteRecord.new()
        rec.trim()
        assertEquals(0, rec.len())
    }

    @Test
    fun emptyField1() {
        val rec = ByteRecord.new()
        rec.pushField(b(""))

        assertEquals(1, rec.len())
        assertEquals("", rec.get(0)?.decodeToString())
        assertNull(rec.get(1))
        assertNull(rec.get(2))
    }

    @Test
    fun emptyField2() {
        val rec = ByteRecord.new()
        rec.pushField(b(""))
        rec.pushField(b(""))

        assertEquals(2, rec.len())
        assertEquals("", rec.get(0)?.decodeToString())
        assertEquals("", rec.get(1)?.decodeToString())
        assertNull(rec.get(2))
        assertNull(rec.get(3))
    }

    @Test
    fun emptySurround1() {
        val rec = ByteRecord.new()
        rec.pushField(b("foo"))
        rec.pushField(b(""))
        rec.pushField(b("quux"))

        assertEquals(3, rec.len())
        assertEquals("foo", rec.get(0)?.decodeToString())
        assertEquals("", rec.get(1)?.decodeToString())
        assertEquals("quux", rec.get(2)?.decodeToString())
        assertNull(rec.get(3))
        assertNull(rec.get(4))
    }

    @Test
    fun emptySurround2() {
        val rec = ByteRecord.new()
        rec.pushField(b("foo"))
        rec.pushField(b(""))
        rec.pushField(b("quux"))
        rec.pushField(b(""))

        assertEquals(4, rec.len())
        assertEquals("foo", rec.get(0)?.decodeToString())
        assertEquals("", rec.get(1)?.decodeToString())
        assertEquals("quux", rec.get(2)?.decodeToString())
        assertEquals("", rec.get(3)?.decodeToString())
        assertNull(rec.get(4))
        assertNull(rec.get(5))
    }

    @Test
    fun utf8Error1() {
        val rec = ByteRecord.new()
        rec.pushField(b("foo"))
        rec.pushField(byteArrayOf('b'.code.toByte(), 0xFF.toByte(), 'a'.code.toByte(), 'r'.code.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(1, err.utf8Error().field())
        assertEquals(1, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8Error2() {
        val rec = ByteRecord.new()
        rec.pushField(byteArrayOf(0xFF.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(0, err.utf8Error().field())
        assertEquals(0, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8Error3() {
        val rec = ByteRecord.new()
        rec.pushField(byteArrayOf('a'.code.toByte(), 0xFF.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(0, err.utf8Error().field())
        assertEquals(1, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8Error4() {
        val rec = ByteRecord.new()
        rec.pushField(b("a"))
        rec.pushField(b("b"))
        rec.pushField(b("c"))
        rec.pushField(b("d"))
        rec.pushField(byteArrayOf('x'.code.toByte(), 'y'.code.toByte(), 'z'.code.toByte(), 0xFF.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(4, err.utf8Error().field())
        assertEquals(3, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8Error5() {
        val rec = ByteRecord.new()
        rec.pushField(b("a"))
        rec.pushField(b("b"))
        rec.pushField(b("c"))
        rec.pushField(b("d"))
        rec.pushField(byteArrayOf(0xFF.toByte(), 'x'.code.toByte(), 'y'.code.toByte(), 'z'.code.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(4, err.utf8Error().field())
        assertEquals(0, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8Error6() {
        val rec = ByteRecord.new()
        rec.pushField(byteArrayOf('a'.code.toByte(), 0xC9.toByte()))
        rec.pushField(byteArrayOf(0x91.toByte(), 'b'.code.toByte()))

        val err = StringRecord.fromByteRecord(rec).exceptionOrNull() as FromUtf8Error
        assertEquals(0, err.utf8Error().field())
        assertEquals(1, err.utf8Error().validUpTo())
    }

    @Test
    fun utf8ClearOk() {
        val rec = ByteRecord.new()
        rec.pushField(byteArrayOf(0xFF.toByte()))
        assertTrue(StringRecord.fromByteRecord(rec).isFailure)

        val rec2 = ByteRecord.new()
        rec2.pushField(byteArrayOf(0xFF.toByte()))
        rec2.clear()
        assertTrue(StringRecord.fromByteRecord(rec2).isSuccess)
    }

    @Test
    fun iter() {
        val data = listOf("foo", "bar", "baz", "quux", "wat")
        val rec = ByteRecord.fromStrings(data)
        val got = rec.map { it.decodeToString() }
        assertEquals(data, got)
    }

    @Test
    fun iterReverse() {
        val data = listOf("foo", "bar", "baz", "quux", "wat")
        val rec = ByteRecord.fromStrings(data)
        val got = rec.toList().reversed().map { it.decodeToString() }
        assertEquals(data.reversed(), got)
    }

    @Test
    fun iterForwardAndReverse() {
        val data = listOf("foo", "bar", "baz", "quux", "wat")
        val rec = ByteRecord.fromStrings(data)
        val it = rec.iter()

        assertEquals("wat", it.nextBack()?.decodeToString())
        assertEquals("foo", it.next().decodeToString())
        assertEquals("bar", it.next().decodeToString())
        assertEquals("quux", it.nextBack()?.decodeToString())
        assertEquals("baz", it.next().decodeToString())
        assertNull(it.nextBack())
        assertTrue(!it.hasNext())
    }

    @Test
    fun eqFieldBoundaries() {
        val test1 = ByteRecord.fromStrings(listOf("12", "34"))
        val test2 = ByteRecord.fromStrings(listOf("123", "4"))
        assertNotEquals(test1, test2)
    }

    @Test
    fun eqRecordLen() {
        val test1 = ByteRecord.fromStrings(listOf("12", "34", "56"))
        val test2 = ByteRecord.fromStrings(listOf("12", "34"))
        assertNotEquals(test1, test2)
    }

    @Test
    fun asSliceAndCloneTruncated() {
        val rec = ByteRecord.withCapacity(100, 10)
        rec.pushField(b("foo"))
        rec.pushField(b("bar"))
        assertEquals("foobar", rec.asSlice().decodeToString())

        val truncated = rec.cloneTruncated()
        assertEquals(2, truncated.len())
        assertEquals("foobar", truncated.asSlice().decodeToString())
        assertTrue(rec.iterEq(truncated))
    }

    @Test
    fun eqAndExtendAndIndexAndParts() {
        val rec = ByteRecord.fromIter(listOf(b("a"), b("b")))
        assertTrue(rec.eq(ByteRecord.from(listOf(b("a"), b("b")))))
        assertTrue(rec.eq(listOf(b("a"), b("b"))))
        assertEquals("a", rec.index(0).decodeToString())
        assertEquals("b", rec.index(1).decodeToString())
        assertEquals(listOf(1, 2), rec.ends())
        assertEquals(1, rec.end(0))
        assertEquals(2, rec.end(1))

        rec.extend(listOf(b("c"), b("d")))
        assertEquals(4, rec.len())
        val parts = rec.asParts()
        assertEquals("abcd", parts.first.decodeToString())
        assertEquals(listOf(1, 2, 3, 4), parts.second)

        rec.setLen(2)
        assertEquals(2, rec.len())

        val iter = rec.intoIter()
        assertEquals(2 to 2, iter.sizeHint())
        assertEquals("a", iter.next().decodeToString())
    }

    @Test
    fun trimAscii() {
        val rec = ByteRecord.from(listOf(b("  hello  "), b("\tworld\r\n")))
        rec.trimAscii()
        assertEquals("hello", rec[0]?.decodeToString())
        assertEquals("world", rec[1]?.decodeToString())
    }

    @Test
    fun testExpandFieldsAndTrimHelpers() {
        val rec = ByteRecord(2, 2)
        rec.pushField(b("abc"))
        rec.expandFields()
        rec.expandEnds()
        assertEquals("abc", rec[0]?.decodeToString())

        val trimmedStart = ByteRecord.trimAsciiStart(b("   test   "))
        assertEquals("test   ", trimmedStart.decodeToString())

        val trimmedEnd = ByteRecord.trimAsciiEnd(b("   test   "))
        assertEquals("   test", trimmedEnd.decodeToString())
    }
}
