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

public class StringParserInput implements ParserInput<Character> {
  private final String _str;
  private final int _pos;

  public StringParserInput(String s, int pos) {
    this._str = s;
    this._pos = pos;
  }

  public StringParserInput(String s) {
    this(s, 0);
  }

  @Override
  public Character first() {
    if (this._pos < _str.length()) {
      return _str.charAt(_pos);
    } else {
      return 0;
    }
  }

  @Override
  public ParserInput<Character> rest() {
    if (_pos < _str.length()) {
      return new StringParserInput(_str, _pos + 1);
    } else {
      return this;
    }
  }

  @Override
  public boolean atEnd() {
    return _pos >= _str.length();
  }

}
