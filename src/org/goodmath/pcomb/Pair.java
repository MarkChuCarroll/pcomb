/*
 * Copyright 2014 Mark C. Chu-Carroll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goodmath.pcomb;

/**
 * A silly utility pair.
 */
public class Pair<X, Y> {
  private final X _x;
  private final Y _y;

  public Pair(X x, Y y) {
    this._x = x;
    this._y = y;
  }

  public X getFirst() { return _x; }
  public Y getSecond() { return _y; }
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) {
      return false;
    }
    @SuppressWarnings("rawtypes")
    Pair op = (Pair)o;
    return op.getFirst().equals(getFirst()) && op.getSecond().equals(getSecond());
  }

  @Override
  public String toString() {
    return "(" + _x + ", " + _y + ")";
  }
}
