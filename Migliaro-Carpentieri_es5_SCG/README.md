# Migliaro-Carpentieri_es5_SCG

## Come compilare un programma Toy
### Windows
Scarica la cartella "scripts" all'interno del progetto e lancia il comando 
`path_to_scripts\Toy2C.bat <path_to_input.toy> <path_to_output.exe>`
e questo produrrà un file eseguibile al path di output specificato.

## Analisi Semantica
### Implementazione tabella dei simboli
La tabella dei simboli è stata implementata mediante le classi `SymbolTable`, `SymbolTableNode` e 
`SymbolTableRecord`. 
`SymbolTable` è una hash map la cui chiave è l'identificatore del simbolo e il valore è la lista delle informazioni
ad esso associato contenute nell'oggetto `SymbolTableRecord`. Inoltre ha anche un attributo che identifica la tabella.  
`SymbolTableRecord` mantiene le informazioni riguardanti i simboli a cui è associato, in particolare ha 3 campi:
- Kind: Indica se l'identificatore è una funzione o una variabile
- Type: Indica il tipo dell'identificatore, nel caso di una funzione indica il tipo dei parametri in input e il tipo dei valori di ritorno
- Properties: Campo addizionale per qualsiasi altra informazione riguardante l'identificatore.

`SymbolTableNode` è il generico nodo dell'albero delle symbol table che mantiene un oggetto `SymbolTable`, un puntatore al padre
nell'albero e i puntatori ai suoi figli. Inoltre implementa una funzione di lookup che prende in input un simbolo e lo cerca seguendo
la regola most closely nested rule.

### Realizzazione dell'analizzatore semantico
Inizialmente definiamo una tabella dei simboli di libreria dove all'interno vengono definite le funzioni di libreria per 
l'input e l'output, ovvero `readln` e `write`. In seguito viene creata una tabella per l'intero programma, che ha come padre
quella delle funzioni di libreria. In quest'ultima saranno mantenute tutte le informazioni riguardanti le procedure e variabili
con scoping globale. Ogni qualvolta viene definita una procedura questa crea un nuovo scope, e così una nuova tabella dei simboli
contenente le informazioni sulle variabili dichiarate all'interno di essa, quest'ultima tabella è figlia di quella relativa al programma.  
In generale abbiamo solamente 3 livelli di scope:
1. Quello delle funzioni di libreria
2. Quello del programma 
3. Quello per ogni procedura

Il type environment è stato realizzato definendo un puntatore alla tabella dei simboli corrente. Utilizzando il riferimento al
padre possiamo ricostruire l'intero type environment.

