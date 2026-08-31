// port-lint: tests ../tests/tests.rs
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

    @kotlinx.serialization.Serializable
    private data class PopRecord(
        val city: String,
        val region: String,
        val country: String,
        val population: Long? = null,
    )

    @kotlinx.serialization.Serializable
    private data class CityPopRecord(
        val city: String,
        val state: String,
        val population: Long? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
    )

    @Test
    fun cookbookReadSerde() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.deserialize<PopRecord>().toList()
        assertEquals(10, records.size)
        for (rec in records) {
            assertTrue(rec.isSuccess)
            val item = rec.getOrThrow()
            assertTrue(item.city.isNotEmpty())
            assertTrue(item.region.isNotEmpty())
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
    fun cookbookWriteSerde() {
        val wtr = Writer.new()
        wtr.serialize(PopRecord("Southborough", "MA", "United States", 9686)).getOrThrow()
        wtr.serialize(PopRecord("Northbridge", "MA", "United States", 14061)).getOrThrow()
        val lines = wtr.asString().lines().filter { it.isNotEmpty() }
        assertEquals(3, lines.size)
        assertEquals("city,region,country,population", lines[0])
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
    fun tutorialError02() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
        assertTrue(records.all { it.isSuccess })
    }

    @Test
    fun tutorialError02Errored() {
        val data = "header1,header2\nfoo,bar\nquux,baz,foobar\n"
        val rdr = Reader.fromString(data)
        val results = rdr.records().toList()
        val err = results.firstOrNull { it.isFailure }?.exceptionOrNull()
        assertTrue(err is CsvError)
    }

    @Test
    fun tutorialError03() {
        val rdr = Reader.fromString(smallpop)
        val count = rdr.records().count { it.isSuccess }
        assertEquals(10, count)
    }

    @Test
    fun tutorialError03Errored() {
        val data = "header1,header2\nfoo,bar\nquux,baz,foobar\n"
        val rdr = Reader.fromString(data)
        val failures = rdr.records().filter { it.isFailure }.toList()
        assertEquals(1, failures.size)
    }

    @Test
    fun tutorialError04() {
        val rdr = Reader.fromString(smallpop)
        var count = 0
        for (result in rdr.records()) {
            if (result.isSuccess) {
                count++
            }
        }
        assertEquals(10, count)
    }

    @Test
    fun tutorialError04Errored() {
        val data = "header1,header2\nfoo,bar\nquux,baz,foobar\n"
        val rdr = Reader.fromString(data)
        var errorCount = 0
        for (result in rdr.records()) {
            if (result.isFailure) {
                errorCount++
            }
        }
        assertEquals(1, errorCount)
    }

    @Test
    fun tutorialRead01() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
    }

    @Test
    fun tutorialReadHeaders01() {
        val rdr = Reader.fromString(smallpop)
        val headers = rdr.headers().getOrThrow()
        assertEquals("city", headers[0])
        assertEquals("population", headers[3])
    }

    @Test
    fun tutorialReadHeaders02() {
        val rdr = Reader.fromString(smallpop)
        val headers = rdr.headers().getOrThrow()
        val records = rdr.records().toList()
        assertEquals(4, headers.len())
        assertEquals(10, records.size)
    }

    @Test
    fun tutorialReadDelimiter01() {
        val rdr = ReaderBuilder.new().delimiter(':'.code.toByte()).fromString(smallpopColon)
        val headers = rdr.headers().getOrThrow()
        assertEquals(listOf("city", "region", "country", "population"), headers.toList())
    }

    @Test
    fun tutorialReadSerde01() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.deserialize<PopRecord>().toList()
        assertEquals(10, records.size)
        assertTrue(records.all { it.isSuccess && it.getOrThrow().population != null })
    }

    @Test
    fun tutorialReadSerde02() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.records().toList()
        assertEquals(10, records.size)
    }

    @Test
    fun tutorialReadSerde03() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.deserialize<PopRecord>().toList()
        assertEquals(10, records.size)
        assertEquals("Southborough", records[0].getOrThrow().city)
    }

    @Test
    fun tutorialReadSerde04() {
        val rdr = Reader.fromString(smallpop)
        val records = rdr.deserialize<PopRecord>().toList()
        assertEquals(10, records.size)
        assertEquals(9686L, records[0].getOrThrow().population)
    }

    @Test
    fun tutorialReadSerde05Invalid() {
        val data = "city,region,country,population\nBoston,MA,USA,not_a_number\n"
        val rdr = Reader.fromString(data)
        val records = rdr.deserialize<PopRecord>().toList()
        assertTrue(records[0].isFailure)
    }

    @Test
    fun tutorialReadSerdeInvalid06() {
        val data = "city,region,country,population\nBoston,MA,USA,\n"
        val rdr = Reader.fromString(data)
        val records = rdr.deserialize<PopRecord>().toList()
        assertTrue(records[0].isSuccess)
        assertEquals(null, records[0].getOrThrow().population)
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

    @Test
    fun tutorialWriteSerde01() {
        val wtr = Writer.new()
        wtr.serialize(CityPopRecord("Framingham", "MA", 66910L, 42.27926, -71.41618)).getOrThrow()
        val lines = wtr.asString().lines().filter { it.isNotEmpty() }
        assertEquals(2, lines.size)
        assertEquals("city,state,population,latitude,longitude", lines[0])
    }

    @Test
    fun tutorialWriteSerde02() {
        val wtr = WriterBuilder.new().hasHeaders(false).fromWriter()
        wtr.serialize(CityPopRecord("Framingham", "MA", 66910L, 42.27926, -71.41618)).getOrThrow()
        val lines = wtr.asString().lines().filter { it.isNotEmpty() }
        assertEquals(1, lines.size)
        assertEquals("Framingham,MA,66910,42.27926,-71.41618", lines[0])
    }

    @Test
    fun tutorialPipelineSearch01() {
        val rdr = Reader.fromString(smallpop)
        val matches =
            rdr
                .records()
                .filter { it.isSuccess && it.getOrThrow().toList().contains("MA") }
                .toList()
        assertEquals(8, matches.size)
    }

    @Test
    fun tutorialPipelineSearch02() {
        val rdr = Reader.fromString(smallpop)
        val matches =
            rdr
                .byteRecords()
                .filter { it.isSuccess && (0 until it.getOrThrow().len()).any { i -> it.getOrThrow()[i]?.decodeToString() == "MA" } }
                .toList()
        assertEquals(8, matches.size)
    }

    @Test
    fun tutorialPipelinePop01() {
        val rdr = Reader.fromString(smallpop)
        val matches =
            rdr
                .deserialize<PopRecord>()
                .filter { it.isSuccess && (it.getOrThrow().population ?: 0L) > 100000L }
                .toList()
        assertEquals(3, matches.size)
    }

    @Test
    fun tutorialPerfAlloc01() {
        val rdr = Reader.fromString(smallpop)
        val rec = ByteRecord.new()
        var count = 0
        while (rdr.readByteRecord(rec).getOrThrow()) {
            count++
        }
        assertEquals(10, count)
    }

    @Test
    fun tutorialPerfAlloc02() {
        val rdr = Reader.fromString(smallpop)
        val rec = StringRecord.new()
        var count = 0
        while (rdr.readRecord(rec).getOrThrow()) {
            count++
        }
        assertEquals(10, count)
    }

    @Test
    fun tutorialPerfAlloc03() {
        val rdr = Reader.fromString(smallpop)
        val count = rdr.records().count()
        assertEquals(10, count)
    }

    @Test
    fun tutorialPerfSerde01() {
        val rdr = Reader.fromString(smallpop)
        val count = rdr.deserialize<PopRecord>().count()
        assertEquals(10, count)
    }

    @Test
    fun tutorialPerfSerde02() {
        val rdr = Reader.fromString(smallpop)
        val count = rdr.deserialize<PopRecord>().filter { it.isSuccess }.count()
        assertEquals(10, count)
    }

    @Test
    fun tutorialPerfSerde03() {
        val rdr = Reader.fromString(smallpop)
        var count = 0
        for (rec in rdr.deserialize<PopRecord>()) {
            if (rec.isSuccess) count++
        }
        assertEquals(10, count)
    }
}
