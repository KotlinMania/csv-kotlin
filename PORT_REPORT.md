=== Deep Analysis: tmp/csv/src (rust) -> src/commonMain/kotlin (kotlin) ===

Scanning source codebase (rust)...
Codebase: tmp/csv/src (rust)
  Files: 11
  Total imports: 47
  Most depended: string_record (1 dependents)

Scanning target codebase (kotlin)...
Codebase: src/commonMain/kotlin (kotlin)
  Files: 20
  Total imports: 47

Comparing codebases...
Computing AST similarities...

=== Codebase Comparison Report ===

Source: tmp/csv/src (11 files)
Target: src/commonMain/kotlin (20 files)
Scoring invariant: FnSim is required function body/parameter similarity. Class/type and symbol parity are reported beside it; whole-file shape is diagnostic only.

Matched:   11 files
Unmatched: 0 source, 2 target

=== Matched Files (by porting priority) ===

Source                        Target                        FnSim     Dependents FunctionParityTypeParity  Priority
 --------------------------------------------------------------------------------------------------------------------------
deserializer                  csv.Deserializer              0.01      0          3/93          1/15        1050809.9 
string_record                 csv.StringRecord              0.55      1          42/42         2/5         1034704.5 
serializer                    csv.Serializer                0.01      0          3/76          0/17        909309.9  
writer                        csv.Writer                    0.51      0          45/53         3/7         126004.9  
byte_record                   csv.ByteRecord                0.51      0          69/73         3/8         98104.9   
reader                        csv.Reader                    0.58      0          61/66         8/12        97804.2   
error                         csv.Error                     0.43      0          14/16         6/6         22205.7   
lib                           csv.Lib                       0.34      0          4/4           3/3         706.6     
debug                         csv.Debug                     0.45      0          3/3           1/1         405.5     
cookbook                      csv.Cookbook [ZERO]           0.00      0          0/0           0/0         10.0      
tutorial                      csv.Tutorial [ZERO]           0.00      0          0/0           0/0         10.0      

=== Function and Symbol Details ===

deserializer -> csv.Deserializer
  similarity: 0.01, priority: 1050809.9, dependents: 0
  functions: 3/93 matched (target total: 26, required body score: 0.01)
  missing functions: has_headers, next_header, next_header_bytes, next_field, next_field_bytes, peek_field, error, infer_deserialize, deserialize_any, deserialize_bool, deserialize_f32, deserialize_f64, deserialize_char, deserialize_str, deserialize_string, deserialize_bytes, deserialize_byte_buf, deserialize_option, deserialize_unit, deserialize_unit_struct, deserialize_newtype_struct, deserialize_seq, deserialize_tuple, deserialize_tuple_struct, deserialize_map, deserialize_struct, deserialize_identifier, deserialize_ignored_any, variant_seed, unit_variant, newtype_variant_seed, tuple_variant, struct_variant, next_element_seed, next_key_seed, next_value_seed, custom, description, fmt, field, kind, try_positive_integer128, try_negative_integer128, try_positive_integer64, try_negative_integer64, try_float, try_positive_integer64_bytes, try_negative_integer64_bytes, try_positive_integer128_bytes, try_negative_integer128_bytes, try_float_bytes, de, de_headers, b, with_header, with_header_unknown, with_header_missing, with_header_missing_ok, with_header_no_fields, with_header_empty, with_header_empty_ok, without_header, no_fields, one_field, one_field_128, two_fields, two_fields_too_many, two_fields_too_few, one_char, no_chars, too_many_chars, simple_seq, simple_hex_seq, mixed_hex_seq, bad_hex_seq, seq_in_struct, seq_in_struct_tail, map_headers, map_no_headers, bytes, adjacent_fixed_arrays, enum_label_simple_tagged, enum_untagged, option_empty_field, option_invalid_field, borrowed, borrowed_map, borrowed_map_bytes, flatten, partially_invalid_utf8
  types: 1/15 matched (target total: 7)
  missing types: DeRecord, DeRecordWrap, DeStringRecord, DeByteRecord, Error, Variant, DeserializeError, DeserializeErrorKind, Foo, Bar, Label, Boolish, Input, Properties
  tests: 0/39 matched

