package org.goodmath.pcomb;

import java.util.List;

/**
 * An interface for a parse action that transforms one output type to another.
 * @param <X>
 * @param <Y>
 */
public abstract class Transformer<From, To> {
  public abstract To transform(From in);

  public static <X> Transformer<List<X>, X> first() {
    return new Transformer<List<X>, X>() {
      @Override
      public X transform(List<X> in) {
        return in.get(0);
      }
    };
  }

  public static <X> Transformer<List<X>, X> second() {
    return new Transformer<List<X>, X>() {
      @Override
      public X transform(List<X> in) {
        return in.get(1);
      }
    };
  }

  public static <X> Transformer<List<X>, X> nth(final int n) {
    return new Transformer<List<X>, X>() {
      @Override
      public X transform(List<X> in) {
        return in.get(n);
      }
    };
  }


}


