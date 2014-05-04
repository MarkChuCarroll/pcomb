from abc import abstractmethod

class ParserInput(object):
  """Input to a pcomb parser. This is basically just a simple cons-list interface
  on top of whatever kind of input - streams, lists, files - you want to
  read parser input from. You should not try to instantiate this directly;
  instead, pick an implementation that inherits from it.
  """

  @abstractmethod
  def first(self):
    """Returns the first element of this input, or None if there isn't any more."""
    pass

  def rest(self):
    """Returns a ParserInput containing the remainder of this input after removing the first
    element."""
    pass

  def at_end(self):
    """Returns true if there is no input remaining"""
    pass

class ParseResult(object):
  """Abstract superclass of all ParseResults."""
  def succeeded(self):
    return False

class Success(ParseResult):
  """A successful parse result, containing the value produced by a parser,
  and the unconsumed part of its input.
  """
  def __init__(self, result, rest):
    self.result = result
    self.rest = rest

  def succeeded(self):
    return True

class Failure(ParseResult):
  def __init__(self):
    pass


class Parser(object):
  @abstractmethod
  def parse(self, inp):
    """The primary parse method. Takes a parser input, and returns a ParseResult."""
    pass

  def and_then(self, other):
    """Returns a new parser joining this parser with another in a sequence.
    The result is a list of the results of the sequence elements.
    """
    parsers = [ self, other ]
    return SequenceParser(parsers)

  def or_else(self, other):
    """Returns a new parser joining this parser with another in a choice.
    The result is the result from the successful alternative.
    """
    parsers = [ self, other ]
    return ChoiceParser(parsers)

  def opt(self):
    """Returns a new parser which accepts an optional occurence of this one.
    Result is either the parse result of self, or None."""
    return OptParser(self)

  def many(self, minrep=0):
    """Returns a new parser which accepts a repetition of this one.
    The result is a list of the results from the repetition."""
    return ManyParser(self, minrep)

  @classmethod
  def match(self, v):
    """Return a parser which accepts any value in an input set.
    The result is the value that matched.
    """
    return SetParser(v)

  def pr(self):
    return ""


class SetParser(Parser):
  def __init__(self, chars):
    self.chars = chars

  def pr(self):
    return "SetParser%s" % self.chars

  def parse(self, inp):
    if inp.first() in self.chars:
      return Success(inp.first(), inp.rest())
    else:
      return Failure()

class SequenceParser(Parser):
  def __init__(self, parsers=[]):
    self.parsers = parsers

  def pr(self):
    result = "Sequence[" +  ",".join(p.pr() for p in self.parsers) + "]"
    return result

  def and_then(self, next_parser):
    result = SequenceParser(self.parsers[:])
    result.parsers.append(next_parser);
    return result

  def parse(self, inp):
    results = []
    remaining = inp
    for p in self.parsers:
      r = p.parse(remaining)
      if not r.succeeded():
        return Failure()
      remaining = r.rest
      results.append(r.result)
    return Success(results, remaining)

class ChoiceParser(Parser):
  def __init__(self, choices=[]):
    self.choices = choices

  def pr(self):
    return "Choice[" +  ",".join(p.pr() for p in self.choices) + "]"

  def or_else(self, other):
    result = ChoiceParser(self.choices[:])
    result.choices.append(other)
    return result

  def parse(self, inp):
    for p in self.choices:
      r = p.parse(inp)
      if r.succeeded():
        return r
    return Failure()

class OptParser(Parser):
  def __init__(self, opt):
    self.opt = opt

  def pr(self):
    return "Opt[%s]" % self.opt.pr()

  def parse(self, inp):
    r = self.opt.parse(inp)
    if r.succeeded():
      return r
    else:
      return Success(None, inp)

class ManyParser(Parser):
  def __init__(self, parser, min_reps):
    self.parser = parser
    self.min_reps = min_reps

  def pr(self):
    return "Many[%s,%s]" % (self.parser.pr(), self.min_reps)

  def parse(self, inp):
    reps = 0
    result = []
    remaining_input = inp
    r = self.parser.parse(remaining_input)
    while r.succeeded():
      result.append(r.result)
      reps += 1
      remaining_input = r.rest
      r = self.parser.parse(remaining_input)
    if reps >= self.min_reps:
      return Success(result, remaining_input)
    else:
      return Failure()

class Reference(Parser):
  """Indirect references to parsers, used for creating recursive grammars.
  A reference parser refers to a parser instance by name. When the parser is
  invoked, that name is looked up in the dictionary of known reference parsers.
  """
  NAMED_PARSERS = {}
  def __init__(self, name):
    self.name = name

  def pr(self):
    return "Ref[%s]" % self.name

  @classmethod
  def register_named_parser(cls, name, parser):
    """Binds a reference name to a parser instance"""
    cls.NAMED_PARSERS[name] = parser

  def parse(self, inp):
    parser = self.NAMED_PARSERS.get(self.name, None)
    if parser is None:
      raise ValueError("No parser with name %s" % self.name)
    return parser.parse(inp)

class Action(Parser):
  """A parser that attaches a semantic action to another parser.
  This succeeds if the embedded parser succeeds; the result
  is value returned by applying the action function to the result
  of the embedded parser.
  """
  def __init__(self, parser, act):
    self.parser = parser
    self.action = act

  def pr(self):
    return "Action[%s]" % self.parser.pr()

  def parse(self, inp):
    result = self.parser.parse(inp)
    if result.succeeded():
      return Success(self.action(result.result), result.rest)
    else:
      return Failure()

class StringParserInput(ParserInput):
  def __init__(self, s):
    self.chars = s

  def pr(self):
    return "In[%s]" % self.chars

  def first(self):
    if len(self.chars) >0:
      return self.chars[0]
    else:
      return None

  def rest(self):
    if (len(self.chars) > 0):
      return StringParserInput(self.chars[1:])
    else:
      return self

  def at_end(self):
    return len(self.chars) == 0
