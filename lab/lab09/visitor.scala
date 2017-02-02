package solution

/*
 * This first variant shows an external visitor for expressions. That is, the
 * traversal of the datastructure is controlled by the visitor.
 *
 * The fact that it is external becomes visible in the implementations of the
 * `visit` methods. Here `visitExp` is explicitly called on the children.
 *
 * The file also shows that the visitor pattern as presented by Gamma et al.
 * can be split into two different technical parts.
 *
 * a) representing operations on trees as object with methods for each variant.
 *    (in our case these are `visitLit` and `visitAdd`).
 * b) dispatching the actual runtime type of the variant to the corresponding
 *    method (in our case `visitExp`).
 */
object external {

  trait Exp
  case class Lit(n: Int) extends Exp
  case class Add(l: Exp, r: Exp) extends Exp

  trait Visitor {

    /**
     * Performs the runtime dispatch on the actual type of e and selects the
     * appropriate method in the visitor.
     */
    def visitExp(e: Exp): Unit = e match {
      case l: Lit => visitLit(l)
      case a: Add => visitAdd(a)
    }
    def visitLit(l: Lit): Unit
    def visitAdd(a: Add): Unit
  }

  trait Eval extends Visitor {
    // Visitors can be stateful
    var result: Int = 0

    def visitLit(l: Lit): Unit = { result = l.n }
    def visitAdd(a: Add): Unit = {
      visitExp(a.l) // explicit call on the left child
      val left  = result
      visitExp(a.r) // explicit call on the right child
      val right = result
      result = left + right
    }
  }
}


/**
 * This object is a variant of `external` using `accept` methods as known from
 * Gamma et al.
 *
 * Please note: This is still an external visitor even though the data structure
 * now performs the runtime dispatch: The traversal is still controlled by the
 * visitor.
 */
object externalAccept {

  trait Exp {
    def accept(v: Visitor): Unit
  }
  case class Lit(n: Int) extends Exp {
    def accept(v: Visitor): Unit = v.visitLit(this);
  }
  case class Add(l: Exp, r: Exp) extends Exp {
    def accept(v: Visitor): Unit = v.visitAdd(this);
  }

  trait Visitor {

    /**
     * Dispatching now is performed by the data structure itself, so we only
     * need to call it here.
     */
    def visitExp(e: Exp): Unit = e.accept(this)
    def visitLit(l: Lit): Unit
    def visitAdd(a: Add): Unit
  }

  trait Eval extends Visitor {
    var result: Int = 0

    def visitLit(l: Lit) { result = l.n }
    def visitAdd(a: Add) {
      visitExp(a.l)
      val left  = result
      visitExp(a.r)
      val right = result
      result = left + right
    }
  }
}

/**
 * The contents of this object build on `externalAccept`. We introduce a
 * generic type parameter `R` (for "Result") to use as a return type of
 * visitor methods instead of `Unit`.
 *
 * Changing the interface of `Visitor` also propagates the expressions. The
 * `accept` method now needs to be generic.
 */
object externalAcceptResult {

  trait Visitor[Result] {
    def visitExp(e: Exp): Result = e.accept(this)
    def visitLit(l: Lit): Result
    def visitAdd(a: Add): Result
  }

  trait Exp {
    def accept[R](v: Visitor[R]): R
  }
  case class Lit(n: Int) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitLit(this);
  }
  case class Add(l: Exp, r: Exp) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitAdd(this);
  }

  // Now passing the result around as a mutable field is not necessary anymore.
  // Instead we articulate the result of visiting the data structure by setting
  // the type-parameter `Result` to `Int`:
  trait Eval extends Visitor[Int] {
    def visitLit(l: Lit) = l.n
    def visitAdd(a: Add) = {
      val left  = visitExp(a.l);
      val right = visitExp(a.r);
      left + right
    }
  }
}

/**
 * Building on the preparations in `externalResult` where generics
 * are used to express the result type of a visitor we can turn the
 * external visitor into an internal visitor by moving the `visitExp` or
 * `accept` calls to the data structure itself.
 *
 * Please note that internal vs. external and accept vs. explicit dispatching
 * are two orthogonal concepts. That is we could imagine the same code as
 * presented in this file, but without `accept`.
 *
 * Also using generics to represent the result type is orthogonal to *internal
 * vs. external* visitors.
 */
object internal {

  trait Visitor[Result] {
    def visitLit(n: Int): Result
    def visitAdd(left: Result, right: Result): Result
  }

  trait Exp {
    def accept[R](v: Visitor[R]): R
  }
  case class Lit(n: Int) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitLit(n);
  }
  case class Add(l: Exp, r: Exp) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitAdd(l.accept(v), r.accept(v));
  }

  trait Eval extends Visitor[Int] {
    def visitLit(n: Int): Int = n
    def visitAdd(left: Int, right: Int): Int = left + right
  }
}

/**
 * Some visitors require access to the actual tree (and subtrees of children)
 * to perform their task. This variant here builds on `internal` and changes
 * the type of a visitor. Now every visitor method also includes a reference
 * to the current visited node as argument.
 */
object internalWithTree {

  trait Visitor[Result] {
    def visitLit(l: Lit): Result
    def visitAdd(a: Add, left: Result, right: Result): Result
  }

  trait Exp { def accept[R](v: Visitor[R]): R }
  case class Lit(n: Int) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitLit(this);
  }
  case class Add(l: Exp, r: Exp) extends Exp {
    def accept[R](v: Visitor[R]) = v.visitAdd(this, l.accept(v), r.accept(v));
  }

  trait Eval extends Visitor[Int] {
    def visitLit(l: Lit): Int = l.n
    def visitAdd(a: Add, left: Int, right: Int): Int = left + right
  }
}

/**
 * Again building on `internal` we can omit the data structure all together!
 * In this variant we just rename the methods of the visitor and immediately
 * use them. This avoids building up a data structure, just to traverse it
 * and compute the result.
 *
 * Note how `someTerm` is polymorph in the result type `R` of the visitor. If
 * we call it with `Eval` below, `R` will be instantiated to `Int` and
 * the function `someTerm` just computes an integer.
 */
object visitorFactory {

  // we just rename the methods!
  trait Visitor[Result] {
    def Lit(n: Int): Result
    def Add(left: Result, right: Result): Result
  }

  object Eval extends Visitor[Int] {
    def Lit(n: Int): Int = n
    def Add(left: Int, right: Int): Int = left + right
  }

  object BuildTree extends Visitor[external.Exp] {
    def Lit(n: Int): external.Exp = new external.Lit(n)
    def Add(left: external.Exp, right: external.Exp): external.Exp = new external.Add(left, right)
  }

  def someTerm[R](v: Visitor[R]): R = {
    import v._
    Add(Add(Lit(3), Lit(5)), Lit(9))
  }

  someTerm(Eval)

  // We can also re-build our original tree and visit it with the external
  // visitor.
  val visitor = new external.Eval {}
  visitor.visitExp(someTerm(BuildTree))
}
