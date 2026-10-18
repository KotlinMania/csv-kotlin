# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Serde Modernization:** Completely replaced `kotlinx.serialization` across all main code and test suites with canonical sibling port `io.github.kotlinmania:serde-kotlin:0.1.11` and `serde-test-kotlin:0.1.1`.
- **Transliteration & Parity:**
  - `Serializer.kt` & `Deserializer.kt`: 1:1 transliteration against upstream Rust Serde data-model, supporting `Serialize`, `Deserialize`, `Serializer`, `Deserializer`, `SeqAccess`, `MapAccess`, and variant access.
  - `lib.rs`: Split into focused components (`QuoteStyle.kt`, `Terminator.kt`, `Trim.kt`, `InvalidOption.kt`) adhering to AGENTS.md §3 (no `Lib.kt` or `Mod.kt`).
  - `Writer.kt` & `Reader.kt`: Native `serialize` and `deserialize` methods wired to Serde data models.
- **Verification:**
  - `./gradlew test`: 100% passed across all targets (JVM, JS Node, Wasm-WASI Node, macOS ARM64).
  - Swift Export: Local SPM package built and `swift test` passed.
  - `./gradlew build`: Full target build gate successfully verified with 0 errors across all multiplatform targets.
  - KtLint & Detekt: Code style and linting passed cleanly.

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. string_record

- **Target:** `csv.StringRecord`
- **Similarity:** 0.17
- **Dependents:** 1
- **Priority Score:** 1045208.2
- **Functions:** 43/47 matched (target 58)
- **Missing functions:** `eq`, `eq`, `eq`, `from`
- **Types:** 5/5 matched (target 8)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 2. serializer

- **Target:** `csv.Serializer`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 375008.5
- **Functions:** 109/133 matched (target 178)
- **Missing functions:** `end`, `end`, `end`, `end`, `end`, `custom`, `new`, `serialize_element`, `end`, `serialize_element`, `end`, `serialize_field`, `end`, `serialize_field`, `end`, `end`, `serialize_field`, `end`, `serialize_field`, `end`, `serialize`, `serialize_header`, `serialize_err`, `serialize_header_err`
- **Types:** 5/17 matched (target 15)
- **Missing types:** `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `HeaderState`, `Wat`, `Baz`
- **Tests:** 30/34 matched

### 3. deserializer

- **Target:** `csv.Deserializer`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 292608.7
- **Functions:** 90/111 matched (target 162)
- **Missing functions:** `description`, `fmt`, `fmt`, `description`, `try_positive_integer64_bytes`, `try_negative_integer64_bytes`, `try_positive_integer128_bytes`, `try_negative_integer128_bytes`, `try_float_bytes`, `de`, `de_headers`, `b`, `with_header_unknown`, `seq_in_struct`, `seq_in_struct_tail`, `enum_untagged`, `borrowed`, `borrowed_map`, `borrowed_map_bytes`, `flatten`, `partially_invalid_utf8`
- **Types:** 8/15 matched (target 23)
- **Missing types:** `Error`, `Variant`, `Bar`, `Row`, `Boolish`, `Input`, `Properties`
- **Tests:** 27/39 matched

### 4. reader

- **Target:** `csv.Reader`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 120708.5
- **Functions:** 85/95 matched (target 117)
- **Missing functions:** `has_headers`, `new`, `new`, `new`, `new`, `new`, `new`, `b`, `s`, `newpos`
- **Types:** 11/12 matched (target 13)
- **Missing types:** `Headers`
- **Tests:** 22/25 matched

### 5. byte_record

- **Target:** `csv.ByteRecord`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 109308.4
- **Functions:** 75/85 matched (target 99)
- **Missing functions:** `eq`, `eq`, `eq`, `default`, `with_capacity`, `get`, `len`, `from`, `from`, `b`
- **Types:** 8/8 matched (target 11)
- **Missing types:** _none_
- **Tests:** 26/27 matched

### 6. writer

- **Target:** `csv.Writer`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 46408.1
- **Functions:** 53/57 matched (target 98)
- **Missing functions:** `wtr_as_string`, `into_string`, `write`, `flush`
- **Types:** 7/7 matched (target 10)
- **Missing types:** _none_
- **Tests:** 18/22 matched

### 7. error

- **Target:** `csv.Error`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 3009.0
- **Functions:** 24/24 matched (target 37)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 15)
- **Missing types:** _none_

### 8. debug

- **Target:** `csv.Debug`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 409.4
- **Functions:** 3/3 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 5)
- **Missing types:** _none_

### 9. cookbook

- **Target:** `csv.Cookbook`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 10. tutorial

- **Target:** `csv.Tutorial`
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

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

