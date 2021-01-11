class Animal {

	-- Math
		good1 : Bool <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		good2 : Int <- ~1;
		bad1 : Int <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		bad2 : Bool <- ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= ((1 + 1) * 1 < 10);

	-- Attribute dereference
		goodeq : Bool <- tRuE = good1;
		badeq : Bool <- tRuE = (5 + 5 + (5 - 5));
		badeq2 : Bool <- "tRuE" = good1;
		badvar : Bool <- not hingadingadurgen;

	-- LUB checks
		dingo : Dingo;
		dog : Dog;
		cat : Cat;
		bear: Bear;
		human : Human;
		nolub : Object <- if true then human else dog fi;
		anmiallub : Object <- if true then cat else dog fi;
		doglub : Object <- if true then dingo else dog fi;
		samelub : Object <- if true then dingo else dingo fi;
		badnotancestor : Animal <- human;
		baddescendent : Dingo <- dog;

  -- Var assignments
		good : Int <- good2 <- good2 * ~1;
		bad1 : Bool <- good2 <- not true;
		bad2 : Bool <- temp <- who * ~1;

	-- Blocks
		good : Int <- { good2 <- good2 * ~1; good1 <- fALSE; good2 * 10; };
		bad : Int <- { a <- 1; a + b; };
		bad2 : Int <- { good1 <- truE; };

	-- Let
		goodlet1 : Int <- let x:Int <- 1, y:Int <- 2 in x + y;
		goodlet2 : Int <- let x:Int <- 1 in let y:Int <- 2 in x + y;
		goodlet3 : Int <- let x:Int, y:Int <- 2 in x + y;
		goodlet4 : Int <- let x:Int in let y:Int <- 2 in x + y;
		goodlet5 : Int <- let x:Int <- good in let y:Int <- good in x + y;
		badlet1 : Int <- let x:Bool <- 1, y:Int <- 2 in x + y;
		badlet2 : Bool <- let x:Bool <- 1, y:Bool <- 2 in x + y;
		badlet3 : Int <- let x:Int <- 1, y:Int <- 2 in x + y + z;
		badlet4 : Int <- x + y;

	-- Loop
		goodloop1 : Object <- while 1 < 0 loop good pool;
		goodloop2 : Object <- while true loop { goodloop1; goodloop2; } pool;
		badloop1 : Int <- while 1 < 0 loop good pool;
		badloop2 : Object <- while 1 loop good pool;
		badloop3 : Object <- while false loop { x; } pool;

  -- Cases 
		goodcase1 : Animal <- case dingo of id1:Dingo => dingo; esac;
		goodcase12 : Dog <- 
			case dingo of 
				id1:Dingo => id1;  
				id1:Dog => id1;  
			esac;
		goodcase2 : Object <- 
			case dingo of 
				id1:Dog => id1;
				id2:Human => id2;
				id3:Cat => id3;
			esac;
		goodcase3 : Animal <- 
			case dingo of 
				id1:Dog => id1;
				id2:Bear => id2;
				id3:Cat => id3;
			esac;
		badcase1 : Human <- case dingo of id1:Dingo => id1; esac;
		badcase3 : Human <- case dingo of id1:Dingo => id1 + 2; esac;
		badcase2 : Animal <- 
			case dingo of 
				id2:Dingo => dog;
				id3:Dingo => dog;
			esac;
};

class Dingo inherits Dog {};
class Dog inherits Animal {};
class Cat inherits Animal {};
class Bear inherits Animal {};
class Human {};

(*
	class B inherits D {
		x : Int <- (new A).foo(1, 2);
		bar() : Int {
			x
		};
	};
*)
