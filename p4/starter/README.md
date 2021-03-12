# P4: Code Generation

## Thoughts 

- This took me 2 months to finish, wow. Unfortuantely, that means edX kicked me out of the course before I could finish the project and watch the remaining lectures (register allocation, garbage collection, and a look at Java)

- Several times I confused raw strings and ints with the `String` and `Int` COOL types. But I get it now

- I am glad I took the approach I did: implementing all the basic infrastructure (attributes, method parameters/arguments, local variables) with just `Int`'s before extending to other types and operations. Made for fairly smooth progress afterward

- Coming up with a stack machine calling convention wasn't too hard, except for when I had 
to accomodate local variables. The solution I have now is a hack where I just allocate enough space for `N` locals and hope that is enough. Ideally the code generator would recursively count the number of locals each method had the potential to create _in it's frame_ and allocate space accordingly. It took me a few days to figure out what a good approach for locals would be

- The reflective aspects of COOL (usage of `SELF_TYPE`) was a bit tricky to implement. More generally, realizing that the compile-time knowledge of the program being compiled wouldn't be sufficient for certain cases made things challening, since the equivalent MIPS assembly had to be written to do the logic at runtime

- The generated code is very inefficient. There are many instances of pushing a value from a register onto the stack as part of one AST node's code generation, and then immediately popping the value back into the same register as part of the next AST node's code. So there is lots of low-hanging fruit for optimization

- Adding garbage collector support at the end wasn't hard

- Still have no idea what `Int_init` and `String_init` routines were meant to do. Since my `Int_protObj` and `String_protObj` objects had the right default values for their attributes, there was nothing left to initialize, so it's just a no-op

- The starter wouldn't compile because I used a Mac with a case insensitive file-system that didn't recognize `formal.java` and `Formal.java` were 2 different pieces of code. So I had to rename the former to `formalc` like it had in the previous projects. That was a bit hard to understand and fix.

- Getting the grading scripts to run was a bit tricky because the test cases were created back in 2003 and included the copyright notice for SPIM at the time of execution, which I didn't have in my 2021-compiled SPIM binary. So I had to edit the test cases to strip that part out

- This was _tons_ of fun, best part of the course hands down.

## Results
```
root@98bc23ad7aac:/usr/class/cs143/p4/starter/grading# ./143publicgrading PA5
Grading .....
make: Entering directory '/usr/class/cs143/p4/starter'
make: *** No rule to make target 'example.cl', needed by 'source'.  Stop.
make: Leaving directory '/usr/class/cs143/p4/starter'
make: Entering directory '/usr/class/cs143/p4/starter'
make: 'cgen' is up to date.
make: Leaving directory '/usr/class/cs143/p4/starter'
=====================================================================
submission: ..

[GC Test Case: lam-gc.cl]
[GC Test Case: simple-gc.cl]
=====================================================================
You got a score of 63 out of 63.

Submit code:
PA4-63:009287d0591014af0f82863b117a77f7
```