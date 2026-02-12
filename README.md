# QuartiereVivo

Applicazione Android per il comitato di quartiere. Il progetto utilizza Kotlin,
Jetpack Compose e l'architettura MVVM. Le funzionalità backend saranno gestite
tramite Firebase.

## Sicurezza Firebase

Sono state aggiunte regole di sicurezza per:

- Firestore: `firestore.rules`
- Firebase Storage: `storage.rules`

Documentazione completa dei casi consentiti/negati:

- `docs/security-rules.md`
