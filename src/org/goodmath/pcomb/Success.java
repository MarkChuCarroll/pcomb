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
 * The result from a successful parser invocation.
 */
public class Success<In, Out> implements ParseResult<In, Out> {
  private final Out _result;
  private final ParserInput<In> _rest;


  public Success(Out result, ParserInput<In> rest) {
    this._result = result;
    this._rest = rest;
  }

  public Out getResult() { return _result; }

  @Override
  public ParserInput<In> getRest() { return _rest; }
}