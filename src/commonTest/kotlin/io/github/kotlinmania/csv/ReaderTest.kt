// port-lint: tests reader.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ReaderTest {
    private fun b(s: String): ByteArray = s.encodeToByteArray()

    private fun newpos(byte: ULong, line: ULong, record: ULong): Position =
        Position.new().setByte(byte).setLine(line).setRecord(record)

    @Test
    fun readByteRecord() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = ByteRecord.new()

        assertTrue(rdr.readByteRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("foo", rec[0]?.decodeToString())

        assertTrue(rdr.readByteRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("a", rec[0]?.decodeToString())

        assertTrue(rdr.readByteRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("d", rec[0]?.decodeToString())

        assertFalse(rdr.readByteRecord(rec).getOrThrow())
    }

    @Test
    fun readTrimmedRecordsAndHeaders() {
        val data = b("  header1 ,  header2 \n  foo , bar \n baz ,  quux ")
        val rdr = ReaderBuilder.new().trim(Trim.ALL).fromReader(data)
        val rec = StringRecord.new()

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("header1", "header2"), headers.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("foo", "bar"), rec.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("baz", "quux"), rec.toList())

        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readTrimmedHeader() {
        val data = b("  header1 ,  header2 \n  foo , bar ")
        val rdr = ReaderBuilder.new().trim(Trim.HEADERS).fromReader(data)
        val rec = StringRecord.new()

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("header1", "header2"), headers.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("  foo ", " bar "), rec.toList())

        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readTrimedHeaderInvalidUtf8() {
        val data =
            byteArrayOf(
                0x20,
                'a'.code.toByte(),
                0xFF.toByte(),
                0x20,
                ','.code.toByte(),
                0x20,
                'b'.code.toByte(),
                0x20,
                '\n'.code.toByte(),
                'c'.code.toByte(),
                ','.code.toByte(),
                'd'.code.toByte(),
            )
        val rdr = ReaderBuilder.new().trim(Trim.HEADERS).fromReader(data)
        val byteHeaders = rdr.byteHeaders().getOrThrow()
        assertEquals(2, byteHeaders.len())
    }

    @Test
    fun readTrimmedRecords() {
        val data = b("  header1 ,  header2 \n  foo , bar ")
        val rdr = ReaderBuilder.new().trim(Trim.FIELDS).fromReader(data)
        val rec = StringRecord.new()

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("  header1 ", "  header2 "), headers.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("foo", "bar"), rec.toList())

        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readTrimmedRecordsWithoutHeaders() {
        val data = b("  foo , bar \n baz ,  quux ")
        val rdr =
            ReaderBuilder
                .new()
                .hasHeaders(false)
                .trim(Trim.FIELDS)
                .fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("foo", "bar"), rec.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("baz", "quux"), rec.toList())

        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readRecordUnequalFails() {
        val data = b("a,b,c\nd,e\nf,g,h")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        val err = rdr.readRecord(rec).exceptionOrNull()
        assertIs<CsvError>(err)
        val kind = err.kind()
        assertIs<ErrorKind.UnequalLengths>(kind)
        assertEquals(3uL, kind.expectedLen)
        assertEquals(2uL, kind.len)
    }

    @Test
    fun readRecordUnequalOk() {
        val data = b("a,b,c\nd,e\nf,g,h")
        val rdr =
            ReaderBuilder
                .new()
                .hasHeaders(false)
                .flexible(true)
                .fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(3, rec.len())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(2, rec.len())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(3, rec.len())

        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readRecordUnequalContinue() {
        val data = b("a,b,c\nd,e\nf,g,h")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertTrue(rdr.readRecord(rec).isFailure)
        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("f", rec[0])
    }

    @Test
    fun readRecordHeaders() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f")
        val rdr = ReaderBuilder.new().hasHeaders(true).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("a", rec[0])

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(3, rec.len())
        assertEquals("d", rec[0])

        assertFalse(rdr.readRecord(rec).getOrThrow())

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("foo", "bar", "baz"), headers.toList())
    }

    @Test
    fun readRecordHeadersInvalidUtf8() {
        val data =
            byteArrayOf(
                'f'.code.toByte(),
                'o'.code.toByte(),
                'o'.code.toByte(),
                ','.code.toByte(),
                'b'.code.toByte(),
                0xFF.toByte(),
                'a'.code.toByte(),
                'r'.code.toByte(),
                ','.code.toByte(),
                'b'.code.toByte(),
                'a'.code.toByte(),
                'z'.code.toByte(),
                '\n'.code.toByte(),
                'a'.code.toByte(),
                ','.code.toByte(),
                'b'.code.toByte(),
                ','.code.toByte(),
                'c'.code.toByte(),
            )
        val rdr = ReaderBuilder.new().hasHeaders(true).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals("a", rec[0])

        val byteHeaders = rdr.byteHeaders().getOrThrow()
        assertEquals(3, byteHeaders.len())

        val err = rdr.headers().exceptionOrNull()
        assertIs<CsvError>(err)
        val kind = err.kind()
        assertIs<ErrorKind.Utf8>(kind)
        assertEquals(1, kind.err.field())
        assertEquals(1, kind.err.validUpTo())
    }

    @Test
    fun readRecordNoHeadersBefore() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("foo", "bar", "baz"), headers.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("foo", "bar", "baz"), rec.toList())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("a", "b", "c"), rec.toList())
    }

    @Test
    fun readRecordNoHeadersAfter() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(listOf("foo", "bar", "baz"), rec.toList())

        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("foo", "bar", "baz"), headers.toList())
    }

    @Test
    fun seek() {
        val data = b("a,b,c\nd,e,f\ng,h,i")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals("a", rec[0])

        val pos = rdr.position()
        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals("d", rec[0])

        rdr.seek(pos)
        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals("d", rec[0])
    }

    @Test
    fun seekHeadersAfter() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f\ng,h,i")
        val rdr = ReaderBuilder.new().fromReader(data)
        rdr.seek(Position(18uL, 3uL, 2uL)).getOrThrow()
        assertEquals(listOf("foo", "bar", "baz"), rdr.headers().getOrThrow().toList())
    }

    @Test
    fun seekHeadersBeforeAfter() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f\ng,h,i")
        val rdr = ReaderBuilder.new().fromReader(data)
        val headers = rdr.headers().getOrThrow().clone()
        rdr.seek(Position(18uL, 3uL, 2uL)).getOrThrow()
        assertEquals(headers, rdr.headers().getOrThrow())
    }

    @Test
    fun seekHeadersNoActualSeek() {
        val data = b("foo,bar,baz\na,b,c\nd,e,f\ng,h,i")
        val rdr = ReaderBuilder.new().fromReader(data)
        rdr.seek(Position.new()).getOrThrow()
        assertEquals("foo", rdr.headers().getOrThrow()[0])
    }

    @Test
    fun positionsNoHeaders() {
        val data = b("a,b,c\nd,e,f\ng,h,i")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(0uL, rec.position()?.record())
        assertEquals(1uL, rec.position()?.line())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(1uL, rec.position()?.record())
        assertEquals(2uL, rec.position()?.line())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(2uL, rec.position()?.record())
        assertEquals(3uL, rec.position()?.line())
    }

    @Test
    fun positionsHeaders() {
        val data = b("h1,h2,h3\na,b,c\nd,e,f")
        val rdr = ReaderBuilder.new().hasHeaders(true).fromReader(data)
        val rec = StringRecord.new()

        val headers = rdr.headers().getOrThrow()
        assertEquals(0uL, headers.position()?.record())
        assertEquals(1uL, headers.position()?.line())

        assertTrue(rdr.readRecord(rec).getOrThrow())
        assertEquals(1uL, rec.position()?.record())
        assertEquals(2uL, rec.position()?.line())
    }

    @Test
    fun headersOnEmptyData() {
        val data = b("")
        val rdr = ReaderBuilder.new().hasHeaders(true).fromReader(data)
        val headers = rdr.headers().getOrThrow()
        assertEquals(0, headers.len())
    }

    @Test
    fun noHeadersOnEmptyData() {
        val data = b("")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val rec = StringRecord.new()
        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun noHeadersOnEmptyDataAfterHeaders() {
        val data = b("")
        val rdr = ReaderBuilder.new().hasHeaders(false).fromReader(data)
        val headers = rdr.headers().getOrThrow()
        assertEquals(0, headers.len())
        val rec = StringRecord.new()
        assertFalse(rdr.readRecord(rec).getOrThrow())
    }

    @Test
    fun readerParityMethods() {
        val data = b("a,b,c\nd,e,f\n")
        val rdr =
            ReaderBuilder
                .default()
                .hasHeaders(false)
                .quoting(true)
                .ascii(true)
                .nfa(true)
                .fromReader(data)

        assertEquals(data.size, rdr.getRef().size)
        assertFalse(rdr.isDone())

        val byteRecords = rdr.intoByteRecords().toList()
        assertEquals(2, byteRecords.size)
        assertTrue(rdr.isDone())
        assertEquals(data.size, rdr.intoInner().size)

        val rdr2 = Reader.fromString("1,2\n3,4\n")
        val strRecords = rdr2.intoRecords().toList()
        assertEquals(1, strRecords.size) // header consumed because default hasHeaders=true

        val rdr3 = Reader.default()
        assertTrue(rdr3.isDone())
    }
}
