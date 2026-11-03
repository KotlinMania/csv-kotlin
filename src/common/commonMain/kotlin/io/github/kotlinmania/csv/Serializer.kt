// port-lint: source serializer.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.csv

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.SerializeMap
import io.github.kotlinmania.serdecore.ser.SerializeSeq
import io.github.kotlinmania.serdecore.ser.SerializeStruct
import io.github.kotlinmania.serdecore.ser.SerializeStructVariant
import io.github.kotlinmania.serdecore.ser.SerializeTuple
import io.github.kotlinmania.serdecore.ser.SerializeTupleStruct
import io.github.kotlinmania.serdecore.ser.SerializeTupleVariant
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlin.native.HiddenFromObjC

/**
 * Serialize the given value to the given writer, and return an error if
 * anything went wrong.
 */
@HiddenFromObjC
public fun <S : Serialize> serialize(
    wtr: Writer,
    value: S,
): Result<Unit> {
    val ser = SeRecord(wtr)
    return value.serialize(ser).fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { err ->
            Result.failure(CsvError.custom(err.message))
        },
    )
}

/**
 * Write header names corresponding to the field names of the value (if the
 * value has field names).
 *
 * If the type to be serialized has field names (e.g. it's a struct), then
 * header names are written, and the `Ok` return value is `true`.
 *
 * If the type to be serialized doesn't have field names, then nothing is
 * written, and the `Ok` return value is `false`.
 */
@HiddenFromObjC
public fun <S : Serialize> serializeHeader(
    wtr: Writer,
    value: S,
): Result<Boolean> {
    val ser = SeHeader(wtr)
    return value.serialize(ser).fold(
        onSuccess = { Result.success(ser.wroteHeader()) },
        onFailure = { err ->
            Result.failure(CsvError.custom(err.message))
        },
    )
}

/**
 * Serialize the given value to the given writer, returning any serialization error.
 */
@HiddenFromObjC
public fun <S : Serialize> serializeErr(
    wtr: Writer,
    value: S,
): Result<Unit> = serialize(wtr, value)

/**
 * Write header names corresponding to the field names of the value, returning any serialization error.
 */
@HiddenFromObjC
public fun <S : Serialize> serializeHeaderErr(
    wtr: Writer,
    value: S,
): Result<Boolean> = serializeHeader(wtr, value)

internal class SeRecord(
    val wtr: Writer,
) : Serializer<Unit>,
    SerializeSeq<Unit>,
    SerializeTuple<Unit>,
    SerializeTupleStruct<Unit>,
    SerializeTupleVariant<Unit>,
    SerializeMap<Unit>,
    SerializeStruct<Unit>,
    SerializeStructVariant<Unit> {
    private fun write(field: String): SerdeResult<Unit> {
        val res = wtr.writeField(field)
        return if (res.isSuccess) {
            SerdeResult.success(Unit)
        } else {
            SerdeResult.failure(SerdeError.custom(res.exceptionOrNull()?.message ?: "write field error"))
        }
    }

    private fun write(field: ByteArray): SerdeResult<Unit> {
        val res = wtr.writeField(field)
        return if (res.isSuccess) {
            SerdeResult.success(Unit)
        } else {
            SerdeResult.failure(SerdeError.custom(res.exceptionOrNull()?.message ?: "write field error"))
        }
    }

    override fun serializeBool(v: Boolean): SerdeResult<Unit> =
        write(if (v) "true" else "false")

    override fun serializeI8(v: Byte): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeI16(v: Short): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeI32(v: Int): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeI64(v: Long): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeI128(value: String): SerdeResult<Unit> =
        write(value)

    override fun serializeU8(v: UByte): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeU16(v: UShort): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeU32(v: UInt): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeU64(v: ULong): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeU128(value: String): SerdeResult<Unit> =
        write(value)

    override fun serializeF32(v: Float): SerdeResult<Unit> =
        write(formatFloat(v))

    override fun serializeF64(v: Double): SerdeResult<Unit> =
        write(formatDouble(v))

    override fun serializeChar(v: Char): SerdeResult<Unit> =
        write(v.toString())

    override fun serializeStr(v: String): SerdeResult<Unit> =
        write(v)

    override fun serializeBytes(v: ByteArray): SerdeResult<Unit> =
        write(v)

    override fun serializeNone(): SerdeResult<Unit> =
        write(byteArrayOf())

    override fun <T : Serialize> serializeSome(value: T): SerdeResult<Unit> =
        value.serialize(this)

    override fun serializeUnit(): SerdeResult<Unit> =
        serializeNone()

    override fun serializeUnitStruct(name: String): SerdeResult<Unit> =
        write(name)

    override fun serializeUnitVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
    ): SerdeResult<Unit> =
        write(variant)

    override fun <T : Serialize> serializeNewtypeStruct(
        name: String,
        value: T,
    ): SerdeResult<Unit> =
        value.serialize(this)

    override fun <T : Serialize> serializeNewtypeVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        value: T,
    ): SerdeResult<Unit> =
        value.serialize(this)

    override fun serializeSeq(len: Int?): SerdeResult<SerializeSeq<Unit>> =
        SerdeResult.success(this)

    override fun serializeTuple(len: Int): SerdeResult<SerializeTuple<Unit>> =
        SerdeResult.success(this)

    override fun serializeTupleStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeTupleStruct<Unit>> =
        SerdeResult.success(this)

    override fun serializeTupleVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeTupleVariant<Unit>> =
        SerdeResult.failure(SerdeError.custom("serializing enum tuple variants is not supported"))

    override fun serializeMap(len: Int?): SerdeResult<SerializeMap<Unit>> =
        SerdeResult.failure(
            SerdeError.custom(
                "serializing maps is not supported, if you have a use case, please file an issue at https://github.com/BurntSushi/rust-csv",
            ),
        )

    override fun serializeStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeStruct<Unit>> =
        SerdeResult.success(this)

    override fun serializeStructVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeStructVariant<Unit>> =
        SerdeResult.failure(SerdeError.custom("serializing enum struct variants is not supported"))

    // SerializeSeq
    override fun <T : Serialize> serializeElement(value: T): SerdeResult<Unit> =
        value.serialize(this)

    // SerializeTupleStruct
    override fun <T : Serialize> serializeField(value: T): SerdeResult<Unit> =
        value.serialize(this)

    // SerializeStruct
    override fun <T : Serialize> serializeField(
        key: String,
        value: T,
    ): SerdeResult<Unit> =
        value.serialize(this)

    override fun <T : Serialize> serializeKey(key: T): SerdeResult<Unit> = throw IllegalStateException("unreachable")

    override fun <T : Serialize> serializeValue(value: T): SerdeResult<Unit> = throw IllegalStateException("unreachable")

    override fun skipField(key: String): SerdeResult<Unit> =
        SerdeResult.success(Unit)

    override fun end(): SerdeResult<Unit> =
        SerdeResult.success(Unit)
}

