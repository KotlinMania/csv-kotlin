// port-lint: source lib.rs
package io.github.kotlinmania.csv

/**
 * The quoting style to use when writing CSV data.
 */
public enum class QuoteStyle {
    /**
     * This puts quotes around every field. Always.
     */
    ALWAYS,

    /**
     * This puts quotes around fields only when necessary.
     *
     * They are necessary when fields contain a delimiter, a quote, a new
     * line or when a record is empty.
     */
    NECESSARY,

    /**
     * This puts quotes around all fields that are non-numeric. Namely, when
     * writing a field that does not parse as a valid float or integer, then
     * quotes will be used even if they aren't strictly necessary.
     */
    NON_NUMERIC,

    /**
     * This puts quotes around fields only when necessary.
     *
     * Quotes are never used, even if the field would require them to be
     * parsed back correctly.
     */
    NEVER,
}

/**
 * A record terminator.
 *
 * Use this to specify the end-of-record line ending.
 */
public sealed class Terminator {
    /**
     * Automatically detect CRLF (`\r\n`) or LF (`\n`) on input, and use CRLF (`\r\n`) on output.
     */
    public data object CRLF : Terminator()

    /**
     * Terminate records with a custom single byte.
     */
    public data class Any(
        val byte: Byte,
    ) : Terminator()
}

/**
 * The whitespace trimming strategy to use when reading CSV data.
 */
public enum class Trim {
    /**
     * Do not trim any whitespace.
     */
    NONE,

    /**
     * Trim whitespace from header fields only.
     */
    HEADERS,

    /**
     * Trim whitespace from non-header fields only.
     */
    FIELDS,

    /**
     * Trim whitespace from all fields.
     */
    ALL,
}
