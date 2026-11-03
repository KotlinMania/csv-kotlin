// port-lint: source deserializer.rs
package io.github.kotlinmania.csv

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.StdError
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.DeserializeSeed
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.MapAccess
import io.github.kotlinmania.serdecore.de.SeqAccess
import io.github.kotlinmania.serdecore.de.Unexpected
import io.github.kotlinmania.serdecore.de.VariantAccess
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.value.BytesDeserializer
import io.github.kotlinmania.serdecore.de.value.StrDeserializer

/**
 * Deserialize a string record.
 */
public fun <D> deserializeStringRecord(
    record: StringRecord,
    deserializer: Deserialize<D>,
    headers: StringRecord? = null,
): Result<D> {
    val deser = DeRecordWrap(DeStringRecord(record, headers))
    return deserializer.deserialize(deser).fold(
        onSuccess = { Result.success(it) },
        onFailure = { err ->
            val dErr = deser.error(DeserializeErrorKind.Message(err.message))
            Result.failure(CsvError.new(ErrorKind.Deserialize(record.position()?.clone(), dErr)))
        },
    )
}

/**
 * Deserialize a byte record.
 */
public fun <D> deserializeByteRecord(
    record: ByteRecord,
    deserializer: Deserialize<D>,
    headers: ByteRecord? = null,
): Result<D> {
    val deser = DeRecordWrap(DeByteRecord(record, headers))
    return deserializer.deserialize(deser).fold(
        onSuccess = { Result.success(it) },
        onFailure = { err ->
            val dErr = deser.error(DeserializeErrorKind.Message(err.message))
            Result.failure(CsvError.new(ErrorKind.Deserialize(record.position()?.clone(), dErr)))
        },
    )
}

internal interface DeRecord {
    fun hasHeaders(): Boolean

    fun nextHeader(): Result<String?>

    fun nextHeaderBytes(): Result<ByteArray?>

    fun nextField(): Result<String>

    fun nextFieldBytes(): Result<ByteArray>

    fun peekField(): ByteArray?

    fun error(kind: DeserializeErrorKind): DeserializeError

    fun currentField(): ULong

    fun <V> inferDeserialize(visitor: Visitor<V>): SerdeResult<V>
}

