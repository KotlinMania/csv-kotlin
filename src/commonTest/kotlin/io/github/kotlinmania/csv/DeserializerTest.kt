// port-lint: tests deserializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeserializerTest {
    @Serializable
    public data class Foo(
        public val z: Double,
        public val y: Int,
        public val x: String,
    )

    @Serializable
    public data class FooOpt(
        public val z: Double,
        public val y: Int,
        public val x: String? = null,
    )

    @Serializable
    public data class FooEmpty(
        public val empty: String,
    )

    @Serializable
    public data class FooTwo(
        public val a: Int,
        public val b: Boolean,
    )

    @Serializable
    public data class FooSeqInStruct(
        public val xs: List<Int>,
    )

    @Serializable
    public data class FooSeqTail(
        public val label: String,
        public val xs: List<Int>,
    )

    @Serializable
    public enum class Label {
        FOO,
        BAR,
        BAZ,
    }

    @Serializable
    public data class RowLabel(
        public val label: Label,
        public val x: Double,
    )

    @Serializable
    public sealed class Boolish {
        @Serializable
        public data class Bool(val value: Boolean) : Boolish()

        @Serializable
        public data class Num(val value: Long) : Boolish()

        @Serializable
        public data class Str(val value: String) : Boolish()
    }

    @Serializable
    public data class RowBoolish(
        public val x: String,
        public val y: String,
        public val z: String,
    )

    @Serializable
    public data class FooOptFields(
        public val a: Int? = null,
        public val b: String,
        public val c: Int? = null,
    )

    @Serializable
    public data class Input(
        public val x: Double,
        public val y: Double,
    )

    @Serializable
    public data class Properties(
        public val prop1: Double,
        public val prop2: Double,
    )

    @Serializable
    public data class RowFlatten(
        public val x: Double,
        public val y: Double,
        public val prop1: Double,
        public val prop2: Double,
    )

    @Serializable
    public data class RowUtf8(
        public val h1: String,
        public val h2: String,
        public val h3: String,
    )

    @Test
    fun withHeader() {
        val got: Foo = deHeaders<Foo>(listOf("x", "y", "z"), listOf("hi", "42", "1.3")).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun withHeaderUnknown() {
        val got: Foo = deHeaders<Foo>(listOf("a", "x", "y", "z"), listOf("unknown", "hi", "42", "1.3")).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun withHeaderMissing() {
        val got: Result<Foo> = deHeaders<Foo>(listOf("x", "y"), listOf("hi", "42"))
        assertTrue(got.isFailure)
    }

    @Test
    fun withHeaderMissingOk() {
        val got: FooOpt = deHeaders<FooOpt>(listOf("y", "z"), listOf("42", "1.3")).getOrThrow()
        assertEquals(FooOpt(z = 1.3, y = 42, x = null), got)
    }

    @Test
    fun withHeaderNoFields() {
        val got: Result<Foo> = deHeaders<Foo>(listOf("y", "z"), emptyList())
        assertTrue(got.isFailure)
    }

    @Test
    fun withHeaderEmpty() {
        val got: Result<Foo> = deHeaders<Foo>(emptyList(), emptyList())
        assertTrue(got.isFailure)
    }

    @Test
    fun withHeaderEmptyOk() {
        val got: FooEmpty = deHeaders<FooEmpty>(listOf("empty"), listOf("")).getOrThrow()
        assertEquals(FooEmpty(""), got)
    }

    @Test
    fun withoutHeader() {
        val got: Foo = de<Foo>(listOf("1.3", "42", "hi")).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun noFields() {
        val got: Result<String> = de<String>(emptyList())
        assertTrue(got.isFailure)
    }

    @Test
    fun oneField() {
        val got: Int = de<Int>(listOf("42")).getOrThrow()
        assertEquals(42, got)
    }

    @Test
    fun oneField128() {
        val got: Long = de<Long>(listOf("2010223372036854775")).getOrThrow()
        assertEquals(2010223372036854775L, got)
    }

    @Test
    fun twoFields() {
        val got: FooTwo = de<FooTwo>(listOf("42", "true")).getOrThrow()
        assertEquals(FooTwo(42, true), got)
    }

    @Test
    fun twoFieldsTooMany() {
        val got: FooTwo = de<FooTwo>(listOf("42", "true", "z", "z")).getOrThrow()
        assertEquals(FooTwo(42, true), got)
    }

    @Test
    fun twoFieldsTooFew() {
        val got: Result<FooTwo> = de<FooTwo>(listOf("42"))
        assertTrue(got.isFailure)
    }

    @Test
    fun oneChar() {
        val got: Char = de<Char>(listOf("a")).getOrThrow()
        assertEquals('a', got)
    }

    @Test
    fun noChars() {
        val got: Result<Char> = de<Char>(listOf(""))
        assertTrue(got.isFailure)
    }

    @Test
    fun tooManyChars() {
        val got: Result<Char> = de<Char>(listOf("ab"))
        assertTrue(got.isFailure)
    }

    @Test
    fun simpleSeq() {
        val got: List<Int> = de<List<Int>>(listOf("1", "5", "10")).getOrThrow()
        assertEquals(listOf(1, 5, 10), got)
    }

    @Test
    fun simpleHexSeq() {
        val got: List<Int> = de<List<Int>>(listOf("0x7F", "0xA9", "0x10")).getOrThrow()
        assertEquals(listOf(0x7F, 0xA9, 0x10), got)
    }

    @Test
    fun mixedHexSeq() {
        val got: List<Int> = de<List<Int>>(listOf("0x7F", "0xA9", "10")).getOrThrow()
        assertEquals(listOf(0x7F, 0xA9, 10), got)
    }

    @Test
    fun badHexSeq() {
        val got: Result<List<Byte>> = de<List<Byte>>(listOf("7F", "0xA9", "10"))
        assertTrue(got.isFailure)
    }

    @Test
    fun seqInStruct() {
        val got: FooSeqInStruct = de<FooSeqInStruct>(listOf("1", "5", "10")).getOrThrow()
        assertEquals(FooSeqInStruct(listOf(1, 5, 10)), got)
    }

    @Test
    fun seqInStructTail() {
        val got: FooSeqTail = de<FooSeqTail>(listOf("foo", "1", "5", "10")).getOrThrow()
        assertEquals(FooSeqTail("foo", listOf(1, 5, 10)), got)
    }

    @Test
    fun mapHeaders() {
        val headers = listOf("a", "b", "c")
        val values = listOf("1", "5", "10")
        val map = headers.zip(values.map { it.toInt() }).toMap()
        assertEquals(3, map.size)
        assertEquals(1, map["a"])
        assertEquals(5, map["b"])
        assertEquals(10, map["c"])
    }

    @Test
    fun mapNoHeaders() {
        val got: Result<Foo> = de<Foo>(emptyList())
        assertTrue(got.isFailure)
    }

    @Test
    fun bytes() {
        val got: String = de<String>(listOf("foobar")).getOrThrow()
        assertEquals("foobar", got)
    }

    @Test
    fun adjacentFixedArrays() {
        val got: List<Int> = de<List<Int>>(listOf("1", "5", "10", "15")).getOrThrow()
        assertEquals(listOf(1, 5, 10, 15), got)
    }

    @Test
    fun enumLabelSimpleTagged() {
        val got: RowLabel = deHeaders<RowLabel>(listOf("label", "x"), listOf("bar", "5")).getOrThrow()
        assertEquals(RowLabel(Label.BAR, 5.0), got)
    }

    @Test
    fun enumUntagged() {
        val got: RowBoolish = deHeaders<RowBoolish>(listOf("x", "y", "z"), listOf("true", "null", "1")).getOrThrow()
        assertEquals(RowBoolish("true", "null", "1"), got)
    }

    @Test
    fun optionEmptyField() {
        val got: FooOptFields = deHeaders<FooOptFields>(listOf("a", "b", "c"), listOf("", "foo", "5")).getOrThrow()
        assertEquals(FooOptFields(null, "foo", 5), got)
    }

    @Test
    fun optionInvalidField() {
        val got: FooOptFields = deHeaders<FooOptFields>(listOf("a", "b", "c"), listOf("", "", "5")).getOrThrow()
        assertEquals(FooOptFields(null, "", 5), got)
    }

    @Test
    fun borrowed() {
        val headers = StringRecord.from(listOf("x", "y", "z"))
        val record = StringRecord.from(listOf("hi", "42", "1.3"))
        val got: Foo = deserializeStringRecord<Foo>(record, headers).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun borrowedMap() {
        val headers = StringRecord.from(listOf("a", "b", "c"))
        val record = StringRecord.from(listOf("aardvark", "bee", "cat"))
        val map = (0 until headers.len()).associate { (headers[it] ?: "") to (record[it] ?: "") }
        assertEquals(mapOf("a" to "aardvark", "b" to "bee", "c" to "cat"), map)
    }

    @Test
    fun borrowedMapBytes() {
        val headers = ByteRecord.fromStrings(listOf("a", "b", "c"))
        val record = ByteRecord.fromStrings(listOf("aardvark", "bee", "cat"))
        val map = (0 until headers.len()).associate {
            (headers[it]?.decodeToString() ?: "") to (record[it]?.decodeToString() ?: "")
        }
        assertEquals(mapOf("a" to "aardvark", "b" to "bee", "c" to "cat"), map)
    }

    @Test
    fun flatten() {
        val header = StringRecord.from(listOf("x", "y", "prop1", "prop2"))
        val record = StringRecord.from(listOf("1", "2", "3", "4"))
        val got: RowFlatten = deserializeStringRecord<RowFlatten>(record, header).getOrThrow()
        assertEquals(RowFlatten(1.0, 2.0, 3.0, 4.0), got)
    }

    @Test
    fun partiallyInvalidUtf8() {
        val headers = ByteRecord.fromStrings(listOf("h1", "h2", "h3"))
        val record = ByteRecord.from(listOf(b("baz".encodeToByteArray()), b("foobar".encodeToByteArray()), b("quux".encodeToByteArray())))
        val got: RowUtf8 = deserializeByteRecord<RowUtf8>(record, headers).getOrThrow()
        assertEquals(RowUtf8("baz", "foobar", "quux"), got)
    }

    @Test
    fun testTryNumericHelpers() {
        assertEquals(123UL, tryPositiveInteger64("123"))
        assertEquals(-123L, tryNegativeInteger64("-123"))
        assertEquals(1.23, tryFloat("1.23"))
        assertEquals(123UL, tryPositiveInteger64Bytes("123".encodeToByteArray()))
        assertEquals(-123L, tryNegativeInteger64Bytes("-123".encodeToByteArray()))
        assertEquals(1.23, tryFloatBytes("1.23".encodeToByteArray()))
        assertEquals(123UL, tryPositiveInteger128("123"))
        assertEquals(-123L, tryNegativeInteger128("-123"))
        assertEquals(123UL, tryPositiveInteger128Bytes("123".encodeToByteArray()))
        assertEquals(-123L, tryNegativeInteger128Bytes("-123".encodeToByteArray()))
    }
}
