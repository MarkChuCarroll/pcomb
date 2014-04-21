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

/**
 * Parser for running a list of parsers of identical type in sequence.
 * A successful parse produces a list of the results of its component parsers.
 */
public class SeqParser<In, Out> extends Parser<In, List<Out>> {
  private final List<Parser<In, Out>> _parsers;

  public SeqParser(List<Parser<In, Out>> parsers) {
    _parsers = parsers;
  }

  public SeqParser(Parser<In, Out> first) {
    _parsers = new ArrayList<Parser<In, Out>>();
    _parsers.add(first);
  }

  public SeqParser<In, Out> andThen(Parser<In, Out> next) {
    List<Parser<In, Out>> parsers = new ArrayList<Parser<In, Out>>();
    parsers.addAll(_parsers);
    parsers.add(next);
    return new SeqParser<In, Out>(parsers);
  }

  @Override
  public ParseResult<In, List<Out>>  parse(ParserInput<In> in) {
    List<Out> result_vals = new ArrayList<Out>();
    for (Parser<In, Out> p : _parsers) {
      ParseResult<In, Out> out = p.parse(in);
      if (out instanceof Failure) {
        return new Failure<In, List<Out>>();
      }
      Success<In, Out> success = (Success<In, Out>)out;
      result_vals.add(success.getResult());
      in = success.getRest();
    }
    return new Success<In, List<Out>>(result_vals, in);
  }
}
