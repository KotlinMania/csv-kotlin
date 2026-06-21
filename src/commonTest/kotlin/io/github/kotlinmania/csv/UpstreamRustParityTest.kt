// port-lint: ignore - common test surface ported from multiple upstream Rust test modules
package io.github.kotlinmania.csv

import kotlin.test.Test
import kotlin.test.fail

private fun blockedByMissingPort(source: String, name: String): Unit =
    fail("Upstream Rust test $source::$name is present in commonTest and is blocked by unported csv-kotlin production API.")

class UpstreamIntegrationTests {
    @Test fun cookbookReadBasic() = blockedByMissingPort("tests/tests.rs", "cookbook_read_basic")

    @Test fun cookbookReadSerde() = blockedByMissingPort("tests/tests.rs", "cookbook_read_serde")

    @Test fun cookbookReadColon() = blockedByMissingPort("tests/tests.rs", "cookbook_read_colon")

    @Test fun cookbookReadNoHeaders() = blockedByMissingPort("tests/tests.rs", "cookbook_read_no_headers")

    @Test fun cookbookWriteBasic() = blockedByMissingPort("tests/tests.rs", "cookbook_write_basic")

    @Test fun cookbookWriteSerde() = blockedByMissingPort("tests/tests.rs", "cookbook_write_serde")

    @Test fun tutorialSetup01() = blockedByMissingPort("tests/tests.rs", "tutorial_setup_01")

    @Test fun tutorialError01() = blockedByMissingPort("tests/tests.rs", "tutorial_error_01")

    @Test fun tutorialError01Errored() = blockedByMissingPort("tests/tests.rs", "tutorial_error_01_errored")

    @Test fun tutorialError02() = blockedByMissingPort("tests/tests.rs", "tutorial_error_02")

    @Test fun tutorialError02Errored() = blockedByMissingPort("tests/tests.rs", "tutorial_error_02_errored")

    @Test fun tutorialError03() = blockedByMissingPort("tests/tests.rs", "tutorial_error_03")

    @Test fun tutorialError03Errored() = blockedByMissingPort("tests/tests.rs", "tutorial_error_03_errored")

    @Test fun tutorialError04() = blockedByMissingPort("tests/tests.rs", "tutorial_error_04")

    @Test fun tutorialError04Errored() = blockedByMissingPort("tests/tests.rs", "tutorial_error_04_errored")

    @Test fun tutorialRead01() = blockedByMissingPort("tests/tests.rs", "tutorial_read_01")

    @Test fun tutorialReadHeaders01() = blockedByMissingPort("tests/tests.rs", "tutorial_read_headers_01")

    @Test fun tutorialReadHeaders02() = blockedByMissingPort("tests/tests.rs", "tutorial_read_headers_02")

    @Test fun tutorialReadDelimiter01() = blockedByMissingPort("tests/tests.rs", "tutorial_read_delimiter_01")

    @Test fun tutorialReadSerde01() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_01")

    @Test fun tutorialReadSerde02() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_02")

    @Test fun tutorialReadSerde03() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_03")

    @Test fun tutorialReadSerde04() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_04")

    @Test fun tutorialReadSerde05Invalid() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_05_invalid")

    @Test fun tutorialReadSerde05InvalidErrored() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_05_invalid_errored")

    @Test fun tutorialReadSerdeInvalid06() = blockedByMissingPort("tests/tests.rs", "tutorial_read_serde_invalid_06")

    @Test fun tutorialWrite01() = blockedByMissingPort("tests/tests.rs", "tutorial_write_01")

    @Test fun tutorialWriteDelimiter01() = blockedByMissingPort("tests/tests.rs", "tutorial_write_delimiter_01")

    @Test fun tutorialWriteSerde01() = blockedByMissingPort("tests/tests.rs", "tutorial_write_serde_01")

    @Test fun tutorialWriteSerde02() = blockedByMissingPort("tests/tests.rs", "tutorial_write_serde_02")

    @Test fun tutorialPipelineSearch01() = blockedByMissingPort("tests/tests.rs", "tutorial_pipeline_search_01")

    @Test fun tutorialPipelineSearch01Errored() = blockedByMissingPort("tests/tests.rs", "tutorial_pipeline_search_01_errored")

    @Test fun tutorialPipelineSearch02() = blockedByMissingPort("tests/tests.rs", "tutorial_pipeline_search_02")

