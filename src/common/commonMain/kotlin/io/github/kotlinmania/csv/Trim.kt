package io.github.kotlinmania.csv

/**
 * The whitespace preservation behaviour when reading CSV data.
 */
public enum class Trim {
    /**
     * Preserves fields and headers. This is the default.
     */
    NONE,

    /**
     * Trim whitespace from headers.
     */
    HEADERS,

    /**
     * Trim whitespace from fields, but not headers.
     */
    FIELDS,

    /**
     * Trim whitespace from fields and headers.
     */
    ALL,
    ;

    /**
     * Return true if fields should be trimmed.
     */
    public fun shouldTrimFields(): Boolean = this == FIELDS || this == ALL

    /**
     * Return true if headers should be trimmed.
     */
    public fun shouldTrimHeaders(): Boolean = this == HEADERS || this == ALL
}
