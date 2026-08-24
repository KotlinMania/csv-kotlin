// port-lint: source deserializer.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.csv

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.native.HiddenFromObjC

/**
 * Deserializes a [StringRecord] into [T] using the given [DeserializationStrategy].
 */
@HiddenFromObjC
public fun <T> deserializeStringRecord(
    record: StringRecord,
    deserializer: DeserializationStrategy<T>,
    headers: StringRecord? = null,
): Result<T> = CsvDeserializer.deserialize(record, deserializer, headers)

/**
 * Deserializes a [StringRecord] into [T] using the default serializer.
 */
@HiddenFromObjC
public inline fun <reified T> deserializeStringRecord(
    record: StringRecord,
    headers: StringRecord? = null,
): Result<T> = CsvDeserializer.deserialize(record, kotlinx.serialization.serializer(), headers)

/**
 * Deserializes a [ByteRecord] into [T] using the given [DeserializationStrategy].
 */
@HiddenFromObjC
public fun <T> deserializeByteRecord(
    record: ByteRecord,
    deserializer: DeserializationStrategy<T>,
    headers: ByteRecord? = null,
): Result<T> = CsvDeserializer.deserialize(record, deserializer, headers)

/**
 * Deserializes a [ByteRecord] into [T] using the default serializer.
 */
@HiddenFromObjC
public inline fun <reified T> deserializeByteRecord(
    record: ByteRecord,
    headers: ByteRecord? = null,
): Result<T> = CsvDeserializer.deserialize(record, kotlinx.serialization.serializer(), headers)

/**
 * Deserializes values from CSV records using kotlinx.serialization.
 */
@HiddenFromObjC
public object CsvDeserializer {
    /**
     * Deserializes a [StringRecord] into [T] using the given [DeserializationStrategy].
     */
    public fun <T> deserialize(
        record: StringRecord,
        deserializer: DeserializationStrategy<T>,
        headers: StringRecord? = null,
    ): Result<T> =
        try {
            val decoder =
                CsvRecordDecoder(
                    fields = (0 until record.len()).map { record[it] ?: "" },
                    headers = headers?.let { h -> (0 until h.len()).map { h[it] ?: "" } },
                    position = record.position(),
                )
            Result.success(deserializer.deserialize(decoder))
        } catch (e: Exception) {
            Result.failure(
                if (e is CsvError) {
                    e
                } else {
                    CsvError(
                        ErrorKind.Deserialize(
                            pos = record.position(),
                            message = e.message ?: "Deserialization failed",
                        ),
                    )
                },
            )
        }

    /**
     * Deserializes a [ByteRecord] into [T] using the given [DeserializationStrategy].
     */
    public fun <T> deserialize(
        record: ByteRecord,
        deserializer: DeserializationStrategy<T>,
        headers: ByteRecord? = null,
    ): Result<T> =
        try {
            val fields = (0 until record.len()).map { record[it]?.decodeToString() ?: "" }
            val headerFields = headers?.let { h -> (0 until h.len()).map { h[it]?.decodeToString() ?: "" } }
            val decoder =
                CsvRecordDecoder(
                    fields = fields,
                    headers = headerFields,
                    position = record.position(),
                )
            Result.success(deserializer.deserialize(decoder))
        } catch (e: Exception) {
            Result.failure(
                if (e is CsvError) {
                    e
                } else {
                    CsvError(
                        ErrorKind.Deserialize(
                            pos = record.position(),
                            message = e.message ?: "Deserialization failed",
                        ),
                    )
                },
            )
        }
}

