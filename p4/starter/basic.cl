
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

(*
 class Animal {
   legs: Int <- 0;
   init(): Int {
     legs <- 2
   };
   legs(): Int {
     legs
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
   (*
    test(a: Animal): Int {
      case a of 
        d:Dog => d.legs();
        e:Animal => e.legs();
      esac
    };
   *)
   simple(a: Animal) : Int {
     a.legs()
   };
   main(): Int {
     let d:Dog <- new Dog, e:Int <- d.init() in simple(d)
   };
 };
*)

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
        getLegs(myDog <- ((new Dog).reflect()).reflect().reflect()) + 
        new Dog.legs() + 
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
      -- output: x + 1
    };
    flerm(x:Int): Int {
      let z:Int <- 1 in
        x <- x + foo(one <- two) + one + foo(zero <- 0) + z
      -- output: x + 3 + 2 + 1 => x + 7
    };
    main():Int { let x:Int in flerm(x) };
    -- output: 7
  };
*)