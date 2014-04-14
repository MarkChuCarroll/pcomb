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

public class OptParser<In, Out> extends Parser<In, Out> {

  private final Parser<In, Out> _base;
  private final Out _nullVal;

  public OptParser(Parser<In, Out> base, Out nullVal) {
    this._base = base;
    this._nullVal = nullVal;
  }

  @Override
  public ParseResult<In, Out> parse(
      ParserInput<In> in) {
    ParseResult<In, Out> p = _base.parse(in);
    if (p instanceof Success) {
      return p;
    } else {
      return new Success<In, Out>(_nullVal, in);
    }
  }

}
