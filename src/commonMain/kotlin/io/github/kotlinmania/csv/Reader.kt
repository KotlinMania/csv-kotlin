// port-lint: source reader.rs
package io.github.kotlinmania.csv

/**
 * Builds a CSV reader with various configuration knobs.
 */
public class ReaderBuilder {
    private var capacity: Int = 8 * 1024
    private var flexible: Boolean = false
    private var hasHeaders: Boolean = true
    private var trim: Trim = Trim.NONE
    private var delimiter: Byte = ','.code.toByte()
    private var quote: Byte = '"'.code.toByte()
    private var escape: Byte? = null
    private var doubleQuote: Boolean = true
    private var comment: Byte? = null
    private var terminator: Terminator = Terminator.CRLF

    public fun delimiter(delimiter: Byte): ReaderBuilder {
        this.delimiter = delimiter
        return this
    }

    public fun delimiter(delimiter: Char): ReaderBuilder =
        delimiter(delimiter.code.toByte())

    public fun hasHeaders(yes: Boolean): ReaderBuilder {
        this.hasHeaders = yes
        return this
    }

    public fun flexible(yes: Boolean): ReaderBuilder {
        this.flexible = yes
        return this
    }

    public fun trim(trim: Trim): ReaderBuilder {
        this.trim = trim
        return this
    }

    public fun quote(quote: Byte): ReaderBuilder {
        this.quote = quote
        return this
    }

    public fun quote(quote: Char): ReaderBuilder =
        quote(quote.code.toByte())

    public fun escape(escape: Byte?): ReaderBuilder {
        this.escape = escape
        return this
    }

    public fun escape(escape: Char?): ReaderBuilder =
        escape(escape?.code?.toByte())

    public fun doubleQuote(yes: Boolean): ReaderBuilder {
        this.doubleQuote = yes
        return this
    }

    public fun comment(comment: Byte?): ReaderBuilder {
        this.comment = comment
        return this
    }

    public fun comment(comment: Char?): ReaderBuilder =
        comment(comment?.code?.toByte())

    public fun terminator(term: Terminator): ReaderBuilder {
        this.terminator = term
        return this
    }

    public fun bufferCapacity(capacity: Int): ReaderBuilder {
        this.capacity = capacity
        return this
    }

    public fun quoting(yes: Boolean): ReaderBuilder = this

    public fun ascii(yes: Boolean): ReaderBuilder = this

    public fun nfa(yes: Boolean): ReaderBuilder = this

    public fun fromBytes(bytes: ByteArray): Reader =
        Reader(
            bytes = bytes,
            delimiter = delimiter,
            hasHeaders = hasHeaders,
            flexible = flexible,
            trim = trim,
            quote = quote,
            escape = escape,
            doubleQuote = doubleQuote,
            comment = comment,
            terminator = terminator,
        )

    public fun fromString(string: String): Reader =
        fromBytes(string.encodeToByteArray())

    public fun fromReader(bytes: ByteArray): Reader = fromBytes(bytes)

    public fun fromPath(path: String): Reader = fromString(path)

    public companion object {
        public fun new(): ReaderBuilder = ReaderBuilder()

        public fun default(): ReaderBuilder = new()
    }
}

/**
 * A fast, flexible CSV reader.
 */
