// port-lint: tests csv/src/writer.rs
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
        wtr.writeRecord(listOf("")).getOrThrow()
        assertEquals("\"\"\n", wtr.asString())
    }

    @Test
    fun rawOneEmptyRecord() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf(""))).getOrThrow()
        assertEquals("\"\"\n", wtr.asString())
    }

    @Test
    fun twoEmptyRecords() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(listOf("")).getOrThrow()
        wtr.writeRecord(listOf("")).getOrThrow()
        assertEquals("\"\"\n\"\"\n", wtr.asString())
    }

    @Test
    fun rawTwoEmptyRecords() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf(""))).getOrThrow()
        wtr.writeByteRecord(ByteRecord.fromStrings(listOf(""))).getOrThrow()
        assertEquals("\"\"\n\"\"\n", wtr.asString())
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

    private class MarkWriteAndFlush(
        val buffer: MutableList<Byte> = mutableListOf(),
    ) {
        fun intoString(): String = buffer.toByteArray().decodeToString()

        fun write(data: ByteArray): Int {
            buffer.add('>'.code.toByte())
            for (b in data) {
                buffer.add(b)
            }
            buffer.add('<'.code.toByte())
            return data.size
        }

        fun flush() {
            buffer.add('!'.code.toByte())
        }
    }

    @Test
    fun fullBufferShouldNotFlushUnderlying() {
        val underlying = MarkWriteAndFlush()
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

    @Test
    fun writerFieldImplAndDelimiters() {
        val wtr = Writer.new()
        wtr.writeFieldImpl("first".encodeToByteArray())
        wtr.writeDelimiter()
        wtr.writeFieldImpl("second".encodeToByteArray())
        wtr.writeTerminatorIntoBuffer()
        assertEquals("first,second\n", wtr.asString())

        val checkRes = wtr.checkFieldCount(2)
        assertEquals(Result.success(Unit), checkRes)

        wtr.clear()
        wtr.writeDelimiter()
        assertEquals(",", wtr.asString())
    }

    @kotlinx.serialization.Serializable
    private data class Row(
        val foo: Int,
        val bar: Double,
        val baz: Boolean,
    )

    @kotlinx.serialization.Serializable
    private data class Row128(
        val foo: Long,
        val bar: Double,
        val baz: Boolean,
    )

    @Test
    fun serializeWithHeaders() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serialize(Row(42, 42.5, true)).getOrThrow()
        assertEquals("foo,bar,baz\n42,42.5,true\n", wtr.asString())
    }

    @Test
    fun serializeNoHeaders() {
        val wtr = WriterBuilder.new().hasHeaders(false).fromWriter()
        wtr.serialize(Row(42, 42.5, true)).getOrThrow()
        assertEquals("42,42.5,true\n", wtr.asString())
    }

    @Test
    fun serializeNoHeaders128() {
        val wtr = WriterBuilder.new().hasHeaders(false).fromWriter()
        wtr.serialize(Row128(9223372036854775807L, 42.5, true)).getOrThrow()
        assertEquals("9223372036854775807,42.5,true\n", wtr.asString())
    }

    @Test
    fun serializeTuple() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(true, 1.3, "hi")).getOrThrow()
        assertEquals("true,1.3,hi\n", wtr.asString())
    }

    private fun wtrAsString(wtr: Writer): String = wtr.asString()
}