### Controlli effettuati dall'analizzatore
|                                                                   Controlli effettuati                                                                  	|                                 Eccezione lanciata                                 	|
|:-------------------------------------------------------------------------------------------------------------------------------------------------------:	|:----------------------------------------------------------------------------------:	|
|                                                                   **Identificatori**                                                                  	|                                                                                    	|
| Variabile non dichiarata                                                                                                                                	| Variable 'x' not declared                                                          	|
| Funzione non dichiarata                                                                                                                                 	| Function 'f' not declared                                                          	|
|                                                                **Chiamate a funzione**                                                                	|                                                                                    	|
| Troppi argomenti passati a una funzione                                                                                                                 	| Too much arguments given to the function 'f'                                       	|
| Pochi argomenti passati a una funzione                                                                                                                  	| Too few arguments given to the function 'f'                                        	|
| I tipi degli argomenti passati non corrispondono ai tipi dei parametri                                                                                 	| Type mismatch in function call 'f' on parameter 'i'                                	|
|                                                                    **Assegnamento**                                                                   	|                                                                                    	|
| Il numero dei risultati delle espressioni a destra dell'operatore di assegnamento è maggiore del numero di id a sinistra dell'operatore di assegnamento 	| Too many value to unpack                                                           	|
| Il numero dei risultati delle espressioni a destra dell'operatore di assegnamento è minore del numero di id a sinistra dell'operatore di assegnamento   	| Too few value to unpack                                                            	|
| Assegnato a un id un valore di tipo non valido per tale id                                                                                              	| Type mismatch, variable 'x' of type 'type1' assigned with type 'type2'             	|
|                                                                     **Condizioni**                                                                    	|                                                                                    	|
| Il tipo dell'espressione usata come condizione di un while/if/elif non è booleano                                                                       	| Type mismatch, expression in while/if/elif condition isn't of type BOOL but 'type' 	|
|                                                             **Dichiarazione di variabili**                                                            	|                                                                                    	|
| Una variabile non può essere dichiarata due volte all'interno di uno stesso scope                                                                       	| Variable 'x' is already declared in this scope                                     	|
| Una variabile in una funzione non può essere dichiarata con lo stesso identificativo di un parametro                                                    	| Cannot declare variable with same identifier of a parameter                        	|
|                                                             **Dichiarazione di funzioni**                                                             	|                                                                                    	|
| Una funzione non può essere ridichiarata                                                                                                                	| Function 'f' is already declared as variable/function                              	|
| Le funzioni readln e write non possono essere ridichiarate                                                                                              	| Cannot redeclare library function                                                  	|
| Non si può usare il return se la funzione ha tipo di ritorno void                                                                                       	| Cannot return a value if result type is VOID in function 'f'                       	|
| Devono essere ritornati dei valori se la funzione dichiara che lo fa                                                                                    	| Must return some values in function 'f'                                            	|
| La funzione torna più valori di quelli dichiarati                                                                                                       	| Function 'f' returns too many values than those declared                           	|
| I tipi dei valori di ritorno della funzione devono essere gli stessi di quelli  dichiarati                                                              	| Type mismatch in return of function 'f'                                            	|
| La funzione torna meno valori di quelli dichiarati                                                                                                      	| Too few return values given to the function 'f'                                    	|
|                                                                      **Generali**                                                                     	|                                                                                    	|
| Nel programma deve essere presenta la funzione main                                                                                                     	| Function main is not declared                                                      	|
|                                                                    **Espressioni**                                                                    	|                                                                                    	|
| Operatori unari applicati ad espressioni rispettando la compatibilità dei tipi                                                                          	| Type mismatch in operation 'op' 'type'                                             	|
| Operatori binari applicati ad espressioni rispettando la compatibilità dei tipi                                                                         	| Type mismatch in operation 'type1' 'op' 'type2'                                    	|
| Operatori binari tra funzioni che tornano più valori devono essere applicati  rispettando la compatibilità dei tipi                                     	| Type mismatch in operation function: cannot do 'type1' 'op' 'type2'                	|
| Operatori unari su funzioni che tornano più valori devono essere applicati rispettando la compatibilità dei tipi                                        	| Type mismatch in operation function: cannot do 'op' 'type'                         	|

### Scelte implementative aggiuntive
#### Funzioni
È stata data la possibilità di effettuare operazione tra funzioni anche quando queste ritornano un numero diverso di valori.
In particolare sia `f1` una funzione che restituisce `s0, s1, ..., sN` valori e `f2` una funzione che restituisce `r0, r1, ..., rM` valori.
Sia `O = min(N, M)` e sia `P = max(N, M) - O` il risultato dell'operazione sarà `s0 op r0, s1 op r1, ..., sO op rO` e i restanti
`P` valori saranno quelli corrispondenti alla funzione che torna più valori tra le due.  

È possibile effettuare una chiamata a funzione che ha come argomenti espressioni che ritornano più valori.
Ad esempio sia `f` una funzione che prende 3 parametri interi e sia `g` una funzione che restituisce 3 valori interi, è possibile 
effettuare la seguente chiamata `f(g)` e ogni tipo di combinazione simile.
#### Assegnamenti
Abbiamo dato la possibilità di assegnare ad un float un int.  

È possibile avere a destra di un assegnamento delle espressioni che tornano più valori.
#### Operazioni con stringhe
Abbiamo dato la possibilità di effettuare delle operazioni con stringhe, in particolare:
- str1 + str2: Permette di concatenare str2 a str1
- str1 - str2: Permette di togliere la prima occorrenza di str2 da str1
- str1 / str2: Permette di conoscere il numero di occorrenze di str2 in str1
- str(int) * int(str): Permette di concatenare str a se stessa valore di int volte
#### Dichiarazioni in scoping diversi
Abbiamo deciso in fase di progettazione che è possibile dichiarare una variabile all'interno di uno scope con lo stesso nome
di una funzione dichiarata in uno scope esterno. In tal caso la visibilità della funzione è sovrascritta da quella della
variabile.  
Tale scelta è stata presa tenendo come riferimento in particolare il linguaggio C e altri.
#### Gestione del null
È possibile assegnare null a una variabile di qualsiasi tipo.  
È possibile utilizzare il null solo con gli operatori `=` e `<>`.  

