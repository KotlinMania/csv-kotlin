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
    fun serializeWithHeaders() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = wtr.serialize(Row(foo = 42, bar = 42.5, baz = true))
        assertTrue(res.isSuccess)
        assertEquals("foo,bar,baz\n42,42.5,true\n", wtr.asString())
    }

    @Test
    fun serializeMultipleRecordsWithHeaders() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serialize(Person("Alice", 30, true)).getOrThrow()
        wtr.serialize(Person("Bob", 25, false)).getOrThrow()
        assertEquals("name,age,active\nAlice,30,true\nBob,25,false\n", wtr.asString())
    }

    @Test
    fun serializeNoHeaders() {
        val wtr = WriterBuilder.new().fromWriter()
        val res = wtr.serializeNoHeaders(Row(foo = 42, bar = 42.5, baz = true))
        assertTrue(res.isSuccess)
        assertEquals("42,42.5,true\n", wtr.asString())
    }

    @Test
    fun serializeEnum() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serialize(Task("Code", Status.ACTIVE)).getOrThrow()
        assertEquals("title,status\nCode,ACTIVE\n", wtr.asString())
    }

    @Test
    fun serializeNoHeaders128() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeNoHeaders128(123456789L).getOrThrow()
        assertEquals("123456789\n", wtr.asString())
    }

    @Test
    fun serializeTuple() {
        val wtr = WriterBuilder.new().fromWriter()
        wtr.serializeTuple(listOf("a", 1, true)).getOrThrow()
        assertEquals("a,1,true\n", wtr.asString())
    }
}
