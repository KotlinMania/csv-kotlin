# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/49 (22.4%)
- **Function parity:** 414/561 matched (target 706) — 73.8%
- **Class/type parity:** 72/97 matched (target 141) — 74.2%
- **Combined symbol parity:** 486/658 matched (target 847) — 73.9%
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

### 1. csv.reader

- **Target:** `csv.Reader`
- **Similarity:** 0.61
- **Dependents:** 1
- **Priority Score:** 1037803.9
- **Functions:** 63/66 matched (target 118)
- **Missing functions:** `b`, `s`, `newpos`
- **Types:** 12/12 matched (target 14)
- **Missing types:** _none_
- **Tests:** 20/23 matched

### 2. csv.string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.55
- **Dependents:** 1
- **Priority Score:** 1004704.5
- **Functions:** 42/42 matched (target 59)
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 9)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 3. csv.deserializer

- **Target:** `csv.Deserializer`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 50805.6
- **Functions:** 90/93 matched (target 131)
- **Missing functions:** `de`, `de_headers`, `b`
- **Types:** 14/15 matched (target 38)
- **Missing types:** `Error`
- **Tests:** 36/39 matched

### 4. csv.serializer

- **Target:** `csv.Serializer`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 39304.3
- **Functions:** 74/76 matched (target 142)
- **Missing functions:** `serialize_err`, `serialize_header_err`
- **Types:** 16/17 matched (target 33)
- **Missing types:** `Error`
- **Tests:** 30/32 matched

### 5. csv.writer

- **Target:** `csv.Writer`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 36004.1
- **Functions:** 50/53 matched (target 90)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`
- **Types:** 7/7 matched (target 9)
- **Missing types:** _none_
- **Tests:** 18/21 matched

### 6. csv.byte_record

- **Target:** `csv.ByteRecord`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 18104.8
- **Functions:** 72/73 matched (target 100)
- **Missing functions:** `b`
- **Types:** 8/8 matched (target 12)
- **Missing types:** _none_
- **Tests:** 25/26 matched

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

- **Target:** `csv.Lib [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 710.0
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_

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

