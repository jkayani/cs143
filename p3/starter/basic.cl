class Animal {

	-- Math
		good1 : Bool <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		bad1 : Int <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		bad2 : Bool <- ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= ((1 + 1) * 1 < 10);

	-- Attribute dereference
		goodeq : Bool <- tRuE = good1;
		badeq : Bool <- tRuE = (5 + 5 + (5 - 5));
		badeq2 : Bool <- "tRuE" = good1;

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
