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

import java.util.Arrays;
import java.util.List;

/**
 * Parser combinator master parser class.
 * @param <In> the type of objects in the input stream feeding the parser
 * @param <Out> the type of objects produced by a successful invocation of the parser.
 */
public abstract class Parser<In, Out> {
  /**
   * The type returned by invoking a parser.
   */
  public static interface ParseResult<I, O> {
    public ParserInput<I> getRest();
  }

  /**
   * The result from a successful parser invocation.
   */
  public static class Success<I, O> implements ParseResult<I, O> {
    private final O _result;
    private final ParserInput<I> _rest;


    public Success(O result, ParserInput<I> rest) {
      this._result = result;
      this._rest = rest;
    }

    public O getResult() { return _result; }

    @Override
    public ParserInput<I> getRest() { return _rest; }
  }

  /**
   * Type representing a failed parser invocation.
   */
  public static class Failure<I, O> implements ParseResult<I, O> {
    public Failure() { }
    @Override
    public ParserInput<I> getRest() { return null; }
  }

  /**
   * The main parse method.
   * @param in the input stream to feed the parser.
   * @return the result of running the parser on the input stream.
   */
  public abstract ParseResult<In, Out> parse(ParserInput<In> in);

  /**
   * Create a parser that chains together two parsers to execute in sequence.
   * @param p1
   * @param p2
   * @return a parser that parses p1 followed by p2.
   */
  public static <In, Out1, Out2, Out> Parser<In, Pair<Out1, Out2>> seq(Parser<In, Out1> p1, Parser<In, Out2> p2) {
    return new SeqParser<In, Out1, Out2>(p1, p2);
  }

  /**
   * Create a choice parser: take two parsers. If the first succeeds, then return its result.
   * Otherwise, run the second parser, and return its result.
   * @param choices
   * @return the result of a parse of either p1, p2, or failure.
   */
  public static <In, Out> Parser<In, Out> choice(Parser<In, Out>... choices) {
    return new ChoiceParser<In, Out>(Arrays.asList(choices));
  }

  /**
   * Return a parser that parses repetitions of this parser.
   * @param atleast
   * @return
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
   * @return
   */
  public static <In, Out> Parser<In, Out> ref() {
    return new RefParser<In, Out>();
  }

  /**
   * An interface for a parse action that transforms one output type to another.
   * @param <X>
   * @param <Y>
   */
  public interface Transformer<X, Y> {
    Y transform(X in);
  }

  /**
   * Create a parser that wraps another, and transforms its result value.
   * In a parser generator, this would be something like a parse action.
   * @param trans
   * @return
   */
  public <X> Parser<In, X> transform(Transformer<Out, X> trans) {
    return new Transform<In, Out, X>(this, trans);
  }


  /**
   * Create a parser which consumes a specific input; anything else will fail.
   * @param i the input to parse.
   * @return
   */
  public static <In> Parser<In, In> consume(final In i) {
    return new Parser<In, In>() {
      @Override
      public org.goodmath.pcomb.Parser.ParseResult<In, In> parse(
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
   * Create a parser which only succeeds on EOF.
   * @param v
   * @return
   */
  public static <In, X> Parser<In, X> parseEOF(final X v) {
    return new Parser<In, X>() {

      @Override
      public org.goodmath.pcomb.Parser.ParseResult<In, X> parse(
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