internal class DeRecordWrap<T : DeRecord>(
    val inner: T,
) : Deserializer,
    EnumAccess,
    VariantAccess,
    SeqAccess,
    MapAccess {
    fun hasHeaders(): Boolean = inner.hasHeaders()

    fun nextHeader(): Result<String?> = inner.nextHeader()

    fun nextHeaderBytes(): Result<ByteArray?> = inner.nextHeaderBytes()

    fun nextField(): Result<String> = inner.nextField()

    fun nextFieldBytes(): Result<ByteArray> = inner.nextFieldBytes()

    fun peekField(): ByteArray? = inner.peekField()

    fun error(kind: DeserializeErrorKind): DeserializeError = inner.error(kind)

    fun currentField(): ULong = inner.currentField()

    fun <V> inferDeserialize(visitor: Visitor<V>): SerdeResult<V> = inner.inferDeserialize(visitor)

    override fun <V> deserializeAny(visitor: Visitor<V>): SerdeResult<V> =
        inferDeserialize(visitor)

    override fun <V> deserializeBool(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val b =
            when (field) {
                "true" -> true
                "false" -> false
                else -> return fail(error(DeserializeErrorKind.ParseBool("invalid boolean: $field")))
            }
        return visitor.visitBool(b)
    }

    override fun <V> deserializeI8(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseSigned(field)?.toByte()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitI8(num)
    }

    override fun <V> deserializeI16(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseSigned(field)?.toShort()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitI16(num)
    }

    override fun <V> deserializeI32(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseSigned(field)?.toInt()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitI32(num)
    }

    override fun <V> deserializeI64(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseSigned(field)
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitI64(num)
    }

    override fun <V> deserializeI128(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        if (tryNegativeInteger128(field) == null && tryPositiveInteger128(field) == null) {
            return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        }
        return visitor.visitI128(field)
    }

    override fun <V> deserializeU8(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseUnsigned(field)?.toUByte()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitU8(num)
    }

    override fun <V> deserializeU16(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseUnsigned(field)?.toUShort()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitU16(num)
    }

    override fun <V> deserializeU32(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseUnsigned(field)?.toUInt()
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitU32(num)
    }

    override fun <V> deserializeU64(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            parseUnsigned(field)
                ?: return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        return visitor.visitU64(num)
    }

    override fun <V> deserializeU128(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        if (tryPositiveInteger128(field) == null) {
            return fail(error(DeserializeErrorKind.ParseInt("invalid integer: $field")))
        }
        return visitor.visitU128(field)
    }

    override fun <V> deserializeF32(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            field.toFloatOrNull()
                ?: return fail(error(DeserializeErrorKind.ParseFloat("invalid float: $field")))
        return visitor.visitF32(num)
    }

    override fun <V> deserializeF64(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        val num =
            field.toDoubleOrNull()
                ?: return fail(error(DeserializeErrorKind.ParseFloat("invalid float: $field")))
        return visitor.visitF64(num)
    }

    override fun <V> deserializeChar(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        if (field.length != 1) {
            return fail(error(DeserializeErrorKind.Message("expected single character but got ${field.length} characters in '$field'")))
        }
        return visitor.visitChar(field[0])
    }

    override fun <V> deserializeStr(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        return visitor.visitStr(field)
    }

    override fun <V> deserializeString(visitor: Visitor<V>): SerdeResult<V> {
        val field = nextField().getOrElse { return fail(it) }
        return visitor.visitString(field)
    }

    override fun <V> deserializeBytes(visitor: Visitor<V>): SerdeResult<V> {
        val bytes = nextFieldBytes().getOrElse { return fail(it) }
        return visitor.visitBytes(bytes)
    }

    override fun <V> deserializeByteBuf(visitor: Visitor<V>): SerdeResult<V> {
        val bytes = nextFieldBytes().getOrElse { return fail(it) }
        return visitor.visitByteBuf(bytes)
    }

    override fun <V> deserializeOption(visitor: Visitor<V>): SerdeResult<V> {
        val peek = peekField()
        return when {
            peek == null -> visitor.visitNone()
            peek.isEmpty() -> {
                nextField()
                visitor.visitNone()
            }
            else -> visitor.visitSome(this)
        }
    }

    override fun <V> deserializeUnit(visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitUnit()

    override fun <V> deserializeUnitStruct(name: String, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitUnit()

    override fun <V> deserializeNewtypeStruct(name: String, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitNewtypeStruct(this)

    override fun <V> deserializeSeq(visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitSeq(this)

    override fun <V> deserializeTuple(len: Int, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitSeq(this)

    override fun <V> deserializeTupleStruct(name: String, len: Int, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitSeq(this)

    override fun <V> deserializeMap(visitor: Visitor<V>): SerdeResult<V> =
        if (!hasHeaders()) visitor.visitSeq(this) else visitor.visitMap(this)

    override fun <V> deserializeStruct(name: String, fields: List<String>, visitor: Visitor<V>): SerdeResult<V> =
        if (!hasHeaders()) visitor.visitSeq(this) else visitor.visitMap(this)

    override fun <V> deserializeIdentifier(visitor: Visitor<V>): SerdeResult<V> =
        fail(error(DeserializeErrorKind.Unsupported("deserialize_identifier")))

    override fun <V> deserializeEnum(name: String, variants: List<String>, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitEnum(this)

    override fun <V> deserializeIgnoredAny(visitor: Visitor<V>): SerdeResult<V> {
        nextFieldBytes().getOrElse { return fail(it) }
        return visitor.visitUnit()
    }

    override fun <V> variantSeed(seed: DeserializeSeed<V>): SerdeResult<Pair<V, VariantAccess>> {
        val variantName = nextField().getOrElse { return fail(it) }
        val strDeser = StrDeserializer.new(variantName)
        return seed.deserialize(strDeser).map { it to this }
    }

    override fun unitVariant(): SerdeResult<Unit> = SerdeResult.success(Unit)

    override fun <U> newtypeVariantSeed(seed: DeserializeSeed<U>): SerdeResult<U> =
        SerdeResult.failure(SerdeError.invalidType(Unexpected.UnitVariant) { "newtype variant" })

    override fun <V> tupleVariant(len: Int, visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(SerdeError.invalidType(Unexpected.UnitVariant) { "tuple variant" })

    override fun <V> structVariant(fields: List<String>, visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(SerdeError.invalidType(Unexpected.UnitVariant) { "struct variant" })

    override fun sizeHint(): Int? = null

    override fun <T> nextElementSeed(seed: DeserializeSeed<T>): SerdeResult<T?> =
        if (peekField() == null) {
            SerdeResult.success(null)
        } else {
            seed.deserialize(this).map { it }
        }

    override fun <K> nextKeySeed(seed: DeserializeSeed<K>): SerdeResult<K?> {
        val headerBytes = nextHeaderBytes().getOrElse { return fail(it) } ?: return SerdeResult.success(null)
        val deser = BytesDeserializer.new(headerBytes)
        return seed.deserialize(deser).map { it }
    }

    override fun <V> nextValueSeed(seed: DeserializeSeed<V>): SerdeResult<V> =
        seed.deserialize(this)

    private fun <V> fail(err: Throwable): SerdeResult<V> {
        val serdeErr = if (err is DeserializeError) SerdeError.custom(err.message) else SerdeError.custom(err.message ?: "deserialization error")
        return SerdeResult.failure(serdeErr)
    }
}

internal class DeStringRecord(
    private val record: StringRecord,
    headersRecord: StringRecord?,
) : DeRecord {
    private var fieldIndex: Int = 0
    private var headerIndex: Int = 0
    private val headers: StringRecord? = headersRecord

    override fun hasHeaders(): Boolean = headers != null

    override fun nextHeader(): Result<String?> {
        val h = headers ?: return Result.success(null)
        if (headerIndex >= h.len()) return Result.success(null)
        val field = h.get(headerIndex)
        headerIndex++
        return Result.success(field)
    }

    override fun nextHeaderBytes(): Result<ByteArray?> {
        val str = nextHeader().getOrElse { return Result.failure(it) } ?: return Result.success(null)
        return Result.success(str.encodeToByteArray())
    }

    override fun nextField(): Result<String> {
        if (fieldIndex >= record.len()) {
            return Result.failure(error(DeserializeErrorKind.UnexpectedEndOfRow))
        }
        val f = record.get(fieldIndex) ?: return Result.failure(error(DeserializeErrorKind.UnexpectedEndOfRow))
        fieldIndex++
        return Result.success(f)
    }

    override fun nextFieldBytes(): Result<ByteArray> {
        val s = nextField().getOrElse { return Result.failure(it) }
        return Result.success(s.encodeToByteArray())
    }

    override fun peekField(): ByteArray? {
        if (fieldIndex >= record.len()) return null
        val f = record.get(fieldIndex) ?: return null
        return f.encodeToByteArray()
    }

    override fun currentField(): ULong =
        if (fieldIndex == 0) 0uL else (fieldIndex - 1).toULong()

    override fun error(kind: DeserializeErrorKind): DeserializeError =
        DeserializeError(
            if (fieldIndex == 0) null else (fieldIndex - 1).toULong(),
            kind,
        )

    override fun <V> inferDeserialize(visitor: Visitor<V>): SerdeResult<V> {
        val x =
            nextField().getOrElse {
                val err = it as? DeserializeError ?: error(DeserializeErrorKind.Message(it.message ?: ""))
                return SerdeResult.failure(SerdeError.custom(err.message))
            }
        return when {
            x == "true" -> visitor.visitBool(true)
            x == "false" -> visitor.visitBool(false)
            tryPositiveInteger64(x) != null -> visitor.visitU64(tryPositiveInteger64(x)!!)
            tryNegativeInteger64(x) != null -> visitor.visitI64(tryNegativeInteger64(x)!!)
            tryPositiveInteger128(x) != null -> visitor.visitU128(x)
            tryNegativeInteger128(x) != null -> visitor.visitI128(x)
            tryFloat(x) != null -> visitor.visitF64(tryFloat(x)!!)
            else -> visitor.visitStr(x)
        }
    }
}

internal class DeByteRecord(
    private val record: ByteRecord,
    headersRecord: ByteRecord?,
) : DeRecord {
    private var fieldIndex: Int = 0
    private var headerIndex: Int = 0
    private val headers: ByteRecord? = headersRecord

    override fun hasHeaders(): Boolean = headers != null

    override fun nextHeader(): Result<String?> {
        val bytes = nextHeaderBytes().getOrElse { return Result.failure(it) } ?: return Result.success(null)
        return try {
            Result.success(bytes.decodeToString())
        } catch (e: Exception) {
            Result.failure(error(DeserializeErrorKind.InvalidUtf8(Utf8Error(headerIndex - 1, 0))))
        }
    }

    override fun nextHeaderBytes(): Result<ByteArray?> {
        val h = headers ?: return Result.success(null)
        if (headerIndex >= h.len()) return Result.success(null)
        val field = h.get(headerIndex)
        headerIndex++
        return Result.success(field)
    }

    override fun nextField(): Result<String> {
        val bytes = nextFieldBytes().getOrElse { return Result.failure(it) }
        return try {
            Result.success(bytes.decodeToString())
        } catch (e: Exception) {
            Result.failure(error(DeserializeErrorKind.InvalidUtf8(Utf8Error(fieldIndex - 1, 0))))
        }
    }

    override fun nextFieldBytes(): Result<ByteArray> {
        if (fieldIndex >= record.len()) {
            return Result.failure(error(DeserializeErrorKind.UnexpectedEndOfRow))
        }
        val f = record.get(fieldIndex) ?: return Result.failure(error(DeserializeErrorKind.UnexpectedEndOfRow))
        fieldIndex++
        return Result.success(f)
    }

    override fun peekField(): ByteArray? {
        if (fieldIndex >= record.len()) return null
        return record.get(fieldIndex)
    }

    override fun currentField(): ULong =
        if (fieldIndex == 0) 0uL else (fieldIndex - 1).toULong()

    override fun error(kind: DeserializeErrorKind): DeserializeError =
        DeserializeError(
            if (fieldIndex == 0) null else (fieldIndex - 1).toULong(),
            kind,
        )

    override fun <V> inferDeserialize(visitor: Visitor<V>): SerdeResult<V> {
        val x =
            nextFieldBytes().getOrElse {
                val err = it as? DeserializeError ?: error(DeserializeErrorKind.Message(it.message ?: ""))
                return SerdeResult.failure(SerdeError.custom(err.message))
            }
        val s = runCatching { x.decodeToString() }.getOrNull()
        return when {
            s == "true" -> visitor.visitBool(true)
            s == "false" -> visitor.visitBool(false)
            s != null && tryPositiveInteger64(s) != null -> visitor.visitU64(tryPositiveInteger64(s)!!)
            s != null && tryNegativeInteger64(s) != null -> visitor.visitI64(tryNegativeInteger64(s)!!)
            s != null && tryPositiveInteger128(s) != null -> visitor.visitU128(s)
            s != null && tryNegativeInteger128(s) != null -> visitor.visitI128(s)
            s != null && tryFloat(s) != null -> visitor.visitF64(tryFloat(s)!!)
            s != null -> visitor.visitStr(s)
            else -> visitor.visitBytes(x)
        }
    }
}

/**
 * A Serde deserialization error.
 */
public class DeserializeError(
    private val fieldVal: ULong?,
    private val kindVal: DeserializeErrorKind,
) : Exception(formatMessage(fieldVal, kindVal)),
    StdError {
    public fun field(): ULong? = fieldVal

    public fun kind(): DeserializeErrorKind = kindVal

    override val message: String get() = formatMessage(fieldVal, kindVal)

    override fun toString(): String = message

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeserializeError) return false
        return fieldVal == other.fieldVal && kindVal == other.kindVal
    }

    override fun hashCode(): Int = (fieldVal?.hashCode() ?: 0) * 31 + kindVal.hashCode()

    public companion object {
        public fun custom(msg: String): DeserializeError =
            DeserializeError(null, DeserializeErrorKind.Message(msg))

        private fun formatMessage(field: ULong?, kind: DeserializeErrorKind): String =
            if (field != null) "field $field: $kind" else kind.toString()
    }
}

/**
 * The type of a Serde deserialization error.
 */
public sealed class DeserializeErrorKind {
    public class Message(
        public val msg: String,
    ) : DeserializeErrorKind() {
        override fun toString(): String = msg

        override fun equals(other: Any?): Boolean = other is Message && other.msg == msg

        override fun hashCode(): Int = msg.hashCode()
    }

    public class Unsupported(
        public val which: String,
    ) : DeserializeErrorKind() {
        override fun toString(): String = "unsupported deserializer method: $which"

        override fun equals(other: Any?): Boolean = other is Unsupported && other.which == which

        override fun hashCode(): Int = which.hashCode()
    }

    public data object UnexpectedEndOfRow : DeserializeErrorKind() {
        override fun toString(): String = "expected field, but got end of row"
    }

    public class InvalidUtf8(
        public val err: Utf8Error,
    ) : DeserializeErrorKind() {
        override fun toString(): String = err.toString()

        override fun equals(other: Any?): Boolean = other is InvalidUtf8 && other.err == err

        override fun hashCode(): Int = err.hashCode()
    }

    public class ParseBool(
        public val err: String,
    ) : DeserializeErrorKind() {
        override fun toString(): String = err

        override fun equals(other: Any?): Boolean = other is ParseBool && other.err == err

        override fun hashCode(): Int = err.hashCode()
    }

    public class ParseInt(
        public val err: String,
    ) : DeserializeErrorKind() {
        override fun toString(): String = err

        override fun equals(other: Any?): Boolean = other is ParseInt && other.err == err

        override fun hashCode(): Int = err.hashCode()
    }

    public class ParseFloat(
        public val err: String,
    ) : DeserializeErrorKind() {
        override fun toString(): String = err

        override fun equals(other: Any?): Boolean = other is ParseFloat && other.err == err

        override fun hashCode(): Int = err.hashCode()
    }
}

internal fun tryPositiveInteger128(s: String): String? =
    if (s.isNotEmpty() && s.all { it in '0'..'9' }) s else null

internal fun tryNegativeInteger128(s: String): String? =
    if (s.startsWith("-") && s.length > 1 && s.substring(1).all { it in '0'..'9' }) s else null

internal fun tryPositiveInteger64(s: String): ULong? =
    s.toULongOrNull()

internal fun tryNegativeInteger64(s: String): Long? =
    s.toLongOrNull()?.takeIf { it < 0 }

internal fun tryFloat(s: String): Double? =
    s.toDoubleOrNull()

private fun parseSigned(field: String): Long? =
    runCatching {
        if (field.startsWith("0x") || field.startsWith("0X")) {
            field.substring(2).toLong(16)
        } else {
            field.toLong()
        }
    }.getOrNull()

private fun parseUnsigned(field: String): ULong? =
    runCatching {
        if (field.startsWith("0x") || field.startsWith("0X")) {
            field.substring(2).toULong(16)
        } else {
            field.toULong()
        }
    }.getOrNull()
