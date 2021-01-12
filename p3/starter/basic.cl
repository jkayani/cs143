class Animal {

	-- Math
		good1 : Bool <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		good2 : Int <- ~1;
		good3 : Bool <- true;
		bad1 : Int <- not ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= (1 + 1) * 1;
		bad2 : Bool <- ~(1 + 1 - (1 - 1) * 1 / (1 + 1)) <= ((1 + 1) * 1 < 10);

	-- Equality and variable dereference
		goodeq : Bool <- tRuE = good1;
		goodinaustralia : Bool <- dog = dingo;
		badeq : Bool <- tRuE = (5 + 5 + (5 - 5));
		badeq2 : Bool <- "tRuE" = good1;
		badeq3 : Bool <- "tRuE" = 5;
		badvar : Bool <- not hingadingadurgen;
		self : Bool <- true;

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
		goodvar2 : SELF_TYPE <- dog;
		goodvar3 : SELF_TYPE <- self;
		bad1 : Bool <- good2 <- not true;
		bad2 : Bool <- temp <- who * ~1;
		bad3 : Bool <- self <- dog;

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
		goodlet6 : SELF_TYPE <- let x:SELF_TYPE <- self in x;
		badlet1 : Int <- let x:Bool <- 1, y:Int <- 2 in x + y;
		badlet2 : Bool <- let x:Bool <- 1, y:Bool <- 2 in x + y;
		badlet3 : Int <- let x:Int <- 1, y:Int <- 2 in x + y + z;
		badlet4 : Int <- x + y;
		badlet5 : Int <- let self:Int in self;

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
		badcase5 : Dingo <- case dingo of self:Dingo => self; esac;

	-- Method declarations
		good1() : Int { 1 };
		good2(good2 : Bool) : Bool  { good2 };
		good3() : Bool  { good3 };
		good4(a : Int, b : Int) : Int  { { true; fAlSe; a + b; } };
		good5(a : Int, b : Int) : Animal  { dog };
		good6() : SELF_TYPE  { self };
		good7() : SELF_TYPE  { dog };
		self(a : Int, b : Int) : Animal  { dog };
		bad1(a : Int, b : Int) : String  { { true; fAlSe; a + b; } };
		bad2() : Int  { a + b };
		bad3() : Bool  { good2 };
		bad4(self:Animal) : Animal  { self };

	-- Isvoid 
		temp: Int;
		goodisvoid1 : Bool <- isvoid temp;
		goodisvoid2 : Bool <- isvoid self;
		badisvoid1 : Int <- isvoid temp;

	-- New 
		goodnew1 : SELF_TYPE <- new Dog;
		goodnew2 : SELF_TYPE <- new Animal;
		badnew1 : Dingo <- new Dog;

	-- Method calls
		goodmethodcall1 : Int <- self.good1();
		goodmethodcall2 : Bool <- self.good2(true);
		goodmethodcall3 : Bool <- good2(true);
		goodmethodcall4 : Bool <- good2(self.good3());

		goodmethodcall45 : Int <- let d:Dog <- new Dog in d.getLegCount();
    goodmethodcall5 : Int <- let d:Dingo <- new Dingo in d@Dog.getLegCount();
	  goodmethodcall6 : Dog <- let d:Dingo <- new Dingo in d@Dog.setName("Comet");
		goodmethodcall7 : Dingo <- let d:Dingo <- new Dingo in d.setName("Dingus");
		goodmethodcall8 : SELF_TYPE <- let d:Dingo <- new Dingo in d.setName("Dingus");

		badcall1 : Int <- hingadingadurgen();
		badcall2 : Int <- self.hingadingadurgen();
		badcall3 : Int <- good1(1, 2, 3);
		badcall4 : Bool <- self.good1();
		badcall45 : Bool <- self.good2(thevariabletrue);
		badcall5 : Bool <- let d:Dog <- new Dog in d.isDingo();
		badcall6 : Bool <- let d:Dog <- new Dog in d@Dingo.isDingo();
		badcall7 : Dingo <- let d:Dingo <- new Dingo in d.setName();
};

class Dingo inherits Dog {
	setName(n: String) : SELF_TYPE { { name <- n.concat(" the Dingo!"); self; } };
	isDingo() : Bool { true };
};
class Dog inherits Animal {
	name : String;
	setName(n: String) : SELF_TYPE { { name <- n; self; } };
	getLegCount() : Int { 4 };
};
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
