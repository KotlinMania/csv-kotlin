// port-lint: source writer.rs
package io.github.kotlinmania.csv

/**
 * Builds a CSV writer with various configuration knobs.
 */
public class WriterBuilder {
    private var capacity: Int = 8 * 1024
    private var flexible: Boolean = false
    private var hasHeaders: Boolean = true
    private var delimiter: Byte = ','.code.toByte()
    private var quote: Byte = '"'.code.toByte()
    private var escape: Byte = '"'.code.toByte()
    private var doubleQuote: Boolean = true
    private var quoteStyle: QuoteStyle = QuoteStyle.NECESSARY
    private var comment: Byte? = null
    private var terminator: Terminator = Terminator.CRLF

    public fun delimiter(delimiter: Byte): WriterBuilder {
        this.delimiter = delimiter
        return this
    }

    public fun delimiter(delimiter: Char): WriterBuilder =
        delimiter(delimiter.code.toByte())

    public fun hasHeaders(yes: Boolean): WriterBuilder {
        this.hasHeaders = yes
        return this
    }

    public fun flexible(yes: Boolean): WriterBuilder {
        this.flexible = yes
        return this
    }

    public fun quote(quote: Byte): WriterBuilder {
        this.quote = quote
        return this
    }

    public fun quote(quote: Char): WriterBuilder =
        quote(quote.code.toByte())

    public fun escape(escape: Byte): WriterBuilder {
        this.escape = escape
        return this
    }

    public fun escape(escape: Char): WriterBuilder =
        escape(escape.code.toByte())

    public fun doubleQuote(yes: Boolean): WriterBuilder {
        this.doubleQuote = yes
        return this
    }

    public fun quoteStyle(style: QuoteStyle): WriterBuilder {
        this.quoteStyle = style
        return this
    }

    public fun comment(comment: Byte?): WriterBuilder {
        this.comment = comment
        return this
    }

    public fun comment(comment: Char?): WriterBuilder =
        comment(comment?.code?.toByte())

    public fun terminator(term: Terminator): WriterBuilder {
        this.terminator = term
        return this
    }

    public fun bufferCapacity(capacity: Int): WriterBuilder {
        this.capacity = capacity
        return this
    }

    public fun fromWriter(): Writer =
        Writer(
            capacity = capacity,
            delimiter = delimiter,
            hasHeaders = hasHeaders,
            flexible = flexible,
            quote = quote,
            escape = escape,
            doubleQuote = doubleQuote,
            quoteStyle = quoteStyle,
            comment = comment,
            terminator = terminator,
        )

    public companion object {
        public fun new(): WriterBuilder = WriterBuilder()

        public fun default(): WriterBuilder = new()
    }
}

/**
 * A fast, flexible CSV writer.
 */
