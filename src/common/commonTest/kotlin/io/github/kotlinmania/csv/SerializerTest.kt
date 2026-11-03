// port-lint: tests serializer.rs
package io.github.kotlinmania.csv

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SerializerTest {
    companion object {
        private fun s(v: Boolean): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeBool(v)
            }

        private fun s(v: Byte): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeI8(v)
            }

        private fun s(v: Short): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeI16(v)
            }

        private fun s(v: Int): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeI32(v)
            }

        private fun s(v: Long): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeI64(v)
            }

        private fun s(v: ULong): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeU64(v)
            }

        private fun sI128(v: String): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeI128(v)
            }

        private fun sU128(v: String): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeU128(v)
            }

        private fun s(v: Double): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeF64(v)
            }

        private fun s(v: Float): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeF32(v)
            }

        private fun s(v: Char): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeChar(v)
            }

        private fun s(v: String): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeStr(v)
            }

        private fun s(v: ByteArray): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeBytes(v)
            }

        private fun sUnit(): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeUnit()
            }

        private fun sNone(): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeNone()
            }

        private fun sSome(v: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeSome(v)
            }

        private fun sUnitStruct(name: String): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeUnitStruct(name)
            }

        private fun sNewtypeStruct(name: String, value: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeNewtypeStruct(name, value)
            }

        private fun sUnitVariant(name: String, index: UInt, variant: String): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeUnitVariant(name, index, variant)
            }

        private fun sNewtypeVariant(name: String, index: UInt, variant: String, value: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>) = serializer.serializeNewtypeVariant(name, index, variant, value)
            }

        private fun sSeq(vararg items: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
                    val seq = serializer.serializeSeq(items.size).getOrElse { return SerdeResult.failure(it) }
                    for (item in items) {
                        seq.serializeElement(item).getOrElse { return SerdeResult.failure(it) }
                    }
                    return seq.end()
                }
            }

        private fun sTuple(vararg items: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
                    val tup = serializer.serializeTuple(items.size).getOrElse { return SerdeResult.failure(it) }
                    for (item in items) {
                        tup.serializeElement(item).getOrElse { return SerdeResult.failure(it) }
                    }
                    return tup.end()
                }
            }

        private fun sTupleStruct(name: String, vararg items: Serialize): Serialize =
            object : Serialize {
                override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
                    val tup = serializer.serializeTupleStruct(name, items.size).getOrElse { return SerdeResult.failure(it) }
                    for (item in items) {
                        tup.serializeField(item).getOrElse { return SerdeResult.failure(it) }
                    }
                    return tup.end()
                }
            }
    }

    private data class Foo(
        val x: Boolean,
        val y: Int,
        val z: String,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Foo", 3).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("x", s(x)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("y", s(y)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("z", s(z)).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class Foo128(
        val x: String,
        val y: String,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Foo", 2).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("x", sI128(x)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("y", sU128(y)).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class Nested(
        val label2: String,
        val value: Int,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Nested", 2).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("label2", s(label2)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("value", s(value)).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class FooNested(
        val label: String,
        val nest: Nested,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Foo", 2).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("label", s(label)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("nest", nest).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class FooSeq(
        val label: String,
        val values: List<Int>,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Foo", 2).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("label", s(label)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("values", sSeq(*values.map { s(it) }.toTypedArray())).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class FooSimple(
        val label: String,
        val num: Double,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Foo", 2).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("label", s(label)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("num", s(num)).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private data class Bar(
        val label2: Boolean,
        val value: Int,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
            val st = serializer.serializeStruct("Bar", 3).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("label2", s(label2)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("value", s(value)).getOrElse { return SerdeResult.failure(it) }
            st.serializeField("empty", sUnit()).getOrElse { return SerdeResult.failure(it) }
            return st.end()
        }
    }

    private fun serialize(s: Serialize): String {
        val wtr = WriterBuilder.new().fromWriter()
        io.github.kotlinmania.csv
            .serialize(wtr, s)
            .getOrThrow()
        wtr.writeRecord(emptyList<String>()).getOrThrow()
        return wtr.asString()
    }

    private fun serializeHeader(s: Serialize): Pair<Boolean, String> {
        val wtr = WriterBuilder.new().fromWriter()
        val wrote =
            io.github.kotlinmania.csv
                .serializeHeader(wtr, s)
                .getOrThrow()
        return Pair(wrote, wtr.asString())
    }

    private fun serializeErr(s: Serialize): CsvError {
        val wtr = WriterBuilder.new().fromWriter()
        val res =
            io.github.kotlinmania.csv
                .serialize(wtr, s)
        return res.exceptionOrNull() as CsvError
    }

    private fun serializeHeaderErr(s: Serialize): CsvError {
        val wtr = WriterBuilder.new().fromWriter()
        val res =
            io.github.kotlinmania.csv
                .serializeHeader(wtr, s)
        return res.exceptionOrNull() as CsvError
    }

    @Test
    fun bool() {
        val got = serialize(s(true))
        assertEquals("true\n", got)
        val (wrote, gotH) = serializeHeader(s(true))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integer() {
        val got = serialize(s(12345))
        assertEquals("12345\n", got)
        val (wrote, gotH) = serializeHeader(s(12345))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integerU128() {
        val got = serialize(sU128("170141183460469231731687303715884105728"))
        assertEquals("170141183460469231731687303715884105728\n", got)
        val (wrote, gotH) = serializeHeader(s(12345))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun integerI128() {
        val got = serialize(sI128("170141183460469231731687303715884105727"))
        assertEquals("170141183460469231731687303715884105727\n", got)
        val (wrote, gotH) = serializeHeader(s(12345))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun float() {
        val got = serialize(s(1.23))
        assertEquals("1.23\n", got)
        val (wrote, gotH) = serializeHeader(s(1.23))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun floatNan() {
        val got = serialize(s(Double.NaN))
        assertEquals("NaN\n", got)
        val (wrote, gotH) = serializeHeader(s(Double.NaN))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun char() {
        val got = serialize(s('☃'))
        assertEquals("☃\n", got)
        val (wrote, gotH) = serializeHeader(s('☃'))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun str() {
        val got = serialize(s("how\nare\n\"you\"?"))
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", got)
        val (wrote, gotH) = serializeHeader(s("how\nare\n\"you\"?"))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun bytes() {
        val bytes = "how\nare\n\"you\"?".encodeToByteArray()
        val got = serialize(s(bytes))
        assertEquals("\"how\nare\n\"\"you\"\"?\"\n", got)
        val (wrote, gotH) = serializeHeader(s(bytes))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun option() {
        val got = serialize(sNone())
        assertEquals("\"\"\n", got)
        val (wrote, gotH) = serializeHeader(sNone())
        assertFalse(wrote)
        assertEquals("", gotH)

        val got2 = serialize(sSome(s(5)))
        assertEquals("5\n", got2)
        val (wrote2, gotH2) = serializeHeader(sSome(s(5)))
        assertFalse(wrote2)
        assertEquals("", gotH2)
    }

    @Test
    fun unit() {
        val got = serialize(sUnit())
        assertEquals("\"\"\n", got)
        val (wrote, gotH) = serializeHeader(sUnit())
        assertFalse(wrote)
        assertEquals("", gotH)

        val got2 = serialize(sTuple(s(5), sUnit()))
        assertEquals("5,\n", got2)
        val (wrote2, gotH2) = serializeHeader(sUnit())
        assertFalse(wrote2)
        assertEquals("", gotH2)
    }

    @Test
    fun structUnit() {
        val foo = sUnitStruct("Foo")
        val got = serialize(foo)
        assertEquals("Foo\n", got)
        val (wrote, gotH) = serializeHeader(foo)
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun structNewtype() {
        val foo = sNewtypeStruct("Foo", s(1.5))
        val got = serialize(foo)
        assertEquals("1.5\n", got)
        val (wrote, gotH) = serializeHeader(foo)
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun enumUnits() {
        val foo = sUnitVariant("Wat", 0u, "Foo")
        assertEquals("Foo\n", serialize(foo))
        val (wroteFoo, gotHFoo) = serializeHeader(foo)
        assertFalse(wroteFoo)
        assertEquals("", gotHFoo)

        val bar = sUnitVariant("Wat", 1u, "Bar")
        assertEquals("Bar\n", serialize(bar))
        val (wroteBar, gotHBar) = serializeHeader(bar)
        assertFalse(wroteBar)
        assertEquals("", gotHBar)

        val baz = sUnitVariant("Wat", 2u, "Baz")
        assertEquals("Baz\n", serialize(baz))
        val (wroteBaz, gotHBaz) = serializeHeader(baz)
        assertFalse(wroteBaz)
        assertEquals("", gotHBaz)
    }

    @Test
    fun enumNewtypes() {
        val foo = sNewtypeVariant("Wat", 0u, "Foo", s(5))
        assertEquals("5\n", serialize(foo))
        val (wroteFoo, gotHFoo) = serializeHeader(foo)
        assertFalse(wroteFoo)
        assertEquals("", gotHFoo)

        val bar = sNewtypeVariant("Wat", 1u, "Bar", s(1.5f))
        assertEquals("1.5\n", serialize(bar))
        val (wroteBar, gotHBar) = serializeHeader(bar)
        assertFalse(wroteBar)
        assertEquals("", gotHBar)

        val baz = sNewtypeVariant("Wat", 2u, "Baz", s(true))
        assertEquals("true\n", serialize(baz))
        val (wroteBaz, gotHBaz) = serializeHeader(baz)
        assertFalse(wroteBaz)
        assertEquals("", gotHBaz)
    }

    @Test
    fun seq() {
        val got = serialize(sSeq(s(1), s(2), s(3)))
        assertEquals("1,2,3\n", got)
        val (wrote, gotH) = serializeHeader(sSeq(s(1), s(2), s(3)))
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun tuple() {
        val row = sTuple(s(true), s(1.5), s("hi"))
        val got = serialize(row)
        assertEquals("true,1.5,hi\n", got)
        val (wrote, gotH) = serializeHeader(row)
        assertFalse(wrote)
        assertEquals("", gotH)

        val row2 = sTuple(s(true), s(1.5), sSeq(s(1), s(2), s(3)))
        val got2 = serialize(row2)
        assertEquals("true,1.5,1,2,3\n", got2)
        val (wrote2, gotH2) = serializeHeader(row2)
        assertFalse(wrote2)
        assertEquals("", gotH2)
    }

    @Test
    fun tupleStruct() {
        val row = sTupleStruct("Foo", s(false), s(42), s("hi"))
        val got = serialize(row)
        assertEquals("false,42,hi\n", got)
        val (wrote, gotH) = serializeHeader(row)
        assertFalse(wrote)
        assertEquals("", gotH)
    }

    @Test
    fun tupleVariant() {
        val err =
            serializeErr(
                object : Serialize {
                    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
                        serializer.serializeTupleVariant("Foo", 0u, "X", 3).flatMap { it.end() }
                },
            )
        assertIs<ErrorKind.Serialize>(err.kind())

        val errH =
            serializeHeaderErr(
                object : Serialize {
                    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
                        serializer.serializeTupleVariant("Foo", 0u, "X", 3).flatMap { it.end() }
                },
            )
        assertIs<ErrorKind.Serialize>(errH.kind())
    }

    @Test
    fun enumStructVariant() {
        val err =
            serializeErr(
                object : Serialize {
                    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
                        serializer.serializeStructVariant("Foo", 0u, "X", 3).flatMap { it.end() }
                },
            )
        assertIs<ErrorKind.Serialize>(err.kind())

        val errH =
            serializeHeaderErr(
                object : Serialize {
                    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
                        serializer.serializeStructVariant("Foo", 0u, "X", 3).flatMap { it.end() }
                },
            )
        assertIs<ErrorKind.Serialize>(errH.kind())
    }

    @Test
    fun structNoHeaders() {
        val got = serialize(Foo(true, 5, "hi"))
        assertEquals("true,5,hi\n", got)
    }

    @Test
    fun structNoHeaders128() {
        val got = serialize(Foo128("170141183460469231731687303715884105727", "340282366920938463463374607431768211455"))
        assertEquals("170141183460469231731687303715884105727,340282366920938463463374607431768211455\n", got)
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

        val err = serializeHeaderErr(row)
        assertIs<ErrorKind.Serialize>(err.kind())
    }

    @Test
    fun structHeadersNestedSeq() {
        val row = FooSeq("foo", listOf(1, 2, 3))
        val got = serialize(row)
        assertEquals("foo,1,2,3\n", got)

        val err = serializeHeaderErr(row)
        assertIs<ErrorKind.Serialize>(err.kind())
    }

    @Test
    fun structHeadersInsideTuple() {
        val row =
            sTuple(
                FooSimple("hi", 5.0),
                Bar(true, 3),
                FooSimple("baz", 2.3),
            )
        val got = serialize(row)
        assertEquals("hi,5.0,true,3,,baz,2.3\n", got)

        val (wrote, gotH) = serializeHeader(row)
        assertTrue(wrote)
        assertEquals("label,num,label2,value,empty,label,num", gotH)
    }

    @Test
    fun structHeadersInsideTupleScalarBefore() {
        val row = sTuple(s(3.14), FooSimple("hi", 5.0))
        val got = serialize(row)
        assertEquals("3.14,hi,5.0\n", got)

        val err = serializeHeaderErr(row)
        assertIs<ErrorKind.Serialize>(err.kind())
    }

    @Test
    fun structHeadersInsideTupleScalarAfter() {
        val row = sTuple(FooSimple("hi", 5.0), s(3.14))
        val got = serialize(row)
        assertEquals("hi,5.0,3.14\n", got)

        val err = serializeHeaderErr(row)
        assertIs<ErrorKind.Serialize>(err.kind())
    }

    @Test
    fun structHeadersInsideSeq() {
        val row = sSeq(FooSimple("hi", 5.0), FooSimple("baz", 2.3))
        val got = serialize(row)
        assertEquals("hi,5.0,baz,2.3\n", got)

        val (wrote, gotH) = serializeHeader(row)
        assertTrue(wrote)
        assertEquals("label,num,label,num", gotH)
    }

    @Test
    fun structHeadersInsideNestedTupleSeq() {
        val row =
            sTuple(
                sSeq(FooSimple("hi", 5.0), Bar(true, 3)),
                FooSimple("baz", 2.3),
            )
        val got = serialize(row)
        assertEquals("hi,5.0,true,3,,baz,2.3\n", got)

        val (wrote, gotH) = serializeHeader(row)
        assertTrue(wrote)
        assertEquals("label,num,label2,value,empty,label,num", gotH)
    }
}
