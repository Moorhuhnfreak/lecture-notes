package hw10

trait Task2 extends Task1 {

  // Now we know that `ExprF` are functors and we have an example expression.
  // But why all this effort? Good news: Since you already told me that `ExprF`
  // is a functor, I can provide you with a generic visitor infrastructure for
  // it:

  def visit[F[_], A](visitor: F[A] => A)(implicit f: Functor[F]): Fix[F] => A = {
    case Fix(ffix) => {
      // recursively visit the children
      val mappedChildren: F[A] = f.map(ffix)(fix => visit(visitor).apply(fix))
      // now visit only one layer (the current node)
      visitor(mappedChildren)
    }
  }

  // Let's use this infrastructure to count the length of an `IntList`:

  val lengthVisitor: IntListF[Int] => Int = {
    case EmptyList() => 0
    case Cons(head, n) => n + 1
  }

  val length = visit(lengthVisitor)
  assert(length(l) == 2)


  // Follow this example and reimplement the `eval` and `pretty` visitors from
  // homework 9.

  // Note: Let's read `F[_]` as if it where a container or a collection. So
  //       we can think `[A]` as an "F" full of "As".
  //
  //       The visitor `F[A] => A` is a function that given an F full of As
  //       computes a single result value of type A.
  //
  //       Compare this to the function `Fix[F] => A`. This is a function that
  //       takes a recursive datatype (where the shape is described by `F`) and
  //       computes a single return value of type `A`. Most of the time this
  //       function will need to be recursive to analyse the recursive datatype
  //       `Fix[F]`. In contrast, the visitor function only perform the
  //       computation *on one level*.
  //
  // Example - *eval* as recursive function:
  val evalRec: Fix[ExprF] => Int = {
    case Fix(Lit(n)) => n
    // evalRec is a recursive function!!!
    //                     vvvvvvv      vvvvvvv
    case Fix(Add(l, r)) => evalRec(l) + evalRec(r)
  }

  // In contrast the evalVisitor only handles *one layer* and assumes
  // the children already have been traversed and the results for them
  // have been computed!
  val evalVisitor: ExprF[Int] => Int = {
    case Lit(n) => n
    case Add(l, r) => l + r
  }
  val prettyVisitor: ExprF[String] => String = {
    case Lit(n) => n.toString
    case Add(l, r) => l + " + " + r
  }

  val eval   = visit(evalVisitor)
  val pretty = visit(prettyVisitor)

  assert(eval(example1) == 10)
  assert(pretty(example1) == "1 + 2 + 3 + 4")
}
