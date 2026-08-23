# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 7/11 (63.6%)
- **Function parity:** 233/501 matched (target 352) — 46.5%
- **Class/type parity:** 30/74 matched (target 46) — 40.5%
- **Combined symbol parity:** 263/575 matched (target 398) — 45.7%
- **Average inline-code cosine:** 0.48 (function body across 7 matched files)
- **Average documentation cosine:** 0.47 (doc text across 7 matched files)
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
- **Similarity:** 0.54
- **Dependents:** 1
- **Priority Score:** 1044704.6
- **Functions:** 41/42 matched (target 57)
- **Missing functions:** `deserialize`
- **Types:** 2/5 matched (target 3)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 9/9 matched

### 2. reader

- **Target:** `csv.Reader`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 127804.9
- **Functions:** 57/66 matched (target 96)
- **Missing functions:** `from_path`, `deserialize`, `into_deserialize`, `set_headers_impl`, `read_byte_record_impl`, `add_record`, `b`, `s`, `newpos`
- **Types:** 9/12 matched (target 10)
- **Missing types:** `DeserializeRecordsIntoIter`, `Item`, `DeserializeRecordsIter`
- **Tests:** 20/23 matched

### 3. writer

- **Target:** `csv.Writer`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 126005.3
- **Functions:** 43/53 matched (target 66)
- **Missing functions:** `from_path`, `drop`, `serialize`, `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
- **Types:** 5/7 matched (target 6)
- **Missing types:** `MarkWriteAndFlush`, `Row`
- **Tests:** 14/21 matched

### 4. byte_record

- **Target:** `csv.Position`
- **Similarity:** 0.54
- **Dependents:** 0
- **Priority Score:** 58104.6
- **Functions:** 71/73 matched (target 92)
- **Missing functions:** `deserialize`, `b`
- **Types:** 5/8 matched (target 6)
- **Missing types:** `Output`, `IntoIter`, `Item`
- **Tests:** 25/26 matched

### 5. error

- **Target:** `csv.Error`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 32205.3
- **Functions:** 14/16 matched (target 24)
- **Missing functions:** `from`, `source`
- **Types:** 5/6 matched (target 12)
- **Missing types:** `Result`

### 6. lib

- **Target:** `csv.Lib`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 706.6
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_

### 7. debug

- **Target:** `csv.Debug`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 405.5
- **Functions:** 3/3 matched (target 10)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

