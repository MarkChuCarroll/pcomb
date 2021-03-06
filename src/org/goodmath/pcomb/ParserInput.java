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
 * An abtract parser input; essentially a lazy stream of some value type.
 * For a lexical analyzer, this would be a stream of characters; for a conventional
 * parser, it would be a stream of tokens.
 * @param <In>
 */
public interface ParserInput<In> {
  /**
   * Get the first element from this input stream.
   */
  In first();

  /**
   * Get the remaining part of this input stream after the first character is consumed.
   */
  ParserInput<In> rest();

  /**
   * Return true if there's no input left in the stream.
   */
  boolean atEnd();
}
