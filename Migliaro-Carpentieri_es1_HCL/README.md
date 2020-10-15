# Migliaro-Carpentieri_es1_HCL

## Specifica lessicale
|        <font size=5> Lessema </font>        	| <font size=5> Token </font> 	| <font size=5> Attributo </font> 	|
|:-------------------------------------------:	|:---------------------------:	|:-------------------------------:	|
|               **Delimitatori**              	|                             	|                                 	|
|              _Qualsiasi spazio_             	|              -              	|                                 	|
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

## Gestione errori
Nella tokenizzazione dei numeri si é adottata la stessa strategia di **JFLEX**, ovvero
flussi di caratteri del tipo:  
00069 diventa <NUM, "0">, <NUM, "0">, <NUM, "0">, <NUM, "69">;  
52a diventa <NUM, "52">, <ID, "a">;  
52.f diventa <NUM, "52">, \<ERROR, ".">, <ID, "f">  
34E diventa <NUM, "34">, <ID, "E">


## Extra 
- Gestione dei commenti inline
- Gestione dei commenti in blocco
- Gestione dei token di errore con attributo il lessema di errore stesso
- Keyword
    - for
    - return