    @Test fun tutorialPipelinePop01() = blockedByMissingPort("tests/tests.rs", "tutorial_pipeline_pop_01")

    @Test fun tutorialPerfAlloc01() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_alloc_01")

    @Test fun tutorialPerfAlloc02() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_alloc_02")

    @Test fun tutorialPerfAlloc03() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_alloc_03")

    @Test fun tutorialPerfSerde01() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_serde_01")

    @Test fun tutorialPerfSerde02() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_serde_02")

    @Test fun tutorialPerfSerde03() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_serde_03")

    @Test fun tutorialPerfCore01() = blockedByMissingPort("tests/tests.rs", "tutorial_perf_core_01")

    @Test fun noInfiniteLoopOnIoErrors() = blockedByMissingPort("tests/tests.rs", "no_infinite_loop_on_io_errors")
}

class ByteRecordRustTests {
    @Test fun record1() = blockedByMissingPort("src/byte_record.rs", "record_1")

    @Test fun record2() = blockedByMissingPort("src/byte_record.rs", "record_2")

    @Test fun emptyRecord() = blockedByMissingPort("src/byte_record.rs", "empty_record")

    @Test fun trimWhitespaceOnly() = blockedByMissingPort("src/byte_record.rs", "trim_whitespace_only")

    @Test fun trimFront() = blockedByMissingPort("src/byte_record.rs", "trim_front")

    @Test fun trimBack() = blockedByMissingPort("src/byte_record.rs", "trim_back")

    @Test fun trimBoth() = blockedByMissingPort("src/byte_record.rs", "trim_both")

    @Test fun trimDoesNotPanicOnEmptyRecords1() = blockedByMissingPort("src/byte_record.rs", "trim_does_not_panic_on_empty_records_1")

    @Test fun trimDoesNotPanicOnEmptyRecords2() = blockedByMissingPort("src/byte_record.rs", "trim_does_not_panic_on_empty_records_2")

    @Test fun trimDoesNotPanicOnEmptyRecords3() = blockedByMissingPort("src/byte_record.rs", "trim_does_not_panic_on_empty_records_3")

    @Test fun emptyField1() = blockedByMissingPort("src/byte_record.rs", "empty_field_1")

    @Test fun emptyField2() = blockedByMissingPort("src/byte_record.rs", "empty_field_2")

    @Test fun emptySurround1() = blockedByMissingPort("src/byte_record.rs", "empty_surround_1")

    @Test fun emptySurround2() = blockedByMissingPort("src/byte_record.rs", "empty_surround_2")

    @Test fun utf8Error1() = blockedByMissingPort("src/byte_record.rs", "utf8_error_1")

    @Test fun utf8Error2() = blockedByMissingPort("src/byte_record.rs", "utf8_error_2")

    @Test fun utf8Error3() = blockedByMissingPort("src/byte_record.rs", "utf8_error_3")

    @Test fun utf8Error4() = blockedByMissingPort("src/byte_record.rs", "utf8_error_4")

    @Test fun utf8Error5() = blockedByMissingPort("src/byte_record.rs", "utf8_error_5")

    @Test fun utf8Error6() = blockedByMissingPort("src/byte_record.rs", "utf8_error_6")

    @Test fun utf8ClearOk() = blockedByMissingPort("src/byte_record.rs", "utf8_clear_ok")

    @Test fun iter() = blockedByMissingPort("src/byte_record.rs", "iter")

    @Test fun iterReverse() = blockedByMissingPort("src/byte_record.rs", "iter_reverse")

    @Test fun iterForwardAndReverse() = blockedByMissingPort("src/byte_record.rs", "iter_forward_and_reverse")

    @Test fun eqFieldBoundaries() = blockedByMissingPort("src/byte_record.rs", "eq_field_boundaries")

    @Test fun eqRecordLen() = blockedByMissingPort("src/byte_record.rs", "eq_record_len")
}

class StringRecordRustTests {
    @Test fun trimFront() = blockedByMissingPort("src/string_record.rs", "trim_front")

    @Test fun trimBack() = blockedByMissingPort("src/string_record.rs", "trim_back")

    @Test fun trimBoth() = blockedByMissingPort("src/string_record.rs", "trim_both")

    @Test fun trimDoesNotPanicOnEmptyRecords1() = blockedByMissingPort("src/string_record.rs", "trim_does_not_panic_on_empty_records_1")

