# Java Shell

A lightweight command-line shell implemented in Java to explore the core mechanics of shell design, including command parsing, process execution, and I/O redirection.

## Features

* Command parsing and dispatch
* Built-in commands (e.g., `cd`)
* Execution of external programs using `ProcessBuilder`
* Output and error redirection (`>`, `2>`, `2>&1`)

## Purpose

This project is intended as a systems learning exercise to understand how shells manage commands, processes, and I/O streams. The focus is on building a clear internal architecture rather than full POSIX compatibility.

## Status

Work in progress. Future enhancements may include support for pipelines (`|`) and a stream-oriented execution model.
