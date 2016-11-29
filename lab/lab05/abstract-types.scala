// We use objects for namespacing, you may ignore them for now.
object abstractMethods  {

  // As you already know, methods in Java and Scala can be abstract:
  abstract class A { def m(): Int }

  // Abstract methods can be specialized in subclasses
  class B extends A { def m() = 42 }

  val a: A = new B()
  // a: A = B@40d91f7a
  a.m()
  // res0: Int = 42
}

object abstractFields {

  // Just like methods, also fields or "value members" can be left abstract
  abstract class A { val m: Int }

  // Also those can be specialized in subclasses
  class B extends A { val m = 42 }

  val a: A = new B()
  // a: A = B@40d91f7a
  a.m
  // res0: Int = 42
}


object genericTypes {
  // Just as in Java generics / also called "type parameters" can be used in
  // Scala to parametrize a class (or method) over types:
  trait Animal[Food] { def feed(food: Food): Unit }

  // A Cow is an animal that eats strings.
  class Cow extends Animal[String] {
    def feed(food: String) = { println("yummy " + food) }
  }

  // Here we see that the fact that a Cow eats strings is part of the
  // type `Animal[String]`
  val c: Animal[String] = new Cow

  // this allows us to feed strings to the cow
  val grass: String = ",.,.,.,.,"
  c.feed(grass)
  // yummy ,.,.,.,.,

  // As with abstract methods and abstract values, type parameters allow us
  // to abstract over implementation details.
  // However, the information leaks as part of the type `Animal[String]`. We
  // are not closed with regard to changing what a cow likes to eat (OCP).
  // Clients will always assume a cow likes strings.
}

object abstractTypes {

  trait Animal {
    // this type-member is abstract:
    type Food

    // We don't know yet what `Food` is, but we know where to get it ...
    def someFood: Food

    // ... and what to do with it:
    def feed(food: Food): Unit
  }

  class Cow extends Animal {
    type Food = String

    def someFood = ",.,.,.,,.,"
    def feed(food: String) = { println("yummy " + food); }
  }

  // If we know want to produce a cow and hide the fact that it likes strings, we
  // can upcast it to `Animal`. This will hide the implementation detail of
  // `type Food = String`:
  val c: Animal = new Cow

  c.feed(c.someFood)
  // c.feed(",.,.,.") // fails!


  val c2: Animal = new Cow

  // Even the following fails to typecheck, since we sealed `type Food = String`
  // away. This gives us the freedom to freely change `type Food` later on in
  // the implementation of `Cow` without risking to brake clients.

  // c.feed(c2.someFood) // fails!
}