    @Test fun trimDoesNotPanicOnEmptyRecords2() = blockedByMissingPort("src/string_record.rs", "trim_does_not_panic_on_empty_records_2")

    @Test fun trimDoesNotPanicOnEmptyRecords3() = blockedByMissingPort("src/string_record.rs", "trim_does_not_panic_on_empty_records_3")

    @Test fun trimWhitespaceOnly() = blockedByMissingPort("src/string_record.rs", "trim_whitespace_only")

    @Test fun eqFieldBoundaries() = blockedByMissingPort("src/string_record.rs", "eq_field_boundaries")

    @Test fun eqRecordLen() = blockedByMissingPort("src/string_record.rs", "eq_record_len")
}

class ReaderRustTests {
    @Test fun readByteRecord() = blockedByMissingPort("src/reader.rs", "read_byte_record")

    @Test fun readTrimmedRecordsAndHeaders() = blockedByMissingPort("src/reader.rs", "read_trimmed_records_and_headers")

    @Test fun readTrimmedHeader() = blockedByMissingPort("src/reader.rs", "read_trimmed_header")

    @Test fun readTrimedHeaderInvalidUtf8() = blockedByMissingPort("src/reader.rs", "read_trimed_header_invalid_utf8")

    @Test fun readTrimmedRecords() = blockedByMissingPort("src/reader.rs", "read_trimmed_records")

    @Test fun readTrimmedRecordsWithoutHeaders() = blockedByMissingPort("src/reader.rs", "read_trimmed_records_without_headers")

    @Test fun readRecordUnequalFails() = blockedByMissingPort("src/reader.rs", "read_record_unequal_fails")

    @Test fun readRecordUnequalOk() = blockedByMissingPort("src/reader.rs", "read_record_unequal_ok")

    @Test fun readRecordUnequalContinue() = blockedByMissingPort("src/reader.rs", "read_record_unequal_continue")

    @Test fun readRecordHeaders() = blockedByMissingPort("src/reader.rs", "read_record_headers")

    @Test fun readRecordHeadersInvalidUtf8() = blockedByMissingPort("src/reader.rs", "read_record_headers_invalid_utf8")

    @Test fun readRecordNoHeadersBefore() = blockedByMissingPort("src/reader.rs", "read_record_no_headers_before")

    @Test fun readRecordNoHeadersAfter() = blockedByMissingPort("src/reader.rs", "read_record_no_headers_after")

    @Test fun seek() = blockedByMissingPort("src/reader.rs", "seek")

    @Test fun seekHeadersAfter() = blockedByMissingPort("src/reader.rs", "seek_headers_after")

    @Test fun seekHeadersBeforeAfter() = blockedByMissingPort("src/reader.rs", "seek_headers_before_after")

    @Test fun seekHeadersNoActualSeek() = blockedByMissingPort("src/reader.rs", "seek_headers_no_actual_seek")

    @Test fun positionsNoHeaders() = blockedByMissingPort("src/reader.rs", "positions_no_headers")

    @Test fun positionsHeaders() = blockedByMissingPort("src/reader.rs", "positions_headers")

    @Test fun headersOnEmptyData() = blockedByMissingPort("src/reader.rs", "headers_on_empty_data")

    @Test fun noHeadersOnEmptyData() = blockedByMissingPort("src/reader.rs", "no_headers_on_empty_data")

    @Test fun noHeadersOnEmptyDataAfterHeaders() = blockedByMissingPort("src/reader.rs", "no_headers_on_empty_data_after_headers")
}

class WriterRustTests {
    @Test fun oneRecord() = blockedByMissingPort("src/writer.rs", "one_record")

    @Test fun oneStringRecord() = blockedByMissingPort("src/writer.rs", "one_string_record")

    @Test fun oneByteRecord() = blockedByMissingPort("src/writer.rs", "one_byte_record")

    @Test fun rawOneByteRecord() = blockedByMissingPort("src/writer.rs", "raw_one_byte_record")

    @Test fun oneEmptyRecord() = blockedByMissingPort("src/writer.rs", "one_empty_record")

    @Test fun rawOneEmptyRecord() = blockedByMissingPort("src/writer.rs", "raw_one_empty_record")

    @Test fun twoEmptyRecords() = blockedByMissingPort("src/writer.rs", "two_empty_records")

