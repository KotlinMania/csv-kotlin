// port-lint: source byte_record.rs
package io.github.kotlinmania.csv

/**
 * A single CSV record stored as raw bytes.
 *
 * A byte record permits reading or writing CSV rows that are not UTF-8.
 */
public class ByteRecord : Iterable<ByteArray> {
    private var pos: Position? = null
    private var fields: ByteArray
    private val ends: MutableList<Int>

    public constructor() : this(0, 0)

    public constructor(bufferCapacity: Int, fieldCapacity: Int) {
        this.fields = ByteArray(bufferCapacity)
        this.ends = ArrayList(fieldCapacity)
    }

    private constructor(pos: Position?, fields: ByteArray, ends: MutableList<Int>) {
        this.pos = pos?.copy()
        this.fields = fields
        this.ends = ArrayList(ends)
    }

    /**
     * Return a copy of this record.
     */
    public fun clone(): ByteRecord = ByteRecord(pos, fields.copyOf(), ends)

    /**
     * Returns the number of fields in this record.
     */
    public fun len(): Int = ends.size

    /**
     * Returns true if and only if this record is empty.
     */
    public fun isEmpty(): Boolean = ends.isEmpty()

    /**
     * Return the position of this record, if available.
     */
    public fun position(): Position? = pos

    /**
     * Set the position of this record.
     */
    public fun setPosition(pos: Position?): ByteRecord {
        this.pos = pos?.copy()
        return this
    }

    /**
     * Clear this record so that it has zero fields.
     */
    public fun clear() {
        ends.clear()
        fields = ByteArray(0)
    }

    /**
     * Truncate this record to [len] fields.
     *
     * If [len] is greater than or equal to the number of fields in this record,
     * then this has no effect.
     */
    public fun truncate(len: Int) {
        if (len < ends.size) {
            while (ends.size > len) {
                ends.removeAt(ends.size - 1)
            }
            val newEnd = if (ends.isEmpty()) 0 else ends.last()
            fields = fields.copyOf(newEnd)
        }
    }

    /**
     * Return the start and end position of a field in this record.
     *
     * If no such field exists at the given index, then return null.
     */
    public fun range(index: Int): IntRange? {
        if (index < 0 || index >= ends.size) return null
        val start = if (index == 0) 0 else ends[index - 1]
        val end = ends[index]
        return start until end
    }

    /**
     * Return the field at index [index].
     *
     * If no field at index [index] exists, then this returns null.
     */
    public operator fun get(index: Int): ByteArray? {
        val r = range(index) ?: return null
        return fields.copyOfRange(r.first, r.last + 1)
    }

    /**
     * Add a new field to this record.
     */
    public fun pushField(bytes: ByteArray) {
        val oldSize = if (ends.isEmpty()) 0 else ends.last()
        val newSize = oldSize + bytes.size
        if (fields.size < newSize) {
            val newCap = maxOf(newSize, fields.size * 2)
            fields = fields.copyOf(newCap)
        }
        bytes.copyInto(fields, destinationOffset = oldSize)
        ends.add(newSize)
    }

    /**
     * Add a new field to this record from a string.
     */
    public fun pushField(string: String) {
        pushField(string.encodeToByteArray())
    }

    /**
     * Trim the fields of this record so that leading and trailing whitespace
     * is removed.
     *
     * This method uses the ASCII definition of whitespace. That is, only
     * bytes in the class `[\t\n\v\f\r ]` are trimmed.
     */
    public fun trim() {
        val newRecord = ByteRecord(fields.size, ends.size)
        newRecord.pos = this.pos
        for (i in 0 until len()) {
            val field = this[i]!!
            var start = 0
            while (start < field.size && isAsciiWhitespace(field[start])) {
                start++
            }
            var end = field.size
            while (end > start && isAsciiWhitespace(field[end - 1])) {
                end--
            }
            newRecord.pushField(field.copyOfRange(start, end))
        }
        this.fields = newRecord.fields
        this.ends.clear()
        this.ends.addAll(newRecord.ends)
    }

    /**
     * Return a new [ByteRecord] containing only the fields in the specified range.
     */
    public fun slice(range: IntRange): ByteRecord {
        val result = ByteRecord()
        result.pos = this.pos
        for (i in range) {
            val f = this[i]
            if (f != null) {
                result.pushField(f)
            }
        }
        return result
    }

