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

    public fun clone(): StringRecord = StringRecord(record.clone())

    public fun len(): Int = record.len()

    public fun isEmpty(): Boolean = record.isEmpty()

    public fun position(): Position? = record.position()

    public fun setPosition(pos: Position?): StringRecord {
        record.setPosition(pos)
        return this
    }

    public fun clear() {
        record.clear()
    }

    public fun truncate(len: Int) {
        record.truncate(len)
    }

    public fun range(index: Int): IntRange? = record.range(index)

    public operator fun get(index: Int): String? {
        val bytes = record.get(index) ?: return null
        return bytes.decodeToString()
    }

    public fun pushField(field: String) {
        record.pushField(field.encodeToByteArray())
    }

    public fun trim() {
        record.trim()
    }

    public fun slice(range: IntRange): StringRecord =
        StringRecord(record.slice(range))

    public fun asByteRecord(): ByteRecord = record

    public fun intoByteRecord(): ByteRecord = record

    override fun iterator(): Iterator<String> =
        object : Iterator<String> {
            private var idx = 0

            override fun hasNext(): Boolean = idx < len()

            override fun next(): String = get(idx++) ?: throw NoSuchElementException()
        }

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
    }
}