string_record -> csv.StringRecord
  similarity: 0.55, priority: 1034704.5, dependents: 1
  functions: 42/42 matched (target total: 59, required body score: 0.55)
  missing functions: none
  types: 2/5 matched (target total: 3)
  missing types: Output, IntoIter, Item
  tests: 9/9 matched

serializer -> csv.Serializer
  similarity: 0.01, priority: 909309.9, dependents: 0
  functions: 3/76 matched (target total: 24, required body score: 0.01)
  missing functions: serialize_bool, serialize_i8, serialize_i16, serialize_i32, serialize_i64, serialize_i128, serialize_u8, serialize_u16, serialize_u32, serialize_u64, serialize_u128, serialize_f32, serialize_f64, serialize_char, serialize_str, serialize_bytes, serialize_none, serialize_some, serialize_unit, serialize_unit_struct, serialize_unit_variant, serialize_newtype_struct, serialize_newtype_variant, serialize_seq, serialize_tuple_struct, serialize_tuple_variant, serialize_map, serialize_struct, serialize_struct_variant, serialize_element, end, serialize_field, serialize_key, serialize_value, custom, error_scalar_outside_struct, error_container_inside_struct, new, wrote_header, handle_scalar, handle_container, serialize_err, serialize_header_err, bool, integer, integer_u128, integer_i128, float, float_nan, char, str, bytes, option, unit, struct_unit, struct_newtype, enum_units, enum_newtypes, seq, tuple, tuple_struct, tuple_variant, enum_struct_variant, struct_no_headers, struct_no_headers_128, struct_headers, struct_headers_nested, struct_headers_nested_seq, struct_headers_inside_tuple, struct_headers_inside_tuple_scalar_before, struct_headers_inside_tuple_scalar_after, struct_headers_inside_seq, struct_headers_inside_nested_tuple_seq
  types: 0/17 matched (target total: 7)
  missing types: SeRecord, Ok, Error, SerializeSeq, SerializeTuple, SerializeTupleStruct, SerializeTupleVariant, SerializeMap, SerializeStruct, SerializeStructVariant, HeaderState, SeHeader, Foo, Wat, Nested, Bar, Baz
  tests: 0/32 matched

writer -> csv.Writer
  similarity: 0.51, priority: 126004.9, dependents: 0
  functions: 45/53 matched (target total: 72, required body score: 0.51)
  missing functions: from_path, wtr_as_string, into_string, write, serialize_with_headers, serialize_no_headers, serialize_no_headers_128, serialize_tuple
  types: 3/7 matched (target total: 4)
  missing types: WriterState, Buffer, MarkWriteAndFlush, Row
  tests: 14/21 matched

byte_record -> csv.ByteRecord
  similarity: 0.51, priority: 98104.9, dependents: 0
  functions: 69/73 matched (target total: 91, required body score: 0.51)
  missing functions: byte, line, record, b
  types: 3/8 matched (target total: 4)
  missing types: ByteRecordInner, Bounds, Output, IntoIter, Item
  tests: 25/26 matched

reader -> csv.Reader
  similarity: 0.58, priority: 97804.2, dependents: 0
  functions: 61/66 matched (target total: 115, required body score: 0.58)
  missing functions: from_path, add_record, b, s, newpos
  types: 8/12 matched (target total: 9)
  missing types: ReaderState, ReaderEofState, Headers, Item
  tests: 20/23 matched

error -> csv.Error
  similarity: 0.43, priority: 22205.7, dependents: 0
  functions: 14/16 matched (target total: 25, required body score: 0.43)
  missing functions: field, valid_up_to
  types: 6/6 matched (target total: 13)
  missing types: none

lib -> csv.Lib
  similarity: 0.34, priority: 706.6, dependents: 0
  functions: 4/4 matched (target total: 7, required body score: 0.34)
  missing functions: none
  types: 3/3 matched (target total: 5)
  missing types: none

debug -> csv.Debug
  similarity: 0.45, priority: 405.5, dependents: 0
  functions: 3/3 matched (target total: 10, required body score: 0.45)
  missing functions: none
  types: 1/1 matched (target total: 4)
  missing types: none

