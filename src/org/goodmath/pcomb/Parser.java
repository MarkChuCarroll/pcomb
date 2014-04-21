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

/**
 * Parser combinator master parser class.
 * @param <In> the type of objects in the input stream feeding the parser
 * @param <Out> the type of objects produced by a successful invocation of the parser.
 */
public abstract class Parser<In, Out> {
  /**
   * The main parse method.
   * @param in the input stream to feed the parser.
   * @return the result of running the parser on the input stream.
   */
  public abstract ParseResult<In, Out> parse(ParserInput<In> in);

  /**
   * Combinator for combining this parser in a sequence with a second, returning a pair containing
   * their results if both succeeded.
   * @param other
   */
  public <Out2> Parser<In, Pair<Out, Out2>> andPair(final Parser<In, Out2> other) {
      return new Parser<In, Pair<Out, Out2>>() {
        @Override
        public ParseResult<In, Pair<Out, Out2>> parse(
            ParserInput<In> in) {
          ParseResult<In, Out> firstStep = Parser.this.parse(in);
          if (firstStep instanceof Failure) {
            return new Failure<In, Pair<Out, Out2>>();
          }
          Out firstResult = ((Success<In, Out>)firstStep).getResult();
          ParseResult<In, Out2> secondStep = other.parse(firstStep.getRest());
          if (secondStep instanceof Failure) {
            return new Failure<In, Pair<Out, Out2>>();
          }
          Out2 secondResult = ((Success<In, Out2>)secondStep).getResult();
          return new Success<In, Pair<Out, Out2>>(new Pair<Out, Out2>(firstResult, secondResult), secondStep.getRest());
        }

      };
  }

  /**
   * Combinator for combining this parser in a sequence with another, returning the result
   * of the first parser.
   * @param other
   */
  public <Out2> Parser<In, Out> andFirst(final Parser<In, Out2> other) {
    return new Parser<In, Out>() {
      @Override
      public ParseResult<In, Out> parse(
          ParserInput<In> in) {
        ParseResult<In, Out> firstStep = Parser.this.parse(in);
        if (firstStep instanceof Failure) {
          return firstStep;
        }
        Out firstResult = ((Success<In, Out>)firstStep).getResult();
        ParseResult<In, Out2> secondStep = other.parse(firstStep.getRest());
        if (secondStep instanceof Failure) {
          return new Failure<In, Out>();
        }
        return new Success<In, Out>(firstResult, secondStep.getRest());
      }
    };
  }

  /**
   * Combinator for combining this parser in a sequence with another, returning the result
   * of the second parser.
   * @param other
   * @return
   */
  public <Out2> Parser<In, Out2> andSecond(final Parser<In, Out2> other) {
    return new Parser<In, Out2>() {
      @Override
      public ParseResult<In, Out2> parse(
          ParserInput<In> in) {
        ParseResult<In, Out> firstStep = Parser.this.parse(in);
        if (firstStep instanceof Failure) {
          return new Failure<In, Out2>();
        }
        ParseResult<In, Out2> secondStep = other.parse(firstStep.getRest());
        if (secondStep instanceof Failure) {
          return secondStep;
        }
        return secondStep;
      }
    };
  }

  /**
   * Combinator for creating a choice between two parsers of the same type.
   * @param other
   */
  public Parser<In, Out> or(final Parser<In, Out> other) {
    return new ChoiceParser<In, Out>(this, other);
  }

  /**
   * Create a parser for a sequence of parsers of the same type in order. This method creates
   * a sequence of the first two elements. Other elements can be added to the
   * list using the SeqParser.andThen method. A successful parse returns a
   * list of the results of its element parsers.
   */
  public static <In, Out> SeqParser<In, Out> seq(Parser<In, Out> first) {
    return new SeqParser<In, Out>(first);
  }

  /**
   * Return a parser that parses repetitions of this parser.
   * @param atleast the minimum number of times that the parse must succeed.
   */
  public Parser<In, List<Out>> many(int atleast) {
    return new ManyParser<In, Out>(this, atleast);
  }

  /**
   * Return a parser that accepts an optional input.
   * @param nullVal the value to return if the optional element is omitted.
   * @return
   */
  public Parser<In, Out> opt(Out nullVal) {
    return new OptParser<In, Out>(this, nullVal);
  }

  /**
   * Create a ref parser, for resolving forward refs.
   */
  public static <In, Out> Parser<In, Out> ref() {
    return new RefParser<In, Out>();
  }


  /**
   * Create a parser that wraps another, and transforms its result value.
   * In a parser generator, this would be something like a parse action.
   * @param trans
   * @return
   */
  public <X> Parser<In, X> transform(Action<Out, X> trans) {
    return new Transform<In, Out, X>(this, trans);
  }


  /**
   * Create a parser which consumes a specific input token; anything else will fail.
   * @param i the input to parse.
   * @return
   */
  public static <In> Parser<In, In> match(final In i) {
    return new Parser<In, In>() {
      @Override
      public org.goodmath.pcomb.ParseResult<In, In> parse(
          ParserInput<In> in) {
        if (in.first() == i) {
          return new Success<In, In>(i, in.rest());
        } else {
          return new Failure<In, In>();
        }
      }
    };
  }

  /**
   * A standard utility parser for accepting whitespace.
   */
  public static Parser<Character, Character> space =
      new CharSetParser(" \t\n").many(0).transform(new Action<List<Character>, Character>() {
        @Override
        public Character run(List<Character> in) {
          return ' ';
        }
      });

  /**
   * Create a parser which accepts a single character, optionally preceeded by any amount of whitespace.
   * @param c
   */
  public static Parser<Character, Character> matchWithSpaces(final char c) {
    return space.andSecond(match(c));
  }

  /**
   * Create a parser which accepts any character from a specific collection of characters, optionally
   * preceeded by any amount of whitespace.
   * @param chars
   */
  public static Parser<Character, Character> charSet(final String chars) {
    return space.andSecond(new CharSetParser(chars));
  }


  /**
   * Create a parser which only succeeds at the end of the input stream.
   * @param v
   * @return
   */
  public static <In, X> Parser<In, X> end(final X v) {
    return new Parser<In, X>() {

      @Override
      public org.goodmath.pcomb.ParseResult<In, X> parse(
          ParserInput<In> in) {
        if (in.atEnd()) {
          return new Success<In, X>(v, in);
        } else {
          return new Failure<In, X>();
        }
      }

    };
  }
}
