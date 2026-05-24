# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/11 (9.1%)
- **Function parity:** 2/555 matched (target 9) — 0.4%
- **Class/type parity:** 1/71 matched (target 4) — 1.4%
- **Combined symbol parity:** 3/626 matched (target 13) — 0.5%
- **Average inline-code cosine:** 0.40 (function body across 1 matched files)
- **Average documentation cosine:** 0.64 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. debug

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

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

