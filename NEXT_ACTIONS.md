# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/49 (22.4%)
- **Function parity:** 382/561 matched (target 659) — 68.1%
- **Class/type parity:** 60/97 matched (target 126) — 61.9%
- **Combined symbol parity:** 442/658 matched (target 785) — 67.2%
- **Average inline-code cosine:** 0.51 (function body across 8 matched files)
- **Average documentation cosine:** 0.52 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 10 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. csv.reader

- **Target:** `csv.Reader`
- **Similarity:** 0.61
- **Dependents:** 1
- **Priority Score:** 1047803.9
- **Functions:** 63/66 matched (target 117)
- **Missing functions:** `b`, `s`, `newpos`
- **Types:** 11/12 matched (target 13)
- **Missing types:** `Item`
- **Tests:** 20/23 matched

### 2. csv.string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1034704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 2/5 matched (target 6)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 9/9 matched

### 3. csv.deserializer

- **Target:** `csv.Deserializer`
- **Similarity:** 0.36
- **Dependents:** 0
- **Priority Score:** 400806.4
- **Functions:** 58/93 matched (target 99)
- **Missing functions:** `infer_deserialize`, `deserialize_any`, `deserialize_bool`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_identifier`, `deserialize_enum`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `custom`, `fmt`, `de`, `de_headers`, `b`
- **Types:** 11/15 matched (target 34)
- **Missing types:** `Error`, `Variant`, `Bar`, `Row`
- **Tests:** 36/39 matched

### 4. csv.serializer

- **Target:** `csv.Serializer`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 59304.3
- **Functions:** 74/76 matched (target 138)
- **Missing functions:** `serialize_err`, `serialize_header_err`
- **Types:** 14/17 matched (target 29)
- **Missing types:** `Ok`, `Error`, `HeaderState`
- **Tests:** 30/32 matched

### 5. csv.byte_record

- **Target:** `csv.ByteRecord`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 48104.8
- **Functions:** 72/73 matched (target 100)
- **Missing functions:** `b`
- **Types:** 5/8 matched (target 9)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 25/26 matched

### 6. csv.writer

- **Target:** `csv.Writer`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 36004.3
- **Functions:** 50/53 matched (target 80)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`
- **Types:** 7/7 matched (target 9)
- **Missing types:** _none_
- **Tests:** 18/21 matched

### 7. csv.error

- **Target:** `csv.Error`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 2205.5
- **Functions:** 16/16 matched (target 36)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 8. csv.lib

- **Target:** `csv.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 710.0
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 1

### 9. csv.debug

- **Target:** `csv.Debug`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 405.5
- **Functions:** 3/3 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 5)
- **Missing types:** _none_

### 10. csv.cookbook

- **Target:** `csv.Cookbook [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 11. csv.tutorial

- **Target:** `csv.Tutorial [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
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

