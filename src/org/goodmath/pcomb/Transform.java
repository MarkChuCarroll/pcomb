package org.goodmath.pcomb;

public class Transform<In, Out1, Out2> extends Parser<In, Out2> {
  
  private Parser<In, Out1> _base;
  private org.goodmath.pcomb.Parser.Transformer<Out1, Out2> _trans;

  public Transform(Parser<In, Out1> base, Transformer<Out1, Out2> trans) {
    this._base = base;
    this._trans = trans;
  }

  @Override
  public org.goodmath.pcomb.Parser.ParseResult<In, Out2> parse(
      ParserInput<In> in) {
    ParseResult<In, Out1> p = _base.parse(in);
    if (p instanceof Failure) {
      return new Failure<In, Out2>();
    }
    Success<In, Out1> success = (Success<In, Out1>)p;
    return new Success<In, Out2>(_trans.transform(success.getResult()), success.getRest());
  }
}