public class Writer internal constructor(
    private val capacity: Int,
    private val delimiter: Byte,
    private val hasHeaders: Boolean,
    private val flexible: Boolean,
    private val quote: Byte,
    private val escape: Byte,
    private val doubleQuote: Boolean,
    private val quoteStyle: QuoteStyle,
    private val comment: Byte?,
    private val terminator: Terminator,
) {
    private val buffer = ArrayList<Byte>(capacity)
    private var expectedFieldCount: Int? = null
    private var fieldCountInCurrentRecord: Int = 0
    private var recordStarted: Boolean = false

    public fun flush(): Result<Unit> = Result.success(Unit)

    public fun flushBuf(): Result<Unit> = Result.success(Unit)

    public fun getRef(): ByteArray = buffer.toByteArray()

    public fun intoInner(): Result<ByteArray> {
        val result = buffer.toByteArray()
        return Result.success(result)
    }

    public fun intoString(): Result<String> = Result.success(asString())

    public fun readable(): Boolean = false

    public fun writable(): Boolean = true

    public fun written(): Long = buffer.size.toLong()

    public fun write(bytes: ByteArray): Result<Unit> {
        for (b in bytes) {
            buffer.add(b)
        }
        return Result.success(Unit)
    }

    public fun asByteArray(): ByteArray = buffer.toByteArray()

    public fun asString(): String = buffer.toByteArray().decodeToString()

    public fun wtrAsString(): String = asString()

    public fun clear() {
        buffer.clear()
        expectedFieldCount = null
        fieldCountInCurrentRecord = 0
        recordStarted = false
    }

    public fun checkFieldCount(count: Int): Result<Unit> {
        if (!flexible) {
            val expected = expectedFieldCount
            if (expected != null && count != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            null,
                            expected.toULong(),
                            count.toULong(),
                        ),
                    ),
                )
            }
            expectedFieldCount = count
        }
        return Result.success(Unit)
    }

    public fun writeByteRecord(record: ByteRecord): Result<Unit> {
        val count = record.len()
        if (!flexible) {
            val expected = expectedFieldCount
            if (expected != null && count != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            null,
                            expected.toULong(),
                            count.toULong(),
                        ),
                    ),
                )
            }
            expectedFieldCount = count
        }

        for (i in 0 until count) {
            if (i > 0) {
                buffer.add(delimiter)
            }
            writeFieldBytes(record[i]!!)
        }
        writeTerminator()
        fieldCountInCurrentRecord = 0
        recordStarted = false
        return Result.success(Unit)
    }

    public fun writeRecord(record: StringRecord): Result<Unit> =
        writeByteRecord(record.asByteRecord())

    public fun writeRecord(record: Iterable<CharSequence>): Result<Unit> {
        val fields = record.toList()
        val count = fields.size
        if (!flexible) {
            val expected = expectedFieldCount
            if (expected != null && count != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            null,
                            expected.toULong(),
                            count.toULong(),
                        ),
                    ),
                )
            }
            expectedFieldCount = count
        }

        for (i in 0 until count) {
            if (i > 0) {
                buffer.add(delimiter)
            }
            writeFieldBytes(fields[i].toString().encodeToByteArray())
        }
        writeTerminator()
        fieldCountInCurrentRecord = 0
        recordStarted = false
        return Result.success(Unit)
    }

    public fun writeRecord(record: Array<String>): Result<Unit> =
        writeRecord(record.asIterable())

    public fun writeField(field: CharSequence): Result<Unit> =
        writeByteField(field.toString().encodeToByteArray())

    public fun writeFieldImpl(field: ByteArray): Result<Unit> = writeByteField(field)

    public fun writeDelimiter() {
        buffer.add(delimiter)
    }

    public fun writeTerminatorIntoBuffer() {
        writeTerminator()
    }

    public fun writeByteField(field: ByteArray): Result<Unit> {
        if (fieldCountInCurrentRecord > 0) {
            buffer.add(delimiter)
        }
        writeFieldBytes(field)
        fieldCountInCurrentRecord++
        recordStarted = true
        return Result.success(Unit)
    }

    public fun endRecord(): Result<Unit> {
        if (!flexible) {
            val expected = expectedFieldCount
            if (expected != null && fieldCountInCurrentRecord != expected) {
                return Result.failure(
                    CsvError(
                        ErrorKind.UnequalLengths(
                            null,
                            expected.toULong(),
                            fieldCountInCurrentRecord.toULong(),
                        ),
                    ),
                )
            }
            expectedFieldCount = fieldCountInCurrentRecord
        }
        writeTerminator()
        fieldCountInCurrentRecord = 0
        recordStarted = false
        return Result.success(Unit)
    }

    private fun writeFieldBytes(field: ByteArray) {
        val shouldQuote = shouldQuote(field)
        if (shouldQuote) {
            buffer.add(quote)
            for (b in field) {
                if (b == quote) {
                    if (doubleQuote) {
                        buffer.add(quote)
                        buffer.add(quote)
                    } else {
                        buffer.add(escape)
                        buffer.add(quote)
                    }
                } else if (b == escape && !doubleQuote) {
                    buffer.add(escape)
                    buffer.add(escape)
                } else {
                    buffer.add(b)
                }
            }
            buffer.add(quote)
        } else {
            for (b in field) {
                buffer.add(b)
            }
        }
    }

    private fun shouldQuote(field: ByteArray): Boolean =
        when (quoteStyle) {
            QuoteStyle.ALWAYS -> true
            QuoteStyle.NEVER -> false
            QuoteStyle.NON_NUMERIC -> {
                val str = field.decodeToString()
                if (str.toDoubleOrNull() == null && str.toLongOrNull() == null) {
                    true
                } else {
                    needsQuotes(field)
                }
            }
            QuoteStyle.NECESSARY -> needsQuotes(field)
        }

    private fun needsQuotes(field: ByteArray): Boolean {
        if (field.isEmpty()) return false
        if (comment != null && field.isNotEmpty() && field[0] == comment) return true
        for (b in field) {
            if (b == delimiter || b == quote || b == '\n'.code.toByte() || b == '\r'.code.toByte()) {
                return true
            }
        }
        return false
    }

    private fun writeTerminator() {
        when (terminator) {
            is Terminator.CRLF -> buffer.add('\n'.code.toByte())
            is Terminator.Any -> buffer.add(terminator.byte)
        }
    }

    public companion object {
        public fun new(): Writer = WriterBuilder.new().fromWriter()

        public fun default(): Writer = new()
    }
}

/**
 * HeaderState encodes a small state machine for handling header writes.
 */
public enum class HeaderState {
    WRITE,
    DID_WRITE,
    DID_NOT_WRITE,
    NONE,
}

/**
 * Writer state machine representation.
 */
public class WriterState(
    public var header: HeaderState = HeaderState.WRITE,
    public var flexible: Boolean = false,
    public var firstFieldCount: ULong? = null,
    public var fieldsWritten: ULong = 0uL,
    public var panicked: Boolean = false,
)

/**
 * Internal buffer for buffering writes.
 */
public class Buffer(
    public val bytes: ArrayList<Byte> = ArrayList(),
) {
    public fun clear() {
        bytes.clear()
    }

    public fun len(): Int = bytes.size

    public fun isEmpty(): Boolean = bytes.isEmpty()
}
