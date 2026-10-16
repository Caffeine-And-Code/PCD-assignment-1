# Documentazione

## Analisi

### Il problema

Il sistema simula un biliardo 2D con migliaia di palline piccole e due palline grandi (umano e bot), con attrito,
rimbalzi e urti elastici. Il punteggio cresce quando un giocatore manda una pallina piccola in buca; la partita termina
quando non restano palline o quando la pallina di un giocatore finisce in buca.

### Attività concorrenti

- **Fisica**: aggiornamento di posizione e velocità di migliaia di palline a ogni passo. L'integrazione del moto è
  data-parallel, mentre il rilevamento delle collisioni (O (N²) in versione naïve) è il collo di bottiglia.
- **Giocatore umano**: input asincrono da tastiera (thread della GUI) che modifica la velocità della sua pallina.
- **Bot**: attività autonoma che applica impulsi alla propria pallina a intervalli propri.
- **GUI**: rendering periodico, separato dal calcolo per restare reattivi.

### Aspetti critici

- **Stato condiviso**: velocità delle palline dei giocatori, punteggi, flag di fine partita e lista delle palline in
  gioco sono letti e scritti da più thread, quindi servono accessi protetti.
- **Collisioni**: un urto coinvolge due palline, quindi worker diversi possono scrivere sulla stessa pallina (lost
  update).
- **Sincronizzazione dei passi**: le fasi di ogni passo hanno un ordine preciso e i worker devono attendersi a fine
  fase.
- **Vista**: la GUI deve leggere uno snapshot coerente senza bloccare il motore.
- **Terminazione**: la fine partita può essere rilevata da attività diverse e richiede uno stop coordinato, senza
  deadlock né thread orfani.

### Soluzione proposta

**Architettura.** MVC, con il **Board** come Model e monitor: custodisce palline, punteggi e stato di gioco ed espone
operazioni atomiche (applicazione di impulsi, snapshot per la View, aggiornamento punteggio). Gli impulsi di umano e bot
vengono accodati nel Board e applicati a inizio passo, così non interferiscono con le fasi di calcolo.

**Partizione del lavoro.** Con *n* worker, il worker *w* gestisce le palline con `id % n == w` (round-robin). Esempio:
con 2 worker e 4 palline, il worker 1 gestisce le palline 0 e 2, il worker 2 le palline 1 e 3.

**Collisioni senza check doppi.** Ogni worker controlla solo le coppie (i, j) con `j > i`, dove i è una sua pallina,
quindi ogni coppia è visitata una sola volta.

**Fasi di ogni passo**, separate da barriera:

1. integrazione del moto (posizione, attrito, rimbalzi sui bordi);
2. rilevamento collisioni: i worker *leggono soltanto* e registrano le collisioni trovate;
3. applicazione delle collisioni;
4. rimozione delle palline in buca e aggiornamento di punteggi e stato di gioco.

### Argomentazioni

- **Round-robin vs blocchi contigui.** Il vincolo `j > i` sbilancia il carico: la pallina 0 controlla N-1 coppie,
  l'ultima nessuna. Con blocchi contigui il primo worker sarebbe molto più carico. Il round-robin mescola id alti e
  bassi e bilancia il lavoro, massimizzando l'uso dei core.
- **Vincolo `j > i`.** Dimezza i controlli (N (N-1)/2 coppie invece di N (N-1)) senza richiedere coordinamento tra i
  worker per decidere chi tratta una coppia.
- **Rilevamento e applicazione separati.** Il vincolo `j > i` evita i check doppi ma non il conflitto di scrittura: il
  worker che gestisce `i` modifica anche `j`, che appartiene a un altro worker. Separare le fasi elimina il problema
  senza lock fine-grained, evitando deadlock e over-synchronization.
- **Numero di worker legato ai core** (≈ `Runtime.availableProcessors()`), invece di un thread per pallina, per evitare
  l'overhead di migliaia di thread.
- **Barriera come monitor proprio**, coerente con il requisito di usare costrutti ad alto livello con implementazione
  personale.
- **Limite noto.** Il rilevamento resta O (N²). Una griglia spaziale ridurrebbe i controlli alle sole palline vicine e
  può essere proposta come ottimizzazione da confrontare nei test.

## Architettura

Il progetto è organizzato secondo il pattern **Model-View-Controller (MVC)**, mantenendo separati la logica di gioco, la
gestione degli input e la rappresentazione grafica. Questa struttura viene mantenuta in tutte e tre le versioni, che si
differenziano per le modalità di gestione della concorrenza.

Nella **versione
0 ([vsynchronized](https://github.com/Caffeine-And-Code/PCD-assignment-1/tree/d7be7ddd448c2c60d066f1e16673c332c3f10349/src/pcd/poool/vsynchronized))**,
l'architettura è suddivisa nei seguenti componenti:

- **Model (`model`)**: contiene le entità del gioco e la logica fisica. La classe `Board` rappresenta lo stato condiviso
  e agisce da monitor tramite un `ReentrantLock`, garantendo l'accesso mutuamente esclusivo durante gli aggiornamenti
  della simulazione, l'applicazione degli impulsi e la creazione degli snapshot.
- **View (`view`)**: gestisce la rappresentazione grafica tramite Swing. La classe `View` implementa `BoardObserver` e
  riceve le notifiche di aggiornamento dal Model. Il `ViewModel` mantiene una copia dei dati necessari al rendering,
  ottenuta attraverso gli snapshot del `Board`.
- **Controller (`controller`)**: gestisce gli input e l'esecuzione della simulazione. `PooolController` riceve i comandi
  della tastiera attraverso un buffer limitato thread-safe, mentre `PhysicsLoop` esegue gli aggiornamenti fisici su un
  thread dedicato.

La versione sincrona costituisce la base di riferimento: il calcolo fisico viene eseguito sequenzialmente da un unico
thread, mentre la gestione dei comandi e la GUI operano su thread separati. La sincronizzazione degli accessi allo stato
condiviso è centralizzata nel `Board`.

Le altre due versioni mantengono la stessa organizzazione MVC, introducendo differenti strategie per parallelizzare il
calcolo:

- **Versione
  1 ([vnativethreads](https://github.com/Caffeine-And-Code/PCD-assignment-1/tree/d7be7ddd448c2c60d066f1e16673c332c3f10349/src/pcd/poool/vnativethreads))**:
  utilizza un numero fisso di thread di piattaforma per distribuire il lavoro, coordinati mediante una barriera tra le
  fasi della simulazione.
- **Versione
  2 ([vjavaexecutorframework](https://github.com/Caffeine-And-Code/PCD-assignment-1/tree/d7be7ddd448c2c60d066f1e16673c332c3f10349/src/pcd/poool/vjavaexecutorframework))**:
  utilizza l'Executor Framework per eseguire le fasi di calcolo come task su un pool di thread, attendendone il
  completamento tramite `Future`.

Questa organizzazione permette di confrontare le diverse strategie di esecuzione mantenendo una separazione coerente tra
le responsabilità dei componenti.

## Performance Tests With Results

## Model-Checking