## Generazione del codice
Per la generazione del codice abbiamo utilizzato uno StringBuilder `generatedCode` all'interno del quale è stato inserito
codice C generato a partire dal programma Toy. Abbiamo utilizzato un altro StringBuilder `serviceCode` per poter mantenere
il codice di servizio per determinati costrutti del linguaggio Toy.  

### Gestione delle funzioni
Poiché le funzioni possono restituire più valori abbiamo deciso di dichiarare per ogni valore di ritorno di una funzione
dei puntatori con visibilità globale chiamati con questo formato: `r_<nome funzione><numero del valore di ritorno>`. Di conseguenza
ogni funzione ha come tipo di ritorno void.  
Inoltre all'inizio di ogni funzione allochiamo la memoria per i puntatori relativi ai valori di ritorno nel caso in cui non
sia già stato fatto. Alla fine della funzione i valori di ritorno vengono salvati all'interno di questi puntatori.

### Espressioni con più valori di ritorno nelle chiamate a funzione
Per ogni espressione generiamo del codice di servizio memorizzando i risultati intermedi in delle temporanee che poi 
saranno passate come argomenti della funzione.

### Operazioni tra espressioni con più valori di ritorno
Per ogni espressione generiamo del codice di servizio memorizzando i risultati intermedi in delle temporanee effettuando
le operazioni richieste tra queste.
Ad esempio sia `f` una funzione che ritorna 2 valori interi: `f() + f()` genera il seguente codice di servizio
```
f_toy();
INT t_0 = *r_f0;
INT t_1 = *r_f1;
f_toy();
INT t_2 = *r_f0;
INT t_3 = *r_f1;
INT t_4 = t_0 + t_2;
INT t_5 = t_1 + t_3;
```

### Assegnamenti multipli
Per ogni espressione viene generato il codice di servizio memorizzando i risultati intermedi in delle temporanee e infine
ogni temporanea (se necessario) viene assegnata al valore corrispondente in un singolo assegnamento.

### Problema degli identificatori usati in Toy
Per necessità alla fine di ogni indicatore dichiarato nel programma Toy appendiamo la stringa '_toy' al fine di non avere
conflitti con gli identificatori utilizzati nel codice generato in C.  
Per questo motivo è stata definita una classe `Temp` utile a distinguere le temporanee dichiarate per la generazione del codice di
servizio dagli id del linguaggio toy.

### Gestione degli if ed elif
Poiché nelle condizioni degli elif possono comparire delle espressioni complesse che necessitano di generare codice di
servizio è stato deciso di implementare gli if come segue:  
- Caso if-else:
    Semplicemente il codice di servizio relativo alla condizione dell'if viene inserito prima dell'if.
- Caso if-elif-else:
    Il codice di servizio per la condizione dell'if viene inserito prima di esso. Viene generato un if con un else e nell'else
    viene inserito il codice di servizio della condizione dell'elif il quale viene trasformato in un if-else. Nell'ultimo else 
    innestato sarà presente il corpo dell'else del codice Toy, se presente.

### Gestione del while
Poiché il while in Toy permette di avere degli statements da eseguire ad ogni iterazione abbiamo generato il codice C facendo
in modo che tali statements siano eseguiti prima dell'entrata nel ciclo e poi anche alla fine del suo corpo.  
Poiché nella condizione di un while possiamo avere un'espressione complessa che necessita di generare codice di servizio
inseriamo questo codice prima di scriverla e alla fine del ciclo per essere rivalutata ogni volta.

### Gestione degli input
Per i tipi diversi da string abbiamo semplicemente chiamato la funzione `scanf` per ogni id seguita da `getchar` per pulire il buffer
di input. Per le stringhe abbiamo scritto una funzione che ci permette di prendere stringhe di lunghezza arbitraria allocando
la memoria necessaria dinamicamente.