    /**
     * Return the entire row as a single byte array. The array returned stores
     * all fields contiguously. The boundaries of each field can be determined
     * via the [range] method.
     */
    public fun asSlice(): ByteArray {
        val end = if (ends.isEmpty()) 0 else ends.last()
        return fields.copyOfRange(0, end)
    }

    /**
     * Clone this record, but only copy fields up to the end of bounds. This
     * is useful when one wants to copy a record, but not necessarily any
     * excess capacity in that record.
     */
    public fun cloneTruncated(): ByteRecord =
        ByteRecord(pos?.copy(), asSlice(), ArrayList(ends))

    /**
     * Compare this record with another byte record for field equality.
     */
    public fun iterEq(other: ByteRecord): Boolean {
        if (len() != other.len()) return false
        for (i in 0 until len()) {
            if (!this[i].contentEquals(other[i])) return false
        }
        return true
    }

    /**
     * Compare this record with another byte record for field equality.
     */
    public fun eq(other: ByteRecord): Boolean = this == other

    /**
     * Compare this record with a list of byte arrays for field equality.
     */
    public fun iterEq(other: List<ByteArray>): Boolean {
        if (len() != other.size) return false
        for (i in 0 until len()) {
            if (!this[i].contentEquals(other[i])) return false
        }
        return true
    }

    /**
     * Compare this record with a list of byte arrays for field equality.
     */
    public fun eq(other: List<ByteArray>): Boolean = iterEq(other)

    /**
     * Format this record for debugging purposes.
     */
    public fun fmt(): String = toString()

    /**
     * Return the field at index [index], or throw an [IndexOutOfBoundsException].
     */
    public fun index(index: Int): ByteArray =
        this[index] ?: throw IndexOutOfBoundsException("index out of bounds: $index (len: ${len()})")

    /**
     * Extend this record with fields from the given iterable.
     */
    public fun extend(iter: Iterable<ByteArray>) {
        for (field in iter) {
            pushField(field)
        }
    }

    /**
     * Convert this record into an iterator over its fields.
     */
    public fun intoIter(): ByteRecordIter = iter()

    /**
     * Return a copy of the list of end offsets for each field.
     */
    public fun ends(): List<Int> = ArrayList(ends)

    /**
     * Return the end offset of the field at [index], if it exists.
     */
    public fun end(index: Int): Int? = ends.getOrNull(index)

    /**
     * Return the raw underlying byte array and list of end offsets.
     */
    public fun asParts(): Pair<ByteArray, List<Int>> = fields to ArrayList(ends)

    /**
     * Set the length (number of fields) in this record.
     */
    public fun setLen(len: Int) {
        truncate(len)
    }

    /**
     * Trim ASCII whitespace from all fields in this record.
     */
    public fun trimAscii() {
        if (len() == 0) return
        val trimmed = ByteRecord(asSlice().size, len())
        trimmed.setPosition(position()?.copy())
        for (field in this) {
            trimmed.pushField(trimAscii(field))
        }
        clear()
        for (field in trimmed) {
            pushField(field)
        }
        setPosition(trimmed.position())
    }

    /**
     * Returns an iterator over all fields in this record.
     */
    public fun iter(): ByteRecordIter = ByteRecordIter(this)

    public fun validate(): Result<Unit> {
        for (i in 0 until len()) {
            val field = this[i]!!
            var idx = 0
            while (idx < field.size) {
                val b = field[idx].toInt() and 0xFF
                val len =
                    when {
                        b <= 0x7F -> 1
                        b in 0xC2..0xDF -> 2
                        b in 0xE0..0xEF -> 3
                        b in 0xF0..0xF4 -> 4
                        else -> return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                    }
                if (idx + len > field.size) {
                    return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                }
                for (k in 1 until len) {
                    val cont = field[idx + k].toInt() and 0xFF
                    if (cont !in 0x80..0xBF) {
                        return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                    }
                }
                if (len == 3) {
                    val b1 = field[idx + 1].toInt() and 0xFF
                    if (b == 0xE0 && b1 < 0xA0) return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                    if (b == 0xED && b1 > 0x9F) return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                } else if (len == 4) {
                    val b1 = field[idx + 1].toInt() and 0xFF
                    if (b == 0xF0 && b1 < 0x90) return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                    if (b == 0xF4 && b1 > 0x8F) return Result.failure(FromUtf8Error(this, Utf8Error(i, idx)))
                }
                idx += len
            }
        }
        return Result.success(Unit)
    }

