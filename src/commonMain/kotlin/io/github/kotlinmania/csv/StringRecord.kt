// port-lint: source string_record.rs
package io.github.kotlinmania.csv

/**
 * A single CSV record stored as valid UTF-8 bytes.
 *
 * A string record permits reading or writing CSV rows that are valid UTF-8.
 */
public class StringRecord private constructor(
    private val record: ByteRecord,
) : Iterable<String> {
    public constructor() : this(ByteRecord())

    public constructor(bufferCapacity: Int, fieldCapacity: Int) : this(
        ByteRecord(bufferCapacity, fieldCapacity),
    )

    /**
     * Return a copy of this record.
     */
    public fun clone(): StringRecord = StringRecord(record.clone())

    /**
     * Returns the number of fields in this record.
     */
    public fun len(): Int = record.len()

    /**
     * Returns true if and only if this record is empty.
     */
    public fun isEmpty(): Boolean = record.isEmpty()

    /**
     * Return the position of this record, if available.
     */
    public fun position(): Position? = record.position()

    /**
     * Set the position of this record.
     */
    public fun setPosition(pos: Position?): StringRecord {
        record.setPosition(pos)
        return this
    }

    /**
     * Clear this record so that it has zero fields.
     */
    public fun clear() {
        record.clear()
    }

    /**
     * Truncate this record to [len] fields.
     *
     * If [len] is greater than the number of fields in this record, then this
     * has no effect.
     */
    public fun truncate(len: Int) {
        record.truncate(len)
    }

    /**
     * Return the start and end position of a field in this record.
     *
     * If no such field exists at the given index, then return null.
     */
    @OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
    @kotlin.native.HiddenFromObjC
    public fun range(index: Int): IntRange? = record.range(index)

    /**
     * Return the field at index [index].
     *
     * If no field at index [index] exists, then this returns null.
     */
    public operator fun get(index: Int): String? {
        val bytes = record.get(index) ?: return null
        return bytes.decodeToString()
    }

    /**
     * Add a new field to this record.
     */
    public fun pushField(field: String) {
        record.pushField(field.encodeToByteArray())
    }

    /**
     * Trim the fields of this record so that leading and trailing whitespace
     * is removed.
     *
     * This method uses the Unicode definition of whitespace.
     */
    public fun trim() {
        if (record.len() == 0) return
        val trimmed = StringRecord(record.asSlice().size, record.len())
        trimmed.setPosition(record.position()?.copy())
        for (i in 0 until record.len()) {
            val field = this[i] ?: ""
            trimmed.pushField(trimUnicode(field))
        }
        this.record.clear()
        for (field in trimmed) {
            this.pushField(field)
        }
        this.setPosition(trimmed.position())
    }

    /**
     * Return a new [StringRecord] containing only the fields in the specified range.
     */
    public fun slice(range: IntRange): StringRecord =
        StringRecord(record.slice(range))

    /**
     * Return the entire row as a single string. The string returned
     * stores all fields contiguously. The boundaries of each field can be
     * determined via the [range] method.
     */
    public fun asSlice(): String = record.asSlice().decodeToString()

    /**
     * Clone this record, but only copy fields up to the end of bounds. This
     * is useful when one wants to copy a record, but not necessarily any
     * excess capacity in that record.
     */
    public fun cloneTruncated(): StringRecord =
        StringRecord(record.cloneTruncated())

    /**
     * Compare this record with another string record for field equality.
     */
    public fun iterEq(other: StringRecord): Boolean = record.iterEq(other.asByteRecord())

    /**
     * Compare this record with another string record for field equality.
     */
    public fun eq(other: StringRecord): Boolean = this == other

    /**
     * Compare this record with a list of strings for field equality.
     */
    public fun iterEq(other: List<String>): Boolean {
        if (len() != other.size) return false
        for (i in 0 until len()) {
            if (get(i) != other[i]) return false
        }
        return true
    }

    /**
     * Compare this record with a list of strings for field equality.
     */
    public fun eq(other: List<String>): Boolean = iterEq(other)

    /**
     * Format this record for debugging purposes.
     */
    public fun fmt(): String = toString()

    /**
     * Return the field at index [index], or throw an [IndexOutOfBoundsException].
     */
    public fun index(index: Int): String =
        this[index] ?: throw IndexOutOfBoundsException("index out of bounds: $index (len: ${len()})")

    /**
     * Extend this record with fields from the given iterable.
     */
    public fun extend(iter: Iterable<String>) {
        for (field in iter) {
            pushField(field)
        }
    }

    /**
     * Convert this record into an iterator over its fields.
     */
    public fun intoIter(): StringRecordIter = iter()

    /**
     * Read the next record from the given reader into this record.
     */
    public fun read(reader: Reader): Result<Boolean> = reader.readRecord(this)

    /**
     * Return a reference to this record's raw [ByteRecord].
     */
    public fun asByteRecord(): ByteRecord = record

    /**
     * Convert this [StringRecord] into a [ByteRecord].
     */
    public fun intoByteRecord(): ByteRecord = record

    /**
     * Returns an iterator over all fields in this record.
     */
    public fun iter(): StringRecordIter = StringRecordIter(record.iter())

    override fun iterator(): StringRecordIter = iter()

    /**
     * Deserialize this record into [T] using the given [deserializer].
     */
    public fun <T> deserialize(
        deserializer: kotlinx.serialization.DeserializationStrategy<T>,
        headers: StringRecord? = null,
    ): Result<T> = CsvDeserializer.deserialize(this, deserializer, headers)

    /**
     * Deserialize this record into [T] using the default serializer.
     */
    public inline fun <reified T> deserialize(headers: StringRecord? = null): Result<T> =
        deserialize(kotlinx.serialization.serializer(), headers)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringRecord) return false
        return record == other.record
    }

    override fun hashCode(): Int = record.hashCode()

    override fun toString(): String {
        val fields = (0 until len()).mapNotNull { get(it) }
        return "StringRecord($fields)"
    }

    public companion object {
        public fun new(): StringRecord = StringRecord()

        public fun default(): StringRecord = new()

        public fun withCapacity(buffer: Int, fields: Int): StringRecord =
            StringRecord(buffer, fields)

        public fun fromByteRecord(record: ByteRecord): Result<StringRecord> {
            val validation = record.validate()
            return if (validation.isSuccess) {
                Result.success(StringRecord(record))
            } else {
                Result.failure(validation.exceptionOrNull()!!)
            }
        }

        public fun fromByteRecordLossy(record: ByteRecord): StringRecord {
            val newRecord = ByteRecord(0, record.len())
            newRecord.setPosition(record.position())
            for (i in 0 until record.len()) {
                val bytes = record[i]!!
                val decoded = bytes.decodeToString()
                newRecord.pushField(decoded.encodeToByteArray())
            }
            return StringRecord(newRecord)
        }

        public fun from(fields: List<String>): StringRecord {
            val rec = StringRecord(0, fields.size)
            for (f in fields) rec.pushField(f)
            return rec
        }

        public fun fromIter(iter: Iterable<String>): StringRecord = from(iter.toList())

        public typealias Output = StringRecord
        public typealias IntoIter = Iterator<String>
        public typealias Item = String
    }
}

