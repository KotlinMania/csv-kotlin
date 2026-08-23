// port-lint: tests writer.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class WriterTest {
    @Test
    fun oneRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(listOf("a", "b", "c"))
        assertEquals("a,b,c\n", wtr.asString())
    }

    @Test
    fun oneStringRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(StringRecord.from(listOf("a", "b", "c")))
        assertEquals("a,b,c\n", wtr.asString())
    }

    @Test
    fun oneByteRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a", "b", "c")))
        assertEquals("a,b,c\n", wtr.asString())
    }

    @Test
    fun rawOneByteRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a", "b", "c")))
        assertEquals("a,b,c\n", wtr.asString())
    }

    @Test
    fun oneEmptyRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(emptyList<String>())
        assertEquals("\n", wtr.asString())
    }

    @Test
    fun rawOneEmptyRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.new())
        assertEquals("\n", wtr.asString())
    }

    @Test
    fun twoEmptyRecords() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(emptyList<String>())
        wtr.writeRecord(emptyList<String>())
        assertEquals("\n\n", wtr.asString())
    }

    @Test
    fun rawTwoEmptyRecords() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.new())
        wtr.writeByteRecord(ByteRecord.new())
        assertEquals("\n\n", wtr.asString())
    }

    @Test
    fun unequalRecordsBad() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(listOf("a", "b", "c"))
        val err = wtr.writeRecord(listOf("a")).exceptionOrNull()
        assertIs<CsvError>(err)
        val kind = err.kind()
        assertIs<ErrorKind.UnequalLengths>(kind)
        assertEquals(3uL, kind.expectedLen)
        assertEquals(1uL, kind.len)
    }

    @Test
    fun rawUnequalRecordsBad() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a", "b", "c")))
        val err = wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a"))).exceptionOrNull()
        assertIs<CsvError>(err)
        val kind = err.kind()
        assertIs<ErrorKind.UnequalLengths>(kind)
        assertEquals(3uL, kind.expectedLen)
        assertEquals(1uL, kind.len)
    }

    @Test
    fun unequalRecordsOk() {
        val wtr = WriterBuilder.new().flexible(true).fromWriter()
        wtr.writeRecord(listOf("a", "b", "c"))
        wtr.writeRecord(listOf("a"))
        assertEquals("a,b,c\na\n", wtr.asString())
    }

    @Test
    fun rawUnequalRecordsOk() {
        val wtr = WriterBuilder.new().flexible(true).fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a", "b", "c")))
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a")))
        assertEquals("a,b,c\na\n", wtr.asString())
    }

    @Test
    fun fullBufferShouldNotFlushUnderlying() {
        val wtr = WriterBuilder.new().bufferCapacity(4).fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("a", "b")))
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("c", "d")))
        wtr.flush()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf("e", "f")))
        val got = wtr.intoInner().getOrThrow().decodeToString()
        assertEquals("a,b\nc,d\ne,f\n", got)
    }

    @Test
    fun commentCharIsAutomaticallyQuoted() {
        val wtr = WriterBuilder.new().comment('#'.code.toByte()).fromWriter()
        wtr.writeRecord(listOf("# comment", "another"))
        val buf = wtr.intoInner().getOrThrow().decodeToString()
        assertEquals("\"# comment\",another\n", buf)
    }

    @Test
    fun writerParityMethods() {
        val wtr = WriterBuilder.default().fromWriter()
        wtr.writeRecord(listOf("foo", "bar"))
        assertEquals("foo,bar\n", wtr.wtrAsString())
        assertEquals("foo,bar\n", wtr.intoString().getOrThrow())
        assertEquals("foo,bar\n", wtr.getRef().decodeToString())

        wtr.clear()
        assertEquals("", wtr.asString())

        val wtr2 = Writer.default()
        assertEquals("", wtr2.asString())
    }
}
