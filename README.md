# forkjoin-sudoku

## Introduction

This is a software I coded for my "multi-core programming" exam.
It finds all the possible solutions of a given Sudoku and it's built to compare execution time between single and multi-core systems. It follows Divide Et Impera paradigm implemented using Java ForkJoin library.

## How to run it

```cd forkjoin-sudoku/src/```

```javac Main.java```

```java Main "../tests/...."``` --> the input is a simple *.txt* file, you can find different tests in "*tests*" directory

For example:
```java Main "../tests/test1/test1_c.txt"```

Enjoy :smile:
