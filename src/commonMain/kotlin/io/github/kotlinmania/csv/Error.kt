// port-lint: source error.rs
package io.github.kotlinmania.csv

public typealias Result<T> = kotlin.Result<T>
public typealias Error = CsvError

/**
 * An error that can occur when processing CSV data.
 *
 * This error can happen when writing or reading CSV data.
 */
public class CsvError(
    private val kind: ErrorKind,
) : Exception(kind.toString()) {
    public fun kind(): ErrorKind = kind

    public fun intoKind(): ErrorKind = kind

    public fun isIoError(): Boolean = kind is ErrorKind.Io

    public fun position(): Position? = kind.position()

    public fun fmt(): String = toString()

    override fun toString(): String =
        when (kind) {
            is ErrorKind.Io -> "CSV IO error: ${kind.message}"
            is ErrorKind.Utf8 -> {
                val pos = kind.pos
                if (pos == null) {
                    "CSV parse error: field ${kind.err.field()}: ${kind.err}"
                } else {
                    "CSV parse error: record ${pos.record()} (line ${pos.line()}, field: ${kind.err.field()}, byte: ${pos.byte()}): ${kind.err}"
                }
            }
            is ErrorKind.UnequalLengths -> {
                val pos = kind.pos
                if (pos == null) {
                    "CSV error: found record with ${kind.len} fields, but the previous record has ${kind.expectedLen} fields"
                } else {
                    "CSV error: record ${pos.record()} (line: ${pos.line()}, byte: ${pos.byte()}): found record with ${kind.len} fields, but the previous record has ${kind.expectedLen} fields"
                }
            }
            is ErrorKind.Seek -> "CSV error: cannot access headers of CSV data when the parser was seeked before the first record could be read"
            is ErrorKind.Serialize -> "CSV write error: ${kind.message}"
            is ErrorKind.Deserialize -> {
                val pos = kind.pos
                if (pos == null) {
                    "CSV deserialize error: ${kind.message}"
                } else {
                    "CSV deserialize error: record ${pos.record()} (line: ${pos.line()}, byte: ${pos.byte()}): ${kind.message}"
                }
            }
        }

    public fun source(): Throwable? = cause

    public companion object {
        public fun new(kind: ErrorKind): CsvError = CsvError(kind)

        public fun from(kind: ErrorKind): CsvError = CsvError(kind)

        public fun from(cause: Throwable): CsvError =
            if (cause is CsvError) cause else CsvError(ErrorKind.Io(cause.message ?: "IO error", cause))
    }
}

/**
 * The specific type of an error.
 */
public sealed class ErrorKind {
    public open fun position(): Position? =
        when (this) {
            is Utf8 -> pos
            is UnequalLengths -> pos
            is Deserialize -> pos
            else -> null
        }

    public open fun fmt(): String = toString()

    public data class Io(
        val message: String,
        val cause: Throwable? = null,
    ) : ErrorKind()

    public data class Utf8(
        val pos: Position?,
        val err: Utf8Error,
    ) : ErrorKind()

    public data class UnequalLengths(
        val pos: Position?,
        val expectedLen: ULong,
        val len: ULong,
    ) : ErrorKind()

    public data object Seek : ErrorKind()

    public data class Serialize(
        val message: String,
    ) : ErrorKind()

    public data class Deserialize(
        val pos: Position?,
        val message: String,
    ) : ErrorKind()
}

/**
 * A UTF-8 validation error during record conversion.
 */
public class FromUtf8Error(
    private val record: ByteRecord,
    private val err: Utf8Error,
) : Exception(err.toString()) {
    public fun intoByteRecord(): ByteRecord = record

    public fun utf8Error(): Utf8Error = err

    public fun fmt(): String = toString()

    public fun intoParts(): Pair<ByteRecord, Utf8Error> = record to err

    public companion object {
        public fun new(record: ByteRecord, err: Utf8Error): FromUtf8Error = FromUtf8Error(record, err)
    }
}

/**
 * A UTF-8 validation error.
 */
public data class Utf8Error(
    val field: Int,
    val validUpTo: Int,
) {
    public fun field(): Int = field

    public fun validUpTo(): Int = validUpTo

    public fun fmt(): String = toString()

    override fun toString(): String =
        "invalid utf-8: invalid UTF-8 in field $field near byte index $validUpTo"
}

public fun newUtf8Error(field: Int, validUpTo: Int): Utf8Error = Utf8Error(field, validUpTo)

/**
 * [IntoInnerError] occurs when consuming a [Writer] fails.
 */
public class IntoInnerError(
    private val writer: Any?,
    private val error: Throwable,
) : Exception(error.message, error) {
    public fun error(): Throwable = error

    public fun intoError(): Throwable = error

    public fun intoInner(): Any? = writer

    public fun fmt(): String = toString()

    public companion object {
        public fun new(writer: Any?, error: Throwable): IntoInnerError = IntoInnerError(writer, error)
    }
}