internal sealed class SeHeaderState {
    data object Write : SeHeaderState()

    data class ErrorIfWrite(
        val err: CsvError,
    ) : SeHeaderState()

    data object EncounteredStructField : SeHeaderState()

    data object InStructField : SeHeaderState()
}

internal class SeHeader(
    val wtr: Writer,
) : Serializer<Unit>,
    SerializeSeq<Unit>,
    SerializeTuple<Unit>,
    SerializeTupleStruct<Unit>,
    SerializeTupleVariant<Unit>,
    SerializeMap<Unit>,
    SerializeStruct<Unit>,
    SerializeStructVariant<Unit> {
    var state: SeHeaderState = SeHeaderState.Write

    fun wroteHeader(): Boolean =
        when (state) {
            is SeHeaderState.Write, is SeHeaderState.ErrorIfWrite -> false
            is SeHeaderState.EncounteredStructField, is SeHeaderState.InStructField -> true
        }

    fun handleScalar(name: String): SerdeResult<Unit> =
        when (val s = state) {
            is SeHeaderState.Write -> {
                state = SeHeaderState.ErrorIfWrite(errorScalarOutsideStruct(name))
                SerdeResult.success(Unit)
            }
            is SeHeaderState.ErrorIfWrite, is SeHeaderState.InStructField -> {
                SerdeResult.success(Unit)
            }
            is SeHeaderState.EncounteredStructField -> {
                SerdeResult.failure(SerdeError.custom(errorScalarOutsideStruct(name).message ?: ""))
            }
        }

    fun handleContainer(name: String): SerdeResult<Unit> =
        if (state is SeHeaderState.InStructField) {
            SerdeResult.failure(SerdeError.custom(errorContainerInsideStruct(name).message ?: ""))
        } else {
            SerdeResult.success(Unit)
        }

    override fun serializeBool(v: Boolean): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeI8(v: Byte): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeI16(v: Short): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeI32(v: Int): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeI64(v: Long): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeI128(value: String): SerdeResult<Unit> =
        handleScalar(value)

    override fun serializeU8(v: UByte): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeU16(v: UShort): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeU32(v: UInt): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeU64(v: ULong): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeU128(value: String): SerdeResult<Unit> =
        handleScalar(value)

    override fun serializeF32(v: Float): SerdeResult<Unit> =
        handleScalar(formatFloat(v))

    override fun serializeF64(v: Double): SerdeResult<Unit> =
        handleScalar(formatDouble(v))

    override fun serializeChar(v: Char): SerdeResult<Unit> =
        handleScalar(v.toString())

    override fun serializeStr(v: String): SerdeResult<Unit> =
        handleScalar(v)

    override fun serializeBytes(v: ByteArray): SerdeResult<Unit> =
        handleScalar("&[u8]")

    override fun serializeNone(): SerdeResult<Unit> =
        handleScalar("None")

    override fun <T : Serialize> serializeSome(value: T): SerdeResult<Unit> =
        handleScalar("Some(_)")

    override fun serializeUnit(): SerdeResult<Unit> =
        handleScalar("()")

    override fun serializeUnitStruct(name: String): SerdeResult<Unit> =
        handleScalar(name)

    override fun serializeUnitVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
    ): SerdeResult<Unit> =
        handleScalar("$name::$variant")

    override fun <T : Serialize> serializeNewtypeStruct(
        name: String,
        value: T,
    ): SerdeResult<Unit> =
        handleScalar("$name(_)")

    override fun <T : Serialize> serializeNewtypeVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        value: T,
    ): SerdeResult<Unit> =
        handleScalar("$name::$variant(_)")

    override fun serializeSeq(len: Int?): SerdeResult<SerializeSeq<Unit>> {
        val res = handleContainer("sequence")
        if (res.isFailure) return SerdeResult.failure(res.exceptionOrNull()!!)
        return SerdeResult.success(this)
    }

    override fun serializeTuple(len: Int): SerdeResult<SerializeTuple<Unit>> {
        val res = handleContainer("tuple")
        if (res.isFailure) return SerdeResult.failure(res.exceptionOrNull()!!)
        return SerdeResult.success(this)
    }

    override fun serializeTupleStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeTupleStruct<Unit>> {
        val res = handleContainer(name)
        if (res.isFailure) return SerdeResult.failure(res.exceptionOrNull()!!)
        return SerdeResult.success(this)
    }

    override fun serializeTupleVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeTupleVariant<Unit>> =
        SerdeResult.failure(SerdeError.custom("serializing enum tuple variants is not supported"))

    override fun serializeMap(len: Int?): SerdeResult<SerializeMap<Unit>> =
        SerdeResult.failure(
            SerdeError.custom(
                "serializing maps is not supported, if you have a use case, please file an issue at https://github.com/BurntSushi/rust-csv",
            ),
        )

    override fun serializeStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeStruct<Unit>> {
        val res = handleContainer(name)
        if (res.isFailure) return SerdeResult.failure(res.exceptionOrNull()!!)
        return SerdeResult.success(this)
    }

    override fun serializeStructVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeStructVariant<Unit>> =
        SerdeResult.failure(SerdeError.custom("serializing enum struct variants is not supported"))

    override fun <T : Serialize> serializeElement(value: T): SerdeResult<Unit> =
        value.serialize(this)

    override fun <T : Serialize> serializeField(value: T): SerdeResult<Unit> =
        value.serialize(this)

    override fun <T : Serialize> serializeField(
        key: String,
        value: T,
    ): SerdeResult<Unit> {
        val oldState = state
        state = SeHeaderState.EncounteredStructField
        if (oldState is SeHeaderState.ErrorIfWrite) {
            return SerdeResult.failure(SerdeError.custom(oldState.err.message ?: ""))
        }
        val writeRes = wtr.writeField(key)
        if (writeRes.isFailure) {
            return SerdeResult.failure(SerdeError.custom(writeRes.exceptionOrNull()?.message ?: "write error"))
        }

        state = SeHeaderState.InStructField
        val valRes = value.serialize(this)
        state = SeHeaderState.EncounteredStructField
        if (valRes.isFailure) {
            return valRes
        }
        return SerdeResult.success(Unit)
    }

    override fun <T : Serialize> serializeKey(key: T): SerdeResult<Unit> = throw IllegalStateException("unreachable")

    override fun <T : Serialize> serializeValue(value: T): SerdeResult<Unit> = throw IllegalStateException("unreachable")

    override fun skipField(key: String): SerdeResult<Unit> =
        SerdeResult.success(Unit)

    override fun end(): SerdeResult<Unit> =
        SerdeResult.success(Unit)
}

private fun errorScalarOutsideStruct(name: String): CsvError =
    CsvError.custom(
        "cannot serialize $name scalar outside struct when writing headers from structs",
    )

private fun errorContainerInsideStruct(name: String): CsvError =
    CsvError.custom(
        "cannot serialize $name container inside struct when writing headers from structs",
    )

private fun formatFloat(v: Float): String =
    if (v.isNaN()) {
        "NaN"
    } else if (v == Float.POSITIVE_INFINITY) {
        "inf"
    } else if (v == Float.NEGATIVE_INFINITY) {
        "-inf"
    } else {
        val s = v.toString()
        if (!s.contains('.') && !s.contains('e') && !s.contains('E')) "$s.0" else s
    }

private fun formatDouble(v: Double): String =
    if (v.isNaN()) {
        "NaN"
    } else if (v == Double.POSITIVE_INFINITY) {
        "inf"
    } else if (v == Double.NEGATIVE_INFINITY) {
        "-inf"
    } else {
        val s = v.toString()
        if (!s.contains('.') && !s.contains('e') && !s.contains('E')) "$s.0" else s
    }