    @Test fun rawTwoEmptyRecords() = blockedByMissingPort("src/writer.rs", "raw_two_empty_records")

    @Test fun unequalRecordsBad() = blockedByMissingPort("src/writer.rs", "unequal_records_bad")

    @Test fun rawUnequalRecordsBad() = blockedByMissingPort("src/writer.rs", "raw_unequal_records_bad")

    @Test fun unequalRecordsOk() = blockedByMissingPort("src/writer.rs", "unequal_records_ok")

    @Test fun rawUnequalRecordsOk() = blockedByMissingPort("src/writer.rs", "raw_unequal_records_ok")

    @Test fun fullBufferShouldNotFlushUnderlying() = blockedByMissingPort("src/writer.rs", "full_buffer_should_not_flush_underlying")

    @Test fun serializeWithHeaders() = blockedByMissingPort("src/writer.rs", "serialize_with_headers")

    @Test fun serializeNoHeaders() = blockedByMissingPort("src/writer.rs", "serialize_no_headers")

    @Test fun serializeNoHeaders128() = blockedByMissingPort("src/writer.rs", "serialize_no_headers_128")

    @Test fun serializeTuple() = blockedByMissingPort("src/writer.rs", "serialize_tuple")

    @Test fun commentCharIsAutomaticallyQuoted() = blockedByMissingPort("src/writer.rs", "comment_char_is_automatically_quoted")
}

class SerializerRustTests {
    @Test fun bool() = blockedByMissingPort("src/serializer.rs", "bool")

    @Test fun integer() = blockedByMissingPort("src/serializer.rs", "integer")

    @Test fun integerU128() = blockedByMissingPort("src/serializer.rs", "integer_u128")

    @Test fun integerI128() = blockedByMissingPort("src/serializer.rs", "integer_i128")

    @Test fun float() = blockedByMissingPort("src/serializer.rs", "float")

    @Test fun floatNan() = blockedByMissingPort("src/serializer.rs", "float_nan")

    @Test fun char() = blockedByMissingPort("src/serializer.rs", "char")

    @Test fun str() = blockedByMissingPort("src/serializer.rs", "str")

    @Test fun bytes() = blockedByMissingPort("src/serializer.rs", "bytes")

    @Test fun option() = blockedByMissingPort("src/serializer.rs", "option")

    @Test fun unit() = blockedByMissingPort("src/serializer.rs", "unit")

    @Test fun structUnit() = blockedByMissingPort("src/serializer.rs", "struct_unit")

    @Test fun structNewtype() = blockedByMissingPort("src/serializer.rs", "struct_newtype")

    @Test fun enumUnits() = blockedByMissingPort("src/serializer.rs", "enum_units")

    @Test fun enumNewtypes() = blockedByMissingPort("src/serializer.rs", "enum_newtypes")

    @Test fun seq() = blockedByMissingPort("src/serializer.rs", "seq")

    @Test fun tuple() = blockedByMissingPort("src/serializer.rs", "tuple")

    @Test fun tupleStruct() = blockedByMissingPort("src/serializer.rs", "tuple_struct")

    @Test fun tupleVariant() = blockedByMissingPort("src/serializer.rs", "tuple_variant")

    @Test fun enumStructVariant() = blockedByMissingPort("src/serializer.rs", "enum_struct_variant")

    @Test fun structNoHeaders() = blockedByMissingPort("src/serializer.rs", "struct_no_headers")

    @Test fun structNoHeaders128() = blockedByMissingPort("src/serializer.rs", "struct_no_headers_128")

    @Test fun structHeaders() = blockedByMissingPort("src/serializer.rs", "struct_headers")

    @Test fun structHeadersNested() = blockedByMissingPort("src/serializer.rs", "struct_headers_nested")

    @Test fun structHeadersNestedSeq() = blockedByMissingPort("src/serializer.rs", "struct_headers_nested_seq")

    @Test fun structHeadersInsideTuple() = blockedByMissingPort("src/serializer.rs", "struct_headers_inside_tuple")

    @Test fun structHeadersInsideTupleScalarBefore() = blockedByMissingPort("src/serializer.rs", "struct_headers_inside_tuple_scalar_before")

    @Test fun structHeadersInsideTupleScalarAfter() = blockedByMissingPort("src/serializer.rs", "struct_headers_inside_tuple_scalar_after")

