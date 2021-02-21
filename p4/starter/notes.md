Decided Conventions:

- $a0, $a1, and $a2 must be preserved during calls

- Caller sets up call frame, cleans it up after the call

- 0 argument functions must have an empty frame setup before call

- Activation record:
  (from bottom of stack)
  - $a0
  - $ra
  - $fp
  - // local variables (to be populated by callee as needed)
  - // arguments

- $fp points to first argument of method. Arguments aren't popped off the stack they are referenced relative to $fp

- $s1 points to the next free slot for local variables, $s3 is the where the local variables start, and $s2 is where they end. $s3 and $s2 are inclusive.

- Method tables are organized so that methods are in order of inherited class, and order within that class. This allows for dynamic dispatch via the classtag, method table table, and a recorded method offset


Next steps:
- ~assign~
- ~new~
- static_dispatch (will do once `dispatch` is finalized)
- ~let~
- ~case~
- ~block~
- ~no_expr~

Needs boolean:
- ~bool_const~
- ~comp~
- ~cond~
- loop
- lt, leq, eq 
- isvoid

Finish math:
- sub, mul, divide, neg