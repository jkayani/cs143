class A {
	foo : Int;
--	bar(x:Bool) : Int { 0 };
--	foo : Int <- 3;
};

class B inherits A {
	bar(y:Int) : Int { new Int };
	bar() : Int { new Int };
};