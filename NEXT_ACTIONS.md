# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/11 (100.0%)
- **Function parity:** 414/426 matched (target 717) — 97.2%
- **Class/type parity:** 72/74 matched (target 143) — 97.3%
- **Combined symbol parity:** 486/500 matched (target 860) — 97.2%
- **Average inline-code cosine:** 0.41 (function body across 11 matched files)
- **Average documentation cosine:** 0.43 (doc text across 11 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 10 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1004704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 9)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 2. deserializer

- **Target:** `csv.Deserializer`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 50805.6
- **Functions:** 90/93 matched (target 131)
- **Missing functions:** `de`, `de_headers`, `b`
- **Types:** 14/15 matched (target 38)
- **Missing types:** `Error`
- **Tests:** 36/39 matched

### 3. serializer

- **Target:** `csv.Serializer`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 39304.3
- **Functions:** 74/76 matched (target 142)
- **Missing functions:** `serialize_err`, `serialize_header_err`
- **Types:** 16/17 matched (target 33)
- **Missing types:** `Error`
- **Tests:** 30/32 matched

### 4. reader

- **Target:** `csv.Reader`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 37804.0
- **Functions:** 63/66 matched (target 116)
- **Missing functions:** `b`, `s`, `newpos`
- **Types:** 12/12 matched (target 14)
- **Missing types:** _none_
- **Tests:** 20/23 matched

### 5. writer

- **Target:** `csv.Writer`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 36004.1
- **Functions:** 50/53 matched (target 91)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`
- **Types:** 7/7 matched (target 9)
- **Missing types:** _none_
- **Tests:** 18/21 matched

### 6. byte_record

- **Target:** `csv.ByteRecord`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 18104.8
- **Functions:** 72/73 matched (target 99)
- **Missing functions:** `b`
- **Types:** 8/8 matched (target 12)
- **Missing types:** _none_
- **Tests:** 25/26 matched

### 7. error

- **Target:** `csv.Error`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 2205.5
- **Functions:** 16/16 matched (target 36)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

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
- **Functions:** 3/3 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 5)
- **Missing types:** _none_

### 10. cookbook

- **Target:** `csv.Cookbook [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 11. tutorial

- **Target:** `csv.Tutorial [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 7)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