    override fun iterator(): ByteRecordIter = iter()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteRecord) return false
        if (len() != other.len()) return false
        for (i in 0 until len()) {
            if (!this[i].contentEquals(other[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (i in 0 until len()) {
            result = 31 * result + this[i].contentHashCode()
        }
        return result
    }

    override fun toString(): String {
        val elements =
            (0 until len()).joinToString(", ") { i ->
                val bytes = this[i]!!
                val sb = StringBuilder("\"")
                for (b in bytes) {
                    val ub = b.toInt() and 0xFF
                    if (ub in 0x20..0x7E && ub != '"'.code && ub != '\\'.code) {
                        sb.append(ub.toChar())
                    } else if (ub == '"'.code) {
                        sb.append("\\\"")
                    } else if (ub == '\\'.code) {
                        sb.append("\\\\")
                    } else if (ub == '\n'.code) {
                        sb.append("\\n")
                    } else if (ub == '\r'.code) {
                        sb.append("\\r")
                    } else if (ub == '\t'.code) {
                        sb.append("\\t")
                    } else {
                        sb.append("\\x").append(ub.toString(16).padStart(2, '0'))
                    }
                }
                sb.append('"').toString()
            }
        return "ByteRecord([$elements])"
    }

    public companion object {
        public fun new(): ByteRecord = ByteRecord()

        public fun default(): ByteRecord = new()

        public fun withCapacity(buffer: Int, fields: Int): ByteRecord =
            ByteRecord(buffer, fields)

        public fun from(fields: List<ByteArray>): ByteRecord {
            val rec = ByteRecord(0, fields.size)
            for (f in fields) rec.pushField(f)
            return rec
        }

        public fun fromIter(iter: Iterable<ByteArray>): ByteRecord = from(iter.toList())

        public fun fromStrings(fields: List<String>): ByteRecord {
            val rec = ByteRecord(0, fields.size)
            for (f in fields) rec.pushField(f)
            return rec
        }

        public fun isAsciiWhitespace(b: Byte): Boolean {
            val ub = b.toInt() and 0xFF
            return ub == 0x20 || ub == 0x09 || ub == 0x0A || ub == 0x0D || ub == 0x0C || ub == 0x0B
        }

        public fun trimAscii(bytes: ByteArray): ByteArray =
            trimAsciiStart(trimAsciiEnd(bytes))

        public fun trimAsciiStart(bytes: ByteArray): ByteArray {
            var start = 0
            while (start < bytes.size && isAsciiWhitespace(bytes[start])) {
                start++
            }
            return if (start == 0) bytes else bytes.copyOfRange(start, bytes.size)
        }

        public fun trimAsciiEnd(bytes: ByteArray): ByteArray {
            var end = bytes.size
            while (end > 0 && isAsciiWhitespace(bytes[end - 1])) {
                end--
            }
            return if (end == bytes.size) bytes else bytes.copyOfRange(0, end)
        }
    }
}

/**
 * An iterator over the fields in a byte record.
 */
public class ByteRecordIter internal constructor(
    private val record: ByteRecord,
) : Iterator<ByteArray> {
    private var iForward = 0
    private var iReverse = record.len()

    override fun hasNext(): Boolean = iForward < iReverse

    override fun next(): ByteArray {
        if (!hasNext()) throw NoSuchElementException()
        return record[iForward++]!!
    }

    /**
     * Return the next field from the back of the iterator, or null if empty.
     */
    public fun nextBack(): ByteArray? {
        if (iForward == iReverse) return null
        return record[--iReverse]
    }

    /**
     * Returns the number of remaining elements in this iterator.
     */
    public fun count(): Int = iReverse - iForward

    /**
     * Returns the lower and upper bounds of remaining elements.
     */
    public fun sizeHint(): Pair<Int, Int?> = count() to count()
}
