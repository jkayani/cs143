
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

 class Main {
   foo:Int <- let a:Int <- 1, b:Int <- 2 in a + b;
   getFoo(): Int { let x:Int <- 0 in x + foo };
   bar: Int <- self@Main.getFoo() + foo;
   main():Int {
     let x: Int <- 50, y: Int <- 60 in bar
   };
 };

(*
  class Animal {
    legs:Int <- legs <- legs <- legs <- legs <- 2;
    reflect(): SELF_TYPE {
      self
    };
    legs():Int {
      legs
    };
  };

  class Dog inherits Animal {
    legs():Int {
      4
    };
  };

  class Main {
    myAnimal: Animal <- new Animal;
    myDog: Dog <- new Dog;
    getLegs(a: Animal):Int {
      a.reflect().legs()
    };
    main(): Int {
      getLegs(myDog <- ((new Dog).reflect())@Animal.reflect().reflect()) + 
      new Dog@Animal.legs() + 
      getLegs(myAnimal.reflect())
    };
  };
*)

(*
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
*)