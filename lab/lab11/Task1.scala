package hw10

trait Task1 extends Task0 {

  // ### Task 1a.
  //
  // Write an instance of `Functor[IntListF]` that allows us to map over the
  // recursive position in a list of integers. That is, for a given function
  // `f: A => B` an empty list should stay empty (without applying the function)
  // and `Cons[A](someInteger, a: A)` should be mapped to a
  // `Cons[B](someInteger, mappedRecursivePosition)`.

  implicit def intListIsFunctor = new Functor[IntListF] {
    def map[A, B](fa: IntListF[A])(f: A => B): IntListF[B] = fa match {
      case EmptyList() => EmptyList()
      case Cons(n, r) => Cons(n, f(r))
    }
  }

  // ### Task 1b.
  //
  // Define the pattern functor for the recursive Datatype of expressions with
  // literals and addition (homework 9). Follow the example of `IntListF` and
  // replace all recursive occurrences by the type parameter `X`.

  // NOTE: The recursive datatype in Haskell-like notation would be:
  //
  //   data Expr = Lit Int | Add Expr Expr
  //
  // If we now replace all recursive occurrences by X this becomes
  //
  //   data Expr X = Lit Int | Add X X

  trait ExprF[X]
  // ... add your case class definitions for Lit and Add here ...
  case class Lit[X](n: Int) extends ExprF[X]
  case class Add[X](l: X, r: X) extends ExprF[X]


  // Similar to `IntList` we can recover the recursive datatype of expressions
  // by using `Fix`:

  type Expr = Fix[ExprF]

  // Define an example tree modelling the expression `(1 + (2 + 3)) + 4`:
  val example1: Expr =
    Fix(Add(
      Fix(Add(
        Fix(Lit(1)),
        Fix(Add(
          Fix(Lit(2)),
          Fix(Lit(3))))
        )
      ),
      Fix(Lit(4)))
    )

  // NOTE: This looks very verbose, right? Also we violate the DRY priniple
  //       here. So it might be advisable to define helper functions to get
  //       rid of the repetitive calls to `Fix`. Those helper functions are
  //       sometimes called "smart constructors":
  def lit(n: Int): Expr = Fix(Lit(n))
  def add(l: Expr, r: Expr): Expr = Fix(Add(l, r))

  // here is the same term with the smart constructors:
  val example1_2 = add(add(lit(1), add(lit(2), lit(3))), lit(4))

  // NOTE: A trick that you might be interested in: since we are in Scala we can
  //       make use of implicit conversions and the pimp-my-library pattern to
  //       make it look almostlike the term in the task description:
  implicit class ExprOps(self: Expr) {
    def +(other: Expr) = Fix(Add(self, other))
  }

  val example1_3: Expr = (lit(1) + (lit(2) + lit(3))) + lit(4)

  // Write an instance of `Functor[ExprF]` that allows us to map over the
  // recursive position in an expression. Again, follow the example of integer
  // lists.
  implicit def exprIsFunctor = new Functor[ExprF] {
    def map[A, B](fa: ExprF[A])(f: A => B): ExprF[B] = fa match {
      case Lit(n)    => Lit(n)
      case Add(l, r) => Add(f(l), f(r))
    }
  }
}
