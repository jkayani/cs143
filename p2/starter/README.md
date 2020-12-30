# P2: Parser

## Thoughts

- The Let construct was complicated and it was not clear (without looking at the test cases) that multiple bindings
was represented in the AST in a curried form. Not sure if this helps with variable scope analysis later on. It also wasn't obvious how much control the semantic action blocks of CUP provided

- `error` productions were tricky to get right

- "Empty" productions caused ambiguities and the starter came with one. Glad I was able to refactor the grammar to get rid of it

- Still not sure if I referred to `self` correctly when parsing implicit same-class method calls 

- Precedence wasn't too difficult to implement

- Is the parser supposed to do anything on `ERROR` tokens? I suppose not since there is little to do other than report it

## Results
```
root@3ef5d493e806:/usr/class/p2/starter# perl pa2-grader.pl
Grading .....
make: Entering directory '/usr/class/p2/starter'
make: 'parser' is up to date.
make: Leaving directory '/usr/class/p2/starter'
=====================================================================
submission: ..

=====================================================================
You got a score of 70 out of 70.

Submit code:
70:7cbbc409ec990f19c78c75bd1e06f215
```