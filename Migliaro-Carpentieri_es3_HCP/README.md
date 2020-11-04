# Migliaro-Carpentieri_es3_HCP

## Grammatica context-free del parser
La grammatica fornitaci per l'esercitazione è la seguente:
```
G = (V, T, P, S) dove:
 N = {Program, Stmt, Expr},
 T = {ID, IF, THEN, ELSE, RELOP, NUMBER, ;, ASSIGN, WHILE, DO, EOF},  
 P = {  
        S -> Program EOF
        Program -> Program ; Stmt
                | Stmt
        Stmt -> IF Expr THEN Stmt ELSE Stmt
                | ID ASSIGN Expr
                | DO Stmt WHILE Expr
        Expr -> Expr RELOP Expr
                | ID 
                | NUMBER
     }
```

Poiché è ambigua relativamente alle produzioni con testa il non terminale
"Expr", tali produzioni sono state modificate nelle seguenti equivalenti:
```
Expr -> Expr RELOP T | T
T -> ID | NUMBER
```

Inoltre, poiché la grammatica è anche ricorsiva sinistra, dovuto alle produzioni con testa i
non terminali "Program" e "Expr", sono state fatte anche le seguenti modifiche:
```
Program -> Stmt A
A -> ; Stmt A | ""
```

```
Expr -> T L
L -> RELOP T L | ""
T -> ID | NUMBER
```

Quindi, la grammatica finale, utilizzata nell'implementazione del parser, è la seguente:
```
G = (V, T, P, S) dove:
 N = {Program, Stmt, Expr},
 T = {ID, IF, THEN, ELSE, RELOP, NUMBER, ;, ASSIGN, WHILE, DO, EOF},  
 P = {  
        S -> Program EOF
        Program -> Stmt A
        A -> ; Stmt A | ""
        Stmt -> IF Expr THEN Stmt ELSE Stmt
                | ID ASSIGN Expr
                | DO Stmt WHILE Expr
        Expr -> T L
        L -> RELOP T L | ""
        T -> ID | NUMBER
     }
```

## Note
È stata utilizzata la strategia on-demand per la richiesta dei token da parsare.  
Per gestire il backtracking utilizzando tale strategia è stato deciso di memorizzare i 
token letti all'interno di una lista.