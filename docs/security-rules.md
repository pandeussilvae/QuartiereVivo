# Regole di sicurezza Firebase (Firestore + Storage)

Questo documento descrive le regole di sicurezza implementate nei file:

- `firestore.rules`
- `storage.rules`

## Obiettivi

1. Accesso consentito solo a utenti autenticati.
2. Protezione dei dati per ownership (`creatoreId == request.auth.uid`).
3. Supporto ruoli **admin** e **moderator**.
4. Aggiornamento dello stato delle segnalazioni consentito solo ai moderatori/admin.
5. Schema unico della collezione `segnalazioni` allineato tra app, Cloud Functions e security rules.

## Schema finale `segnalazioni/{segnalazioneId}`

| Campo | Tipo | Obbligatorio | Note |
|---|---|---:|---|
| `titolo` | `string` | ✅ | Titolo segnalazione |
| `descrizione` | `string` | ✅ | Descrizione segnalazione |
| `categoria` | `string` | ✅ | Categoria utente |
| `latitudine` | `number` | ✅ | Coordinate geografiche (naming canonico) |
| `longitudine` | `number` | ✅ | Coordinate geografiche (naming canonico) |
| `immagineUrl` | `string \| null` | ❌ | URL pubblico immagine |
| `creatoreId` | `string` | ✅ | UID autore |
| `stato` | `string` | ✅ | Solo valori canonicali: `nuova`, `in_carico`, `risolta` |
| `createdAt` | `timestamp` | ✅ | Server timestamp creazione |
| `updatedAt` | `timestamp` | ✅ | Server timestamp ultimo update |
| `updatedBy` | `string` | ✅ | UID ultimo autore modifica |

## Modello ruoli

Le regole supportano due modalità in parallelo:

- **Custom Claims** sul token Firebase Auth:
  - `admin: true`
  - `moderator: true`
- **Collezione Firestore `roles/{uid}`** con documento:
  - `{ "role": "admin" }`
  - `{ "role": "moderator" }`

> In `firestore.rules` è supportata sia la lettura da claim sia da collezione `roles`.
> In `storage.rules` è usata la strada a custom claims (più efficiente per Storage Rules).

## Firestore: regole principali

### Collezione `users/{userId}`

- **read / write**: consentito al proprietario (`userId == auth.uid`) o ad admin.

### Collezione `roles/{userId}`

- **read**: consentito al proprietario o ad admin.
- **create/update/delete**: solo admin.

### Collezione `segnalazioni/{segnalazioneId}`

- **read**: qualsiasi utente autenticato.
- **create**:
  - utente autenticato;
  - schema validato (allow-list campi + campi obbligatori + controllo tipi);
  - `creatoreId == auth.uid`;
  - `updatedBy == auth.uid`;
  - `stato` iniziale obbligatoriamente `nuova`.
- **update**:
  - proprietario: può modificare contenuti ma **non** `stato` e **non** `createdAt`;
  - moderatore/admin: può aggiornare solo `stato` (con `updatedBy` / `updatedAt`) lasciando invariati i campi core.
- **delete**: proprietario o admin.

## Storage: regole principali

### Path `reports/{reportId}/{fileName}`

- **read/write**: proprietario della segnalazione o admin/moderator.
- **delete**: proprietario o admin.

L'ownership è verificata leggendo `reports/{reportId}.ownerId` da Firestore.

### Path `users/{userId}/**`

- **read/write**: proprietario o admin.

## Casi consentiti / negati

### Firestore

| Caso | Esito |
|---|---|
| Utente non autenticato legge `segnalazioni` | ❌ Negato |
| Utente autenticato crea `segnalazioni` con `creatoreId` diverso dal suo UID | ❌ Negato |
| Owner aggiorna `titolo`/`descrizione` senza toccare `stato` | ✅ Consentito |
| Owner prova a impostare `stato = risolta` | ❌ Negato |
| Moderator aggiorna solo `stato` e `updatedBy` | ✅ Consentito |
| Utente normale aggiorna `stato` | ❌ Negato |
| Admin crea/aggiorna `roles/{uid}` | ✅ Consentito |
| Utente normale crea/aggiorna `roles/{uid}` | ❌ Negato |

## Note operative

- Per coerenza applicativa, aggiornare sempre `updatedBy` e `updatedAt` ad ogni modifica.
- Lo schema applicativo è centralizzato anche in `domain/model/Segnalazione.kt` (`SegnalazioneSchema`).