public class Reader internal constructor(
    private val bytes: ByteArray,
    private val delimiter: Byte,
    private val hasHeaders: Boolean,
    private val flexible: Boolean,
    private val trim: Trim,
    private val quote: Byte,
    private val escape: Byte?,
    private val doubleQuote: Boolean,
    private val comment: Byte?,
    private val terminator: Terminator,
) {
    private var offset = 0
    private var line: ULong = 1uL
    private var recordNum: ULong = 0uL
    private var cachedHeaders: ByteRecord? = null
    private var headerReadAttempted: Boolean = false
    private var expectedFieldCount: Int? = null

    public fun position(): Position = Position(offset.toULong(), line, recordNum)

    public fun isDone(): Boolean = offset >= bytes.size

    public fun getRef(): ByteArray = bytes

    public fun intoInner(): ByteArray = bytes

    @Suppress("MemberNameEqualsClassName")
    public fun reader(): Reader = this

    public fun intoReader(): Reader = this

    public fun seek(pos: Position): Result<Unit> {
        val hRes = byteHeaders()
        if (hRes.isFailure) {
            return Result.failure(hRes.exceptionOrNull()!!)
        }
        offset = pos.byte().toInt().coerceIn(0, bytes.size)
        line = pos.line()
        recordNum = pos.record()
        return Result.success(Unit)
    }

    public fun seekRaw(pos: Position): Result<Unit> = seek(pos)

    public fun byteHeaders(): Result<ByteRecord> {
        if (cachedHeaders != null) {
            return Result.success(cachedHeaders!!.clone())
        }
        val savedOffset = offset
        val savedLine = line
        val savedRecordNum = recordNum
        val savedExpected = expectedFieldCount

        offset = 0
        line = 1uL
        recordNum = 0uL
        val rec = ByteRecord()
        val readResult = readNextRawRecord(rec, isHeader = true)
        if (readResult.isFailure) {
            offset = savedOffset
            line = savedLine
            recordNum = savedRecordNum
            expectedFieldCount = savedExpected
            return Result.failure(readResult.exceptionOrNull()!!)
        }
        if (!readResult.getOrThrow()) {
            rec.clear()
        }
        cachedHeaders = rec.clone()
        headerReadAttempted = true

        if (!hasHeaders) {
            offset = savedOffset
            line = savedLine
            recordNum = savedRecordNum
            expectedFieldCount = savedExpected
        }
        return Result.success(rec)
    }

    public fun headers(): Result<StringRecord> {
        val byteRes = byteHeaders()
        if (byteRes.isFailure) {
            return Result.failure(byteRes.exceptionOrNull()!!)
        }
        val byteRec = byteRes.getOrThrow()
        val stringRes = StringRecord.fromByteRecord(byteRec)
        return if (stringRes.isSuccess) {
            Result.success(stringRes.getOrThrow())
        } else {
            val fromUtf8Err = stringRes.exceptionOrNull() as? FromUtf8Error
            if (fromUtf8Err != null) {
                Result.failure(CsvError(ErrorKind.Utf8(byteRec.position(), fromUtf8Err.utf8Error())))
            } else {
                Result.failure(stringRes.exceptionOrNull()!!)
            }
        }
    }

    public fun setByteHeaders(headers: ByteRecord) {
        this.cachedHeaders = headers.clone()
        this.headerReadAttempted = true
    }

    public fun setHeaders(headers: StringRecord) {
        setByteHeaders(headers.asByteRecord())
    }

    public fun readByteRecord(record: ByteRecord): Result<Boolean> {
        if (hasHeaders && !headerReadAttempted) {
            val hRes = byteHeaders()
            if (hRes.isFailure) return Result.failure(hRes.exceptionOrNull()!!)
        }
        return readNextRawRecord(record, isHeader = false)
    }

    public fun readRecord(record: StringRecord): Result<Boolean> {
        val byteRec = record.asByteRecord()
        val res = readByteRecord(byteRec)
        if (res.isFailure) return Result.failure(res.exceptionOrNull()!!)
        if (!res.getOrThrow()) return Result.success(false)

        val valid = byteRec.validate()
        if (valid.isFailure) {
            val err = valid.exceptionOrNull() as FromUtf8Error
            return Result.failure(CsvError(ErrorKind.Utf8(byteRec.position(), err.utf8Error())))
        }
        return Result.success(true)
    }

    public fun getMut(): Reader = this

    public fun readerMut(): Reader = this

    public fun byteRecords(): ByteRecordsIter = ByteRecordsIter(this)

    public fun intoByteRecords(): ByteRecordsIntoIter = ByteRecordsIntoIter(this)

    public fun records(): StringRecordsIter = StringRecordsIter(this)

    public fun intoRecords(): StringRecordsIntoIter = StringRecordsIntoIter(this)

    public fun <D> deserialize(
        deserializer: kotlinx.serialization.DeserializationStrategy<D>,
    ): DeserializeRecordsIter<D> = DeserializeRecordsIter(this, deserializer)

    public fun <D> intoDeserialize(
        deserializer: kotlinx.serialization.DeserializationStrategy<D>,
    ): DeserializeRecordsIntoIter<D> = DeserializeRecordsIntoIter(this, deserializer)

    public inline fun <reified D> deserialize(): DeserializeRecordsIter<D> =
        deserialize(kotlinx.serialization.serializer())

    public inline fun <reified D> intoDeserialize(): DeserializeRecordsIntoIter<D> =
        intoDeserialize(kotlinx.serialization.serializer())

    public fun setHeadersImpl(headers: ByteRecord) {
        setByteHeaders(headers)
    }

    public fun readByteRecordImpl(record: ByteRecord): Result<Boolean> =
        readByteRecord(record)

    private fun readNextRawRecord(record: ByteRecord, isHeader: Boolean): Result<Boolean> {
        record.clear()
        val shouldTrim =
            when (trim) {
                Trim.NONE -> false
                Trim.HEADERS -> isHeader
                Trim.FIELDS -> !isHeader
                Trim.ALL -> true
            }

        while (offset < bytes.size) {
            // Check comment
            if (comment != null && bytes[offset] == comment) {
                while (offset < bytes.size && bytes[offset] != '\n'.code.toByte()) {
                    offset++
                }
                if (offset < bytes.size && bytes[offset] == '\n'.code.toByte()) {
                    offset++
                    line++
                }
                continue
            }
            break
        }

        if (offset >= bytes.size) {
            return Result.success(false)
        }

        val startPos = Position(offset.toULong(), line, recordNum)
        record.setPosition(startPos)

        var inQuote = false
        val currentField = ArrayList<Byte>()
        var fieldHasQuote = false

        while (offset < bytes.size) {
            val b = bytes[offset]

            if (inQuote) {
                if (escape != null && b == escape && offset + 1 < bytes.size) {
                    val next = bytes[offset + 1]
                    if (next == quote || next == escape) {
                        currentField.add(next)
                        offset += 2
                        continue
                    }
                }
                if (b == quote) {
                    if (doubleQuote && offset + 1 < bytes.size && bytes[offset + 1] == quote) {
                        currentField.add(quote)
                        offset += 2
                        continue
                    }
                    inQuote = false
                    fieldHasQuote = true
                    offset++
                    continue
                }
                if (b == '\n'.code.toByte()) {
                    line++
                }
                currentField.add(b)
                offset++
                continue
            }

            // Not in quote
            if (b == quote && currentField.isEmpty() && !fieldHasQuote) {
                inQuote = true
                offset++
                continue
            }

            if (b == delimiter) {
                addFieldToRecord(record, currentField, shouldTrim, fieldHasQuote)
                currentField.clear()
                fieldHasQuote = false
                offset++
                continue
            }

            if (b == '\n'.code.toByte()) {
                offset++
                line++
                if (currentField.isNotEmpty() && currentField.last() == '\r'.code.toByte()) {
                    currentField.removeAt(currentField.size - 1)
                }
                break
            }

            if (b == '\r'.code.toByte()) {
                if (offset + 1 < bytes.size && bytes[offset + 1] == '\n'.code.toByte()) {
                    offset += 2
                    line++
                    break
                }
                offset++
                line++
                break
            }

            currentField.add(b)
            offset++
        }

        addFieldToRecord(record, currentField, shouldTrim, fieldHasQuote)

        if (!flexible) {
            val currentLen = record.len()
            val expected = expectedFieldCount
            if (expected != null && currentLen != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            pos = startPos,
                            expectedLen = expected.toULong(),
                            len = currentLen.toULong(),
                        ),
                    ),
                )
            }
            expectedFieldCount = currentLen
        }

        recordNum++
        return Result.success(true)
    }

    private fun addFieldToRecord(
        record: ByteRecord,
        fieldBytes: List<Byte>,
        shouldTrim: Boolean,
        fieldHasQuote: Boolean,
    ) {
        var arr = ByteArray(fieldBytes.size) { fieldBytes[it] }
        if (shouldTrim && !fieldHasQuote) {
            var start = 0
            while (start < arr.size && isAsciiWhitespace(arr[start])) start++
            var end = arr.size
            while (end > start && isAsciiWhitespace(arr[end - 1])) end--
            arr = arr.copyOfRange(start, end)
        }
        record.pushField(arr)
    }

    public fun addRecord(record: StringRecord): Result<Unit> = Result.success(Unit)

    public companion object {
        public fun new(): Reader = ReaderBuilder.new().fromReader(ByteArray(0))

        public fun default(): Reader = new()

        public fun fromReader(bytes: ByteArray): Reader =
            ReaderBuilder.new().fromReader(bytes)

        public fun fromString(string: String): Reader =
            ReaderBuilder.new().fromString(string)

        public fun fromPath(path: String): Reader =
            ReaderBuilder.new().fromPath(path)

        public typealias Item = Result<StringRecord>

        private fun isAsciiWhitespace(b: Byte): Boolean {
            val ub = b.toInt() and 0xFF
            return ub == 0x20 || ub == 0x09 || ub == 0x0A || ub == 0x0D || ub == 0x0C || ub == 0x0B
        }
    }
}

