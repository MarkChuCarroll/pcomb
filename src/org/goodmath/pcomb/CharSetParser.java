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
 * A parser which matches any single character from a string containing a set of characters.
 */
public class CharSetParser extends Parser<Character, Character> {
  private final String _chars;

  public CharSetParser(String chars) {
    this._chars = chars;
  }

  @Override
  public org.goodmath.pcomb.ParseResult<Character, Character> parse(
      ParserInput<Character> in) {
    if (_chars.indexOf(in.first()) == -1) {
      return new Failure<Character, Character>();
    } else {
      return new Success<Character, Character>(in.first(), in.rest());
    }
  }

}
