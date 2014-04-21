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
 * A parser which runs an action on a parse result, transforming the result type.
 *
 * @param <In>
 * @param <Orig> the original parse result type.
 * @param <Transformed> the transformed parse result type.
 */
public class Transform<In, Orig, Transformed> extends Parser<In, Transformed> {
  private final Parser<In, Orig> _base;
  private final Action<Orig, Transformed> _action;

  public Transform(Parser<In, Orig> base, Action<Orig, Transformed> trans) {
    this._base = base;
    this._action = trans;
  }

  @Override
  public ParseResult<In, Transformed> parse(
      ParserInput<In> in) {
    ParseResult<In, Orig> p = _base.parse(in);
    if (p instanceof Failure) {
      return new Failure<In, Transformed>();
    }
    Success<In, Orig> success = (Success<In, Orig>)p;
    return new Success<In, Transformed>(_action.run(success.getResult()), success.getRest());
  }
}
