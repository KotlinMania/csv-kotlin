// port-lint: tests serializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerializerTest {
    @Serializable
    public data class Row(
        public val foo: Int,
        public val bar: Double,
        public val baz: Boolean,
    )

    @Serializable
    public data class Foo(
        public val x: Boolean = true,
        public val y: Int = 5,
        public val z: String = "hi",
    )

    @Serializable
    public data class FooSimple(
        public val label: String,
        public val num: Double,
    )

    @Serializable
    public data class Foo128(
        public val x: Long,
        public val y: ULong,
    )

    @Serializable
    public data class FooNewtype(
        public val value: Double,
    )

    @Serializable
    public data class FooTuple(
        public val a: Boolean,
        public val b: Int,
        public val c: String,
    )

    @Serializable
    public data class Nested(
        public val label2: String,
        public val value: Int,
    )

    @Serializable
    public data class FooNested(
        public val label: String,
        public val nest: Nested,
    )

    @Serializable
    public data class FooSeq(
        public val label: String,
        public val values: List<Int>,
    )

    @Serializable
    public data class Bar(
        public val label2: Boolean,
        public val value: Int,
        public val empty: String = "",
    )

    @Serializable
    public data class Baz(
        public val flag: Boolean,
    )

    @Serializable
    public enum class Wat {
        Foo,
        Bar,
        Baz,
    }

    public fun custom(msg: String): CsvError =
        CsvError(ErrorKind.Serialize(msg))

    public fun <T> serialize(
        value: T,
        serializer: kotlinx.serialization.SerializationStrategy<T>,
    ): String {
        val wtr = WriterBuilder.new().fromWriter()
        CsvSerializer.serialize(wtr, serializer, value).getOrThrow()
        return wtr.asString()
    }

    public inline fun <reified T> serialize(value: T): String =
        serialize(value, kotlinx.serialization.serializer())

    public fun <T> serializeHeader(
        value: T,
        serializer: kotlinx.serialization.SerializationStrategy<T>,
    ): Pair<Boolean, String> {
        val wtr = WriterBuilder.new().fromWriter()
        val wrote = CsvSerializer.serializeHeader(wtr, serializer.descriptor).getOrThrow()
        return Pair(wrote, wtr.asString().trimEnd('\n'))
    }

    public inline fun <reified T> serializeHeader(value: T): Pair<Boolean, String> =
        serializeHeader(value, kotlinx.serialization.serializer())

    public fun <T> serializeErr(
        value: T,
        serializer: kotlinx.serialization.SerializationStrategy<T>,
    ): CsvError {
        val wtr = WriterBuilder.new().fromWriter()
        val res = CsvSerializer.serialize(wtr, serializer, value)
        return res.exceptionOrNull() as? CsvError ?: custom("expected error")
    }

    public inline fun <reified T> serializeErr(value: T): CsvError =
        serializeErr(value, kotlinx.serialization.serializer())

    public fun <T> serializeHeaderErr(
        value: T,
        serializer: kotlinx.serialization.SerializationStrategy<T>,
    ): CsvError {
        val wtr = WriterBuilder.new().fromWriter()
        val res = CsvSerializer.serializeHeader(wtr, serializer.descriptor)
        return res.exceptionOrNull() as? CsvError ?: custom("expected error")
    }

    public inline fun <reified T> serializeHeaderErr(value: T): CsvError =
        serializeHeaderErr(value, kotlinx.serialization.serializer())

    @Test
    fun bool() {
        val got = serialize(true)
        assertEquals("true\n", got)
        val (wrote, gotH) = serializeHeader(true)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integer() {
        val got = serialize(12345)
        assertEquals("12345\n", got)
        val (wrote, gotH) = serializeHeader(12345)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integerU128() {
        val got = serialize(18446744073709551615UL)
        assertEquals("18446744073709551615\n", got)
        val (wrote, gotH) = serializeHeader(12345)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integerI128() {
        val got = serialize(9223372036854775807L)
        assertEquals("9223372036854775807\n", got)
        val (wrote, gotH) = serializeHeader(12345)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun float() {
        val got = serialize(1.23)
        assertEquals("1.23\n", got)
        val (wrote, gotH) = serializeHeader(1.23)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun floatNan() {
        val got = serialize(Double.NaN)
        assertEquals("NaN\n", got)
        val (wrote, gotH) = serializeHeader(Double.NaN)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun char() {
        val got = serialize('☃')
        assertEquals("☃\n", got)
        val (wrote, gotH) = serializeHeader('☃')
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun str() {
        val got = serialize("how\nare\n\"you\"?")
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", got)
        val (wrote, gotH) = serializeHeader("how\nare\n\"you\"?")
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun bytes() {
        val got = serialize("how\nare\n\"you\"?".encodeToByteArray())
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", got)
        val (wrote, gotH) = serializeHeader("how\nare\n\"you\"?".encodeToByteArray())
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun option() {
        val got = serialize(null as String?)
        assertEquals("\n", got)
        val (wrote, gotH) = serializeHeader(null as String?)
        assertEquals(false, wrote)
        assertEquals("", gotH)

        val got2 = serialize(5)
        assertEquals("5\n", got2)
        val (wrote2, gotH2) = serializeHeader(5)
        assertEquals(false, wrote2)
        assertEquals("", gotH2)
    }

    @Test
    fun unit() {
        val got = serialize(Unit)
        assertEquals("\n", got)
        val (wrote, gotH) = serializeHeader(Unit)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun structUnit() {
        val got = serialize(Unit)
        assertEquals("\n", got)
        val (wrote, gotH) = serializeHeader(Unit)
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun structNewtype() {
        val got = serialize(FooNewtype(1.5))
        assertEquals("1.5\n", got)
        val (wrote, gotH) = serializeHeader(FooNewtype(1.5))
        assertEquals(true, wrote)
        assertEquals("value", gotH)
    }

    @Test
    fun enumUnits() {
        val gotFoo = serialize(Wat.Foo)
        assertEquals("Foo\n", gotFoo)
        val (wroteFoo, gotHFoo) = serializeHeader(Wat.Foo)
        assertEquals(false, wroteFoo)
        assertEquals("", gotHFoo)

        val gotBar = serialize(Wat.Bar)
        assertEquals("Bar\n", gotBar)
        val (wroteBar, gotHBar) = serializeHeader(Wat.Bar)
        assertEquals(false, wroteBar)
        assertEquals("", gotHBar)

        val gotBaz = serialize(Wat.Baz)
        assertEquals("Baz\n", gotBaz)
        val (wroteBaz, gotHBaz) = serializeHeader(Wat.Baz)
        assertEquals(false, wroteBaz)
        assertEquals("", gotHBaz)
    }

    @Test
    fun enumNewtypes() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.writeRecord(listOf("5")).getOrThrow()
        assertEquals("5\n", wtr.asString())
    }

    @Test
    fun seq() {
        val got = serialize(listOf(1, 2, 3))
        assertEquals("1,2,3\n", got)
        val (wrote, gotH) = serializeHeader(listOf(1, 2, 3))
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun tuple() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(true, 1.5, "hi")).getOrThrow()
        assertEquals("true,1.5,hi\n", wtr.asString())

        val (wrote, gotH) = serializeHeader(listOf("true", "1.5", "hi"))
        assertEquals(false, wrote)
        assertEquals("", gotH)
    }

    @Test
    fun tupleStruct() {
        val row = FooTuple(false, 42, "hi")
        val got = serialize(row)
        assertEquals("false,42,hi\n", got)
        val (wrote, gotH) = serializeHeader(row)
        assertEquals(true, wrote)
        assertEquals("a,b,c", gotH)
    }

    @Test
    fun tupleVariant() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = SeRecord(wtr).serializeTupleVariant("Foo", 0u, "X", 3)
        assertTrue(res.isFailure)
    }

    @Test
    fun enumStructVariant() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = SeRecord(wtr).serializeStructVariant("Foo", 0u, "X", 3)
        assertTrue(res.isFailure)
    }

    @Test
    fun structNoHeaders() {
        val wtr = WriterBuilder.new().hasHeaders(false).fromWriter()
        wtr.serializeNoHeaders(Foo(true, 5, "hi")).getOrThrow()
        assertEquals("true,5,hi\n", wtr.asString())
    }

    @Test
    fun structNoHeaders128() {
        val wtr = WriterBuilder.new().hasHeaders(false).fromWriter()
        wtr.serializeNoHeaders(Foo128(9223372036854775807L, 18446744073709551615UL)).getOrThrow()
        assertEquals("9223372036854775807,18446744073709551615\n", wtr.asString())
    }

    @Test
    fun structHeaders() {
        val row = Foo(true, 5, "hi")
        val (wrote, gotH) = serializeHeader(row)
        assertTrue(wrote)
        assertEquals("x,y,z", gotH)
        val got = serialize(row)
        assertEquals("true,5,hi\n", got)
    }

    @Test
    fun structHeadersNested() {
        val row = FooNested("foo", Nested("bar", 5))
        val got = serialize(row)
        assertEquals("foo,bar,5\n", got)
    }

    @Test
    fun structHeadersNestedSeq() {
        val row = FooSeq("foo", listOf(1, 2, 3))
        val got = serialize(row)
        assertEquals("foo,1,2,3\n", got)
    }

    @Test
    fun structHeadersInsideTuple() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(FooSimple("hi", 5.0), Bar(true, 3), FooSimple("baz", 2.3))).getOrThrow()
        assertTrue(wtr.asString().contains("hi"))
    }

    @Test
    fun structHeadersInsideTupleScalarBefore() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(3.14, FooSimple("hi", 5.0))).getOrThrow()
        assertTrue(wtr.asString().contains("3.14"))
    }

    @Test
    fun structHeadersInsideTupleScalarAfter() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(FooSimple("hi", 5.0), 3.14)).getOrThrow()
        assertTrue(wtr.asString().contains("3.14"))
    }

    @Test
    fun structHeadersInsideSeq() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(FooSimple("hi", 5.0), FooSimple("baz", 2.3))).getOrThrow()
        assertTrue(wtr.asString().contains("hi"))
    }

    @Test
    fun structHeadersInsideNestedTupleSeq() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf(FooSimple("hi", 5.0), Bar(true, 3), FooSimple("baz", 2.3))).getOrThrow()
        assertTrue(wtr.asString().contains("baz"))
    }
}
