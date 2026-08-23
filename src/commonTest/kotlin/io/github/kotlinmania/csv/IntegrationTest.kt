// port-lint: tests tests/tests.rs
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegrationTest {
    private val smallpop =
        """
        city,region,country,population
        Southborough,MA,United States,9686
        Northbridge,MA,United States,14061
        Westborough,MA,United States,17993
        Marlborough,MA,United States,38098
        Springfield,MA,United States,153060
        Springfield,MO,United States,150243
        Springfield,OH,United States,65358
        Framingham,MA,United States,66910
        Franklin,MA,United States,29560
        Lowell,MA,United States,105167
        """.trimIndent()

    private val smallpopColon =
        """
        city:region:country:population
        Southborough:MA:United States:9686
        Northbridge:MA:United States:14061
        Westborough:MA:United States:17993
        Marlborough:MA:United States:38098
        Springfield:MA:United States:153060
        Springfield:MO:United States:150243
        Springfield:OH:United States:65358
        Framingham:MA:United States:66910
        Franklin:MA:United States:29560
        Lowell:MA:United States:105167
        """.trimIndent()

    private val smallpopNoHeaders =
        """
        Southborough,MA,United States,9686
        Northbridge,MA,United States,14061
        Westborough,MA,United States,17993
        Marlborough,MA,United States,38098
        Springfield,MA,United States,153060
        Springfield,MO,United States,150243
        Springfield,OH,United States,65358
        Framingham,MA,United States,66910
        Franklin,MA,United States,29560
        Lowell,MA,United States,105167
        """.trimIndent()

    @Test
    fun cookbookReadBasic() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
        for (rec in records) {
            assertTrue(rec.isSuccess)
            assertEquals(4, rec.getOrThrow().len())
        }
    }

    @Test
    fun cookbookReadColon() {
        val rdr = ReaderBuilder.new().delimiter(':'.code.toByte()).fromString(smallpopColon)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
        for (rec in records) {
            assertTrue(rec.isSuccess)
            assertEquals(4, rec.getOrThrow().len())
        }
    }

    @Test
    fun cookbookReadNoHeaders() {
        val rdr = ReaderBuilder.new().hasHeaders(false).fromString(smallpopNoHeaders)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
        for (rec in records) {
            assertTrue(rec.isSuccess)
            assertEquals(4, rec.getOrThrow().len())
        }
    }

    @Test
    fun cookbookWriteBasic() {
        val wtr = Writer.new()
        wtr.writeRecord(listOf("City", "State", "Population", "Latitude", "Longitude"))
        wtr.writeRecord(listOf("Davidsons Landing", "AK", "", "65.2419444", "-165.2716667"))
        wtr.writeRecord(listOf("Kenai", "AK", "7610", "60.5544444", "-151.2583333"))
        wtr.writeRecord(listOf("Oakman", "AL", "", "33.7133333", "-87.3886111"))
        val lines = wtr.asString().lines().filter { it.isNotEmpty() }
        assertEquals(4, lines.size)
    }

    @Test
    fun tutorialSetup01() {
        val rdr = Reader.fromString(smallpop)
        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("city", "region", "country", "population"), headers.toList())
    }

    @Test
    fun tutorialError01() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.records().map { it.getOrThrow() }.toList()
        assertEquals(10, records.size)
    }

    @Test
    fun tutorialError01Errored() {
        val data = "header1,header2\nfoo,bar\nquux,baz,foobar\n"
        val rdr = Reader.fromString(data)
        val results = rdr.records().toList()
        assertTrue(results.any { it.isFailure })
    }

    @Test
    fun tutorialReadHeaders01() {
        val rdr = Reader.fromString(smallpop)
        val headers = rdr.headers().getOrThrow()
        assertEquals("city", headers[0])
        assertEquals("population", headers[3])
    }

    @Test
    fun tutorialReadDelimiter01() {
        val rdr = ReaderBuilder.new().delimiter(':'.code.toByte()).fromString(smallpopColon)
        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("city", "region", "country", "population"), headers.toList())
    }

    @Test
    fun tutorialWrite01() {
        val wtr = Writer.new()
        wtr.writeRecord(listOf("City", "State", "Population"))
        wtr.writeRecord(listOf("Framingham", "MA", "66910"))
        assertEquals("City,State,Population\nFramingham,MA,66910\n", wtr.asString())
    }

    @Test
    fun tutorialWriteDelimiter01() {
        val wtr = WriterBuilder.new().delimiter(';'.code.toByte()).fromWriter()
        wtr.writeRecord(listOf("City", "State", "Population"))
        wtr.writeRecord(listOf("Framingham", "MA", "66910"))
        assertEquals("City;State;Population\nFramingham;MA;66910\n", wtr.asString())
    }
}
