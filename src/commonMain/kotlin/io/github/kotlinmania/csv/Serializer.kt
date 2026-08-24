// port-lint: source serializer.rs
package io.github.kotlinmania.csv

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * Serialize the given value to the given writer using the provided [SerializationStrategy].
 */
public fun <T> serialize(
    writer: Writer,
    serializer: SerializationStrategy<T>,
    value: T,
): Result<Unit> = CsvSerializer.serialize(writer, serializer, value)

/**
 * Serialize the given value to the given writer using the default serializer.
 */
public inline fun <reified T> serialize(
    writer: Writer,
    value: T,
): Result<Unit> = CsvSerializer.serialize(writer, kotlinx.serialization.serializer(), value)

/**
 * Serializes values into CSV records using kotlinx.serialization.
 */
public object CsvSerializer {
    /**
     * Serializes a value as a CSV record using the provided [SerializationStrategy].
     */
    public fun <T> serialize(
        writer: Writer,
        serializer: SerializationStrategy<T>,
        value: T,
    ): Result<Unit> =
        try {
            val encoder = CsvRecordEncoder(writer)
            serializer.serialize(encoder, value)
            writer.endRecord()
        } catch (e: Exception) {
            Result.failure(if (e is CsvError) e else CsvError(ErrorKind.Serialize(e.message ?: "Serialization failed")))
        }

    /**
     * Serializes the headers of a struct or object descriptor into the writer.
     * Returns true if headers were written, false otherwise.
     */
    public fun serializeHeader(
        writer: Writer,
        descriptor: SerialDescriptor,
    ): Result<Boolean> {
        return try {
            when (descriptor.kind) {
                is StructureKind.CLASS, is StructureKind.OBJECT -> {
                    val count = descriptor.elementsCount
                    if (count == 0) {
                        return Result.success(false)
                    }
                    for (i in 0 until count) {
                        writer.writeField(descriptor.getElementName(i))
                    }
                    val endRes = writer.endRecord()
                    if (endRes.isFailure) {
                        return Result.failure(endRes.exceptionOrNull()!!)
                    }
                    Result.success(true)
                }
                else -> Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(if (e is CsvError) e else CsvError(ErrorKind.Serialize(e.message ?: "Header serialization failed")))
        }
    }
}

/**
 * An [Encoder] that writes each element as a CSV field into a [Writer].
 */
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
public class CsvRecordEncoder(
    private val writer: Writer,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractEncoder() {
    private var depth = 0

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        depth++
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        depth--
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean = true

    override fun encodeBoolean(value: Boolean) {
        writer.writeField(if (value) "true" else "false")
    }

    override fun encodeByte(value: Byte) {
        writer.writeField(value.toString())
    }

    override fun encodeShort(value: Short) {
        writer.writeField(value.toString())
    }

    override fun encodeInt(value: Int) {
        writer.writeField(value.toString())
    }

    override fun encodeLong(value: Long) {
        writer.writeField(value.toString())
    }

    override fun encodeFloat(value: Float) {
        writer.writeField(value.toString())
    }

    override fun encodeDouble(value: Double) {
        writer.writeField(value.toString())
    }

    override fun encodeChar(value: Char) {
        writer.writeField(value.toString())
    }

    override fun encodeString(value: String) {
        writer.writeField(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        writer.writeField(enumDescriptor.getElementName(index))
    }

    override fun encodeNull() {
        writer.writeField("")
    }
}
