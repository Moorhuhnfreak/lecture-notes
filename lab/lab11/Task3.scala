package hw10

trait Task3 extends Task2 {

  // If we want to compute both the pretty printed string and the result of
  // evaluating a term, we need to traverse the expression tree twice. This
  // however is inefficient, we can do better! If first compute both results at
  // every level and store them in a tuple, we can compute both results in one
  // visit!

  // Implement a tupling combinator that takes two visitors and returns a
  // visitor that performs both computations in one visit:
  def tuple[A, B, F[_]](
    visitor1: F[A] => A,
    visitor2: F[B] => B
  )(implicit f: Functor[F]): F[(A, B)] => (A, B) =
    // as argument we have an F full of pairs with As and Bs:
    fab => {

      // 1. We use the fact that `F` is a functor and extract the first
      //    component of the tuple.
      val fa: F[A] = f.map(fab) { _._1 }

      // 2. Again for the second component
      val fb: F[B] = f.map(fab) { _._2 }

      // 3. Now we use `visitor1` and `visitor2` that know how to handle
      //    `F[A]` and `F[B]`:
      val resultA: A = visitor1(fa)
      val resultB: B = visitor2(fb)

      // 4. finally we tupled them back up:
      (resultA, resultB)
    }

  val bothVisitor = tuple(evalVisitor, prettyVisitor)
  val both = visit(bothVisitor)

  assert(both(example1) == (10, "1 + 2 + 3 + 4"))

  // *Tipp:* You need to make use of the fact that `F` is a functor!
}

object Test extends Task3 with App
