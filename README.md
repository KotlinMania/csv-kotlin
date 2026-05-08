# csv-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fcsv--kotlin-blue.svg)](https://github.com/KotlinMania/csv-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/csv-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/csv-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/csv-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/csv-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`BurntSushi/rust-csv`](https://github.com/BurntSushi/rust-csv).

**Original Project:** This port is based on [`BurntSushi/rust-csv`](https://github.com/BurntSushi/rust-csv). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `BurntSushi/rust-csv`

> The text below is reproduced and lightly edited from [`https://github.com/BurntSushi/rust-csv`](https://github.com/BurntSushi/rust-csv). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## csv

A fast and flexible CSV reader and writer for Rust, with support for Serde.

[![Build status](https://github.com/BurntSushi/rust-csv/workflows/ci/badge.svg)](https://github.com/BurntSushi/rust-csv/actions)
[![crates.io](https://img.shields.io/crates/v/csv.svg)](https://crates.io/crates/csv)

Dual-licensed under MIT or the [UNLICENSE](http://unlicense.org).


### Documentation

https://docs.rs/csv

If you're new to Rust, the
[tutorial](https://docs.rs/csv/1.*/csv/tutorial/index.html)
is a good place to start.


### Usage

To bring this crate into your repository, either add `csv` to your
`Cargo.toml`, or run `cargo add csv`.


### Example

This example shows how to read CSV data from stdin and print each record to
stdout.

There are more examples in the
[cookbook](https://docs.rs/csv/1.*/csv/cookbook/index.html).

```rust
use std::{error::Error, io, process};

fn example() -> Result<(), Box<dyn Error>> {
    // Build the CSV reader and iterate over each record.
    let mut rdr = csv::Reader::from_reader(io::stdin());
    for result in rdr.records() {
        // The iterator yields Result<StringRecord, Error>, so we check the
        // error here.
        let record = result?;
        println!("{:?}", record);
    }
    Ok(())
}

fn main() {
    if let Err(err) = example() {
        println!("error running example: {}", err);
        process::exit(1);
    }
}
```

The above example can be run like so:

```text
$ git clone git://github.com/BurntSushi/rust-csv
$ cd rust-csv
$ cargo run --example cookbook-read-basic < examples/data/smallpop.csv
```

### Example with Serde

This example shows how to read CSV data from stdin into your own custom struct.
By default, the member names of the struct are matched with the values in the
header record of your CSV data.

```rust
use std::{error::Error, io, process};

#[derive(Debug, serde::Deserialize)]
struct Record {
    city: String,
    region: String,
    country: String,
    population: Option<u64>,
}

fn example() -> Result<(), Box<dyn Error>> {
    let mut rdr = csv::Reader::from_reader(io::stdin());
    for result in rdr.deserialize() {
        // Notice that we need to provide a type hint for automatic
        // deserialization.
        let record: Record = result?;
        println!("{:?}", record);
    }
    Ok(())
}

fn main() {
    if let Err(err) = example() {
        println!("error running example: {}", err);
        process::exit(1);
    }
}
```

The above example can be run like so:

```
$ git clone git://github.com/BurntSushi/rust-csv
$ cd rust-csv
$ cargo run --example cookbook-read-serde < examples/data/smallpop.csv
```

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:csv-kotlin:0.1.0")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same Unlicense license as the upstream [`BurntSushi/rust-csv`](https://github.com/BurntSushi/rust-csv). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the rust-csv authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`BurntSushi/rust-csv`](https://github.com/BurntSushi/rust-csv) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
