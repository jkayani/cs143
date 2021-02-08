
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)


class Main {
  -- prints the sequence: 0123...
  zero: Int <- 0 + 0 + 0;
  one: Int <- zero + 1 + 0;
  two: Int <- (one <- 1) + 1;
  three: Int <- (two <- two) + 1;
  foo(x:Int): Int {
    x <- (x <- x) + 1
  };
  flerm(x:Int): Int {
    x <- x + foo(one <- two) + one + foo(zero <- 0)
  };
  main():Int { flerm(22) };
};