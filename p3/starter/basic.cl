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
};

class Dingo inherits Dog {};
class Dog inherits Animal {};
class Cat inherits Animal {};
class Human {};

(*
	class B inherits D {
		x : Int <- (new A).foo(1, 2);
		bar() : Int {
			x
		};
	};
*)
