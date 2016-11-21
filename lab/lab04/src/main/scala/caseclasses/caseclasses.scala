// We can show the desugaring for case classes by asking the compiler to print
// out the program after type-checking.
//
//    scalac -Xprint:typer src/main/caseclasses/caseclasses.scala
case class Goo(n: Int)

// One important detail is that the generated implementation `equals` follows
// the pattern of hw04.

object desugared {

  import scala.runtime.{ Statics, ScalaRunTime }

  // Here is the output with some clutter removed
  class Goo(val n: Int) extends AnyRef with Product with Serializable {
    def copy(n: Int = n): Goo = new Goo(n);
    override def productPrefix: String = "Goo";
    def productArity: Int = 1;
    def productElement(n: Int): Any = n match {
      case 0 => this.n
      case _ => throw new IndexOutOfBoundsException(n.toString)
    };

    def canEqual(other: Any): Boolean = other.isInstanceOf[Goo];

    override def hashCode(): Int = {
      var acc: Int = -889275714;
      acc = Statics.mix(acc, n);
      Statics.finalizeHash(acc, 1)
    };
    override def toString(): String = ScalaRunTime._toString(this);
    override def equals(other: Any): Boolean =
      (this eq other.asInstanceOf[AnyRef]) || (other match {
        case (_: Goo) => true
        case _ => false
      }) && {
        val otherGoo: Goo = other.asInstanceOf[Goo];
        otherGoo.canEqual(this) && this.n == otherGoo.n
      }
  }

  // And the corresponding companion object
  object Goo extends (Int => Goo) with Serializable {
    final override def toString(): String = "Goo"
    def apply(n: Int): Goo = new Goo(n)

    def unapply(goo: Goo): Option[Int] =
      if (goo == null)
        None
      else
        Some(goo.n)
  }
}
