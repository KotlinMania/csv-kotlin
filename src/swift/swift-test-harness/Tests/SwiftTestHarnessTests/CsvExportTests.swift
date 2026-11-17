import Testing
import Csv

@Suite struct CsvExportTests {
    @Test func testStringRecordPushTrimSliceAndClear() throws {
        let record = StringRecord()
        #expect(record.isEmpty())
        record.pushField(field: "  city  ")
        record.pushField(field: " state ")
        #expect(record.len() == 2)
        #expect(record[0] == "  city  ")

        record.trim()
        #expect(record[0] == "city")
        #expect(record[1] == "state")
        #expect(record.iterEq(other: ["city", "state"]))

        let cloned = record.clone()
        #expect(cloned.eq(other: record))
        #expect(record.slice(range: 0...0)[0] == "city")

        let built = StringRecord.Companion.shared.from(fields: ["one", "two"])
        #expect(built.len() == 2)
        #expect(built[1] == "two")

        record.clear()
        #expect(record.isEmpty())
    }

    @Test func testReaderAndWriterRoundTripSurface() throws {
        let reader = ReaderBuilder.Companion.shared.new()
            .hasHeaders(yes: true)
            .flexible(yes: false)
            .trim(trim: Trim.ALL)
            .fromString(string: "city,state\nSeattle,WA\n")
        #expect(!reader.isDone())
        #expect(reader.position().byte() == 0)
        #expect(reader.position().line() == 1)

        let writer = WriterBuilder.Companion.shared.new()
            .hasHeaders(yes: true)
            .quoteStyle(style: QuoteStyle.NECESSARY)
            .terminator(term: Terminator.CRLF.shared)
            .fromWriter()
        #expect(writer.writable())
        #expect(!writer.readable())
        #expect(writer.written() == 0)
        #expect(writer.wtrAsString().isEmpty)
    }
}
