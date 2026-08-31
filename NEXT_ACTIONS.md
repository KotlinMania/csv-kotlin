# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/11 (100.0%)
- **Function parity:** 414/426 matched (target 706) — 97.2%
- **Class/type parity:** 72/74 matched (target 141) — 97.3%
- **Combined symbol parity:** 486/500 matched (target 847) — 97.2%
- **Average inline-code cosine:** 0.52 (function body across 8 matched files)
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

### 1. string_record

- **Target:** `csv.StringRecord [PROVENANCE-FALLBACK]`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1004704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 9)
- **Missing types:** _none_
- **Tests:** 9/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/string_record.rs` vs expected `string_record.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/string_record.rs` vs expected `string_record.rs`
- **Proposed provenance header:** `// port-lint: source string_record.rs` (current: `// port-lint: source csv/src/string_record.rs`)
- **Proposed provenance header:** `// port-lint: tests string_record.rs` (current: `// port-lint: tests csv/src/string_record.rs`)
- **Lint issues:** 2

### 2. deserializer

- **Target:** `csv.Deserializer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 50805.6
- **Functions:** 90/93 matched (target 131)
- **Missing functions:** `de`, `de_headers`, `b`
- **Types:** 14/15 matched (target 38)
- **Missing types:** `Error`
- **Tests:** 36/39 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/deserializer.rs` vs expected `deserializer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/deserializer.rs` vs expected `deserializer.rs`
- **Proposed provenance header:** `// port-lint: source deserializer.rs` (current: `// port-lint: source csv/src/deserializer.rs`)
- **Proposed provenance header:** `// port-lint: tests deserializer.rs` (current: `// port-lint: tests csv/src/deserializer.rs`)
- **Lint issues:** 2

### 3. serializer

- **Target:** `csv.Serializer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 39304.3
- **Functions:** 74/76 matched (target 142)
- **Missing functions:** `serialize_err`, `serialize_header_err`
- **Types:** 16/17 matched (target 33)
- **Missing types:** `Error`
- **Tests:** 30/32 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/serializer.rs` vs expected `serializer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/serializer.rs` vs expected `serializer.rs`
- **Proposed provenance header:** `// port-lint: source serializer.rs` (current: `// port-lint: source csv/src/serializer.rs`)
- **Proposed provenance header:** `// port-lint: tests serializer.rs` (current: `// port-lint: tests csv/src/serializer.rs`)
- **Lint issues:** 2

### 4. reader

- **Target:** `csv.Reader [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 37803.9
- **Functions:** 63/66 matched (target 118)
- **Missing functions:** `b`, `s`, `newpos`
- **Types:** 12/12 matched (target 14)
- **Missing types:** _none_
- **Tests:** 20/23 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/reader.rs` vs expected `reader.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/reader.rs` vs expected `reader.rs`
- **Proposed provenance header:** `// port-lint: source reader.rs` (current: `// port-lint: source csv/src/reader.rs`)
- **Proposed provenance header:** `// port-lint: tests reader.rs` (current: `// port-lint: tests csv/src/reader.rs`)
- **Lint issues:** 2

### 5. writer

- **Target:** `csv.Writer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 36004.1
- **Functions:** 50/53 matched (target 90)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`
- **Types:** 7/7 matched (target 9)
- **Missing types:** _none_
- **Tests:** 18/21 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/writer.rs` vs expected `writer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/writer.rs` vs expected `writer.rs`
- **Proposed provenance header:** `// port-lint: source writer.rs` (current: `// port-lint: source csv/src/writer.rs`)
- **Proposed provenance header:** `// port-lint: tests writer.rs` (current: `// port-lint: tests csv/src/writer.rs`)
- **Lint issues:** 2

### 6. byte_record

- **Target:** `csv.ByteRecord [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 18104.8
- **Functions:** 72/73 matched (target 100)
- **Missing functions:** `b`
- **Types:** 8/8 matched (target 12)
- **Missing types:** _none_
- **Tests:** 25/26 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/byte_record.rs` vs expected `byte_record.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/byte_record.rs` vs expected `byte_record.rs`
- **Proposed provenance header:** `// port-lint: source byte_record.rs` (current: `// port-lint: source csv/src/byte_record.rs`)
- **Proposed provenance header:** `// port-lint: tests byte_record.rs` (current: `// port-lint: tests csv/src/byte_record.rs`)
- **Lint issues:** 2

### 7. error

- **Target:** `csv.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 2205.5
- **Functions:** 16/16 matched (target 36)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/error.rs` vs expected `error.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source csv/src/error.rs`)
- **Proposed provenance header:** `// port-lint: tests error.rs` (current: `// port-lint: tests csv/src/error.rs`)
- **Lint issues:** 2

### 8. lib

- **Target:** `csv.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 710.0
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source csv/src/lib.rs`)
- **Lint issues:** 1

### 9. debug

- **Target:** `csv.Debug [PROVENANCE-FALLBACK]`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 405.5
- **Functions:** 3/3 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/debug.rs` vs expected `debug.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:csv/src/debug.rs` vs expected `debug.rs`
- **Proposed provenance header:** `// port-lint: source debug.rs` (current: `// port-lint: source csv/src/debug.rs`)
- **Proposed provenance header:** `// port-lint: tests debug.rs` (current: `// port-lint: tests csv/src/debug.rs`)
- **Lint issues:** 2

### 10. cookbook

- **Target:** `csv.Cookbook [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/cookbook.rs` vs expected `cookbook.rs`
- **Proposed provenance header:** `// port-lint: source cookbook.rs` (current: `// port-lint: source csv/src/cookbook.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `csv/src/tutorial.rs` vs expected `tutorial.rs`
- **Proposed provenance header:** `// port-lint: source tutorial.rs` (current: `// port-lint: source csv/src/tutorial.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