/**
 * A borrowed iterator over records as strings.
 */
public class StringRecordsIter internal constructor(
    private val rdr: Reader,
) : Iterator<Result<StringRecord>>,
    Iterable<Result<StringRecord>> {
    private val rec = StringRecord()

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    override fun iterator(): Iterator<Result<StringRecord>> = this

    override fun hasNext(): Boolean = !rdr.isDone()

    override fun next(): Result<StringRecord> {
        if (!hasNext()) throw NoSuchElementException("No more records")
        val res = rdr.readRecord(rec)
        if (res.isFailure) return Result.failure(res.exceptionOrNull()!!)
        if (!res.getOrThrow()) {
            throw NoSuchElementException("No more records")
        }
        return Result.success(rec.clone())
    }
}

/**
 * An owned iterator over records as strings.
 */
public class StringRecordsIntoIter internal constructor(
    private val rdr: Reader,
) : Iterator<Result<StringRecord>>,
    Iterable<Result<StringRecord>> {
    private val iter = StringRecordsIter(rdr)

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    public fun intoReader(): Reader = rdr

    override fun iterator(): Iterator<Result<StringRecord>> = iter

    override fun hasNext(): Boolean = iter.hasNext()

    override fun next(): Result<StringRecord> {
        if (!hasNext()) throw NoSuchElementException("No more records")
        return iter.next()
    }
}

