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

import java.util.ArrayList;
import java.util.List;

public class ManyParser<In, Out> extends Parser<In, List<Out>> {
  private final int _atLeast;
  private final Parser<In, Out> _base;

  public ManyParser(Parser<In, Out> base, int atLeast) {
    this._base = base;
    this._atLeast = atLeast;
  }

  @Override
  public ParseResult<In, List<Out>> parse(ParserInput<In> in) {
    List<Out> results = new ArrayList<Out>();
    ParserInput<In> unparsed = in;
    ParseResult<In, Out> r = _base.parse(unparsed);
    while (r != null && r instanceof Success) {
      Success<In, Out> success = (Success<In, Out>)r;
      results.add(success.getResult());
      unparsed = success.getRest();
      r = _base.parse(unparsed);
    }
    if (results.size() >= _atLeast) {
      return new Success<In, List<Out>>(results, unparsed);
    } else {
      return new Failure<In, List<Out>>();
    }
  }

}
