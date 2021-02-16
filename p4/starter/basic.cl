
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

 class Animal {
   legs: Int;
   init(): Int {
     legs <- 2
   };
 };

 class Dog inherits Animal {
   name: String;
   name(): String {
     name
   };
   init(): Int {
     legs <- 4
   };
 };

 class Main {
   test(a: Animal): Int {
      5
   };
   test2(a: Dog): String {
     a.name()
   };
   main(): String {
     test2(new Dog)
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