/**
 * A borrowed iterator over records as raw bytes.
 */
public class ByteRecordsIter internal constructor(
    private val rdr: Reader,
) : Iterator<Result<ByteRecord>>,
    Iterable<Result<ByteRecord>> {
    private val rec = ByteRecord()

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    override fun iterator(): Iterator<Result<ByteRecord>> = this

    override fun hasNext(): Boolean = !rdr.isDone()

    override fun next(): Result<ByteRecord> {
        if (!hasNext()) throw NoSuchElementException("No more records")
        val res = rdr.readByteRecord(rec)
        if (res.isFailure) return Result.failure(res.exceptionOrNull()!!)
        if (!res.getOrThrow()) {
            throw NoSuchElementException("No more records")
        }
        return Result.success(rec.clone())
    }
}

/**
 * An owned iterator over records as raw bytes.
 */
public class ByteRecordsIntoIter internal constructor(
    private val rdr: Reader,
) : Iterator<Result<ByteRecord>>,
    Iterable<Result<ByteRecord>> {
    private val iter = ByteRecordsIter(rdr)

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    public fun intoReader(): Reader = rdr

    override fun iterator(): Iterator<Result<ByteRecord>> = iter

    override fun hasNext(): Boolean = iter.hasNext()

    override fun next(): Result<ByteRecord> {
        if (!hasNext()) throw NoSuchElementException("No more records")
        return iter.next()
    }
}

/**
 * An iterator over deserialized records.
 */
public class DeserializeRecordsIter<D> internal constructor(
    private val rdr: Reader,
    private val deserializer: kotlinx.serialization.DeserializationStrategy<D>,
) : Iterator<Result<D>>,
    Iterable<Result<D>> {
    private val stringIter = StringRecordsIter(rdr)
    private var headers: StringRecord? = null
    private var headersRead = false

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    override fun iterator(): Iterator<Result<D>> = this

    override fun hasNext(): Boolean = stringIter.hasNext()

    override fun next(): Result<D> {
        if (!headersRead) {
            headers = rdr.headers().getOrNull()
            headersRead = true
        }
        val recordRes = stringIter.next()
        val record = recordRes.getOrElse { return Result.failure(it) }
        return record.deserialize(deserializer, headers)
    }
}

/**
 * An owned iterator over deserialized records.
 */
public class DeserializeRecordsIntoIter<D> internal constructor(
    private val rdr: Reader,
    private val deserializer: kotlinx.serialization.DeserializationStrategy<D>,
) : Iterator<Result<D>>,
    Iterable<Result<D>> {
    private val iter = DeserializeRecordsIter(rdr, deserializer)

    public fun reader(): Reader = rdr

    public fun readerMut(): Reader = rdr

    public fun intoReader(): Reader = rdr

    override fun iterator(): Iterator<Result<D>> = iter

    override fun hasNext(): Boolean = iter.hasNext()

    override fun next(): Result<D> {
        if (!hasNext()) throw NoSuchElementException("No more records")
        return iter.next()
    }
}

/**
 * Testing helper to create string from byte array.
 */
public fun s(b: ByteArray): String = b.decodeToString()

/**
 * Testing helper to create Position.
 */
public fun newpos(byte: ULong, line: ULong, record: ULong): Position = Position(byte, line, record)

internal enum class ReaderState {
    Start,
    Record,
}

internal enum class ReaderEofState {
    None,
    Eof,
}

public typealias Headers = StringRecord
public typealias ReaderItem = Result<StringRecord>
