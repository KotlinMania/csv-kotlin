// port-lint: source cookbook.rs
package io.github.kotlinmania.csv

/**
 * A cookbook of examples for CSV reading and writing in Kotlin Multiplatform.
 *
 * ## List of examples
 *
 * For reading CSV:
 * 1. Basic reading
 * 2. Reading with serialization
 * 3. Custom delimiter
 * 4. Without headers
 *
 * For writing CSV:
 * 5. Basic writing
 * 6. Writing with serialization
 */
public object Cookbook {
    /**
     * Reads CSV from a string and returns all records.
     */
    public fun readBasic(data: String): List<StringRecord> {
        val reader = ReaderBuilder.new().fromString(data)
        return reader.records().map { it.getOrThrow() }.toList()
    }

    /**
     * Writes given string records to CSV format.
     */
    public fun writeBasic(records: List<List<String>>): String {
        val writer = WriterBuilder.new().fromWriter()
        for (record in records) {
            writer.writeRecord(record).getOrThrow()
        }
        return writer.asString()
    }
}
