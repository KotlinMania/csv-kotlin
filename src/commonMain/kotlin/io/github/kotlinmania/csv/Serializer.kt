// port-lint: source serializer.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.csv

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.native.HiddenFromObjC

/**
 * Serialize the given value to the given writer using the provided [SerializationStrategy].
 */
@HiddenFromObjC
public fun <T> serialize(
    writer: Writer,
    serializer: SerializationStrategy<T>,
    value: T,
): Result<Unit> = CsvSerializer.serialize(writer, serializer, value)

/**
 * Serialize the given value to the given writer using the default serializer.
 */
@HiddenFromObjC
public inline fun <reified T> serialize(
    writer: Writer,
    value: T,
): Result<Unit> = CsvSerializer.serialize(writer, kotlinx.serialization.serializer(), value)

/**
 * Write header names corresponding to the field names of the value (if the value has field names).
 */
@HiddenFromObjC
public fun <T> serializeHeader(
    writer: Writer,
    serializer: SerializationStrategy<T>,
    value: T,
): Result<Boolean> {
    val ser = SeHeader(writer)
    return try {
        val encoder = CsvRecordEncoder(writer)
        serializer.serialize(encoder, value)
        Result.success(ser.wroteHeader())
    } catch (e: Exception) {
        Result.failure(if (e is CsvError) e else CsvError(ErrorKind.Serialize(e.message ?: "Header serialization failed")))
    }
}

/**
 * Write header names corresponding to the field names of the value (if the value has field names).
 */
@HiddenFromObjC
public inline fun <reified T> serializeHeader(
    writer: Writer,
    value: T,
): Result<Boolean> = serializeHeader(writer, kotlinx.serialization.serializer(), value)

/**
 * Serializes values into CSV records using kotlinx.serialization.
 */
@HiddenFromObjC
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
            if (value is ByteArray) {
                writer.writeField(value)
                writer.endRecord()
            } else {
                val encoder = CsvRecordEncoder(writer)
                serializer.serialize(encoder, value)
                writer.endRecord()
            }
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
 * Internal record serializer matching Serde SeRecord.
 */
internal class SeRecord(
    public val wtr: Writer,
) : SerializeSeq,
    SerializeTuple,
    SerializeTupleStruct,
    SerializeTupleVariant,
    SerializeMap,
    SerializeStruct,
    SerializeStructVariant {
    public fun serializeBool(v: Boolean): Result<Unit> =
        wtr.writeField(if (v) "true" else "false")

    public fun serializeI8(v: Byte): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeI16(v: Short): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeI32(v: Int): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeI64(v: Long): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeI128(v: String): Result<Unit> = wtr.writeField(v)

    public fun serializeU8(v: UByte): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeU16(v: UShort): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeU32(v: UInt): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeU64(v: ULong): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeU128(v: String): Result<Unit> = wtr.writeField(v)

    public fun serializeF32(v: Float): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeF64(v: Double): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeChar(v: Char): Result<Unit> = wtr.writeField(v.toString())

    public fun serializeStr(value: String): Result<Unit> = wtr.writeField(value)

    public fun serializeBytes(value: ByteArray): Result<Unit> = wtr.writeField(value)

    public fun serializeNone(): Result<Unit> = wtr.writeField("")

    public fun serializeSome(value: String): Result<Unit> = wtr.writeField(value)

    public fun serializeUnit(): Result<Unit> = wtr.writeField("")

    public fun serializeUnitStruct(name: String): Result<Unit> = wtr.writeField(name)

    public fun serializeUnitVariant(name: String, variantIndex: UInt, variant: String): Result<Unit> =
        wtr.writeField(variant)

    public fun serializeNewtypeStruct(name: String, value: String): Result<Unit> = wtr.writeField(value)

    public fun serializeNewtypeVariant(name: String, variantIndex: UInt, variant: String, value: String): Result<Unit> =
        wtr.writeField(value)

    public fun serializeSeq(len: Int?): Result<SerializeSeq> = Result.success(this)

    public fun serializeTuple(len: Int): Result<SerializeTuple> = Result.success(this)

    public fun serializeTupleStruct(name: String, len: Int): Result<SerializeTupleStruct> = Result.success(this)

    public fun serializeTupleVariant(name: String, variantIndex: UInt, variant: String, len: Int): Result<SerializeTupleVariant> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing enum tuple variants is not supported")))

    public fun serializeMap(len: Int?): Result<SerializeMap> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing maps is not supported, if you have a use case, please file an issue at https://github.com/BurntSushi/rust-csv")))

    public fun serializeStruct(name: String, len: Int): Result<SerializeStruct> = Result.success(this)

    public fun serializeStructVariant(name: String, variantIndex: UInt, variant: String, len: Int): Result<SerializeStructVariant> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing enum struct variants is not supported")))

    override fun serializeElement(value: Any?): Result<Unit> = wtr.writeField(value?.toString() ?: "")

    override fun end(): Result<Unit> = Result.success(Unit)

    override fun serializeField(key: String, value: Any?): Result<Unit> = wtr.writeField(value?.toString() ?: "")

    override fun serializeField(value: Any?): Result<Unit> = wtr.writeField(value?.toString() ?: "")

    override fun serializeKey(key: Any?): Result<Unit> =
        Result.failure(CsvError(ErrorKind.Serialize("unreachable serializeKey")))

    override fun serializeValue(value: Any?): Result<Unit> =
        Result.failure(CsvError(ErrorKind.Serialize("unreachable serializeValue")))
}

