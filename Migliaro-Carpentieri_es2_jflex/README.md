# Migliaro-Carpentieri_es2_Jflex

## Specifica lessicale
|        <font size=5> Lessema </font>        	| <font size=5> Token </font> 	| <font size=5> Attributo </font> 	|
|:-------------------------------------------:	|:---------------------------:	|:-------------------------------:	|
|               **Delimitatori**              	|                             	|                                 	|
|            _Qualsiasi spaziatura_             |              -              	|                                 	|
|                **Letterali**                	|                             	|                                 	|
|                   _Numeri_                  	|             NUM             	|         numero letterale        	|
|              **Identificatori**             	|                             	|                                 	|
|                     _id_                    	|              ID             	|           id letterale          	|
|                 **Keywords**                	|                             	|                                 	|
|                      if                     	|              IF             	|                -                	|
|                     then                    	|             THEN            	|                -                	|
|                     else                    	|             ELSE            	|                -                	|
|                    while                    	|            WHILE            	|                -                	|
|                     for                     	|             FOR             	|                -                	|
|                     int                     	|             INT             	|                -                	|
|                    float                    	|            FLOAT            	|                -                	|
|                    return                   	|            RETURN           	|                -                	|
|                **Operatori**                	|                             	|                                 	|
|                      <                      	|            RELOP            	|                LT               	|
|                      <=                     	|            RELOP            	|               LEQ               	|
|                      >                      	|            RELOP            	|                GT               	|
|                      >=                     	|            RELOP            	|               GEQ               	|
|                      ==                     	|            RELOP            	|                EQ               	|
|                      !=                     	|            RELOP            	|               NEQ               	|
|                     <--                     	|            ASSIGN           	|                -                	|
|                **Separatori**               	|                             	|                                 	|
|                      (                      	|             LPAR            	|                -                	|
|                      )                      	|             RPAR            	|                -                	|
|                      {                      	|            LBRAC            	|                -                	|
|                      }                      	|            RBRAC            	|                -                	|
|                      ;                      	|          SEMICOLON          	|                -                	|
|                      ,                      	|            COMMA            	|                -                	|
|                 **Commenti**                	|                             	|                                 	|
|       Qualsiasi cosa preceduta da "//"      	|              -              	|                                 	|
| Qualsiasi cosa all'interno di "/\*" e "\*/" 	| -                           	|                                 	|
|                  **Errori**                 	|                             	|                                 	|
|           _Nessuno dei precedenti_          	|            ERROR            	|         errore letterale        	|

## Definizioni Regolari per JFlex
| <font size=5> Nome </font> 	|           <font size=5> Regular Expression </font>          	| <font size=5> Token </font> 	|
|:--------------------------:	|:-----------------------------------------------------------:	|:---------------------------:	|
|      **Delimitatori**      	|                                                             	|                             	|
|       LineTerminator       	|                     \r `or` \n `or` \r\n                    	|              -              	|
|         Whitespace         	|                 LineTerminator `or` [ \t\f]                 	|              -              	|
|        **Letterali**       	|                                                             	|                             	|
|            Digit           	|                            [0-9]                            	|              -              	|
|           Digits           	|                            [0-9]+                           	|              -              	|
|         NumericInt         	|                      [1-9]Digit* `or` 0                     	|              -              	|
|        NumericFloat        	|                     NumericInt"."Digits                     	|              -              	|
|  NumericScientifcNotation  	|        (NumericInt `or` NumericFloat)E[+-]?NumericInt       	|              -              	|
|       NumericLiterals      	| NumericInt `or` NumericFloat `or` NumericScientificNotation 	|         <NUM, _num_>        	|
|     **Identificatori**     	|                                                             	|                             	|
|           Letter           	|                           [a-zA-Z]                          	|              -              	|
|         Identifiers        	|                  Letter(Letter `or` Digit)*                 	| <ID, _id_> `or` <_KEYWORD_> 	|
|        **Operatori**       	|                                                             	|                             	|
|              <             	|                              <                              	|         <RELOP, LT>         	|
|             <=             	|                              <=                             	|         <RELOP, LEQ>        	|
|              >             	|                              >                              	|         <RELOP, GT>         	|
|             >=             	|                              >=                             	|         <RELOP, GEQ>        	|
|             ==             	|                              ==                             	|         <RELOP, EQ>         	|
|             !=             	|                              !=                             	|         <RELOP, NEQ>        	|
|             <--            	|                             <--                             	|           <ASSIGN>          	|
|       **Separatori**       	|                                                             	|                             	|
|              (             	|                              (                              	|            <LPAR>           	|
|              )             	|                              )                              	|            <RPAR>           	|
|              {             	|                              {                              	|           <LBRAC>           	|
|              }             	|                              }                              	|           <RBRAC>           	|
|              ;             	|                              ;                              	|         <SEMICOLON>         	|
|              ,             	|                              ,                              	|           <COMMA>           	|
|        **Commenti**        	|                                                             	|                             	|
|        BlockComment        	|                /*"[^*] ~"*/" `or` "/*""*"+ "/               	|              -              	|
|       InputCharacter       	|                           [^\r\n]                           	|              -              	|
|        InlineComment       	|               //InputCharacter*LineTerminator?              	|              -              	|
|           Comment          	|               BlockComment `or` InlineComment               	|              -              	|
|         **Errori**         	|                                                             	|                             	|
|              -             	|                             [^]                             	|       <ERROR, _error_>      	|
## Extra 
- Gestione dei commenti inline
- Gestione dei commenti in blocco
- Gestione dei token di errore con attributo la stringa che lo genera
- Keyword
    - for
    - return

## Nota
Nella tabella delle definizioni regolari per JFlex anziché usare il carattere "|"
per definire l'operatore "or" è stato scritto con la formattazione da codice inline
poiché a causa della sintassi di Markdown non poteva essere inserito all'interno di 
cella di una tabella