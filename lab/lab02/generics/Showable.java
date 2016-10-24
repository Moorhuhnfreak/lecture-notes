package hw01;

public abstract class Showable<T> {

  abstract String show(T t);


  // --- Instances ---
  public static Showable<String> stringShowable = new Showable<String>() {
    String show(String str) {
      return "\"" + str + "\"";
    }
  };

  public static Showable<Integer> integerShowable = new Showable<Integer>() {
    String show(Integer str) {
      return str.toString();
    }
  };

  // class PairShowable<S, T> {
  //   Showable<S> fst
  //   Showable<T> snd
  //   PairShowable(Showable<S> fst, Showable<T> snd) { ... }
  //   ...
  // }
  public static <S, T> Showable<Pair<S, T>> pairShowable(Showable<S> fst, Showable<T> snd) {
    return new Showable<Pair<S, T>>() {
      String show(Pair<S, T> p) {
        return "(" + fst.show(p.getKey()) + ", " + snd.show(p.getValue()) + ")";
      }
    };
  }
}
