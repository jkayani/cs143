cLaSs Foo2Class INHERITS IO {
  foo : String <- "\n\"Test\""
  bar : Bool <- true;
  beep : Bool <- fAlsE; -- this is valid, apparently
  flerm : String;
  (* this is a class:
     class Foo2Class INHERITS IO {
       foo: String;
     }
  product : Int <- 3 * 3
  truth : Bool <- 2 <= 3
  -- <- this is another comment and 3 * 3 = 9
  baz : String <- "Hello, \nBeautiful World"
  digs : String <- "123"
  darrow : String <- "<-"
  aprogram : String <- "class Foo2Class INHERITS IO {\n foo: String;\n}"
  err : String <- "Hello\0" 
  err2 : String <- "Hello, 
  World"
  *)
}