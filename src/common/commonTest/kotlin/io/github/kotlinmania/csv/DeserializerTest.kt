// port-lint: tests deserializer.rs
package io.github.kotlinmania.csv

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.BooleanDeserialize
import io.github.kotlinmania.serdecore.de.CharDeserialize
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.Expected
import io.github.kotlinmania.serdecore.de.F64Deserialize
import io.github.kotlinmania.serdecore.de.I32Deserialize
import io.github.kotlinmania.serdecore.de.IgnoredAny
import io.github.kotlinmania.serdecore.de.MapAccess
import io.github.kotlinmania.serdecore.de.SeqAccess
import io.github.kotlinmania.serdecore.de.SeqElement
import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdecore.de.U32Deserialize
import io.github.kotlinmania.serdecore.de.U8Deserialize
import io.github.kotlinmania.serdecore.de.UnitDeserialize
import io.github.kotlinmania.serdecore.de.Visitor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeserializerTest {
    private fun <D> de(deserializer: Deserialize<D>, fields: List<String>): Result<D> {
        val record = StringRecord.from(fields)
        return deserializeStringRecord(record, deserializer, null)
    }

    private fun <D> deHeaders(
        deserializer: Deserialize<D>,
        headers: List<String>,
        fields: List<String>,
    ): Result<D> {
        val headersRec = StringRecord.from(headers)
        val record = StringRecord.from(fields)
        return deserializeStringRecord(record, deserializer, headersRec)
    }

    private fun b(bytes: ByteArray): ByteArray = bytes

    private data class Foo(
        val z: Double,
        val y: Int,
        val x: String,
    ) {
        companion object : Deserialize<Foo> {
            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Foo> {
                return deserializer.deserializeStruct(
                    "Foo",
                    listOf("z", "y", "x"),
                    object : Visitor<Foo> {
                        override fun expecting(): String = "struct Foo"

                        override fun <M : MapAccess> visitMap(access: M): SerdeResult<Foo> {
                            var z: Double? = null
                            var y: Int? = null
                            var x: String? = null
                            while (true) {
                                val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                when (key) {
                                    "z" -> z = access.nextValue(F64Deserialize).getOrElse { return SerdeResult.failure(it) }
                                    "y" -> y = access.nextValue(I32Deserialize).getOrElse { return SerdeResult.failure(it) }
                                    "x" -> x = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                    else -> access.nextValue(IgnoredAny).getOrElse { return SerdeResult.failure(it) }
                                }
                            }
                            if (z == null || y == null || x == null) {
                                return SerdeResult.failure(SerdeError.missingField("field missing"))
                            }
                            return SerdeResult.success(Foo(z, y, x))
                        }

                        override fun <S : SeqAccess> visitSeq(access: S): SerdeResult<Foo> {
                            val z =
                                when (val el = access.nextElement(F64Deserialize).getOrElse { return SerdeResult.failure(it) }) {
                                    is SeqElement.Some -> el.value
                                    SeqElement.None -> return SerdeResult.failure(SerdeError.invalidLength(0, Expected { "3 elements" }))
                                }
                            val y =
                                when (val el = access.nextElement(I32Deserialize).getOrElse { return SerdeResult.failure(it) }) {
                                    is SeqElement.Some -> el.value
                                    SeqElement.None -> return SerdeResult.failure(SerdeError.invalidLength(1, Expected { "3 elements" }))
                                }
                            val x =
                                when (val el = access.nextElement(StringDeserialize).getOrElse { return SerdeResult.failure(it) }) {
                                    is SeqElement.Some -> el.value
                                    SeqElement.None -> return SerdeResult.failure(SerdeError.invalidLength(2, Expected { "3 elements" }))
                                }
                            return SerdeResult.success(Foo(z, y, x))
                        }
                    },
                )
            }
        }
    }

    private data class FooOpt(
        val z: Double,
        val y: Int,
        val x: String?,
    ) {
        companion object : Deserialize<FooOpt> {
            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<FooOpt> {
                return deserializer.deserializeStruct(
                    "Foo",
                    listOf("z", "y", "x"),
                    object : Visitor<FooOpt> {
                        override fun expecting(): String = "struct Foo"

                        override fun <M : MapAccess> visitMap(access: M): SerdeResult<FooOpt> {
                            var z: Double? = null
                            var y: Int? = null
                            var x: String? = null
                            while (true) {
                                val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                when (key) {
                                    "z" -> z = access.nextValue(F64Deserialize).getOrElse { return SerdeResult.failure(it) }
                                    "y" -> y = access.nextValue(I32Deserialize).getOrElse { return SerdeResult.failure(it) }
                                    "x" -> x = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                    else -> access.nextValue(IgnoredAny).getOrElse { return SerdeResult.failure(it) }
                                }
                            }
                            if (z == null || y == null) {
                                return SerdeResult.failure(SerdeError.missingField("field missing"))
                            }
                            return SerdeResult.success(FooOpt(z, y, x))
                        }
                    },
                )
            }
        }
    }

    private data object FooUnit : Deserialize<FooUnit> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<FooUnit> =
            deserializer.deserializeUnit(
                object : Visitor<FooUnit> {
                    override fun expecting(): String = "unit"

                    override fun visitUnit(): SerdeResult<FooUnit> = SerdeResult.success(FooUnit)
                },
            )
    }

    private class PairDeserialize<A, B>(
        val deA: Deserialize<A>,
        val deB: Deserialize<B>,
    ) : Deserialize<Pair<A, B>> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Pair<A, B>> {
            return deserializer.deserializeTuple(
                2,
                object : Visitor<Pair<A, B>> {
                    override fun expecting(): String = "tuple of 2"

                    override fun <S : SeqAccess> visitSeq(access: S): SerdeResult<Pair<A, B>> {
                        val a =
                            when (val el = access.nextElement(deA).getOrElse { return SerdeResult.failure(it) }) {
                                is SeqElement.Some -> el.value
                                SeqElement.None -> return SerdeResult.failure(SerdeError.invalidLength(0, Expected { "2 elements" }))
                            }
                        val b =
                            when (val el = access.nextElement(deB).getOrElse { return SerdeResult.failure(it) }) {
                                is SeqElement.Some -> el.value
                                SeqElement.None -> return SerdeResult.failure(SerdeError.invalidLength(1, Expected { "2 elements" }))
                            }
                        return SerdeResult.success(a to b)
                    }
                },
            )
        }
    }

    private class ListDeserialize<T>(
        val deT: Deserialize<T>,
    ) : Deserialize<List<T>> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<List<T>> {
            return deserializer.deserializeSeq(
                object : Visitor<List<T>> {
                    override fun expecting(): String = "sequence"

                    override fun <S : SeqAccess> visitSeq(access: S): SerdeResult<List<T>> {
                        val list = mutableListOf<T>()
                        while (true) {
                            when (val el = access.nextElement(deT).getOrElse { return SerdeResult.failure(it) }) {
                                is SeqElement.Some -> list.add(el.value)
                                SeqElement.None -> break
                            }
                        }
                        return SerdeResult.success(list)
                    }
                },
            )
        }
    }

    private class MapDeserialize<K, V>(
        val deK: Deserialize<K>,
        val deV: Deserialize<V>,
    ) : Deserialize<Map<K, V>> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Map<K, V>> {
            return deserializer.deserializeMap(
                object : Visitor<Map<K, V>> {
                    override fun expecting(): String = "map"

                    override fun <M : MapAccess> visitMap(access: M): SerdeResult<Map<K, V>> {
                        val res = mutableMapOf<K, V>()
                        while (true) {
                            val key = access.nextKey(deK).getOrElse { return SerdeResult.failure(it) } ?: break
                            val value = access.nextValue(deV).getOrElse { return SerdeResult.failure(it) }
                            res[key] = value
                        }
                        return SerdeResult.success(res)
                    }
                },
            )
        }
    }

    private data class FooOptFields(
        val a: Int?,
        val b: String,
        val c: Int?,
    ) {
        companion object : Deserialize<FooOptFields> {
            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<FooOptFields> {
                return deserializer.deserializeStruct(
                    "Foo",
                    listOf("a", "b", "c"),
                    object : Visitor<FooOptFields> {
                        override fun expecting(): String = "struct Foo"

                        override fun <M : MapAccess> visitMap(access: M): SerdeResult<FooOptFields> {
                            var a: Int? = null
                            var b: String? = null
                            var c: Int? = null
                            while (true) {
                                val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                when (key) {
                                    "a" -> a = access.nextValue(StringDeserialize).getOrNull()?.toIntOrNull()
                                    "b" -> b = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                    "c" -> c = access.nextValue(StringDeserialize).getOrNull()?.toIntOrNull()
                                    else -> access.nextValue(IgnoredAny).getOrElse { return SerdeResult.failure(it) }
                                }
                            }
                            return SerdeResult.success(FooOptFields(a, b.orEmpty(), c))
                        }
                    },
                )
            }
        }
    }

    enum class Label {
        Foo,
        Bar,
        Baz,
        ;

        companion object : Deserialize<Label> {
            private val labelNameSeed =
                object : io.github.kotlinmania.serdecore.de.DeserializeSeed<String> {
                    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<String> =
                        StringDeserialize.deserialize(deserializer)
                }

            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Label> {
                return deserializer.deserializeEnum(
                    "Label",
                    listOf("foo", "bar", "baz"),
                    object : Visitor<Label> {
                        override fun expecting(): String = "enum Label"

                        override fun <E : EnumAccess> visitEnum(access: E): SerdeResult<Label> {
                            val (name, _) =
                                access
                                    .variantSeed(labelNameSeed)
                                    .getOrElse { return SerdeResult.failure(it) }
                            return when (name.lowercase()) {
                                "foo" -> SerdeResult.success(Foo)
                                "bar" -> SerdeResult.success(Bar)
                                "baz" -> SerdeResult.success(Baz)
                                else -> SerdeResult.failure(SerdeError.unknownVariant(name, listOf("foo", "bar", "baz")))
                            }
                        }
                    },
                )
            }
        }
    }

    private data class RowLabel(
        val label: Label,
        val x: Double,
    ) {
        companion object : Deserialize<RowLabel> {
            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<RowLabel> {
                return deserializer.deserializeStruct(
                    "Row",
                    listOf("label", "x"),
                    object : Visitor<RowLabel> {
                        override fun expecting(): String = "struct Row"

                        override fun <M : MapAccess> visitMap(access: M): SerdeResult<RowLabel> {
                            var label: Label? = null
                            var x: Double? = null
                            while (true) {
                                val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                when (key) {
                                    "label" -> label = access.nextValue(Label).getOrElse { return SerdeResult.failure(it) }
                                    "x" -> x = access.nextValue(F64Deserialize).getOrElse { return SerdeResult.failure(it) }
                                    else -> access.nextValue(IgnoredAny).getOrElse { return SerdeResult.failure(it) }
                                }
                            }
                            if (label == null || x == null) {
                                return SerdeResult.failure(SerdeError.missingField("field missing"))
                            }
                            return SerdeResult.success(RowLabel(label, x))
                        }
                    },
                )
            }
        }
    }

    @Test
    fun withHeader() {
        val got: Foo = deHeaders(Foo, listOf("x", "y", "z"), listOf("hi", "42", "1.3")).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun withHeaderMissing() {
        val res = deHeaders(Foo, listOf("y", "z"), listOf("42", "1.3"))
        assertTrue(res.isFailure)
    }

    @Test
    fun withHeaderMissingOk() {
        val got: FooOpt = deHeaders(FooOpt, listOf("y", "z"), listOf("42", "1.3")).getOrThrow()
        assertEquals(FooOpt(z = 1.3, y = 42, x = null), got)
    }

    @Test
    fun withHeaderNoFields() {
        val res = deHeaders(FooOpt, listOf("y", "z"), emptyList())
        assertTrue(res.isFailure)
    }

    @Test
    fun withHeaderEmpty() {
        val res = deHeaders(FooOpt, emptyList(), emptyList())
        assertTrue(res.isFailure)
    }

    @Test
    fun withHeaderEmptyOk() {
        val got = deHeaders(FooUnit, emptyList(), emptyList()).getOrThrow()
        assertEquals(FooUnit, got)

        val gotUnit = deHeaders(UnitDeserialize, emptyList(), emptyList()).getOrThrow()
        assertEquals(Unit, gotUnit)
    }

    @Test
    fun withoutHeader() {
        val got: Foo = de(Foo, listOf("1.3", "42", "hi")).getOrThrow()
        assertEquals(Foo(z = 1.3, y = 42, x = "hi"), got)
    }

    @Test
    fun noFields() {
        val res = de(StringDeserialize, emptyList())
        assertTrue(res.isFailure)
    }

    @Test
    fun oneField() {
        val got = de(I32Deserialize, listOf("42")).getOrThrow()
        assertEquals(42, got)
    }

    @Test
    fun oneField128() {
        val got = de(StringDeserialize, listOf("2010223372036854775808")).getOrThrow()
        assertEquals("2010223372036854775808", got)
    }

    @Test
    fun twoFields() {
        val got = de(PairDeserialize(I32Deserialize, BooleanDeserialize), listOf("42", "true")).getOrThrow()
        assertEquals(Pair(42, true), got)
    }

    @Test
    fun twoFieldsTooMany() {
        val got = de(PairDeserialize(I32Deserialize, BooleanDeserialize), listOf("42", "true", "z", "z")).getOrThrow()
        assertEquals(Pair(42, true), got)
    }

    @Test
    fun twoFieldsTooFew() {
        val res = de(PairDeserialize(I32Deserialize, BooleanDeserialize), listOf("42"))
        assertTrue(res.isFailure)
    }

    @Test
    fun oneChar() {
        val got = de(CharDeserialize, listOf("a")).getOrThrow()
        assertEquals('a', got)
    }

    @Test
    fun noChars() {
        val res = de(CharDeserialize, listOf(""))
        assertTrue(res.isFailure)
    }

    @Test
    fun tooManyChars() {
        val res = de(CharDeserialize, listOf("ab"))
        assertTrue(res.isFailure)
    }

    @Test
    fun simpleSeq() {
        val got = de(ListDeserialize(I32Deserialize), listOf("1", "5", "10")).getOrThrow()
        assertEquals(listOf(1, 5, 10), got)
    }

    @Test
    fun simpleHexSeq() {
        val got = de(ListDeserialize(I32Deserialize), listOf("0x7F", "0xA9", "0x10")).getOrThrow()
        assertEquals(listOf(0x7F, 0xA9, 0x10), got)
    }

    @Test
    fun mixedHexSeq() {
        val got = de(ListDeserialize(I32Deserialize), listOf("0x7F", "0xA9", "10")).getOrThrow()
        assertEquals(listOf(0x7F, 0xA9, 10), got)
    }

    @Test
    fun badHexSeq() {
        val res = de(ListDeserialize(U8Deserialize), listOf("7F", "0xA9", "10"))
        assertTrue(res.isFailure)
    }

    @Test
    fun mapHeaders() {
        val got = deHeaders(MapDeserialize(StringDeserialize, I32Deserialize), listOf("a", "b", "c"), listOf("1", "5", "10")).getOrThrow()
        assertEquals(3, got.size)
        assertEquals(1, got["a"])
        assertEquals(5, got["b"])
        assertEquals(10, got["c"])
    }

    @Test
    fun mapNoHeaders() {
        val res = de(MapDeserialize(StringDeserialize, I32Deserialize), listOf("1", "5", "10"))
        assertTrue(res.isFailure)
    }

    @Test
    fun bytes() {
        val got = de(StringDeserialize, listOf("foobar")).getOrThrow()
        assertEquals("foobar", got)
    }

    @Test
    fun adjacentFixedArrays() {
        val got =
            de(
                PairDeserialize(
                    PairDeserialize(U32Deserialize, U32Deserialize),
                    PairDeserialize(U32Deserialize, U32Deserialize),
                ),
                listOf("1", "5", "10", "15"),
            ).getOrThrow()
        assertEquals(Pair(Pair(1u, 5u), Pair(10u, 15u)), got)
    }

    @Test
    fun enumLabelSimpleTagged() {
        val got = deHeaders(RowLabel, listOf("label", "x"), listOf("bar", "5")).getOrThrow()
        assertEquals(RowLabel(Label.Bar, 5.0), got)
    }

    @Test
    fun optionEmptyField() {
        val got = deHeaders(FooOptFields, listOf("a", "b", "c"), listOf("", "foo", "5")).getOrThrow()
        assertEquals(FooOptFields(null, "foo", 5), got)
    }

    @Test
    fun optionInvalidField() {
        val got = deHeaders(FooOptFields, listOf("a", "b", "c"), listOf("xyz", "", "5")).getOrThrow()
        assertEquals(FooOptFields(null, "", 5), got)
    }

    @Test
    fun withHeaderUnknown() {
        val denyUnknown =
            object : Deserialize<Foo> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Foo> =
                    deserializer.deserializeStruct(
                        "Foo",
                        listOf("z", "y", "x"),
                        object : Visitor<Foo> {
                            override fun expecting(): String = "struct Foo"

                            override fun <M : MapAccess> visitMap(access: M): SerdeResult<Foo> {
                                var z: Double? = null
                                var y: Int? = null
                                var x: String? = null
                                while (true) {
                                    val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                    when (key) {
                                        "z" -> z = access.nextValue(F64Deserialize).getOrElse { return SerdeResult.failure(it) }
                                        "y" -> y = access.nextValue(I32Deserialize).getOrElse { return SerdeResult.failure(it) }
                                        "x" -> x = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                        else -> return SerdeResult.failure(SerdeError.unknownField(key, listOf("z", "y", "x")))
                                    }
                                }
                                val finalZ = z ?: return SerdeResult.failure(SerdeError.missingField("z"))
                                val finalY = y ?: return SerdeResult.failure(SerdeError.missingField("y"))
                                val finalX = x ?: return SerdeResult.failure(SerdeError.missingField("x"))
                                return SerdeResult.success(Foo(z = finalZ, y = finalY, x = finalX))
                            }
                        },
                    )
            }
        val res = deHeaders(denyUnknown, listOf("a", "x", "y", "z"), listOf("foo", "hi", "42", "1.3"))
        assertTrue(res.isFailure)
    }

    @Test
    fun seqInStruct() {
        val seqStruct =
            object : Deserialize<List<Int>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<List<Int>> =
                    deserializer.deserializeStruct(
                        "Foo",
                        listOf("xs"),
                        object : Visitor<List<Int>> {
                            override fun expecting(): String = "struct Foo"

                            override fun <S : SeqAccess> visitSeq(access: S): SerdeResult<List<Int>> {
                                val xs =
                                    when (val elem = access.nextElement(ListDeserialize(I32Deserialize)).getOrElse { return SerdeResult.failure(it) }) {
                                        is SeqElement.Some -> elem.value
                                        is SeqElement.None -> return SerdeResult.failure(SerdeError.missingField("xs"))
                                    }
                                return SerdeResult.success(xs)
                            }
                        },
                    )
            }
        val got = de(seqStruct, listOf("1", "5", "10")).getOrThrow()
        assertEquals(listOf(1, 5, 10), got)
    }

    @Test
    fun seqInStructTail() {
        val seqTail =
            object : Deserialize<Pair<String, List<Int>>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Pair<String, List<Int>>> =
                    deserializer.deserializeStruct(
                        "Foo",
                        listOf("label", "xs"),
                        object : Visitor<Pair<String, List<Int>>> {
                            override fun expecting(): String = "struct Foo"

                            override fun <S : SeqAccess> visitSeq(access: S): SerdeResult<Pair<String, List<Int>>> {
                                val label =
                                    when (val elem = access.nextElement(StringDeserialize).getOrElse { return SerdeResult.failure(it) }) {
                                        is SeqElement.Some -> elem.value
                                        is SeqElement.None -> return SerdeResult.failure(SerdeError.missingField("label"))
                                    }
                                val xs =
                                    when (val elem = access.nextElement(ListDeserialize(I32Deserialize)).getOrElse { return SerdeResult.failure(it) }) {
                                        is SeqElement.Some -> elem.value
                                        is SeqElement.None -> return SerdeResult.failure(SerdeError.missingField("xs"))
                                    }
                                return SerdeResult.success(label to xs)
                            }
                        },
                    )
            }
        val got = de(seqTail, listOf("foo", "1", "5", "10")).getOrThrow()
        assertEquals("foo" to listOf(1, 5, 10), got)
    }

    @Test
    fun partiallyInvalidUtf8() {
        val bytesDeserialize =
            object : Deserialize<List<Byte>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<List<Byte>> =
                    deserializer.deserializeBytes(
                        object : Visitor<List<Byte>> {
                            override fun expecting(): String = "byte buffer"

                            override fun visitBytes(v: ByteArray): SerdeResult<List<Byte>> = SerdeResult.success(v.toList())
                        },
                    )
            }
        val rowDeserialize =
            object : Deserialize<Triple<String, List<Byte>, String>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Triple<String, List<Byte>, String>> =
                    deserializer.deserializeStruct(
                        "Row",
                        listOf("h1", "h2", "h3"),
                        object : Visitor<Triple<String, List<Byte>, String>> {
                            override fun expecting(): String = "struct Row"

                            override fun <M : MapAccess> visitMap(access: M): SerdeResult<Triple<String, List<Byte>, String>> {
                                var h1: String? = null
                                var h2: List<Byte>? = null
                                var h3: String? = null
                                while (true) {
                                    val key = access.nextKey(StringDeserialize).getOrElse { return SerdeResult.failure(it) } ?: break
                                    when (key) {
                                        "h1" -> h1 = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                        "h2" -> h2 = access.nextValue(bytesDeserialize).getOrElse { return SerdeResult.failure(it) }
                                        "h3" -> h3 = access.nextValue(StringDeserialize).getOrElse { return SerdeResult.failure(it) }
                                        else -> access.nextValue(IgnoredAny).getOrElse { return SerdeResult.failure(it) }
                                    }
                                }
                                val r1 = h1 ?: return SerdeResult.failure(SerdeError.missingField("h1"))
                                val r2 = h2 ?: return SerdeResult.failure(SerdeError.missingField("h2"))
                                val r3 = h3 ?: return SerdeResult.failure(SerdeError.missingField("h3"))
                                return SerdeResult.success(Triple(r1, r2, r3))
                            }
                        },
                    )
            }
        val headers = ByteRecord.from(listOf("h1".encodeToByteArray(), "h2".encodeToByteArray(), "h3".encodeToByteArray()))
        val rawMid =
            byteArrayOf(
                'f'.code.toByte(),
                'o'.code.toByte(),
                'o'.code.toByte(),
                0xFF.toByte(),
                'b'.code.toByte(),
                'a'.code.toByte(),
                'r'.code.toByte(),
            )
        val record = ByteRecord.from(listOf(b("baz".encodeToByteArray()), rawMid, b("quux".encodeToByteArray())))
        val got = deserializeByteRecord(record, rowDeserialize, headers).getOrThrow()
        assertEquals(Triple("baz", rawMid.toList(), "quux"), got)
    }

    // Rust test parity notes (§6):
    // - enum_untagged: relies on #[serde(untagged)] buffered Content deserialization generated by serde_derive.
    // - borrowed, borrowed_map, borrowed_map_bytes: test Rust 'de zero-copy borrow lifetimes (&'a str / &'a [u8]);
    //   Kotlin Strings and ByteArrays are heap values rather than borrowed record slices.
    // - flatten: relies on #[serde(flatten)] map-merging codegen from serde_derive.
}
