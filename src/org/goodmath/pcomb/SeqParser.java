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

public class SeqParser<In, Out1, Out2> extends Parser<In, Pair<Out1, Out2>> {
  private final Parser<In, Out1> _first;
  private final Parser<In, Out2> _second;

  public SeqParser(Parser<In, Out1> first, Parser<In, Out2> second) {
    this._first = first;
    this._second = second;
  }

  @Override
  public ParseResult<In, Pair<Out1, Out2>> parse(ParserInput<In> in) {
    ParseResult<In, Out1> out1 = _first.parse(in);
    if (out1 instanceof Parser.Failure) {
      return new Failure<In, Pair<Out1, Out2>>();
    }
    Parser.Success<In, Out1> success1 = (Parser.Success<In, Out1>)out1;
    ParseResult<In, Out2> out2 = _second.parse(success1.getRest());
    if (out2 instanceof Parser.Failure) {
      return new Failure<In, Pair<Out1, Out2>>();
    }
    Parser.Success<In, Out2> success2 = (Parser.Success<In, Out2>)out2;
    return new Parser.Success<In, Pair<Out1, Out2>>(new Pair<Out1, Out2>(success1.getResult(),
        success2.getResult()), success2.getRest());
  }
}
