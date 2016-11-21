package showable

import java.util.Comparator

// Here we implement the very first homework in Scala. By using implicit
// parameters we improve the boilerplate overhead needed to create instances of
// Showable[T] for T being some nested tuple.

// Sidenote:
//   If you are interested, also see:
//     https://ropas.snu.ac.kr/~bruno/papers/TypeClasses.pdf
//
//   We are also not talking about "implicit conversions" here, only about
//   "implicit parameters".

// Remember the interface from hw01:
trait Showable[T] {
  def show(t: T): String
}

object step1 {

  // Let us define instances for String and Int, just as in the Java solution
  // to hw01:
  val strShowable: Showable[String] = new Showable[String] {
    def show(t: String) = '"' + t + '"'
  }
  val intShowable: Showable[Int] = new Showable[Int] {
    def show(t: Int) = t.toString
  }

  // As in the Java solution we can use strShowable and intShowable to
  // display values of the corresponding type:

  println(strShowable.show("hello"))
  println(intShowable.show(42))

  // As in Java, this is type-safe and thus the following code does not
  // compile:

  // intShowable.show("hello")

  // Let us define some pair, using the tuple constructing syntax in Scala:

  //           pair-type
  //          vvvvvvvvvvvvv
  val myPair: (String, Int) = ("hello", 42)
  //                          ^^^^^^^^^^^^^
  //                            pair-value

  // Now let us define a showable instance for pairs (S, T) that works for
  // all `S` and `T`:
  def pairShowable[S, T](s: Showable[S], t: Showable[T]): Showable[(S, T)] =
    new Showable[(S, T)] {
      def show(p: (S, T)) = "(" + s.show(p._1) + ", " + t.show(p._2) + ")"
    }

  // Let's use pairShowable with myPair:
  println(pairShowable(strShowable, intShowable).show(myPair))

  // type `S` will be instantiated to `String` and `T` will be instantiated to
  // `Int` -- so this is equivalent to
  //     `pairShowable[String, Int](...).show(myPair)`
}

object step2 {

  // In our homework 01 we observed that creating instances for nested tuples
  // can be quite verbose. Here is one for `((Int, String), (String, Int))`:

  val nestedShowable: Showable[((Int, String), (String, Int))] = pairShowable(
    pairShowable(intShowable, strShowable),
    pairShowable(strShowable, intShowable))


  // We can give a recursive algorithm to construct a showable for a pair:
  //
  // To find a Showable[(S, T)] perform the following steps:
  // - find a Showable[S], let's call it `s`
  // - find a Showable[T], let's call it `t`
  // - create a Showable[(S, T)] by calling `pairShowable(s, t)`

  // We can use Scala's feature "implicit arguments" to let the compiler
  // execute this search.

  // What are implicit arguments? Let's assume the following function and
  // the corresponding call:
  def inc(n: Int): Int = n + 1
  println( inc(42) ) // prints 43

  // We can teach the compiler that whenever an integer is required it may
  // use `42` for it:
  implicit val answerToAllQuestions: Int = 42

  // In addition we can mark the argument list of `inc` to be implicit saying:
  // "Whenever the full argument list is omitted in a call, try to find
  //  implicit values with the correct types."
  def inc2(implicit n: Int): Int = n + 1

  // We still can call `inc2` explicitly:
  println( inc2(43) ) // prints 44

  // or we can have the compiler search for an implicit value of type `Int`:
  println( inc2 ) // prints 43

  // another way of finding an implicit value for a type T is to call
  // `implicitly[T]` which is implemented in the Scala Predef as follows:
  //
  //   def implicitly[T](implicit t: T): T = t

  println( implicitly[Int] ) // prints 42


  // We can use the same mechanism to mark `strShowable` and `intShowable`
  // as "standard"-values for `Showable[String]` and `Showable[Int]`
  // respectively.

  implicit def strShowable: Showable[String] = new Showable[String] {
    def show(t: String) = '"' + t + '"'
  }
  implicit def intShowable: Showable[Int] = new Showable[Int] {
    def show(t: Int) = t.toString
  }

  // Note: The only difference is the leading `implicit` keyword.

  // Now we mark the argument list of pairShowable to be implicit. That is:
  // If the Scala compiler can find implicit arguments for `Showable[S]` and
  // `Showable[T]`, then it can also implictly find a value of type
  // `Showable[(S, T)]`.

  implicit def pairShowable[S, T](implicit s: Showable[S], t: Showable[T]): Showable[(S, T)] =
    new Showable[(S, T)] {
      def show(p: (S, T)) = "(" + s.show(p._1) + ", " + t.show(p._2) + ")"
    }

  // Let's try that.

  val myPair: (String, Int) = ("hello", 42)

  val pairShow = implicitly[Showable[(String, Int)]]

  println(pairShow.show(myPair))

  // We also define a method show, that works on all types `T` but automatically
  // searches for a corresponding value of type: `Showable[T]`.
  //
  // In Scala a method can have multiple arugment lists. Only the last one
  // can be marked as implicit.
  def show[T](value: T)(implicit showable: Showable[T]): String =
    showable.show(value)

  // Now this...
  println(show(myPair))

  // ... is the same as
  println(implicitly[Showable[(String, Int)]].show(myPair))

  // This also works for nested pairs!
  val nested = ((42, "hello"), ("world", 43))

  println(show(nested))

  // which is the same as
  println(nestedShowable.show(nested))
}

// For more details on where Scala searches for implicit values, see
//   http://www.scala-lang.org/files/archive/spec/2.11/07-implicit-parameters-and-views.html#implicit-parameters
//
// It is common practice to provide some implicit instances of `Showable[T]`
// in the companion object `Showable`.
//
// Companion objects: Instead of defining static members (as you would do in
//   Java), in Scala we define the members on a companion object. A companion
//   object is an object that has the same name as some trait or class.
object Showable {

  // The implementations of
  implicit val strShowable: Showable[String] = new Showable[String] {
    def show(t: String) = '"' + t + '"'
  }
  implicit val intShowable: Showable[Int] = new Showable[Int] {
    def show(t: Int) = t.toString
  }

  implicit def pairShowable[S, T](implicit s: Showable[S], t: Showable[T]): Showable[(S, T)] =
    new Showable[(S, T)] {
      def show(p: (S, T)) = "(" + s.show(p._1) + ", " + t.show(p._2) + ")"
    }

  def show[T](value: T)(implicit showable: Showable[T]): String =
    showable.show(value)
}

// In addition to the current static scope, the companion object is one of the
// positions where the Scala compiler will search for instances. That is it
// will automatically work for client-code like:
object client {
  val nested = ((42, "hello"), ("world", 43))

  println( Showable.show(nested) )
}
