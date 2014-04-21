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
 * The type returned by invoking a parser.
 */
public interface ParseResult<In, Out> {
  /**
   * This really doesn't belong here, but it makes some code a bit cleaner if you can still
   * ask for the rest of the input without casting to success.
   * @return the unconsumed part of the input stream if a parse succeeded, or null if it failed.
   */
  public ParserInput<In> getRest();
}