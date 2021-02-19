
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

-- blocks
(*
 class Animal {
   legs: Int <- 0;
   init(): Int {
     legs <- 2
   };
   legs(): Int {
     legs
   };
   wishes(): Int {
     100
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

 class Main inherits IO {
    test2(): Int { {
      let nullAnimal:Animal,
          stillNull2:Animal <- { { let a:Animal <- new Animal in a; }; },
          notNullAnimal:Animal <- new Animal in 
        case stillNull2 of 
          d:Animal => let e:Animal <- new Animal, f:Int <- e.init() in e.legs() + f;
          e:Main => e.wishes();
        esac;
    }
    };
    test(): SELF_TYPE {
      out_int(10)
    };
    wishes(): Int { 0 };
   main(): SELF_TYPE {
     out_int(let d:Dog <- new Dog, e:Int <- d.init() in test2())
   };
 };
*)

-- case statements
(*
 class Animal {
   legs: Int <- 0;
   init(): Int {
     legs <- 2
   };
   legs(): Int {
     legs
   };
   wishes(): Int {
     100
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

 class Main inherits IO {
    test2(): Int {
      let nullAnimal:Animal, notNullAnimal:Animal <- new Animal in 
        case notNullAnimal of 
          d:Dog => let e:Animal <- new Animal, f:Int <- e.init() in d.legs();
          e:Main => e.wishes();
        esac
    };
    test(a: Animal): Int {
      case a of 
        d:Dog => let e:Animal <- new Animal, f:Int <- e.init() in d.legs();
        e:Main => e.wishes();
      esac
    };
    wishes(): Int { 0 };
   main(): SELF_TYPE {
     out_int(let d:Dog <- new Dog, e:Int <- d.init() in test2())
   };
 };
*)

-- Local variables, dispatch
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
   bad(): Int {
     let a:Animal in a.legs()
   };
   simple(a: Animal) : Int {
     a.legs()
   };
   main(): Int {
     -- let d:Dog <- new Dog, e:Int <- d.init() in simple(d)
     bad()
   };
 };
*)
-- Inheritance and complicated dispatch
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

-- Attributes, simple dispatch, arithmetic
  class Main inherits IO {
    -- prints the sequence: 0123...
    zero: Int <- 0 + 0 + 0;
    one: Int <- zero + 1 + 0;
    two: Int <- (one <- 1) + 1;
    three: Int <- (two <- two) + 1;
    fourIsZero: Int; 
    foo(x:Int): Int {
      x <- (x <- x) + 1
      -- output: x + 1
    };
    flerm(x:Int): Int {
      let z:Int <- 1  in
        x <- x + foo(one <- two) + one + foo(zero <- 0) + z + fourIsZero
      -- output: x + 3 + 2 + 1 + 0 => x + 7
    };
    main():SELF_TYPE { let x:Int in out_int(flerm(x)) };
    -- output: 7
  };