cookbook -> csv.Cookbook [ZERO]
  similarity: 0.00, priority: 10.0, dependents: 0
  functions: 0/0 matched (target total: 2, required body score: 0.00)
  missing functions: none
  types: 0/0 matched (target total: 1)
  missing types: none
  *** CHEAT DETECTION / SCORING FAILURE ***
  function-by-function score forced to 0: no source functions found; target defines functions; report scoring is function-by-function only

tutorial -> csv.Tutorial [ZERO]
  similarity: 0.00, priority: 10.0, dependents: 0
  functions: 0/0 matched (target total: 1, required body score: 0.00)
  missing functions: none
  types: 0/0 matched (target total: 1)
  missing types: none
  *** CHEAT DETECTION / SCORING FAILURE ***
  function-by-function score forced to 0: no source functions found; target defines functions; report scoring is function-by-function only


=== Scores Forced To 0 ===

  - cookbook -> csv.Cookbook: no source functions found; target defines functions; report scoring is function-by-function only
  - tutorial -> csv.Tutorial: no source functions found; target defines functions; report scoring is function-by-function only

=== Porting Quality Summary ===

Matched by exact header:          11 / 11
Matched by provenance fallback:   0 / 11
Matched by name:                  0 / 11
Total TODOs in target: 0
Total lint errors:    2
Stub files:           0

=== Big Picture ===

- Missing files: 0
- Incomplete ports (similarity < 60%): 11
- Stub files: 0
- Files missing functions: 6 (total deficit: 182 functions)
- Type definitions missing: 47
- Files missing tests: 5 (total deficit: 82 unported `#[test]` functions)
- Documentation coverage: 493 / 6760 lines (7%)

Primary focus: port missing functions/tests to reach per-file parity (182 functions, 82 tests)

=== Files with Issues ===

File                          Similarity LineRatio  FunctionParityTests     TODOs Lint  Status
----------------------------------------------------------------------------------------------------
csv.Deserializer              0.01       0.00       3/93          0/39      0     0     LOW_SIM
  missing functions: `has_headers`, `next_header`, `next_header_bytes`, `next_field`, `next_field_bytes`, `peek_field`, `error`, `infer_deserialize`, `deserialize_any`, `deserialize_bool`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_identifier`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `custom`, `description`, `fmt`, `field`, `kind`, `try_positive_integer128`, `try_negative_integer128`, `try_positive_integer64`, `try_negative_integer64`, `try_float`, `try_positive_integer64_bytes`, `try_negative_integer64_bytes`, `try_positive_integer128_bytes`, `try_negative_integer128_bytes`, `try_float_bytes`, `de`, `de_headers`, `b`, `with_header`, `with_header_unknown`, `with_header_missing`, `with_header_missing_ok`, `with_header_no_fields`, `with_header_empty`, `with_header_empty_ok`, `without_header`, `no_fields`, `one_field`, `one_field_128`, `two_fields`, `two_fields_too_many`, `two_fields_too_few`, `one_char`, `no_chars`, `too_many_chars`, `simple_seq`, `simple_hex_seq`, `mixed_hex_seq`, `bad_hex_seq`, `seq_in_struct`, `seq_in_struct_tail`, `map_headers`, `map_no_headers`, `bytes`, `adjacent_fixed_arrays`, `enum_label_simple_tagged`, `enum_untagged`, `option_empty_field`, `option_invalid_field`, `borrowed`, `borrowed_map`, `borrowed_map_bytes`, `flatten`, `partially_invalid_utf8`
  missing types: `DeRecord`, `DeRecordWrap`, `DeStringRecord`, `DeByteRecord`, `Error`, `Variant`, `DeserializeError`, `DeserializeErrorKind`, `Foo`, `Bar`, `Label`, `Boolish`, `Input`, `Properties`
csv.StringRecord              0.55       0.00       42/42         9/9       0     0     MISSING_TYPES
  missing types: `Output`, `IntoIter`, `Item`
csv.Serializer                0.01       0.00       3/76          0/32      0     2     LOW_SIM
  missing functions: `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `serialize_bytes`, `serialize_none`, `serialize_some`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_seq`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `serialize_element`, `end`, `serialize_field`, `serialize_key`, `serialize_value`, `custom`, `error_scalar_outside_struct`, `error_container_inside_struct`, `new`, `wrote_header`, `handle_scalar`, `handle_container`, `serialize_err`, `serialize_header_err`, `bool`, `integer`, `integer_u128`, `integer_i128`, `float`, `float_nan`, `char`, `str`, `bytes`, `option`, `unit`, `struct_unit`, `struct_newtype`, `enum_units`, `enum_newtypes`, `seq`, `tuple`, `tuple_struct`, `tuple_variant`, `enum_struct_variant`, `struct_no_headers`, `struct_no_headers_128`, `struct_headers`, `struct_headers_nested`, `struct_headers_nested_seq`, `struct_headers_inside_tuple`, `struct_headers_inside_tuple_scalar_before`, `struct_headers_inside_tuple_scalar_after`, `struct_headers_inside_seq`, `struct_headers_inside_nested_tuple_seq`
  missing types: `SeRecord`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `HeaderState`, `SeHeader`, `Foo`, `Wat`, `Nested`, `Bar`, `Baz`
csv.Writer                    0.51       0.00       45/53         14/21     0     0     MISSING_FUNCS
  missing functions: `from_path`, `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
  missing types: `WriterState`, `Buffer`, `MarkWriteAndFlush`, `Row`
