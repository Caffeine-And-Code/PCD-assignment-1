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

## Le due versioni

- **Versione 0 (versione sincrona)**: un thread per la grafica (JavaFx Thread) ed un thread per la logica quale update e
  collisioni
- **Versione 1 (thread di piattaforma)**: *n* worker fissi coordinati dalla barriera, più thread separati per bot e
  simulazione.
- **Versione 2 (Executor Framework)**: ogni fase diventa *n* task (uno per resto `w`) su un pool a dimensione fissa, con
  attesa dei `Future` al posto della barriera; il bot è un task periodico.

## Architecture

## Performance Tests With Results

## Model-Checking

