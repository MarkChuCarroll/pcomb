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
package org.goodmath.tests.pcomb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.goodmath.pcomb.Pair;
import org.goodmath.pcomb.Parser;
import org.goodmath.pcomb.StringParserInput;
import org.goodmath.pcomb.Parser.ParseResult;
import org.junit.Test;

public class ParserTest {

  public <In, Out> void assertSuccessfulParseEquals(Parser.ParseResult<In, Out> result, Out expected) {
    assertTrue(result instanceof Parser.Success);
    Parser.Success<In, Out> success = (Parser.Success<In, Out>)result;
    assertEquals(expected, success.getResult());
  }

  @Test
  public void testConsumeOne() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.consume('a');
    Parser<Character, Character> bparser = Parser.consume('b');
    Parser<Character, Character> cparser = Parser.consume('c');
    ParseResult<Character, Character> aresult = aparser.parse(in);
    assertSuccessfulParseEquals(aresult, 'a');
    ParseResult<Character, Character> bresult = bparser.parse(aresult.getRest());
    assertSuccessfulParseEquals(bresult, 'b');
    ParseResult<Character, Character> cresult = cparser.parse(bresult.getRest());
    assertSuccessfulParseEquals(cresult, 'c');
  }

  @Test
  public void testConsumeSeq() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.consume('a');
    Parser<Character, Character> bparser = Parser.consume('b');
    Parser<Character, Character> cparser = Parser.consume('c');
    Parser<Character, Pair<Character, Pair<Character, Character>>> abcparser = Parser.seq(aparser, Parser.seq(bparser, cparser));
    ParseResult<Character, Pair<Character, Pair<Character, Character>>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result, new Pair<Character, Pair<Character, Character>>('a', new Pair<Character, Character>('b', 'c')));
  }

  @Test
  public void testAlternatives() {
    StringParserInput in = new StringParserInput("abc");
    final Parser<Character, Character> aparser = Parser.consume('a');
    final Parser<Character, Character> bparser = Parser.consume('b');
    final Parser<Character, Character> cparser = Parser.consume('c');
    Parser<Character, Character> choice = Parser.choice(aparser, bparser, cparser);
    ParseResult<Character, Character> aresult = choice.parse(in);
    assertSuccessfulParseEquals(aresult, 'a');
    ParseResult<Character, Character> bresult = choice.parse(aresult.getRest());
    assertSuccessfulParseEquals(bresult, 'b');
    ParseResult<Character, Character> cresult = choice.parse(bresult.getRest());
    assertSuccessfulParseEquals(cresult, 'c');
  }

  @Test
  public void testRepetition() {
    StringParserInput in = new StringParserInput("abc");
    final Parser<Character, Character> aparser = Parser.consume('a');
    final Parser<Character, Character> bparser = Parser.consume('b');
    final Parser<Character, Character> cparser = Parser.consume('c');
    Parser<Character, Character> choice = Parser.choice(aparser, bparser, cparser);
    Parser<Character, List<Character>> rep0 = choice.many(0);
    Parser<Character, List<Character>> rep2 = choice.many(2);
    Parser<Character, List<Character>> rep4 = choice.many(4);
    ParseResult<Character, List<Character>> result = rep0.parse(in);
    List<Character> expected = new ArrayList<Character>();
    expected.add('a');
    expected.add('b');
    expected.add('c');
    assertSuccessfulParseEquals(result, expected);
    assertTrue(result.getRest().atEnd());

    result = rep2.parse(in);
    assertSuccessfulParseEquals(result, expected);

    result = rep4.parse(in);
    assertTrue(result instanceof Parser.Failure);
  }

  @Test
  public void testOpt() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.consume('a').opt('0');
    Parser<Character, Character> bparser = Parser.consume('b');
        Parser<Character, Pair<Character, Pair<Character, Character>>> abcparser = Parser.seq(aparser, Parser.seq(bparser, aparser));
    ParseResult<Character, Pair<Character, Pair<Character, Character>>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result, new Pair<Character, Pair<Character, Character>>('a', new Pair<Character, Character>('b', '0')));
    assertFalse(result.getRest().atEnd());
  }

  @Test
  public void testEOF() {
    StringParserInput in = new StringParserInput("ab");
    Parser<Character, Character> aparser = Parser.consume('a');
    Parser<Character, Character> bparser = Parser.consume('b');
    Parser<Character, Character> eofparser = Parser.parseEOF('x');
    Parser<Character, Pair<Character, Pair<Character, Character>>> abcparser = Parser.seq(aparser, Parser.seq(bparser, eofparser));
    ParseResult<Character, Pair<Character, Pair<Character, Character>>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result, new Pair<Character, Pair<Character, Character>>('a', new Pair<Character, Character>('b', 'x')));
  }


}
