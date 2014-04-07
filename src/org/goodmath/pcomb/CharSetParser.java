package org.goodmath.pcomb;

public class CharSetParser extends Parser<Character, Character> {
  private final String _chars;

  public CharSetParser(String chars) {
    this._chars = chars;
  }

  @Override
  public org.goodmath.pcomb.Parser.ParseResult<Character, Character> parse(
      ParserInput<Character> in) {
    if (_chars.indexOf(in.first()) == -1) {
      return new Failure<Character, Character>();
    } else {
      return new Success<Character, Character>(in.first(), in.rest());
    }
  }

}
