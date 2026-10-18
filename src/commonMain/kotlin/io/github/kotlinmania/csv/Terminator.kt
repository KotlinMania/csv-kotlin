package io.github.kotlinmania.csv

/**
 * A record terminator.
 *
 * Use this to specify the record terminator while parsing CSV. The default is
 * CRLF, which treats `\r`, `\n` or `\r\n` as a single record terminator.
 */
public sealed class Terminator {
    /**
     * Convert this terminator to its core representation.
     */
    public fun toCore(): Terminator = this

    /**
     * Parses `\r`, `\n` or `\r\n` as a single record terminator.
     */
    public data object CRLF : Terminator()

    /**
     * Parses the byte given as a record terminator.
     */
    public data class Any(
        val byte: Byte,
    ) : Terminator()
}
