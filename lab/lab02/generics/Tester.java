package hw01;

import static hw01.Showable.stringShowable;
import static hw01.Showable.integerShowable;
import static hw01.Showable.pairShowable;

public class Tester {

  public static <T> void assertEquals(T t1, T t2) {
    if (!t1.equals(t2)) {
      throw new RuntimeException(
        "Expected " + t1.toString() + " to equal " + t2.toString());
    }
  }

  public static void main(String[] args) {
    Pair<String, Integer> p = new Pair<>("Hello World", 42);

    assertEquals(p.getKey(), "Hello World");
    assertEquals(p.getValue(), 42);
    assertEquals(stringShowable.show("Hello World"), "\"Hello World\"");
    assertEquals(integerShowable.show(42), "42");

    Showable<Pair<String, Integer>> ps = pairShowable(stringShowable, integerShowable);
    assertEquals(ps.show(p), "(\"Hello World\", 42)");
  }
}