csv.ByteRecord                0.51       0.00       69/73         25/26     0     0     MISSING_FUNCS
  missing functions: `byte`, `line`, `record`, `b`
  missing types: `ByteRecordInner`, `Bounds`, `Output`, `IntoIter`, `Item`
csv.Reader                    0.58       0.00       61/66         20/23     0     0     MISSING_FUNCS
  missing functions: `from_path`, `add_record`, `b`, `s`, `newpos`
  missing types: `ReaderState`, `ReaderEofState`, `Headers`, `Item`
csv.Error                     0.43       0.00       14/16         -         0     0     MISSING_FUNCS
  missing functions: `field`, `valid_up_to`
csv.Lib                       0.34       0.00       4/4           -         0     0     LOW_SIM
csv.Debug                     0.45       0.00       3/3           -         0     0     
csv.Cookbook [ZERO]           0.00       0.00       -             -         0     0     LOW_SIM
csv.Tutorial [ZERO]           0.00       0.00       -             -         0     0     LOW_SIM

=== Porting Recommendations ===

Incomplete ports (similarity < 60%): 11
Missing files: 0

Incomplete ports to complete:
  deserializer                   similarity=0.01 function_parity=3/93 dependents=0
    missing functions: `has_headers`, `next_header`, `next_header_bytes`, `next_field`, `next_field_bytes`, `peek_field`, `error`, `infer_deserialize`, `deserialize_any`, `deserialize_bool`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_identifier`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `custom`, `description`, `fmt`, `field`, `kind`, `try_positive_integer128`, `try_negative_integer128`, `try_positive_integer64`, `try_negative_integer64`, `try_float`, `try_positive_integer64_bytes`, `try_negative_integer64_bytes`, `try_positive_integer128_bytes`, `try_negative_integer128_bytes`, `try_float_bytes`, `de`, `de_headers`, `b`, `with_header`, `with_header_unknown`, `with_header_missing`, `with_header_missing_ok`, `with_header_no_fields`, `with_header_empty`, `with_header_empty_ok`, `without_header`, `no_fields`, `one_field`, `one_field_128`, `two_fields`, `two_fields_too_many`, `two_fields_too_few`, `one_char`, `no_chars`, `too_many_chars`, `simple_seq`, `simple_hex_seq`, `mixed_hex_seq`, `bad_hex_seq`, `seq_in_struct`, `seq_in_struct_tail`, `map_headers`, `map_no_headers`, `bytes`, `adjacent_fixed_arrays`, `enum_label_simple_tagged`, `enum_untagged`, `option_empty_field`, `option_invalid_field`, `borrowed`, `borrowed_map`, `borrowed_map_bytes`, `flatten`, `partially_invalid_utf8`
    missing types: `DeRecord`, `DeRecordWrap`, `DeStringRecord`, `DeByteRecord`, `Error`, `Variant`, `DeserializeError`, `DeserializeErrorKind`, `Foo`, `Bar`, `Label`, `Boolish`, `Input`, `Properties`
  string_record                  similarity=0.55 function_parity=42/42 dependents=1
    missing types: `Output`, `IntoIter`, `Item`
  serializer                     similarity=0.01 function_parity=3/76 dependents=0
    missing functions: `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `serialize_bytes`, `serialize_none`, `serialize_some`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_seq`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `serialize_element`, `end`, `serialize_field`, `serialize_key`, `serialize_value`, `custom`, `error_scalar_outside_struct`, `error_container_inside_struct`, `new`, `wrote_header`, `handle_scalar`, `handle_container`, `serialize_err`, `serialize_header_err`, `bool`, `integer`, `integer_u128`, `integer_i128`, `float`, `float_nan`, `char`, `str`, `bytes`, `option`, `unit`, `struct_unit`, `struct_newtype`, `enum_units`, `enum_newtypes`, `seq`, `tuple`, `tuple_struct`, `tuple_variant`, `enum_struct_variant`, `struct_no_headers`, `struct_no_headers_128`, `struct_headers`, `struct_headers_nested`, `struct_headers_nested_seq`, `struct_headers_inside_tuple`, `struct_headers_inside_tuple_scalar_before`, `struct_headers_inside_tuple_scalar_after`, `struct_headers_inside_seq`, `struct_headers_inside_nested_tuple_seq`
    missing types: `SeRecord`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `HeaderState`, `SeHeader`, `Foo`, `Wat`, `Nested`, `Bar`, `Baz`
  writer                         similarity=0.51 function_parity=45/53 dependents=0
    missing functions: `from_path`, `wtr_as_string`, `into_string`, `write`, `serialize_with_headers`, `serialize_no_headers`, `serialize_no_headers_128`, `serialize_tuple`
    missing types: `WriterState`, `Buffer`, `MarkWriteAndFlush`, `Row`
  byte_record                    similarity=0.51 function_parity=69/73 dependents=0
    missing functions: `byte`, `line`, `record`, `b`
    missing types: `ByteRecordInner`, `Bounds`, `Output`, `IntoIter`, `Item`
  reader                         similarity=0.58 function_parity=61/66 dependents=0
    missing functions: `from_path`, `add_record`, `b`, `s`, `newpos`
    missing types: `ReaderState`, `ReaderEofState`, `Headers`, `Item`
  error                          similarity=0.43 function_parity=14/16 dependents=0
    missing functions: `field`, `valid_up_to`
  lib                            similarity=0.34 function_parity=4/4 dependents=0
  debug                          similarity=0.45 function_parity=3/3 dependents=0
  cookbook                       similarity=0.00 function_parity=- dependents=0
  tutorial                       similarity=0.00 function_parity=- dependents=0

=== Documentation Gaps ===

There is missing documentation that is hurting overall scoring.
Documentation coverage: 493 / 6760 lines (7%)
Files with >20% doc gap: 8

File                          Src Docs    Tgt Docs    Gap %     DocSim    DocAmt    DocEq     
----------------------------------------------------------------------------------------------
reader                        2950        33          98%       0.24      0.01      0.13      
writer                        1642        34          97%       0.24      0.02      0.13      
string_record                 852         115         86%       0.66      0.13      0.40      
byte_record                   762         158         79%       0.76      0.21      0.48      
error                         180         17          90%       0.77      0.09      0.43      
lib                           156         12          92%       0.16      0.08      0.12      
serializer                    118         19          83%       0.28      0.16      0.22      
deserializer                  76          24          68%       0.16      0.32      0.24      

=== Generating Reports ===

✅ Generated: port_status_report.md
✅ Generated: high_priority_ports.md
✅ Generated: NEXT_ACTIONS.md
✅ Generated: port_lint_proposed_changes.md

📁 All reports generated successfully!
