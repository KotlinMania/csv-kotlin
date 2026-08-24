# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/11 (100.0%)
- **Function parity:** 248/426 matched (target 435) — 58.2%
- **Class/type parity:** 30/74 matched (target 61) — 40.5%
- **Combined symbol parity:** 278/500 matched (target 496) — 55.6%
- **Average inline-code cosine:** 0.32 (function body across 11 matched files)
- **Average documentation cosine:** 0.35 (doc text across 11 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 11 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. deserializer

- **Target:** `csv.Deserializer`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 1060810.0
- **Functions:** 2/93 matched (target 22)
- **Missing functions:** `deserialize_string_record`, `has_headers`, `next_header`, `next_header_bytes`, `next_field`, `next_field_bytes`, `peek_field`, `error`, `infer_deserialize`, `deserialize_any`, `deserialize_bool`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_identifier`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `custom`, `description`, `fmt`, `field`, `kind`, `try_positive_integer128`, `try_negative_integer128`, `try_positive_integer64`, `try_negative_integer64`, `try_float`, `try_positive_integer64_bytes`, `try_negative_integer64_bytes`, `try_positive_integer128_bytes`, `try_negative_integer128_bytes`, `try_float_bytes`, `de`, `de_headers`, `b`, `with_header`, `with_header_unknown`, `with_header_missing`, `with_header_missing_ok`, `with_header_no_fields`, `with_header_empty`, `with_header_empty_ok`, `without_header`, `no_fields`, `one_field`, `one_field_128`, `two_fields`, `two_fields_too_many`, `two_fields_too_few`, `one_char`, `no_chars`, `too_many_chars`, `simple_seq`, `simple_hex_seq`, `mixed_hex_seq`, `bad_hex_seq`, `seq_in_struct`, `seq_in_struct_tail`, `map_headers`, `map_no_headers`, `bytes`, `adjacent_fixed_arrays`, `enum_label_simple_tagged`, `enum_untagged`, `option_empty_field`, `option_invalid_field`, `borrowed`, `borrowed_map`, `borrowed_map_bytes`, `flatten`, `partially_invalid_utf8`
- **Types:** 1/15 matched (target 7)
- **Missing types:** `DeRecord`, `DeRecordWrap`, `DeStringRecord`, `DeByteRecord`, `Error`, `Variant`, `DeserializeError`, `DeserializeErrorKind`, `Foo`, `Bar`, `Label`, `Boolish`, `Input`, `Properties`
- **Tests:** 0/39 matched

### 2. string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1034704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 2/5 matched (target 3)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 9/9 matched

### 3. serializer

- **Target:** `csv.Serializer`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 909309.9
- **Functions:** 3/76 matched (target 22)
- **Missing functions:** `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `serialize_bytes`, `serialize_none`, `serialize_some`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_seq`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `serialize_element`, `end`, `serialize_field`, `serialize_key`, `serialize_value`, `custom`, `error_scalar_outside_struct`, `error_container_inside_struct`, `new`, `wrote_header`, `handle_scalar`, `handle_container`, `serialize_err`, `serialize_header_err`, `bool`, `integer`, `integer_u128`, `integer_i128`, `float`, `float_nan`, `char`, `str`, `bytes`, `option`, `unit`, `struct_unit`, `struct_newtype`, `enum_units`, `enum_newtypes`, `seq`, `tuple`, `tuple_struct`, `tuple_variant`, `enum_struct_variant`, `struct_no_headers`, `struct_no_headers_128`, `struct_headers`, `struct_headers_nested`, `struct_headers_nested_seq`, `struct_headers_inside_tuple`, `struct_headers_inside_tuple_scalar_before`, `struct_headers_inside_tuple_scalar_after`, `struct_headers_inside_seq`, `struct_headers_inside_nested_tuple_seq`
- **Types:** 0/17 matched (target 7)
- **Missing types:** `SeRecord`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `HeaderState`, `SeHeader`, `Foo`, `Wat`, `Nested`, `Bar`, `Baz`
- **Tests:** 0/32 matched
- **Lint issues:** 2

### 4. writer

- **Target:** `csv.Writer`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 106004.9
- **Functions:** 45/53 matched (target 75)
- **Missing functions:** `from_path`, `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
- **Types:** 5/7 matched (target 6)
- **Missing types:** `MarkWriteAndFlush`, `Row`
- **Tests:** 14/21 matched

### 5. reader

- **Target:** `csv.Reader`
- **Similarity:** 0.58
- **Dependents:** 0
- **Priority Score:** 97804.2
- **Functions:** 61/66 matched (target 115)
- **Missing functions:** `from_path`, `add_record`, `b`, `s`, `newpos`
- **Types:** 8/12 matched (target 9)
- **Missing types:** `ReaderState`, `ReaderEofState`, `Headers`, `Item`
- **Tests:** 20/23 matched

### 6. byte_record

- **Target:** `csv.ByteRecord`
- **Similarity:** 0.55
- **Dependents:** 0
- **Priority Score:** 48104.5
- **Functions:** 72/73 matched (target 95)
- **Missing functions:** `b`
- **Types:** 5/8 matched (target 6)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 25/26 matched

### 7. error

- **Target:** `csv.Error`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 12204.9
- **Functions:** 16/16 matched (target 27)
- **Missing functions:** _none_
- **Types:** 5/6 matched (target 12)
- **Missing types:** `Result`

### 8. lib

- **Target:** `csv.Lib`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 706.6
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_

### 9. debug

- **Target:** `csv.Debug`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 405.5
- **Functions:** 3/3 matched (target 10)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_

### 10. cookbook

- **Target:** `csv.Cookbook [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 11. tutorial

- **Target:** `csv.Tutorial [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

