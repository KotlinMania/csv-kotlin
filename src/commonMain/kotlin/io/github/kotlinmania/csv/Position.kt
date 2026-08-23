// port-lint: source byte_record.rs
package io.github.kotlinmania.csv

/**
 * A position in CSV data.
 *
 * A position is used to report errors in CSV data. All positions include the
 * byte offset, line number and record index at which the error occurred.
 *
 * Byte offsets and record indices start at `0`. Line numbers start at `1`.
 *
 * A CSV reader will automatically assign the position of each record.
 */
public data class Position(
    var byte: ULong = 0uL,
    var line: ULong = 1uL,
    var record: ULong = 0uL,
) {
    /**
     * The byte offset, starting at `0`, of this position.
     */
    public fun byte(): ULong = byte

    /**
     * The line number, starting at `1`, of this position.
     */
    public fun line(): ULong = line

    /**
     * The record index, starting with the first record at `0`.
     */
    public fun record(): ULong = record

    /**
     * Set the byte offset of this position.
     */
    public fun setByte(byte: ULong): Position {
        this.byte = byte
        return this
    }

    /**
     * Set the line number of this position.
     *
     * If the line number is less than `1`, then this method throws [IllegalArgumentException].
     */
    public fun setLine(line: ULong): Position {
        require(line > 0uL) { "line number must be > 0" }
        this.line = line
        return this
    }

    /**
     * Set the record index of this position.
     */
    public fun setRecord(record: ULong): Position {
        this.record = record
        return this
    }

    public companion object {
        public fun new(): Position = Position(0uL, 1uL, 0uL)
    }
}