/**
 * State machine for [SeHeader].
 */
internal sealed class SeHeaderState {
    public object Write : SeHeaderState()

    public data class ErrorIfWrite(
        public val err: CsvError,
    ) : SeHeaderState()

    public object EncounteredStructField : SeHeaderState()

    public object InStructField : SeHeaderState()
}

internal fun errorScalarOutsideStruct(name: Any?): CsvError =
    CsvError(ErrorKind.Serialize("cannot serialize $name scalar outside struct when writing headers from structs"))

internal fun errorContainerInsideStruct(name: Any?): CsvError =
    CsvError(ErrorKind.Serialize("cannot serialize $name container inside struct when writing headers from structs"))

/**
 * Header serializer matching Serde SeHeader.
 */
internal class SeHeader(
    public val wtr: Writer,
    public var state: SeHeaderState = SeHeaderState.Write,
) : SerializeSeq,
    SerializeTuple,
    SerializeTupleStruct,
    SerializeTupleVariant,
    SerializeMap,
    SerializeStruct,
    SerializeStructVariant {
    public fun wroteHeader(): Boolean =
        when (state) {
            SeHeaderState.Write, is SeHeaderState.ErrorIfWrite -> false
            SeHeaderState.EncounteredStructField, SeHeaderState.InStructField -> true
        }

    public fun handleScalar(name: Any?): Result<Unit> =
        when (val s = state) {
            SeHeaderState.Write -> {
                state = SeHeaderState.ErrorIfWrite(errorScalarOutsideStruct(name))
                Result.success(Unit)
            }
            is SeHeaderState.ErrorIfWrite, SeHeaderState.InStructField -> Result.success(Unit)
            SeHeaderState.EncounteredStructField -> Result.failure(errorScalarOutsideStruct(name))
        }

    public fun handleContainer(name: Any?): Result<SeHeader> =
        if (state is SeHeaderState.InStructField) {
            Result.failure(errorContainerInsideStruct(name))
        } else {
            Result.success(this)
        }

    public fun serializeBool(v: Boolean): Result<Unit> = handleScalar(v)

    public fun serializeI8(v: Byte): Result<Unit> = handleScalar(v)

    public fun serializeI16(v: Short): Result<Unit> = handleScalar(v)

    public fun serializeI32(v: Int): Result<Unit> = handleScalar(v)

    public fun serializeI64(v: Long): Result<Unit> = handleScalar(v)

    public fun serializeI128(v: String): Result<Unit> = handleScalar(v)

    public fun serializeU8(v: UByte): Result<Unit> = handleScalar(v)

    public fun serializeU16(v: UShort): Result<Unit> = handleScalar(v)

    public fun serializeU32(v: UInt): Result<Unit> = handleScalar(v)

    public fun serializeU64(v: ULong): Result<Unit> = handleScalar(v)

    public fun serializeU128(v: String): Result<Unit> = handleScalar(v)

    public fun serializeF32(v: Float): Result<Unit> = handleScalar(v)

    public fun serializeF64(v: Double): Result<Unit> = handleScalar(v)

    public fun serializeChar(v: Char): Result<Unit> = handleScalar(v)

    public fun serializeStr(value: String): Result<Unit> = handleScalar(value)

    public fun serializeBytes(value: ByteArray): Result<Unit> = handleScalar("&[u8]")

    public fun serializeNone(): Result<Unit> = handleScalar("None")

    public fun serializeSome(value: String): Result<Unit> = handleScalar("Some(_)")

    public fun serializeUnit(): Result<Unit> = handleScalar("()")

    public fun serializeUnitStruct(name: String): Result<Unit> = handleScalar(name)

    public fun serializeUnitVariant(name: String, variantIndex: UInt, variant: String): Result<Unit> =
        handleScalar("$name::$variant")

    public fun serializeNewtypeStruct(name: String, value: String): Result<Unit> = handleScalar("$name(_)")

    public fun serializeNewtypeVariant(name: String, variantIndex: UInt, variant: String, value: String): Result<Unit> =
        handleScalar("$name::$variant(_)")

    public fun serializeSeq(len: Int?): Result<SerializeSeq> = handleContainer("sequence")

    public fun serializeTuple(len: Int): Result<SerializeTuple> = handleContainer("tuple")

    public fun serializeTupleStruct(name: String, len: Int): Result<SerializeTupleStruct> = handleContainer(name)

    public fun serializeTupleVariant(name: String, variantIndex: UInt, variant: String, len: Int): Result<SerializeTupleVariant> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing enum tuple variants is not supported")))

    public fun serializeMap(len: Int?): Result<SerializeMap> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing maps is not supported, if you have a use case, please file an issue at https://github.com/BurntSushi/rust-csv")))

    public fun serializeStruct(name: String, len: Int): Result<SerializeStruct> = handleContainer(name)

    public fun serializeStructVariant(name: String, variantIndex: UInt, variant: String, len: Int): Result<SerializeStructVariant> =
        Result.failure(CsvError(ErrorKind.Serialize("serializing enum struct variants is not supported")))

    override fun serializeElement(value: Any?): Result<Unit> = handleScalar(value)

    override fun end(): Result<Unit> = Result.success(Unit)

    override fun serializeField(key: String, value: Any?): Result<Unit> {
        val oldState = state
        state = SeHeaderState.EncounteredStructField
        if (oldState is SeHeaderState.ErrorIfWrite) {
            return Result.failure(oldState.err)
        }
        val writeRes = wtr.writeField(key)
        if (writeRes.isFailure) return writeRes

        state = SeHeaderState.InStructField
        if (value is String) {
            val res = handleScalar(value)
            if (res.isFailure) return res
        }
        state = SeHeaderState.EncounteredStructField
        return Result.success(Unit)
    }

    override fun serializeField(value: Any?): Result<Unit> = handleScalar(value)

    override fun serializeKey(key: Any?): Result<Unit> =
        Result.failure(CsvError(ErrorKind.Serialize("unreachable serializeKey")))

    override fun serializeValue(value: Any?): Result<Unit> =
        Result.failure(CsvError(ErrorKind.Serialize("unreachable serializeValue")))

    public companion object {
        public fun new(wtr: Writer): SeHeader = SeHeader(wtr)
    }
}

