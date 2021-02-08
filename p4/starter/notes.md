Decided Conventions:

- $a0, $a1, and $a2 must be preserved during calls

- Caller sets up call frame, cleans it up after the call

- 0 argument functions must have an empty frame setup before call

- Activation record:
  (from bottom of stack)
  - $a0
  - $ra
  - $fp
  - // arguments

- $fp points to first argument of method. Arguments aren't popped off the stack, they 
are referenced relative to $fp


Next steps:
- assign
- new
- let 
- static_dispatch
- case