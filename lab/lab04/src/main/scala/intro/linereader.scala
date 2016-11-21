package intro

import scala.io.Source

// from `sbt` you can run this example, using the following command
//
//     run src/main/scala/intro/linereader.scala
//
// Then choose `intro.Reader` from the list.
//
// This file reimplements an example from Chapter 3 of the book:
//
//     Programming in Scala -- Second Edition. artima press.
//     Odersky, Spoon, Venners.
//
trait LineReader {

  // Off-topic (1): Approx. correspondence between Scala and Java definitions:
  //
  //   val foo: Int = 42 <---> final Integer foo = 42
  //   var foo: Int = 42 <---> Integer foo = 42
  //   def foo: Int = 42 <---> Integer foo() { return 42; }

  //   def doSomething() : Unit = ??? <---> void doSomething() { return ???; }
  //


  // Off-topic (2): `???` is just a method call, ??? is approx defined as:
  //
  //   def ???: Nothing = throw new MissingImplementationException();
  //
  // `???` can be used as a "hole" when developing programs top-down.
  // The program can still type-check, but execution will raise an exception.

  def printLines(lines: List[String]): Unit = {
    val max = widthOf(maxLength(lines))

    for (line <- lines) {
      println(showLine(line, max))
    }
    // desugares to ====>
    // lines.foreach((line) => println(showLine(line, max)))
  }

  // If the body of a method is a single expression, we don't need to wrap
  // it in `{ ... }`.
  // We also don't need to explicitly mention `return`.
  def showLine(line: String, maxWidth: Int): String =
    paddedLength(line, maxWidth) + " | " + line

  // Examples:
  //   pl("abcdef", 1) returns "6"
  //   pl("abcdef", 2) returns " 6"
  //   pl("abcdef", 3) returns "  6"
  //   pl("abc", 3)    returns "  3"
  def paddedLength(line: String, maxWidth: Int): String =
    " " * (maxWidth - widthOf(line.length)) + line.length

  def readLines(filename: String): List[String] =
    Source.fromFile(filename).getLines().toList

  def maxLength(lines: List[String]): Int =
    // On a `List[E]` the method `foldLeft` has approx. the signature:
    //
    //    def foldLeft[R](z: R)(c: (R, E) => R): R
    //
    // Here the second argument to `foldLeft` is a function combining the
    // current result with the next element.
    lines.foldLeft(0)(
      // `if` is an expression in Scala and returns a value.
      (currMax, l) => if (l.length > currMax) l.length else currMax
    )

  def widthOf(b: Int): Int = b.toString.length
}

// `App` is a trait from the Scala standard library that implements
// a `main` method for us.
object Reader extends LineReader with App {
  if (args.length > 0) {
    printLines(readLines(args(0)))
  } else {
    sys.error("Please provide filename")
  }
}