    @Test fun structHeadersInsideSeq() = blockedByMissingPort("src/serializer.rs", "struct_headers_inside_seq")

    @Test fun structHeadersInsideNestedTupleSeq() = blockedByMissingPort("src/serializer.rs", "struct_headers_inside_nested_tuple_seq")
}

class DeserializerRustTests {
    @Test fun withHeader() = blockedByMissingPort("src/deserializer.rs", "with_header")

    @Test fun withHeaderUnknown() = blockedByMissingPort("src/deserializer.rs", "with_header_unknown")

    @Test fun withHeaderMissing() = blockedByMissingPort("src/deserializer.rs", "with_header_missing")

    @Test fun withHeaderMissingOk() = blockedByMissingPort("src/deserializer.rs", "with_header_missing_ok")

    @Test fun withHeaderNoFields() = blockedByMissingPort("src/deserializer.rs", "with_header_no_fields")

    @Test fun withHeaderEmpty() = blockedByMissingPort("src/deserializer.rs", "with_header_empty")

    @Test fun withHeaderEmptyOk() = blockedByMissingPort("src/deserializer.rs", "with_header_empty_ok")

    @Test fun withoutHeader() = blockedByMissingPort("src/deserializer.rs", "without_header")

    @Test fun noFields() = blockedByMissingPort("src/deserializer.rs", "no_fields")

    @Test fun oneField() = blockedByMissingPort("src/deserializer.rs", "one_field")

    @Test fun oneField128() = blockedByMissingPort("src/deserializer.rs", "one_field_128")

    @Test fun twoFields() = blockedByMissingPort("src/deserializer.rs", "two_fields")

    @Test fun twoFieldsTooMany() = blockedByMissingPort("src/deserializer.rs", "two_fields_too_many")

    @Test fun twoFieldsTooFew() = blockedByMissingPort("src/deserializer.rs", "two_fields_too_few")

    @Test fun oneChar() = blockedByMissingPort("src/deserializer.rs", "one_char")

    @Test fun noChars() = blockedByMissingPort("src/deserializer.rs", "no_chars")

    @Test fun tooManyChars() = blockedByMissingPort("src/deserializer.rs", "too_many_chars")

    @Test fun simpleSeq() = blockedByMissingPort("src/deserializer.rs", "simple_seq")

    @Test fun simpleHexSeq() = blockedByMissingPort("src/deserializer.rs", "simple_hex_seq")

    @Test fun mixedHexSeq() = blockedByMissingPort("src/deserializer.rs", "mixed_hex_seq")

    @Test fun badHexSeq() = blockedByMissingPort("src/deserializer.rs", "bad_hex_seq")

    @Test fun seqInStruct() = blockedByMissingPort("src/deserializer.rs", "seq_in_struct")

    @Test fun seqInStructTail() = blockedByMissingPort("src/deserializer.rs", "seq_in_struct_tail")

    @Test fun mapHeaders() = blockedByMissingPort("src/deserializer.rs", "map_headers")

    @Test fun mapNoHeaders() = blockedByMissingPort("src/deserializer.rs", "map_no_headers")

    @Test fun bytes() = blockedByMissingPort("src/deserializer.rs", "bytes")

    @Test fun adjacentFixedArrays() = blockedByMissingPort("src/deserializer.rs", "adjacent_fixed_arrays")

    @Test fun enumLabelSimpleTagged() = blockedByMissingPort("src/deserializer.rs", "enum_label_simple_tagged")

    @Test fun enumUntagged() = blockedByMissingPort("src/deserializer.rs", "enum_untagged")

    @Test fun optionEmptyField() = blockedByMissingPort("src/deserializer.rs", "option_empty_field")

    @Test fun optionInvalidField() = blockedByMissingPort("src/deserializer.rs", "option_invalid_field")

    @Test fun borrowed() = blockedByMissingPort("src/deserializer.rs", "borrowed")

    @Test fun borrowedMap() = blockedByMissingPort("src/deserializer.rs", "borrowed_map")

    @Test fun borrowedMapBytes() = blockedByMissingPort("src/deserializer.rs", "borrowed_map_bytes")

    @Test fun flatten() = blockedByMissingPort("src/deserializer.rs", "flatten")

    @Test fun partiallyInvalidUtf8() = blockedByMissingPort("src/deserializer.rs", "partially_invalid_utf8")
}
