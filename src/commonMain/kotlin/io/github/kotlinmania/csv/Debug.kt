// port-lint: source src/debug.rs
package io.github.kotlinmania.csv

/**
 * A type that provides a human readable debug impl for arbitrary bytes.
 *
 * This generally works best when the bytes are presumed to be mostly UTF-8,
 * but will work for anything.
 *
 * N.B. This is copied nearly verbatim from regex-automata. Sigh.
 */
internal class Bytes(val data: UByteArray) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('"')
        // This is a sad re-implementation of a similar impl found in bstr.
        var bytes = data
        while (true) {
            val result = utf8Decode(bytes) ?: break
            val ch: Char = when (result) {
                is Utf8DecodeResult.Ok -> result.ch
                is Utf8DecodeResult.Err -> {
                    appendByteEscape(sb, result.byte)
                    bytes = bytes.copyOfRange(1, bytes.size)
                    continue
                }
            }
            bytes = bytes.copyOfRange(charLenUtf8(ch), bytes.size)
            val code = ch.code
            when {
                code == 0x00 -> sb.append("\\0")
                // ASCII control characters except \0, \n, \r, \t
                code in 0x01..0x08 ||
                    code == 0x0B ||
                    code == 0x0C ||
                    code in 0x0E..0x19 ||
                    code == 0x7F -> {
                    appendByteEscape(sb, code.toUByte())
                }
                else -> {
                    // '\n' | '\r' | '\t' | _
                    appendEscapeDebug(sb, ch)
                }
            }
        }
        sb.append('"')
        return sb.toString()
    }
}

/**
 * Decodes the next UTF-8 encoded codepoint from the given byte slice.
 *
 * If no valid encoding of a codepoint exists at the beginning of the given
 * byte slice, then the first byte is returned instead.
 *
 * This returns null (the Rust `Option::None`) if and only if `bytes` is empty.
 */
internal fun utf8Decode(bytes: UByteArray): Utf8DecodeResult? {
    fun len(byte: UByte): Int? {
        val b = byte.toInt()
        return when {
            b <= 0x7F -> 1
            b and 0b1100_0000 == 0b1000_0000 -> null
            b <= 0b1101_1111 -> 2
            b <= 0b1110_1111 -> 3
            b <= 0b1111_0111 -> 4
            else -> null
        }
    }

    if (bytes.isEmpty()) {
        return null
    }
    val first = bytes[0]
    val expected = len(first) ?: return Utf8DecodeResult.Err(first)
    if (expected > bytes.size) {
        return Utf8DecodeResult.Err(first)
    }
    if (expected == 1) {
        return Utf8DecodeResult.Ok(Char(first.toInt()))
    }
    val decoded = decodeUtf8(bytes, expected)
        ?: return Utf8DecodeResult.Err(first)
    return Utf8DecodeResult.Ok(decoded)
}

/** Result of [utf8Decode]: either a decoded char or the offending byte. */
internal sealed class Utf8DecodeResult {
    data class Ok(val ch: Char) : Utf8DecodeResult()
    data class Err(val byte: UByte) : Utf8DecodeResult()
}

/**
 * Decode the first `len` bytes of `bytes` as a single UTF-8 codepoint.
 *
 * Returns the first Char of the decoded sequence (matching the upstream Rust
 * `s.chars().next().unwrap()`), or null if the bytes are not valid UTF-8.
 *
 * For codepoints outside the BMP, the returned Char is the high surrogate
 * (mirroring the way Kotlin exposes a String's first code unit).
 */
