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
        val rec =
            StringRecord.from(
                listOf(
                    "\u0009\u000A\u000B\u000C\u000D\u0020\u0085\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u2028\u2029\u202F\u205F\u3000",
                ),
            )
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

    @Test
    fun iterForwardAndReverse() {
        val data = listOf("foo", "bar", "baz", "quux", "wat")
        val rec = StringRecord.from(data)
        val it = rec.iter()

        assertEquals("wat", it.nextBack())
        assertEquals("foo", it.next())
        assertEquals("bar", it.next())
        assertEquals("quux", it.nextBack())
        assertEquals("baz", it.next())
        kotlin.test.assertNull(it.nextBack())
        kotlin.test.assertTrue(!it.hasNext())
    }

    @Test
    fun asSliceAndCloneTruncated() {
        val rec = StringRecord.withCapacity(100, 10)
        rec.pushField("foo")
        rec.pushField("bar")
        assertEquals("foobar", rec.asSlice())

        val truncated = rec.cloneTruncated()
        assertEquals(2, truncated.len())
        assertEquals("foobar", truncated.asSlice())
        kotlin.test.assertTrue(rec.iterEq(truncated))
    }
}
