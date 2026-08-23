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

    public fun clone(): ByteRecord = ByteRecord(pos, fields.copyOf(), ends)

    public fun len(): Int = ends.size

    public fun isEmpty(): Boolean = ends.isEmpty()

    public fun position(): Position? = pos

    public fun setPosition(pos: Position?): ByteRecord {
        this.pos = pos?.copy()
        return this
    }

    public fun clear() {
        ends.clear()
        fields = ByteArray(0)
    }

    public fun truncate(len: Int) {
        if (len < ends.size) {
            while (ends.size > len) {
                ends.removeAt(ends.size - 1)
            }
            val newEnd = if (ends.isEmpty()) 0 else ends.last()
            fields = fields.copyOf(newEnd)
        }
    }

    public fun range(index: Int): IntRange? {
        if (index < 0 || index >= ends.size) return null
        val start = if (index == 0) 0 else ends[index - 1]
        val end = ends[index]
        return start until end
    }

    public operator fun get(index: Int): ByteArray? {
        val r = range(index) ?: return null
        return fields.copyOfRange(r.first, r.last + 1)
    }

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

    public fun pushField(string: String) {
        pushField(string.encodeToByteArray())
    }

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

    override fun iterator(): Iterator<ByteArray> =
        object : Iterator<ByteArray> {
            private var idx = 0

            override fun hasNext(): Boolean = idx < len()

            override fun next(): ByteArray = get(idx++) ?: throw NoSuchElementException()
        }

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

        public fun withCapacity(buffer: Int, fields: Int): ByteRecord =
            ByteRecord(buffer, fields)

        public fun from(fields: List<ByteArray>): ByteRecord {
            val rec = ByteRecord(0, fields.size)
            for (f in fields) rec.pushField(f)
            return rec
        }

        public fun fromStrings(fields: List<String>): ByteRecord {
            val rec = ByteRecord(0, fields.size)
            for (f in fields) rec.pushField(f)
            return rec
        }

        private fun isAsciiWhitespace(b: Byte): Boolean {
            val ub = b.toInt() and 0xFF
            return ub == 0x20 || ub == 0x09 || ub == 0x0A || ub == 0x0D || ub == 0x0C || ub == 0x0B
        }
    }
}
