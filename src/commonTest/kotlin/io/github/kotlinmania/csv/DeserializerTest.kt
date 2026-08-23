// port-lint: tests deserializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeserializerTest {
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
        val city: String,
    )

    @Serializable
    enum class Color {
        RED,
        GREEN,
        BLUE,
    }

    @Serializable
    data class Item(
        val label: String,
        val color: Color,
    )

    @Test
    fun deserializeStringRecordPositional() {
        val record = StringRecord.from(listOf("42", "42.5", "true"))
        val row = record.deserialize<Row>().getOrThrow()
        assertEquals(42, row.foo)
        assertEquals(42.5, row.bar)
        assertTrue(row.baz)
    }

    @Test
    fun deserializeStringRecordWithHeaders() {
        val headers = StringRecord.from(listOf("city", "name", "age"))
        val record = StringRecord.from(listOf("New York", "Alice", "30"))
        val person = record.deserialize<Person>(headers).getOrThrow()
        assertEquals("Alice", person.name)
        assertEquals(30, person.age)
        assertEquals("New York", person.city)
    }

    @Test
    fun deserializeByteRecord() {
        val record = ByteRecord.fromStrings(listOf("42", "42.5", "true"))
        val row = record.deserialize<Row>().getOrThrow()
        assertEquals(42, row.foo)
        assertEquals(42.5, row.bar)
        assertTrue(row.baz)
    }

    @Test
    fun deserializeReaderStream() {
        val csv = "foo,bar,baz\n10,1.2,false\n20,3.4,true\n"
        val rdr = ReaderBuilder.new().hasHeaders(true).fromString(csv)
        val rows = rdr.deserialize<Row>().map { it.getOrThrow() }.toList()
        assertEquals(2, rows.size)
        assertEquals(Row(10, 1.2, false), rows[0])
        assertEquals(Row(20, 3.4, true), rows[1])
    }

    @Test
    fun deserializeEnum() {
        val record = StringRecord.from(listOf("Widget", "GREEN"))
        val item = record.deserialize<Item>().getOrThrow()
        assertEquals("Widget", item.label)
        assertEquals(Color.GREEN, item.color)
    }
}
