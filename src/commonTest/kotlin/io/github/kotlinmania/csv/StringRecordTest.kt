// port-lint: tests string_record.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StringRecordTest {
    @Test
    fun trimFront() {
        val rec = StringRecord.from(listOf(" abc"))
        rec.trim()
        assertEquals("abc", rec[0])

        val rec2 = StringRecord.from(listOf(" abc", "  xyz"))
        rec2.trim()
        assertEquals("abc", rec2[0])
        assertEquals("xyz", rec2[1])
    }

    @Test
    fun trimBack() {
        val rec = StringRecord.from(listOf("abc "))
        rec.trim()
        assertEquals("abc", rec[0])

        val rec2 = StringRecord.from(listOf("abc ", "xyz  "))
        rec2.trim()
        assertEquals("abc", rec2[0])
        assertEquals("xyz", rec2[1])
    }

    @Test
    fun trimBoth() {
        val rec = StringRecord.from(listOf(" abc "))
        rec.trim()
        assertEquals("abc", rec[0])

        val rec2 = StringRecord.from(listOf(" abc ", "  xyz  "))
        rec2.trim()
        assertEquals("abc", rec2[0])
        assertEquals("xyz", rec2[1])
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords1() {
        val rec = StringRecord.from(listOf(""))
        rec.trim()
        assertEquals("", rec[0])
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords2() {
        val rec = StringRecord.from(listOf("", ""))
        rec.trim()
        assertEquals("", rec[0])
        assertEquals("", rec[1])
    }

    @Test
    fun trimDoesNotPanicOnEmptyRecords3() {
        val rec = StringRecord.new()
        rec.trim()
        assertEquals(0, rec.len())
    }

    @Test
    fun trimWhitespaceOnly() {
        val rec = StringRecord.from(listOf(" \t\n\r\u000C"))
        rec.trim()
        assertEquals("", rec[0])
    }

    @Test
    fun eqFieldBoundaries() {
        val test1 = StringRecord.from(listOf("12", "34"))
        val test2 = StringRecord.from(listOf("123", "4"))
        assertNotEquals(test1, test2)
    }

    @Test
    fun eqRecordLen() {
        val test1 = StringRecord.from(listOf("12", "34", "56"))
        val test2 = StringRecord.from(listOf("12", "34"))
        assertNotEquals(test1, test2)
    }
}
