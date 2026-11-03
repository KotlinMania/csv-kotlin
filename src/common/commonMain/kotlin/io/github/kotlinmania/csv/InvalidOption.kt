package io.github.kotlinmania.csv

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer

/**
 * A custom Serde deserializer for possibly invalid optional fields.
 *
 * When deserializing CSV data, it is sometimes desirable to simply ignore
 * fields with invalid data. For example, there might be a field that is
 * usually a number, but will occasionally contain garbage data that causes
 * number parsing to fail.
 *
 * If `T` is deserialized with non-empty but invalid data, then the value
 * will be `null` and the error will be ignored.
 */
public fun <D : Deserializer, T> invalidOption(
    deserializer: D,
    deserialize: Deserialize<T>,
): SerdeResult<T?> =
    deserialize.deserialize(deserializer).fold(
        onSuccess = { SerdeResult.success(it) },
        onFailure = { SerdeResult.success(null) },
    )
