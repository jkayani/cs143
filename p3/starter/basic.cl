class A {
	trick : A <- new B;
	trickier : Int <- trick.foo();
};

class B inherits A {
	foo() : Int { 0 };
};