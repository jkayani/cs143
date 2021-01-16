# P3: Semantic Analysis

## Thoughts

- It's difficult to write a type-checker for a programming language without understand it's semantics fully. I struggled with understanding
`SELF_TYPE` and what it actually meant and where it could be used 

- `AbstractSymbol`'s can represent both names of variables, and names of types. Making this distinction more clear in the type checker would've helped fix a few bugs

- The generated `cool-tree` class would've been more convenient if it generated getter methods. Perhaps if it also categorized `Expression`'s a bit more granularly, similar to how I added a `BinaryExpression` interface to all the expression variants with 2 operands

- The `Makefile` for this project was clearly intended for the next phase (code generation) so I had to spend time hacking on it to get it work for this project. Also ran into problems with the grading script since it expected the directory structure to be `/usr/class/cs143`, but that's mostly my fault

- The instructions said the analyzer must report _all_ semantic errors; however, the grading script flags a test case as failing if it reports more than one error for a semantically incorrect program

- This was most challenging, but most fun phase to work on so far

## Results
```
make: Leaving directory '/usr/class/p3/starter'
=====================================================================
submission: ..

=====================================================================
You got a score of 74 out of 74.

Submit code:
PA3-74:a88e7b09ee0cf795e75880f1c74677af

root@6c50efbfc2ac:/usr/class/p3/starter/grading#
```