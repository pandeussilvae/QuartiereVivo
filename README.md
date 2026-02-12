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
