// port-lint: tests serializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerializerTest {
    @Serializable
    data class Row(
        val foo: Int,
        val bar: Double,
        val baz: Boolean,
    )

    @Serializable
    data class Person(
        val name: String,
        val age: Int,
        val active: Boolean,
    )

    @Serializable
    enum class Status {
        PENDING,
        ACTIVE,
        COMPLETED,
    }

    @Serializable
    data class Task(
        val title: String,
        val status: Status,
    )

    @Test
    fun testBool() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeBool(true).getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("true\n", wtr.asString())

        val wtrH = WriterBuilder.new().fromWriter()
        val serH = SeHeader.new(wtrH)
        serH.serializeBool(true).getOrThrow()
        assertEquals(false, serH.wroteHeader())
        assertEquals("", wtrH.asString())
    }

    @Test
    fun testInteger() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeI32(12345).getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("12345\n", wtr.asString())

        val wtrH = WriterBuilder.new().fromWriter()
        val serH = SeHeader.new(wtrH)
        serH.serializeI32(12345).getOrThrow()
        assertEquals(false, serH.wroteHeader())
        assertEquals("", wtrH.asString())
    }

    @Test
    fun testIntegerU128() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeU128("170141183460469231731687303715884105728").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("170141183460469231731687303715884105728\n", wtr.asString())
    }

    @Test
    fun testIntegerI128() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeI128("170141183460469231731687303715884105727").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("170141183460469231731687303715884105727\n", wtr.asString())
    }

    @Test
    fun testFloat() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeF64(1.23).getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("1.23\n", wtr.asString())

        val wtrH = WriterBuilder.new().fromWriter()
        val serH = SeHeader.new(wtrH)
        serH.serializeF64(1.23).getOrThrow()
        assertEquals(false, serH.wroteHeader())
        assertEquals("", wtrH.asString())
    }

    @Test
    fun testFloatNan() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeF64(Double.NaN).getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("NaN\n", wtr.asString())
    }

    @Test
    fun testChar() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeChar('☃').getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("☃\n", wtr.asString())
    }

    @Test
    fun testStr() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeStr("how\nare\n\"you\"?").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", wtr.asString())
    }

    @Test
    fun testBytes() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeBytes("how\nare\n\"you\"?".encodeToByteArray()).getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", wtr.asString())
    }

    @Test
    fun testOption() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeNone().getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("\n", wtr.asString())

        val wtr2 = WriterBuilder.new().fromWriter()
        val ser2 = SeRecord(wtr2)
        ser2.serializeSome("5").getOrThrow()
        wtr2.endRecord().getOrThrow()
        assertEquals("5\n", wtr2.asString())
    }

    @Test
    fun testUnit() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeUnit().getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("\n", wtr.asString())
    }

    @Test
    fun testStructUnit() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeUnitStruct("Foo").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("Foo\n", wtr.asString())
    }

    @Test
    fun testStructNewtype() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeNewtypeStruct("Foo", "1.5").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("1.5\n", wtr.asString())
    }

    @Test
    fun testEnumUnits() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeUnitVariant("Wat", 0u, "Foo").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("Foo\n", wtr.asString())
    }

    @Test
    fun testEnumNewtypes() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeRecord(wtr)
        ser.serializeNewtypeVariant("Wat", 0u, "Foo", "5").getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("5\n", wtr.asString())
    }

    @Test
    fun testSeq() {
        val wtr = WriterBuilder.new().fromWriter()
        val seq = SeRecord(wtr).serializeSeq(3).getOrThrow()
        seq.serializeElement(1).getOrThrow()
        seq.serializeElement(2).getOrThrow()
        seq.serializeElement(3).getOrThrow()
        seq.end().getOrThrow()
        wtr.endRecord().getOrThrow()
        assertEquals("1,2,3\n", wtr.asString())
    }

    @Test
    fun testTupleVariantUnsupported() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = SeRecord(wtr).serializeTupleVariant("Foo", 0u, "X", 3)
        assertTrue(res.isFailure)
    }

    @Test
    fun testEnumStructVariantUnsupported() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = SeRecord(wtr).serializeStructVariant("Foo", 0u, "X", 3)
        assertTrue(res.isFailure)
    }

    @Test
    fun testStructHeaders() {
        val wtr = WriterBuilder.new().fromWriter()
        val ser = SeHeader.new(wtr)
        ser.serializeField("x", "true").getOrThrow()
        ser.serializeField("y", "5").getOrThrow()
        ser.serializeField("z", "hi").getOrThrow()
        assertTrue(ser.wroteHeader())
        assertEquals("x,y,z", wtr.asString())
    }
}
