# Release Checklist - QuartiereVivo

## 1) Versioning
- [ ] Update `versionCode` in `app/build.gradle.kts` with an incremented integer.
- [ ] Update `versionName` in `app/build.gradle.kts` using semantic versioning (`MAJOR.MINOR.PATCH`).
- [ ] Verify release notes match the declared version.

## 2) Quality Gate (must pass)
- [ ] `gradle ktlintCheck`
- [ ] `gradle detekt`
- [ ] `gradle testDebugUnitTest`
- [ ] `gradle connectedDebugAndroidTest`
- [ ] `gradle assembleRelease`

## 3) Firebase and environment checks
- [ ] Confirm Firebase project targets production environment.
- [ ] Confirm Firestore/Storage rules are aligned with release requirements.
- [ ] Confirm `google-services.json` belongs to the release application ID.

## 4) Signing
- [ ] Validate release keystore availability in CI secrets.
- [ ] Validate `signingConfig` is configured for release builds.
- [ ] Build signed artifact (`AAB` preferred for Play Store).
- [ ] Verify generated signature with `apksigner verify` (if APK is produced).

## 5) Changelog and communication
- [ ] Update `CHANGELOG.md` with added/changed/fixed sections.
- [ ] Add migration notes for Firebase or schema changes.
- [ ] Communicate rollout plan to committee maintainers.

## 6) Final release actions
- [ ] Create Git tag `v<versionName>`.
- [ ] Publish GitHub Release with changelog summary.
- [ ] Upload signed bundle to Play Console and complete staged rollout plan.
