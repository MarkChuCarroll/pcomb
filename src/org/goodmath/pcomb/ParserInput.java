package org.goodmath.pcomb;

/**
 * An abtract parser input; essentially a lazy stream of some value type.
 * For a lexical analyzer, this would be a stream of characters; for a conventional
 * parser, it would be a stream of tokens.
 * @param <In>
 */
public interface ParserInput<In> {
  In first();
  ParserInput<In> rest();
  boolean atEnd();
}
