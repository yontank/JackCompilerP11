# Jack Assembler

This is an assembler for the Nand2Tetris course, capable of translating `.jack` files written in the Hack assembly language into `.vm` byte code files that run on the Hack platform.

## 📚 About

This assembler is part of the [Nand2Tetris](https://www.nand2tetris.org/) course. It implements the two-pass assembler specified in Project 6 of the course, which translates symbolic Hack assembly code into 16-bit binary machine code.

## 🚀 Features

- Fully supports the Hack assembly language as specified in the Nand2Tetris book.
- Implements:
  - Symbol resolution (labels and variables)
  - A-instructions (`@value`)
  - C-instructions (`dest=comp;jump`)
- Supports predefined symbols (`SP`, `LCL`, `ARG`, `THIS`, `THAT`, `R0` to `R15`, `SCREEN`, `KBD`).
