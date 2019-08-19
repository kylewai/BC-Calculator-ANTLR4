# BC-Calculator
ANTLR4 grammar and visitor class for generating a parser for major features of the bc language. 
For loops, while loops, if/else statements, functions, and scopes for functions have also been
implemented. The parse tree is evaluated with visitors in MyVisitor.java. Function.java is a class containing information about
a function including the parameter names and the function context.
With antlr4 installed, the code can be run with antlr -visitor BC.g4 && javac MyVisitor.java BC*.java Function.java. Testing can then be performed with java BC [testname].bc
