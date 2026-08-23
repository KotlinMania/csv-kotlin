// port-lint: source lib.rs
package io.github.kotlinmania.csv

/**
 * Convenience method to create a [Reader] from a CSV string.
 */
public fun csvReader(data: String): Reader = Reader.fromString(data)

/**
 * Convenience method to create a [Reader] from CSV bytes.
 */
public fun csvReader(data: ByteArray): Reader = Reader.fromReader(data)

/**
 * Convenience method to create a [Writer].
 */
public fun csvWriter(): Writer = Writer.new()

/**
 * Try to run an operation and return null if it fails.
 */
public fun <T> invalidOption(supplier: () -> T?): T? =
    runCatching { supplier() }.getOrNull()