private fun decodeUtf8(bytes: UByteArray, len: Int): Char? {
    val codePoint: Int = when (len) {
        2 -> {
            val b0 = bytes[0].toInt()
            val b1 = bytes[1].toInt()
            if (b1 and 0xC0 != 0x80) return null
            val cp = (b0 and 0x1F) shl 6 or (b1 and 0x3F)
            if (cp < 0x80) return null
            cp
        }
        3 -> {
            val b0 = bytes[0].toInt()
            val b1 = bytes[1].toInt()
            val b2 = bytes[2].toInt()
            if (b1 and 0xC0 != 0x80) return null
            if (b2 and 0xC0 != 0x80) return null
            val cp = (b0 and 0x0F) shl 12 or ((b1 and 0x3F) shl 6) or (b2 and 0x3F)
            if (cp < 0x800) return null
            if (cp in 0xD800..0xDFFF) return null
            cp
        }
        4 -> {
            val b0 = bytes[0].toInt()
            val b1 = bytes[1].toInt()
            val b2 = bytes[2].toInt()
            val b3 = bytes[3].toInt()
            if (b1 and 0xC0 != 0x80) return null
            if (b2 and 0xC0 != 0x80) return null
            if (b3 and 0xC0 != 0x80) return null
            val cp = (b0 and 0x07) shl 18 or
                ((b1 and 0x3F) shl 12) or
                ((b2 and 0x3F) shl 6) or
                (b3 and 0x3F)
            if (cp < 0x10000) return null
            if (cp > 0x10FFFF) return null
            cp
        }
        else -> return null
    }
    return if (codePoint <= 0xFFFF) {
        Char(codePoint)
    } else {
        // Code points outside the BMP are represented as a surrogate pair in
        // Kotlin Strings; return the high surrogate here so the matching
        // [charLenUtf8] call advances by the full UTF-8 byte length.
        val adjusted = codePoint - 0x10000
        Char(0xD800 or (adjusted ushr 10))
    }
}

/** Number of UTF-8 bytes used to encode [ch] (mirrors Rust `char::len_utf8`). */
private fun charLenUtf8(ch: Char): Int {
    val code = ch.code
    return when {
        code < 0x80 -> 1
        code < 0x800 -> 2
        // High surrogate stands for the full 4-byte sequence of the
        // non-BMP code point it half-encodes.
        ch.isHighSurrogate() -> 4
        else -> 3
    }
}

private fun appendByteEscape(sb: StringBuilder, byte: UByte) {
    sb.append("\\x")
    val hi = (byte.toInt() ushr 4) and 0xF
    val lo = byte.toInt() and 0xF
    sb.append(hexDigit(hi))
    sb.append(hexDigit(lo))
}

private fun hexDigit(value: Int): Char =
    if (value < 10) Char('0'.code + value) else Char('a'.code + value - 10)

/**
 * Rust `char::escape_debug` output for a single char.
 *
 * The Rust contract for the subset of characters that reach this branch in
 * `Bytes::fmt`:
 *
 * - `\t`, `\r`, `\n` produce the two-character escapes `\t`, `\r`, `\n`.
 * - `\\` produces `\\\\`.
 * - `"` produces `\\"`.
 * - `'` is left as-is (Rust's debug escape only quotes `"`).
 * - Other printable Unicode characters are emitted as-is.
 * - Other non-printable code points use the `\u{XXXX}` form.
 */
private fun appendEscapeDebug(sb: StringBuilder, ch: Char) {
    when (ch) {
        '\t' -> sb.append("\\t")
        '\r' -> sb.append("\\r")
        '\n' -> sb.append("\\n")
        '\\' -> sb.append("\\\\")
        '"' -> sb.append("\\\"")
        else -> if (isPrintable(ch)) {
            sb.append(ch)
        } else {
            sb.append("\\u{")
            sb.append(ch.code.toString(16))
            sb.append('}')
        }
    }
}

/**
 * Approximate the set of code points Rust treats as printable for
 * `char::escape_debug`.
 *
 * The exact Unicode "printable" table is large; the subset we need to
 * distinguish here is the one reached from `Bytes::fmt`. Everything outside
 * `0x20..0x7E` that was not already handled by the control-character branch
 * above falls into the non-ASCII region: surrogates stay non-printable, and
 * everything else is treated as printable (the upstream Rust crate's debug
 * output is consumed as diagnostic text, not as a stable serialization).
 */
private fun isPrintable(ch: Char): Boolean {
    val code = ch.code
    return when {
        code in 0x20..0x7E -> true
        ch.isHighSurrogate() -> false
        ch.isLowSurrogate() -> false
        else -> code >= 0x80
    }
}
