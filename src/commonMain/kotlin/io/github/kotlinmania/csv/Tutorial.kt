// port-lint: source tutorial.rs
package io.github.kotlinmania.csv

/**
 * A tutorial guide for handling CSV data in Kotlin Multiplatform.
 *
 * Covers basic reading and writing, automatic serialization and deserialization,
 * error handling, and performance considerations.
 */
public object Tutorial {
    /**
     * Demonstrates parsing a simple CSV record string.
     */
    public fun parseSimpleCsv(data: String): List<StringRecord> {
        val reader = ReaderBuilder.new().hasHeaders(true).fromString(data)
        return reader.records().map { it.getOrThrow() }.toList()
    }
}
