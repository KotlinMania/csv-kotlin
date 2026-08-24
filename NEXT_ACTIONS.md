# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/49 (22.4%)
- **Function parity:** 311/561 matched (target 579) — 55.4%
- **Class/type parity:** 51/97 matched (target 100) — 52.6%
- **Combined symbol parity:** 362/658 matched (target 679) — 55.0%
- **Average inline-code cosine:** 0.45 (function body across 9 matched files)
- **Average documentation cosine:** 0.48 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 10 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. reader

- **Target:** `csv.Reader [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 1
- **Priority Score:** 1047803.9
- **Functions:** 63/66 matched (target 117)
- **Missing functions:** `b`, `s`, `newpos`
- **Types:** 11/12 matched (target 13)
- **Missing types:** `Item`
- **Tests:** 20/23 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `reader.rs` vs expected `reader.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:reader.rs` vs expected `reader.rs`
- **Proposed provenance header:** `// port-lint: source reader.rs` (current: `// port-lint: source reader.rs`)
- **Proposed provenance header:** `// port-lint: tests reader.rs` (current: `// port-lint: tests reader.rs`)
- **Lint issues:** 2

### 2. string_record

- **Target:** `csv.StringRecord [PROVENANCE-FALLBACK]`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1034704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 2/5 matched (target 6)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 9/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `string_record.rs` vs expected `string_record.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:string_record.rs` vs expected `string_record.rs`
- **Proposed provenance header:** `// port-lint: source string_record.rs` (current: `// port-lint: source string_record.rs`)
- **Proposed provenance header:** `// port-lint: tests string_record.rs` (current: `// port-lint: tests string_record.rs`)
- **Lint issues:** 2

### 3. deserializer

- **Target:** `csv.Deserializer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 800808.5
- **Functions:** 22/93 matched (target 65)
- **Missing functions:** `infer_deserialize`, `deserialize_any`, `deserialize_bool`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_identifier`, `deserialize_enum`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `custom`, `fmt`, `de`, `de_headers`, `b`, `with_header`, `with_header_unknown`, `with_header_missing`, `with_header_missing_ok`, `with_header_no_fields`, `with_header_empty`, `with_header_empty_ok`, `without_header`, `no_fields`, `one_field`, `one_field_128`, `two_fields`, `two_fields_too_many`, `two_fields_too_few`, `one_char`, `no_chars`, `too_many_chars`, `simple_seq`, `simple_hex_seq`, `mixed_hex_seq`, `bad_hex_seq`, `seq_in_struct`, `seq_in_struct_tail`, `map_headers`, `map_no_headers`, `bytes`, `adjacent_fixed_arrays`, `enum_label_simple_tagged`, `enum_untagged`, `option_empty_field`, `option_invalid_field`, `borrowed`, `borrowed_map`, `borrowed_map_bytes`, `flatten`, `partially_invalid_utf8`
- **Types:** 7/15 matched (target 20)
- **Missing types:** `Error`, `Variant`, `Foo`, `Bar`, `Label`, `Boolish`, `Input`, `Properties`
- **Tests:** 0/39 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `deserializer.rs` vs expected `deserializer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:deserializer.rs` vs expected `deserializer.rs`
- **Proposed provenance header:** `// port-lint: source deserializer.rs` (current: `// port-lint: source deserializer.rs`)
- **Proposed provenance header:** `// port-lint: tests deserializer.rs` (current: `// port-lint: tests deserializer.rs`)
- **Lint issues:** 2

### 4. serializer

- **Target:** `csv.Serializer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 409305.6
- **Functions:** 43/76 matched (target 117)
- **Missing functions:** `custom`, `serialize_err`, `serialize_header_err`, `bool`, `integer`, `integer_u128`, `integer_i128`, `float`, `float_nan`, `char`, `str`, `bytes`, `option`, `unit`, `struct_unit`, `struct_newtype`, `enum_units`, `enum_newtypes`, `seq`, `tuple`, `tuple_struct`, `tuple_variant`, `enum_struct_variant`, `struct_no_headers`, `struct_no_headers_128`, `struct_headers`, `struct_headers_nested`, `struct_headers_nested_seq`, `struct_headers_inside_tuple`, `struct_headers_inside_tuple_scalar_before`, `struct_headers_inside_tuple_scalar_after`, `struct_headers_inside_seq`, `struct_headers_inside_nested_tuple_seq`
- **Types:** 10/17 matched (target 21)
- **Missing types:** `Ok`, `Error`, `Foo`, `Wat`, `Nested`, `Bar`, `Baz`
- **Tests:** 0/32 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serializer.rs` vs expected `serializer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:serializer.rs` vs expected `serializer.rs`
- **Proposed provenance header:** `// port-lint: source serializer.rs` (current: `// port-lint: source serializer.rs`)
- **Proposed provenance header:** `// port-lint: tests serializer.rs` (current: `// port-lint: tests serializer.rs`)
- **Lint issues:** 4

### 5. writer

- **Target:** `csv.Writer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.54
- **Dependents:** 0
- **Priority Score:** 86004.6
- **Functions:** 46/53 matched (target 75)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
- **Types:** 6/7 matched
- **Missing types:** `HeaderState`
- **Tests:** 14/21 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `writer.rs` vs expected `writer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:writer.rs` vs expected `writer.rs`
- **Proposed provenance header:** `// port-lint: source writer.rs` (current: `// port-lint: source writer.rs`)
- **Proposed provenance header:** `// port-lint: tests writer.rs` (current: `// port-lint: tests writer.rs`)
- **Lint issues:** 2

### 6. byte_record

- **Target:** `csv.ByteRecord [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 48104.8
- **Functions:** 72/73 matched (target 100)
- **Missing functions:** `b`
- **Types:** 5/8 matched (target 9)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 25/26 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `byte_record.rs` vs expected `byte_record.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:byte_record.rs` vs expected `byte_record.rs`
- **Proposed provenance header:** `// port-lint: source byte_record.rs` (current: `// port-lint: source byte_record.rs`)
- **Proposed provenance header:** `// port-lint: tests byte_record.rs` (current: `// port-lint: tests byte_record.rs`)
- **Lint issues:** 2

### 7. error

- **Target:** `csv.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 2205.5
- **Functions:** 16/16 matched (target 29)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 13)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Lint issues:** 1

### 8. lib

- **Target:** `csv.Lib [PROVENANCE-FALLBACK]`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 706.6
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 2

### 9. debug

- **Target:** `csv.Debug [PROVENANCE-FALLBACK]`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 405.5
- **Functions:** 3/3 matched (target 10)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `debug.rs` vs expected `debug.rs`
- **Proposed provenance header:** `// port-lint: source debug.rs` (current: `// port-lint: source debug.rs`)
- **Lint issues:** 1

### 10. cookbook

- **Target:** `csv.Cookbook [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `cookbook.rs` vs expected `cookbook.rs`
- **Proposed provenance header:** `// port-lint: source cookbook.rs` (current: `// port-lint: source cookbook.rs`)
- **Lint issues:** 1

### 11. tutorial

- **Target:** `csv.Tutorial [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tutorial.rs` vs expected `tutorial.rs`
- **Proposed provenance header:** `// port-lint: source tutorial.rs` (current: `// port-lint: source tutorial.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

