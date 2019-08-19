grammar BC;
/*multExpr: (((exprWrap|assign|print) (NEWLINE+|SEMICOLON NEWLINE*)) | ((exprWrap | assign | print) COMMENT (NEWLINE+|SEMICOLON NEWLINE*)) | (COMMENT (NEWLINE+|SEMICOLON NEWLINE*)?) | 
((exprWrap | assign | print) INLINE) | NEWLINE+ | INLINE)*
;*/

multExpr: (NEWLINE | stat | function)+
;
stat: forStat (NEWLINE | INLINE)*      #stat6
    | ifStat (NEWLINE | INLINE)*       #stat1
    | whileStat (NEWLINE | INLINE)*    #stat2
    | exprWrap              #stat3
    | assignWrap            #stat4
    | printWrap             #stat5
;
forStat: 'for' '(' (assign | expr) ';' (assign | expr) ';' (assign | expr) ')' NEWLINE* statBlock
;
ifStat: 'if' condPlusStat NEWLINE* ('else' statBlock)?
;
whileStat: 'while' condPlusStat NEWLINE*
;
function: 'define ' VAR '(' varList? ')' statBlock
;
funcCall: var=VAR '(' (expr list?)? ')' ';'? #funcCall1
    | var=VAR '(' (assign list?)? ')' ';'?   #funcCall2
;
varList: VAR (',' VAR)*;

condPlusStat: '(' expr ')' NEWLINE* statBlock   #condPlusStat1
    | '(' assign ')' NEWLINE* statBlock         #condPlusStat2
;
statBlock: '{' NEWLINE* stat* NEWLINE * '}'
    | NEWLINE* ((exprStatement | assignStatement)+ | (expr | assign))
;
exprWrap: expr (NEWLINE | INLINE)+   #exprWrap1
    | exprStatement (NEWLINE | INLINE)+  #exprWrap2
    | exprStatement     #exprWrap3
;

assignWrap: assignStatement (NEWLINE | INLINE)+   #assignWrap1
    | assign (NEWLINE | INLINE)+   #assignWrap2
    | assignStatement   #assignWrap3
;

printWrap: printStatement (NEWLINE | INLINE)+   #printWrap1
    | print (NEWLINE | INLINE)+                 #printWrap2
    | printStatement                            #printWrap3
;

exprStatement: expr SEMICOLON+
;
assignStatement: assign SEMICOLON+
;
printStatement: print SEMICOLON+
;

print: ('print ' expr list?)  #print1
    | ('print ' assign list?)  #print2
;

list: (exprListEl | assignListEl)+
;
exprListEl: ',' expr
;
assignListEl: ',' assign
;

expr:

    INT  #integer
    | DOUBLE  #double
    | 'read()'  #read
    | funcCall NEWLINE*     #stat7
    | '(' expr ')'   #parensExpr
    | '(' assign ')'  #parensAssign
    | var=VAR #var
    | '++' var=VAR   #preIncrement
    | var=VAR '++'   #postIncrement
    | var=VAR '--'   #postDecrement
    | '--' var=VAR   #preDecrement
    | '-' ex=expr   #negExpr
    | '-' a=assign   #negAssign
    | l=expr op=('%' | '*' | '/') ra=assign   #exprOp2Assign
    | l=expr '^' r=expr  #exprExpExpr
    | l=expr '^' ra=assign   #exprExpAssign
    | l=expr op=('%' | '*' | '/') r=expr   #exprOp2Expr
    | l=expr op=('+' | '-') r=expr   #exprOp1Expr
    | l=expr op=('+' | '-') ra=assign   #exprOp1Assign
    | 'return'    #return0
    | 'return' expr #return1
    | 'return' assign #return2
    | 'continue' #continue
    | 'break' #break
    | expr '<' expr   #exprLess1Expr
    | expr '<' assign   #exprLess2Expr
    | assign '<' expr   #exprLess3Expr
    | assign '<' assign   #exprLess4Expr
    | expr '<=' expr   #exprLessEq1Expr
    | expr '<=' assign   #exprLessEq2Expr
    | assign '<=' expr   #exprLessEq3Expr
    | assign '<=' assign   #exprLessEq4Expr
    | expr '>' expr   #exprGreat1Expr
    | expr '>' assign   #exprGreat2Expr
    | assign '>' expr   #exprGreat3Expr
    | assign '>' assign   #exprGreat4Expr
    | expr '>=' expr   #exprGreatEq1Expr
    | expr '>=' assign   #exprGreatEq2Expr
    | assign '>=' expr   #exprGreatEq3Expr
    | assign '>=' assign   #exprGreatEq4Expr
    | expr '==' expr   #exprEq1Expr
    | expr '==' assign   #exprEq2Expr
    | assign '==' expr   #exprEq3Expr
    | assign '==' assign   #exprEq4Expr
    | expr '!=' expr   #exprNotEq1Expr
    | expr '!=' assign   #exprNotEq2Expr
    | assign '!=' expr   #exprNotEq3Expr
    | assign '!=' assign   #exprNotEq4Expr
    | '!' expr   #notExpr
    | '!' assign   #notAssign
    | l=expr '&&' r=expr   #exprAndExpr
    | la=assign '&&' r=expr  #assignAndExpr
    | l=expr '&&' ra=assign  #exprAndAssign
    | la=assign '&&' ra=assign #assignAndAssign
    | l=expr '||' r=expr   #exprOrExpr
    | la=assign '||' r=expr  #assignOrExpr
    | l=expr '||' ra=assign  #exprOrAssign
    | la=assign '||' ra=assign  #assignOrAssign
    | ('s' '(' expr ')') #sinExpr
    | ('s' '(' assign ')') #sinAssign
    | ('c' '(' expr ')') #cosExpr
    | ('c' '(' assign ')') #cosAssign
    | ('l' '(' expr ')') #logExpr
    | ('l' '(' assign ')')  #logAssign
    | ('e' '(' expr ')')  #expExpr
    | ('e' '(' assign ')')  #expAssign
    | ('sqrt' '(' expr ')')  #sqrtExpr
    | ('sqrt' '(' assign ')')   #sqrtAssign
;


assign: 
    var=VAR '=' ex=expr #assign1
    | var=VAR '=' a=assign #assign2
    | var=VAR op=('%' | '*' | '/' | '+' | '-' | '^') '=' r=expr  #assign3
;

COMMENT: ('/*' .*? '*/') ->skip;
INLINE: ('#' .*? NEWLINE);
WS: [ \t]+ -> skip;
INT: [0-9]+;
DOUBLE: [0-9]+'.'[0-9]+;
VAR: [a-z]+[a-z0-9_]*;
SEMICOLON: ';';
NEWLINE: '\r\n';