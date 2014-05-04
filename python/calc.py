# A simple example of using parser combinators to build an arithmetic
# expression parser.

from pcomb import *

## Actions

def digits_to_number(digits, running=0):
  """Convert a list of digits to an integer"""
  if len(digits) == 0:
    return running
  else:
    r = (running * 10) + int(digits[0])
    return digits_to_number(digits[1:], r)

def unary_to_number(n):
  if n[0] == None:
    return n[1]
  else:
    return -n[1]

def eval_add(lst):
  """Evaluate an addition expression. For addition rules, the parser will return
  [number, [[op, number], [op, number], ...]]
  To evaluate that, we start with the first element of the list as result value,
  and then we iterate over the pairs that make up the rest of the list, adding
  or subtracting depending on the operator.
  """
  first = lst[0]
  result = first
  for n in lst[1]:
    if n[0] == '+':
      result += n[1]
    else:
      result -= n[1]
  return result

def eval_mult(lst):
  """Evaluate a multiplication expression. This is the same idea as evaluating
  addition, but with multiplication and division operators instead of addition and
  subtraction.
  """
  first = lst[0]
  result = first
  for n in lst[1]:
    if n[0] == '*':
      result = result * n[1]
    else:
      result = result / n[1]
  return result

## The Grammar
# expr : add_expr ( ( '*' | '/' ) add_expr )*
# add_expr : unary_expr ( ( '+' | '-' ) unary_expr )*
# unary_expr :  ( '-' )? simple
# simple : number | parens
# parens : '(' expr ')'
# number: digit+


digit = Parser.match(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])
number = Action(digit.many(1), digits_to_number)

parens = Action(Parser.match(['(']) & Reference('expr') & Parser.match([')']),
               lambda result: result[1])
simple = number | parens
unary_expr = Action(Parser.match(['-']).opt()  & simple, unary_to_number)
mult_expr = Action(unary_expr &  (Parser.match(['*', '/']) & unary_expr).many(), eval_mult)
add_expr = Action(mult_expr & (Parser.match(['-', '+']) & mult_expr).many(), eval_add)
expr = add_expr
Reference.register_named_parser('expr', add_expr)


inp = StringParserInput("1+2*(3+5*4)*(6+7)")
print(expr.parse(inp).output)
