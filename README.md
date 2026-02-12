# QuartiereVivo

Applicazione Android per il comitato di quartiere basata su Kotlin, Jetpack Compose,
Firebase e architettura MVVM.

## Notifiche push implementate

- Registrazione del token FCM lato app (`fcmTokens` su Firestore).
- Gestione messaggi push con deep-link interno verso `DettaglioSegnalazioneScreen`.
- Preferenze notifiche per categoria e zona con persistenza locale (DataStore) e
  sottoscrizione topic FCM.
- Trigger backend via Cloud Functions (`functions/index.js`) quando cambia il campo
  `status` di un documento in `segnalazioni/{segnalazioneId}`.

## Deploy Cloud Functions

```bash
cd functions
npm install
firebase deploy --only functions
```
Applicazione Android per il comitato di quartiere. Il progetto utilizza Kotlin,
Jetpack Compose e l'architettura MVVM. Le funzionalità backend sono gestite
tramite Firebase.

## Sicurezza Firebase

Sono state aggiunte regole di sicurezza per:

- Firestore: `firestore.rules`
- Firebase Storage: `storage.rules`

Documentazione completa dei casi consentiti/negati:

- `docs/security-rules.md`
## Configurazione ambiente e segreti

> Non committare mai chiavi reali o file `google-services.json` contenenti credenziali.

### 1) Firebase (`google-services.json`)

Il plugin `com.google.gms.google-services` è già attivo nel progetto. Verifica la
presenza del file di configurazione Firebase in una delle seguenti posizioni:

- `app/google-services.json` (unico file per tutti gli ambienti)
- `app/src/debug/google-services.json` e `app/src/release/google-services.json`
  (file separati per ambiente, consigliato)

Se usi file separati, il plugin seleziona automaticamente quello coerente con la
build variant (`debug`/`release`).

### 2) Google Maps API Key

La chiave Maps viene letta dal metadata Manifest
`com.google.android.geo.API_KEY` tramite placeholder `${MAPS_API_KEY}`.

Definisci i valori in `~/.gradle/gradle.properties` (oppure in un file locale non
versionato importato dal tuo setup CI/CD):

```properties
MAPS_API_KEY_DEBUG=INSERISCI_CHIAVE_DEBUG
MAPS_API_KEY_RELEASE=INSERISCI_CHIAVE_RELEASE
# opzionale fallback comune
MAPS_API_KEY=INSERISCI_CHIAVE_DI_DEFAULT
```

> Le chiavi reali non devono essere inserite nel repository.

### 3) Permessi Android

Nel Manifest sono presenti:

- `android.permission.INTERNET`
- `android.permission.ACCESS_FINE_LOCATION`
- `android.permission.ACCESS_COARSE_LOCATION`

`ACCESS_COARSE_LOCATION` è utile per supportare la posizione approssimata,
allineandosi al modello permessi Android più recente.