/**
 * An iterator over the fields in a string record.
 */
public class StringRecordIter internal constructor(
    private val iter: ByteRecordIter,
) : Iterator<String> {
    override fun hasNext(): Boolean = iter.hasNext()

    override fun next(): String {
        if (!hasNext()) throw NoSuchElementException("No more elements in StringRecordIter")
        return iter.next().decodeToString()
    }

    /**
     * Return the next field from the back of the iterator, or null if empty.
     */
    public fun nextBack(): String? = iter.nextBack()?.decodeToString()

    /**
     * Returns the number of remaining elements in this iterator.
     */
    public fun count(): Int = iter.count()

    /**
     * Returns the lower and upper bounds of remaining elements.
     */
    public fun sizeHint(): Pair<Int, Int?> = count() to count()
}

internal fun isUnicodeWhitespace(c: Char): Boolean {
    val code = c.code
    return when (code) {
        0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x0020, 0x0085, 0x00A0,
        0x1680, 0x2028, 0x2029, 0x202F, 0x205F, 0x3000,
        -> true
        in 0x2000..0x200A -> true
        else -> false
    }
}

internal fun trimUnicode(s: String): String {
    var start = 0
    while (start < s.length && isUnicodeWhitespace(s[start])) {
        start++
    }
    var end = s.length
    while (end > start && isUnicodeWhitespace(s[end - 1])) {
        end--
    }
    return s.substring(start, end)
}

public typealias StringRecordOutput = StringRecord
public typealias StringRecordIntoIter = Iterator<String>
public typealias StringRecordItem = String
