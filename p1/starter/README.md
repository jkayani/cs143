# P1: Lexer

## Thoughts

- `Makefile` assumed the `libs` folder with the CUP and JLEX jars was always 2 folders "up" relatively even though it's all designed to let you compile/grade the assignment from anywhere

- Mapping of `TokenConstants` to actual symbols was confusing at times. E.g, still not sure what `TokenConstants.OF` was supposed to be

- Glad I switched to using Lex to manage the states rather than doing it myself in code (easier to iterate on rules)

- Language spec was confusing at times. Still not clear about why every conceivable ASCII char is allowed in COOL strings. 

- Difference between null characters in strings and escaped null characters in strings, and the need to catch both, wasn't made clear in the instructions

- "Comments can be nested" doesn't make it obvious that "Comments can be nested and the nesting must be balanced"

- `"\v"` not being allowed in Java strings was annoying

## Results
```
root@1dc7d2e4e4a9:/usr/class/p1/starter# perl pa1-grader.pl
Grading .....
make: Entering directory '/usr/class/p1/starter'
make: 'lexer' is up to date.
make: Leaving directory '/usr/class/p1/starter'
=====================================================================
submission: ..

=====================================================================
You got a score of 63 out of 63.

Submit code:
63:03afdbd66e7929b125f8597834fa83a4

root@1dc7d2e4e4a9:/usr/class/p1/starter#
```