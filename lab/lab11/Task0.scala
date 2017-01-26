package hw10

trait Task0 {

  // In Scala, the pattern functor for integer lists can be expressed as
  // follows:
  trait IntListF[X]
  case class EmptyList[X]() extends IntListF[X]
  case class Cons[X](head: Int, tail: X) extends IntListF[X]

  // In Scala, `Fix` is defined as:
  case class Fix[F[_]](out: F[Fix[F]])

  // And as in the README the recursive datatype is obtained by:
  type IntList = Fix[IntListF]

  // An example list:
  val l: IntList = Fix(Cons(1, Fix(Cons(2, Fix(EmptyList())))))

  // (Arguably this list looks a bit complicated, we will see how to improve
  // this in the next lab-session).


  // ## Background Info: Functors

  // In Homework 5, task 1 you had to write the function `lengths`. Lenghts
  // transformed a list of strings into a list of integers. In functional
  // programming jargon we also say, "we map a list of strings to a list of
  // integers" or "we map `lenghts` over a list of strings". Since mapping
  // occurs quite often, the Scala standard library includes a function to map
  // over lists. Hence `length` could have also been implemented as:

  def lengths(strings: List[String]): List[Int] = strings.map(s => s.length)


  // Informally, something that can be "mapped" over is called a "Functor". This
  // property can be captured in an interface (similar to homework 1, where we
  // captured the fact that something is "showable" in an interface):

  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // Again, the trait `Functor` captures the fact that a type constructor,
  // something takes a type parameter (like `List[_]` or `Set[_]` or `IntListF`
  // above) can be mapped over. Mapping preserves the structure, only the
  // content of the container will be changed. Let's show that lists are
  // functors:

  implicit val listsAreFunctors = new Functor[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa match {
      case Nil => Nil
      case a :: rest => f(a) :: map(rest)(f)
    }
  }

  // Now let's use the fact to implement lengths again, but this time we don't
  // care whether it is a list or some other kind of functor, we are parametric
  // in the shape of the container:

  def lengths[F[_]](strings: F[String])(implicit f: Functor[F]): F[Int] =
    f.map(strings)(s => s.length)

  assert(lengths(List("hello", "world")) == List(5, 5))

  // To let the compiler search for the functor instance, we marked it as
  // implicit (just as we did in earlier lab sessions and homeworks).
}
