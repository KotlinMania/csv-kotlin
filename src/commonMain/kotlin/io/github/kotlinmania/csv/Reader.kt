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

    public companion object {
        public fun new(): ReaderBuilder = ReaderBuilder()
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

    public fun byteRecords(): Sequence<Result<ByteRecord>> =
        sequence {
            while (true) {
                val rec = ByteRecord()
                val res = readByteRecord(rec)
                if (res.isFailure) {
                    yield(Result.failure(res.exceptionOrNull()!!))
                    break
                }
                if (!res.getOrThrow()) break
                yield(Result.success(rec))
            }
        }

    public fun records(): Sequence<Result<StringRecord>> =
        sequence {
            while (true) {
                val rec = StringRecord()
                val res = readRecord(rec)
                if (res.isFailure) {
                    yield(Result.failure(res.exceptionOrNull()!!))
                    break
                }
                if (!res.getOrThrow()) break
                yield(Result.success(rec))
            }
        }

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

            val isTerminator =
                when (terminator) {
                    is Terminator.CRLF -> b == '\n'.code.toByte() || (b == '\r'.code.toByte() && offset + 1 < bytes.size && bytes[offset + 1] == '\n'.code.toByte())
                    is Terminator.Any -> b == terminator.byte
                }

            if (isTerminator) {
                if (b == '\r'.code.toByte() && offset + 1 < bytes.size && bytes[offset + 1] == '\n'.code.toByte()) {
                    offset += 2
                    line++
                } else if (b == '\n'.code.toByte()) {
                    offset++
                    line++
                } else {
                    offset++
                }
                addFieldToRecord(record, currentField, shouldTrim, fieldHasQuote)
                break
            }

            currentField.add(b)
            offset++
        }

        if (offset >= bytes.size && (currentField.isNotEmpty() || fieldHasQuote || record.len() > 0)) {
            if (currentField.isNotEmpty() || fieldHasQuote) {
                addFieldToRecord(record, currentField, shouldTrim, fieldHasQuote)
            }
        }

        if (record.len() == 0 && offset >= bytes.size) {
            return Result.success(false)
        }

        if (!flexible) {
            val currentLen = record.len()
            val expected = expectedFieldCount
            if (expected != null && currentLen != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            record.position(),
                            expected.toULong(),
                            currentLen.toULong(),
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

    public companion object {
        public fun fromReader(bytes: ByteArray): Reader =
            ReaderBuilder.new().fromReader(bytes)

        public fun fromString(string: String): Reader =
            ReaderBuilder.new().fromString(string)

        private fun isAsciiWhitespace(b: Byte): Boolean {
            val ub = b.toInt() and 0xFF
            return ub == 0x20 || ub == 0x09 || ub == 0x0A || ub == 0x0D || ub == 0x0C || ub == 0x0B
        }
    }
}
