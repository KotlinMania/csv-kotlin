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