/**
 * An [AbstractDecoder] that reads elements sequentially or by header name from a list of CSV field strings.
 */
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
internal class CsvRecordDecoder(
    private val fields: List<String>,
    private val headers: List<String>?,
    private val position: Position?,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractDecoder() {
    private var currentIndex = 0
    private var currentField = 0
    private var isHeaderMapped = false
    private var headerIndices: IntArray? = null

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (headers != null && (descriptor.kind is StructureKind.CLASS || descriptor.kind is StructureKind.OBJECT)) {
            isHeaderMapped = true
            headerIndices =
                IntArray(descriptor.elementsCount) { i ->
                    val name = descriptor.getElementName(i)
                    headers.indexOf(name)
                }
        }
        return this
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (isHeaderMapped && headerIndices != null) {
            while (currentIndex < descriptor.elementsCount) {
                val fieldIdx = headerIndices!![currentIndex]
                val idx = currentIndex++
                if (fieldIdx in fields.indices) {
                    currentField = fieldIdx
                    return idx
                }
            }
            return CompositeDecoder.DECODE_DONE
        }

        if (currentIndex < fields.size && currentIndex < descriptor.elementsCount) {
            currentField = currentIndex
            return currentIndex++
        }
        return CompositeDecoder.DECODE_DONE
    }

    private fun getCurrentValue(): String =
        if (currentField in fields.indices) {
            fields[currentField]
        } else {
            ""
        }

    override fun decodeNotNullMark(): Boolean {
        val v = getCurrentValue()
        return v.isNotEmpty()
    }

    override fun decodeNull(): Nothing? = null

    override fun decodeBoolean(): Boolean {
        val s = getCurrentValue().trim()
        return when (s.lowercase()) {
            "true", "t", "1", "yes", "y" -> true
            "false", "f", "0", "no", "n" -> false
            else ->
                s.toBooleanStrictOrNull()
                    ?: throw CsvError(
                        ErrorKind.Deserialize(
                            pos = position,
                            message = "cannot parse '$s' as boolean for field $currentField",
                        ),
                    )
        }
    }

    override fun decodeByte(): Byte {
        val s = getCurrentValue().trim()
        return s.toByteOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as byte for field $currentField",
                ),
            )
    }

    override fun decodeShort(): Short {
        val s = getCurrentValue().trim()
        return s.toShortOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as short for field $currentField",
                ),
            )
    }

    override fun decodeInt(): Int {
        val s = getCurrentValue().trim()
        return s.toIntOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as int for field $currentField",
                ),
            )
    }

    override fun decodeLong(): Long {
        val s = getCurrentValue().trim()
        return s.toLongOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as long for field $currentField",
                ),
            )
    }

    override fun decodeFloat(): Float {
        val s = getCurrentValue().trim()
        return s.toFloatOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as float for field $currentField",
                ),
            )
    }

    override fun decodeDouble(): Double {
        val s = getCurrentValue().trim()
        return s.toDoubleOrNull()
            ?: throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as double for field $currentField",
                ),
            )
    }

    override fun decodeChar(): Char {
        val s = getCurrentValue()
        return if (s.length == 1) {
            s[0]
        } else {
            throw CsvError(
                ErrorKind.Deserialize(
                    pos = position,
                    message = "cannot parse '$s' as char for field $currentField",
                ),
            )
        }
    }

    override fun decodeString(): String = getCurrentValue()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val name = getCurrentValue()
        val index = enumDescriptor.getElementIndex(name)
        if (index != CompositeDecoder.UNKNOWN_NAME) {
            return index
        }
        for (i in 0 until enumDescriptor.elementsCount) {
            if (enumDescriptor.getElementName(i).equals(name, ignoreCase = true)) {
                return i
            }
        }
        throw CsvError(
            ErrorKind.Deserialize(
                pos = position,
                message = "cannot parse '$name' as enum for ${enumDescriptor.serialName}",
            ),
        )
    }
}

/**
 * A Serde deserialization error.
 */
public class DeserializeError(
    private val fieldVal: ULong?,
    private val kindVal: DeserializeErrorKind,
) {
    public fun field(): ULong? = fieldVal

    public fun kind(): DeserializeErrorKind = kindVal

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeserializeError) return false
        return fieldVal == other.fieldVal && kindVal == other.kindVal
    }

    override fun hashCode(): Int {
        var result = fieldVal?.hashCode() ?: 0
        result = 31 * result + kindVal.hashCode()
        return result
    }

    override fun toString(): String = "DeserializeError(field=$fieldVal, kind=$kindVal)"
}

/**
 * The kind of a Serde deserialization error.
 */
public sealed class DeserializeErrorKind {
    public data class Message(
        public val msg: String,
    ) : DeserializeErrorKind()

    public data class Unsupported(
        public val which: String,
    ) : DeserializeErrorKind()

    public object UnexpectedEndOfRow : DeserializeErrorKind()

    public data class InvalidUtf8(
        public val message: String,
    ) : DeserializeErrorKind()

    public data class ParseBool(
        public val message: String,
    ) : DeserializeErrorKind()

    public data class ParseInt(
        public val message: String,
    ) : DeserializeErrorKind()

    public data class ParseFloat(
        public val message: String,
    ) : DeserializeErrorKind()

    public fun description(): String =
        when (this) {
            is Message -> "deserialization error"
            is Unsupported -> "unsupported deserializer method"
            UnexpectedEndOfRow -> "expected field, but got end of row"
            is InvalidUtf8 -> "invalid utf-8"
            is ParseBool -> "failed to parse bool"
            is ParseInt -> "failed to parse integer"
            is ParseFloat -> "failed to parse float"
        }
}