internal interface SerializeSeq {
    public fun serializeElement(value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeTuple {
    public fun serializeElement(value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeTupleStruct {
    public fun serializeField(value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeTupleVariant {
    public fun serializeField(value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeMap {
    public fun serializeKey(key: Any?): Result<Unit>

    public fun serializeValue(value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeStruct {
    public fun serializeField(key: String, value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

internal interface SerializeStructVariant {
    public fun serializeField(key: String, value: Any?): Result<Unit>

    public fun end(): Result<Unit>
}

/**
 * An [Encoder] that writes each element as a CSV field into a [Writer].
 */
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
internal class CsvRecordEncoder(
    private val writer: Writer,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractEncoder() {
    private var depth = 0

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        checkNotNull(descriptor)
        depth++
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        checkNotNull(descriptor)
        depth--
    }

    private var isUnsigned = false

    override fun encodeInline(descriptor: SerialDescriptor): kotlinx.serialization.encoding.Encoder {
        val name = descriptor.serialName
        if (name == "kotlin.UInt" || name == "kotlin.ULong" || name == "kotlin.UByte" || name == "kotlin.UShort") {
            isUnsigned = true
        }
        return this
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean = true

    override fun encodeBoolean(value: Boolean) {
        writer.writeField(if (value) "true" else "false")
    }

    override fun encodeByte(value: Byte) {
        if (isUnsigned) {
            isUnsigned = false
            writer.writeField(value.toUByte().toString())
        } else {
            writer.writeField(value.toString())
        }
    }

    override fun encodeShort(value: Short) {
        if (isUnsigned) {
            isUnsigned = false
            writer.writeField(value.toUShort().toString())
        } else {
            writer.writeField(value.toString())
        }
    }

    override fun encodeInt(value: Int) {
        if (isUnsigned) {
            isUnsigned = false
            writer.writeField(value.toUInt().toString())
        } else {
            writer.writeField(value.toString())
        }
    }

    override fun encodeLong(value: Long) {
        if (isUnsigned) {
            isUnsigned = false
            writer.writeField(value.toULong().toString())
        } else {
            writer.writeField(value.toString())
        }
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
