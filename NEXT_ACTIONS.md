# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 7/11 (63.6%)
- **Function parity:** 162/501 matched (target 222) — 32.3%
- **Class/type parity:** 15/74 matched (target 31) — 20.3%
- **Combined symbol parity:** 177/575 matched (target 253) — 30.8%
- **Average inline-code cosine:** 0.32 (function body across 7 matched files)
- **Average documentation cosine:** 0.44 (doc text across 7 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.36
- **Dependents:** 1
- **Priority Score:** 1194706.4
- **Functions:** 27/42 matched (target 34)
- **Missing functions:** `eq`, `fmt`, `default`, `deserialize`, `iter`, `as_slice`, `clone_truncated`, `read`, `index`, `from_iter`, `extend`, `into_iter`, `size_hint`, `count`, `next_back`
- **Types:** 1/5 matched (target 2)
- **Missing types:** `Output`, `IntoIter`, `Item`, `StringRecordIter`
- **Tests:** 9/9 matched

### 2. reader

- **Target:** `csv.Reader`
- **Similarity:** 0.31
- **Dependents:** 0
- **Priority Score:** 347806.8
- **Functions:** 42/66 matched (target 56)
- **Missing functions:** `default`, `from_path`, `quoting`, `ascii`, `nfa`, `deserialize`, `into_deserialize`, `into_records`, `into_byte_records`, `set_headers_impl`, `read_byte_record_impl`, `is_done`, `get_ref`, `get_mut`, `into_inner`, `seek_raw`, `add_record`, `reader`, `reader_mut`, `into_reader`, `next`, `b`, `s`, `newpos`
- **Types:** 2/12 matched (target 3)
- **Missing types:** `ReaderState`, `ReaderEofState`, `Headers`, `DeserializeRecordsIntoIter`, `Item`, `DeserializeRecordsIter`, `StringRecordsIntoIter`, `StringRecordsIter`, `ByteRecordsIntoIter`, `ByteRecordsIter`
- **Tests:** 20/23 matched

### 3. byte_record

- **Target:** `csv.Position`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 328106.1
- **Functions:** 47/73 matched (target 59)
- **Missing functions:** `eq`, `fmt`, `default`, `deserialize`, `as_slice`, `clone_truncated`, `as_parts`, `set_len`, `expand_fields`, `expand_ends`, `iter_eq`, `ends`, `end`, `expand`, `add`, `index`, `from_iter`, `extend`, `into_iter`, `size_hint`, `count`, `next_back`, `trim_ascii`, `trim_ascii_start`, `trim_ascii_end`, `b`
- **Types:** 2/8 matched (target 3)
- **Missing types:** `ByteRecordInner`, `Bounds`, `Output`, `ByteRecordIter`, `IntoIter`, `Item`
- **Tests:** 25/26 matched

### 4. writer

- **Target:** `csv.Writer`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 266005.9
- **Functions:** 32/53 matched (target 46)
- **Missing functions:** `default`, `from_path`, `drop`, `serialize`, `write_field_impl`, `flush_buf`, `get_ref`, `write_delimiter`, `write_terminator_into_buffer`, `check_field_count`, `readable`, `writable`, `written`, `clear`, `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
- **Types:** 2/7 matched (target 3)
- **Missing types:** `WriterState`, `HeaderState`, `Buffer`, `MarkWriteAndFlush`, `Row`
- **Tests:** 14/21 matched

### 5. error

- **Target:** `csv.Error`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 62206.6
- **Functions:** 12/16 matched (target 15)
- **Missing functions:** `new`, `from`, `fmt`, `source`
- **Types:** 4/6 matched (target 11)
- **Missing types:** `Result`, `Error`

### 6. lib

- **Target:** `csv.Lib`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40710.0
- **Functions:** 0/4 matched (target 3)
- **Missing functions:** `to_core`, `should_trim_fields`, `should_trim_headers`, `invalid_option`
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_

### 7. debug

- **Target:** `csv.Debug`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 10406.0
- **Functions:** 2/3 matched (target 9)
- **Missing functions:** `fmt`
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

