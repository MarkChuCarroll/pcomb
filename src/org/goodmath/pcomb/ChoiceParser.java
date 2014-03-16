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

import java.util.List;

public class ChoiceParser<In, Out> extends Parser<In, Out> {
  private final List<Parser<In, Out>> _choices;

  public ChoiceParser(List<Parser<In, Out>> choices) {
    this._choices = choices;
  }

  @Override
  public org.goodmath.pcomb.Parser.ParseResult<In, Out> parse(
      ParserInput<In> in) {
    for (Parser<In, Out> parser: _choices) {
      ParseResult<In, Out> result = parser.parse(in);
      if (result instanceof Parser.Success) {
        return result;
      }
    }
    return new Parser.Failure<In, Out>();
  }

}
