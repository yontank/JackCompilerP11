# Jack Compiler (Nand2Tetris Project)

This project is a full implementation of a **Jack Compiler**, created as part of the Nand2Tetris course. It translates high-level Jack programs into Virtual Machine (VM) code that can be executed on the Hack platform.

## 🧠 What It Does

The compiler reads `.jack` source code and outputs `.vm` files. It performs the following main tasks:

1. **Tokenization**  
   Breaks the input source code into a sequence of tokens (keywords, symbols, identifiers, etc.).

2. **Parsing / Compilation**  
   Analyzes the structure of the code based on Jack's grammar and builds a syntax tree representing the program.

3. **Symbol Table Management**  
   Tracks variables, arguments, class fields, and subroutines across different scopes to ensure correct references and memory usage.

4. **VM Code Generation**  
   Translates the parsed syntax into VM commands that conform to the Nand2Tetris virtual machine specification.

## 🚀 How to Use

Compile the Java project using a standard Java compiler or IDE, then run the compiler on a `.jack` file or a directory containing multiple `.jack` files. For each input file, a corresponding `.vm` file will be generated in the same folder.

## 📚 Background
This project is based on the Jack language and the Hack platform described in the book  
**"Nand2Tetris"** by Nisan and Schocken. It corresponds to the final projects of the course.

## 🛠 Requirements

- Java Development Kit (JDK) 8 or higher


## ✅ Status

- Lexical analysis: ✅  
- Syntax parsing and compilation: ✅  
- Symbol table for scope management: ✅  
- VM code generation: ✅  
- Support for class declarations, methods, functions, variables, control flow, expressions, and subroutine calls

## 📖 Learn More
- [nand2tetris.org](https://www.nand2tetris.org/)
- Chapters 9–11 of *The Elements of Computing Systems*

---

**Built for learning. Compiles for fun.**
