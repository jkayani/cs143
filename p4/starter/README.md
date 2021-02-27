
## Results
root@1dd694cfd504:/usr/class/cs143/p4/starter/grading# ./143publicgrading PA5
Grading .....
make: Entering directory '/usr/class/cs143/p4/starter'
make: Nothing to be done for 'source'.
make: Leaving directory '/usr/class/cs143/p4/starter'
make: Entering directory '/usr/class/cs143/p4/starter'
make: 'cgen' is up to date.
make: Leaving directory '/usr/class/cs143/p4/starter'
=====================================================================
submission: ..

        -1 (basic-init)  Default initial values of baisc classes
java.util.NoSuchElementException
        at java.util.LinkedList$ListItr.next(LinkedList.java:890)
        at new_.code(cool-tree.java:2289)
        at let.code(cool-tree.java:1335)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at let.code(cool-tree.java:1343)
        at CoolGen.writeMethods(CoolGen.java:614)
        at CoolGen.layoutCode(CoolGen.java:646)
        at programc.cgen(cool-tree.java:304)
        at Cgen.main(Cgen.java:59)
        -1 (bool)        Boolean objects
java.util.NoSuchElementException
        at java.util.LinkedList$ListItr.next(LinkedList.java:890)
        at new_.code(cool-tree.java:2289)
        at CoolGen.initAttributes(CoolGen.java:564)
        at CoolGen.layoutCode(CoolGen.java:644)
        at programc.cgen(cool-tree.java:304)
        at Cgen.main(Cgen.java:59)
        -1 (calls)       Method calls
        -1 (init-order-super)    Evaluation order of superclass vs subclass attribute initializers
java.util.NoSuchElementException
        at java.util.LinkedList$ListItr.next(LinkedList.java:890)
        at new_.code(cool-tree.java:2289)
        at let.code(cool-tree.java:1335)
        at block.code(cool-tree.java:1238)
        at loop.code(cool-tree.java:1059)
        at block.code(cool-tree.java:1238)
        at CoolGen.writeMethods(CoolGen.java:614)
        at CoolGen.layoutCode(CoolGen.java:646)
        at programc.cgen(cool-tree.java:304)
        at Cgen.main(Cgen.java:59)
        -1 (many_objects_on_heap)        Allocating many objects on the heap
        -1 (new-self-dispatch)   Dispatch on a "new"d object
        -1 (new-self-init)       Checking evaluation of attribute initialization exprs on a "new"d object
java.util.NoSuchElementException
        at java.util.LinkedList$ListItr.next(LinkedList.java:890)
        at new_.code(cool-tree.java:2289)
        at dispatch.code(cool-tree.java:846)
        at block.code(cool-tree.java:1238)
        at CoolGen.writeMethods(CoolGen.java:614)
        at CoolGen.layoutCode(CoolGen.java:646)
        at programc.cgen(cool-tree.java:304)
        at Cgen.main(Cgen.java:59)
        -1 (new-st)      New SELF_TYPE behavior
java.util.NoSuchElementException
        at java.util.LinkedList$ListItr.next(LinkedList.java:890)
        at new_.code(cool-tree.java:2289)
        at CoolGen.initAttributes(CoolGen.java:564)
        at CoolGen.layoutCode(CoolGen.java:644)
        at programc.cgen(cool-tree.java:304)
        at Cgen.main(Cgen.java:59)
        -1 (newbasic)    Use of new with basic classes
        -1 (objectequality)      Object equality tests
        -1 (primes)      prime number program from examples directory
        -1 (scoping)     Scoping test
        -1 (selftypeattribute)   Attribute of type SELF_TYPE
[GC Test Case: lam-gc.cl]
        -5 (lam-gc)      Lambda example with garbage collection
[GC Test Case: simple-gc.cl]
=====================================================================
You got a score of 45 out of 63.

Submit code:
PA4-45:348748e1cb0d5229da7224fcea6e0c27
