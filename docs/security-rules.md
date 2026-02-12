# Regole di sicurezza Firebase (Firestore + Storage)

Questo documento descrive le regole di sicurezza implementate nei file:

- `firestore.rules`
- `storage.rules`

## Obiettivi

1. Accesso consentito solo a utenti autenticati.
2. Protezione dei dati per ownership (`ownerId == request.auth.uid`).
3. Supporto ruoli **admin** e **moderator**.
4. Aggiornamento dello stato delle segnalazioni consentito solo ai moderatori/admin.

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

### Collezione `reports/{reportId}`

- **read**: qualsiasi utente autenticato.
- **create**:
  - utente autenticato;
  - `ownerId == auth.uid`;
  - campi consentiti limitati da allow-list;
  - `status` iniziale solo `open` o `pending`;
  - `updatedBy == auth.uid`.
- **update**:
  - proprietario: può modificare contenuti ma **non** `status`;
  - moderatore/admin: può aggiornare solo `status` (e `updatedBy` / eventuale `moderationNote`) lasciando invariati i campi core.
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
| Utente non autenticato legge `reports` | ❌ Negato |
| Utente autenticato crea `reports` con `ownerId` diverso dal suo UID | ❌ Negato |
| Owner aggiorna `title`/`description` senza toccare `status` | ✅ Consentito |
| Owner prova a impostare `status = resolved` | ❌ Negato |
| Moderator aggiorna solo `status` e `updatedBy` | ✅ Consentito |
| Utente normale aggiorna `status` | ❌ Negato |
| Admin crea/aggiorna `roles/{uid}` | ✅ Consentito |
| Utente normale crea/aggiorna `roles/{uid}` | ❌ Negato |

### Storage

| Caso | Esito |
|---|---|
| Utente autenticato owner carica file su `reports/{reportId}/...` | ✅ Consentito |
| Utente autenticato non owner carica file su report altrui | ❌ Negato |
| Moderator legge/allega file su report | ✅ Consentito |
| Utente non autenticato accede a Storage | ❌ Negato |

## Note operative

- Le regole assumono che il documento `reports/{reportId}` esista prima del caricamento file in Storage.
- Se si usa la collezione `roles`, prevedere una Cloud Function / pannello admin per la gestione sicura dei ruoli.
- Per coerenza applicativa, aggiornare sempre `updatedBy` e `updatedAt` ad ogni modifica.