internal interface DeRecord {
    public fun hasHeaders(): Boolean

    public fun nextHeader(): String?

    public fun nextHeaderBytes(): ByteArray?

    public fun nextField(): Result<String>

    public fun nextFieldBytes(): Result<ByteArray>

    public fun peekField(): ByteArray?

    public fun error(kind: DeserializeErrorKind): DeserializeError
}

internal class DeRecordWrap<T : DeRecord>(
    public val inner: T,
) : DeRecord {
    override fun hasHeaders(): Boolean = inner.hasHeaders()

    override fun nextHeader(): String? = inner.nextHeader()

    override fun nextHeaderBytes(): ByteArray? = inner.nextHeaderBytes()

    override fun nextField(): Result<String> = inner.nextField()

    override fun nextFieldBytes(): Result<ByteArray> = inner.nextFieldBytes()

    override fun peekField(): ByteArray? = inner.peekField()

    override fun error(kind: DeserializeErrorKind): DeserializeError = inner.error(kind)
}

internal class DeStringRecord(
    private val record: StringRecord,
    private val headers: StringRecord? = null,
) : DeRecord {
    private var idx = 0
    private var headerIdx = 0

    override fun hasHeaders(): Boolean = headers != null

    override fun nextHeader(): String? =
        if (headers != null && headerIdx < headers.len()) {
            headers[headerIdx++]
        } else {
            null
        }

    override fun nextHeaderBytes(): ByteArray? = nextHeader()?.encodeToByteArray()

    override fun nextField(): Result<String> {
        if (idx < record.len()) {
            val f = record[idx++] ?: ""
            return Result.success(f)
        }
        return Result.failure(CsvError(ErrorKind.Deserialize(record.position(), "Unexpected end of row")))
    }

    override fun nextFieldBytes(): Result<ByteArray> = nextField().map { it.encodeToByteArray() }

    override fun peekField(): ByteArray? =
        if (idx < record.len()) {
            (record[idx] ?: "").encodeToByteArray()
        } else {
            null
        }

    override fun error(kind: DeserializeErrorKind): DeserializeError =
        DeserializeError(if (idx > 0) (idx - 1).toULong() else null, kind)
}

internal class DeByteRecord(
    private val record: ByteRecord,
    private val headers: ByteRecord? = null,
) : DeRecord {
    private var idx = 0
    private var headerIdx = 0

    override fun hasHeaders(): Boolean = headers != null

    override fun nextHeader(): String? = nextHeaderBytes()?.decodeToString()

    override fun nextHeaderBytes(): ByteArray? =
        if (headers != null && headerIdx < headers.len()) {
            headers[headerIdx++]
        } else {
            null
        }

    override fun nextField(): Result<String> = nextFieldBytes().map { it.decodeToString() }

    override fun nextFieldBytes(): Result<ByteArray> {
        if (idx < record.len()) {
            val f = record[idx++] ?: ByteArray(0)
            return Result.success(f)
        }
        return Result.failure(CsvError(ErrorKind.Deserialize(record.position(), "Unexpected end of row")))
    }

    override fun peekField(): ByteArray? =
        if (idx < record.len()) {
            record[idx]
        } else {
            null
        }

    override fun error(kind: DeserializeErrorKind): DeserializeError =
        DeserializeError(if (idx > 0) (idx - 1).toULong() else null, kind)
}

internal fun tryPositiveInteger128(s: String): ULong? = s.toULongOrNull()

internal fun tryNegativeInteger128(s: String): Long? = s.toLongOrNull()

internal fun tryPositiveInteger64(s: String): ULong? = s.toULongOrNull()

internal fun tryNegativeInteger64(s: String): Long? = s.toLongOrNull()

internal fun tryFloat(s: String): Double? = s.toDoubleOrNull()

internal fun tryPositiveInteger64Bytes(s: ByteArray): ULong? = s.decodeToString().toULongOrNull()

internal fun tryNegativeInteger64Bytes(s: ByteArray): Long? = s.decodeToString().toLongOrNull()

internal fun tryPositiveInteger128Bytes(s: ByteArray): ULong? = s.decodeToString().toULongOrNull()

internal fun tryNegativeInteger128Bytes(s: ByteArray): Long? = s.decodeToString().toLongOrNull()

internal fun tryFloatBytes(s: ByteArray): Double? = s.decodeToString().toDoubleOrNull()
