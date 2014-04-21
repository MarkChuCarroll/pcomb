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
import java.util.Arrays;
import java.util.List;

import org.goodmath.pcomb.Failure;
import org.goodmath.pcomb.ParseResult;
import org.goodmath.pcomb.Parser;
import org.goodmath.pcomb.Success;
import org.goodmath.pcomb.Action;
import org.goodmath.pcomb.RefParser;
import org.goodmath.pcomb.StringParserInput;
import org.junit.Test;

public class ParserTest {

  public <In, Out> void assertSuccessfulParseEquals(ParseResult<In, Out> result, Out expected) {
    assertTrue(result instanceof Success);
    Success<In, Out> success = (Success<In, Out>)result;
    assertEquals(expected, success.getResult());
  }

  @Test
  public void testConsumeOne() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.match('a');
    Parser<Character, Character> bparser = Parser.match('b');
    Parser<Character, Character> cparser = Parser.match('c');
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
    Parser<Character, Character> aparser = Parser.match('a');
    Parser<Character, Character> bparser = Parser.match('b');
    Parser<Character, Character> cparser = Parser.match('c');
    Parser<Character, List<Character>> abcparser =  Parser.seq(aparser).andThen(bparser).andThen(cparser);
    ParseResult<Character, List<Character>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result,
        Arrays.asList(new Character[] { 'a',  'b', 'c' }));
  }

  @Test
  public void testAndCombinator() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.match('a');
    Parser<Character, Character> bparser = Parser.match('b');
    Parser<Character, Character> cparser = Parser.match('c');
    Parser<Character, Character> abcparser =  aparser.andSecond(bparser).andFirst(cparser);
    ParseResult<Character, Character> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result, 'b');
  }


  @Test
  public void testAlternatives() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> choice = Parser.match('a').or(Parser.match('b')).or(Parser.match('c'));
    ParseResult<Character, Character> aresult = choice.parse(in);
    assertSuccessfulParseEquals(aresult, 'a');
    ParseResult<Character, Character> bresult = choice.parse(aresult.getRest());
    assertSuccessfulParseEquals(bresult, 'b');
    ParseResult<Character, Character> cresult = choice.parse(bresult.getRest());
    assertSuccessfulParseEquals(cresult, 'c');
  }

  @Test
  public void testOrCombinator() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> choice = Parser.match('a').or(Parser.match('b')).or(Parser.match('c'));

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
    Parser<Character, Character> choice = Parser.match('a').or(Parser.match('b')).or(Parser.match('c'));
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
    assertTrue(result instanceof Failure);
  }

  @Test
  public void testOpt() {
    StringParserInput in = new StringParserInput("abc");
    Parser<Character, Character> aparser = Parser.match('a').opt('0');
    Parser<Character, Character> bparser = Parser.match('b');
    Parser<Character, List<Character>> abcparser = Parser.seq(aparser).andThen(bparser).andThen(aparser);
    ParseResult<Character, List<Character>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result, Arrays.asList(new Character[] { 'a', 'b', '0' }));
    assertFalse(result.getRest().atEnd());
  }

  @Test
  public void testEOF() {
    StringParserInput in = new StringParserInput("ab");
    Parser<Character, Character> aparser = Parser.match('a');
    Parser<Character, Character> bparser = Parser.match('b');
    Parser<Character, Character> eofparser = Parser.end('x');
    Parser<Character, List<Character>> abcparser = Parser.seq(aparser).andThen(bparser).andThen(eofparser);
    ParseResult<Character, List<Character>> result = abcparser.parse(in);
    assertSuccessfulParseEquals(result,
        Arrays.asList(new Character[] { 'a', 'b', 'x' }));
  }

  @Test
  public void testRefParser() {
    StringParserInput in = new StringParserInput("(((a)))");
    RefParser<Character, Character> ref = new RefParser<Character, Character>();
    Parser<Character, Character> parens = Parser.match('(').andSecond(ref).andFirst(Parser.match(')'));
    Parser<Character, Character> choice = parens.or(Parser.match('a'));
    ref.setRef(choice);
    ParseResult<Character, Character> result = choice.parse(in);
    assertTrue(result instanceof Success);
  }

  @Test
  public void testParensParser() {
    // P -> ( P+ )
    // P -> a
    Action<List<String>, String> listToString = new Action<List<String>, String>() {
      @Override
      public String run(List<String> in) {
        return in.toString();
      }
    };
    Action<Character, String> charToString = new Action<Character, String>() {
      @Override
      public String run(Character in) {
        return in.toString();
      }
    };

    Parser<Character, String> id = Parser.charSet("abcdefghijklmnopqrstuvwxyz").transform(charToString);
    RefParser<Character, String> ref = new RefParser<Character, String>();
    Parser<Character, String> parens = Parser.matchWithSpaces('(').andSecond(ref.many(1).transform(listToString)).andFirst(Parser.matchWithSpaces(')'));
    Parser<Character, String> choice = parens.or(id);
    ref.setRef(choice);

    StringParserInput in = new StringParserInput("(((a (d e) (q)) ((a b c))))");
    ParseResult<Character, String> result = choice.parse(in);
    assertTrue(result instanceof Success);
    Success<Character, String> success = (Success<Character, String>) result;

    assertEquals("[[[a, [d, e], [q]], [[a, b, c]]]]", success.getResult());

  }
}
