
(*  Example cool program testing as many aspects of the code generator
    as possible.
 *)

(*
  class Main inherits IO {
    --io:IO <- new IO;
    x:Int <- new Int;
    main():Object {
        out_int((new Int) + 1)
    };
  };
*)

(*
-- SELF_TYPE (reflection) tests
  class Animal inherits IO {
    legs:Int <- 2;
    name():String { "Animal" };
    inner: SELF_TYPE;
    init(): Animal {
      inner <- new SELF_TYPE
    };
    test(): Animal {
      new SELF_TYPE
    };
    getInner():Animal {
      inner
    };
    getLegs():Int {
      legs
    };
  };
  class Dog inherits Animal {
    name():String { "Dog" };
    init(): Animal {
      {
        legs <- 4;
        self@Animal.init();
      }
    };
  };
  class Main inherits IO {
      newline(): Object {
        out_string("\n")
      };
    main():Object {
      let a:Animal <- new Dog in {
        out_string("dog has legs: ");
        out_int(a.getLegs());
        newline();

        if isvoid a.getInner() then out_string("correct - inner dog of uninit'd dog isvoid") else out_string("WRONG") fi;
        newline();

        a.init();

        out_string("init'd dog has legs: ");
        out_int(a.getLegs());
        newline();

        out_string("init'd dog's inner dog has legs: ");
        out_int(a.getInner().getLegs());
        newline();

        a.getInner().init();

        out_string("init'd dog's init'd inner dog has legs: ");
        out_int(a.getInner().getLegs());
        newline();

        out_string("Animal is a: ".concat(a.getInner().name()));
        newline();

        let x:Animal <- (new Animal), y:Animal in {
          out_string("brand new animal has legs: ");
          out_int(x.getLegs());
          newline();

          y <- x.test();

          out_string("brand new animal via SELF_TYPE has legs: ");
          out_int(y.getLegs());
          newline();
        };
      }
    };
  };
*)

-- builtin methods 
(*
  class Animal inherits IO {
    legs():Int { 2 };
  };
  class Dog inherits Animal {
    legs():Int { 4 };
  };
  class Main inherits IO {
    newline(): Object {
      out_string("\n")
    };
    main(): Object {{
      out_string("Welcome to the builtin test! What's your name?");
      newline();
      let name:String <- in_string() in {
        out_string(name.concat(", hello! Welcome to the main method of ".concat(type_name()).concat("enter 0: ")));
        newline();
        out_string("Or, put another way, hello: ".concat(name.substr(in_int(), name.length())).concat(", glad you're here"));
        newline();
        let d:Animal <- new Dog in {
          out_int(d.legs());
          newline();
          out_int(d@Animal.legs());
        };
        abort();
      };
    }};
  };
*)

-- fizzbuzz: dispatch, all arithmetic, loops, conds
(*
  class Main inherits IO {
      times(res:Int, n:Int, m:Int, c:Int): Int {
        if c = m then res else times(res + n, n, m, c + 1) fi
      };
      div(n: Int, m:Int, res:Int): Int {
        if n < m
        then res 
        else 
          if n = 0 then res else div(n + ~m, m, res + 1) fi
        fi
      };
      mod(n: Int, m: Int): Int {
        n - times(0, m, div(n, m, 0), 0)
      };
      mod2(n: Int, m: Int): Int {
        n - m * (n / m)
      };
      fizzBuzz(n: Int, k: Int): Object {
        while n < k loop {
          if mod(n, 15) = 0 then out_string("fizzbuzz\n")
          else 
            if mod(n, 5) = 0 then out_string("buzz\n")
            else
              if mod(n, 3) = 0 then out_string("fizz\n")
              else
                {
                  out_int(n);
                  out_string("\n");
                }
              fi
            fi
          fi;
          n <- n + 1;
        }
        pool
      };
      fizzBuzz2(n: Int, k: Int): Object {
        while n < k loop {
          if mod2(n, 15) = 0 then out_string("fizzbuzz\n")
          else 
            if mod2(n, 5) = 0 then out_string("buzz\n")
            else
              if mod2(n, 3) = 0 then out_string("fizz\n")
              else
                {
                  out_int(n);
                  out_string("\n");
                }
              fi
            fi
          fi;
          n <- n + 1;
        }
        pool
      };
    main(): SELF_TYPE { {
      fizzBuzz(1, 20);
      fizzBuzz2(1, 20);
      self;
    } };
  };
*)
-- bool, comp, cond, eq, loop
(*
  class Animal {};
  class Main inherits IO {
    isMain: Bool <- true;
    anotherBool: Bool <- new Bool;
  (*
    numTest():Int {
      let x:Int <- 1, y:Int <- 2 in x
    };

    test: Int;
    testAnimal:Animal;
    testAnimal2:Animal;
    -- output: 01 01 01 0
      eqTest(): SELF_TYPE {{
        test <- 1 + 1;
        out_int(if "false" = "false" then 0 else 1 fi);
        out_int(if 2 = 0 then 0 else 1 fi);

        out_int(if 2 = test then 0 else 1 fi);
        out_int(if new Animal = new Animal then 0 else 1 fi);

        out_int(if testAnimal = testAnimal2 then 0 else 1 fi);
        testAnimal <- new Animal;
        out_int(if testAnimal = testAnimal2 then 0 else 1 fi);

        testAnimal2 <- testAnimal;
        out_int(if testAnimal = testAnimal2 then 0 else 1 fi);
      }};
      condTest(): Bool {
        let x:Bool, y:Bool in {
          y <- not {
            if not x then 
              out_string("true branch") 
            else 
              out_string("false branch")
            fi;
            x;
          };
          if y then 
            if not x then 
              out_string("consistent")
            else 
              out_string("INconsistent")
            fi
          else 
            out_string("false???")
          fi;
          y;
        } 
      };
  *)
      loopTest(): Int {
        let x:Object <-
          let start:Int <- 0, end:Int <- 10, cond:Bool <- start < end in 
            while cond loop {
              out_int(start);
              start <- start + 1;
              cond <- start < end;
              start;
            }
            pool
        in case x of y:Int => y; esac
      };

    main(): Int { {
      -- eqTest();
      out_int(loopTest());
      let a:Animal in {
        if isvoid a then out_string("a is null\n") else out_string("wrong") fi;
        if isvoid isMain then out_string("wrong") else out_string("isMain is non-null\n") fi;
      };
      0;
    }};
  };
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
    goodCase(): Int {
      let nullAnimal:Animal, notNullAnimal:Animal <- new Dog in 
        case notNullAnimal of 
          d:Dog => let e:Animal <- new Animal, f:Int <- e.init() in { d.init(); d.legs(); };
          e:Main => e.wishes();
        esac
    };
    badNoMatching(a: Animal): Int {
      case a of 
        d:Dog => let e:Animal <- new Animal, f:Int <- e.init() in d.legs();
        e:Main => e.wishes();
      esac
    };
    badNull(a: Animal): Int {
      case a of 
        e:Animal => e.wishes();
        e:Main => e.wishes();
      esac
    };
    wishes(): Int { 50 };
   main(): Object { {
     out_int(let d:Dog <- new Dog, e:Int <- d.init() in goodCase());
     --out_int(badNoMatching(let a:Animal <- new Animal in a));
     -- out_int(badNull(let a:Animal in a));
   } };
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

(*
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
*)