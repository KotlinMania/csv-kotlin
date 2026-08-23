// port-lint: source deserializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * Deserializes values from CSV records using kotlinx.serialization.
 */
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
public class CsvRecordDecoder(
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
