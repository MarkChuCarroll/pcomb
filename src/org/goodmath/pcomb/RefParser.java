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
 * A RefParser is a workaround in Java for scoping limitations.
 * When we have recursive rules, they need to be able to reference themselves. But in Java,
 * we can't statically build a graph of parsers that contains cycles, and any recursive parse construct
 * is going to need to have a cycle. A RefParser allows us to insert a node into the parser graph, and
 * then later bind it.
 *
 * For example, if you wanted to parse:
 * X := ( X )
 * | 'y'
 * You'd write:
 * leftParenParser = parser.match(LeftParen)
 * rightParentParser = parser.match(rightParen)
 * ref = Parser.ref()
 * parens = parser.seq(leftParenParser, ref, rightParenParser)
 * yparser = ...
 * xparser = parser.choice(new List<Parser>(parens, yparser)
 * ref.setRef(xparser)
 *
 * @param <In> the type of object returned by the parser input
 * @param <Out> the type of parse result produced by the ref
 */
public class RefParser<In, Out> extends Parser<In, Out> {

  public Parser<In, Out> _ref;

  public RefParser() {
    this._ref = null;
  }

  @Override
  public org.goodmath.pcomb.ParseResult<In, Out> parse(
      ParserInput<In> in) {
    if (_ref == null) {
      return new Failure<In, Out>();
    } else {
      return _ref.parse(in);
    }
  }

  public void setRef(Parser<In, Out> p) { _ref = p; }

}
