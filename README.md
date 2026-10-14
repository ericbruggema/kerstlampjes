# Kerstlampjes

Android-app met kerstlampjes, interactieve kerstbomen (getekend en op foto), haarden, scènes,
eieren op speciale dagen, live wallpaper, widgets, geluid/kerstliedjes en een lampjes-effect op de muziek.
De app is beschikbaar in het Nederlands, Engels, Duits, Frans, Spaans en Chinees.

- `Kerstlampjes/` — de Android-app (Kotlin, minSdk 26, geen AndroidX)
- `website/` — statische website (index.html, verborgen eierenpagina, media, APK)
- `CREDITS.md` — bronnen en licenties van de gebruikte foto's (Wikimedia Commons)
- `.github/workflows/android.yml` — CI: unit tests + debug- en release-build

## Bouwen

```
cd Kerstlampjes
./gradlew testDebugUnitTest assembleDebug
```

### Release (ondertekend)

1. Maak een keystore: `keytool -genkeypair -keystore release.keystore -alias kerstlampjes -keyalg RSA -keysize 2048 -validity 10000`
2. Maak `Kerstlampjes/keystore.properties` (staat in `.gitignore`):

   ```
   storeFile=release.keystore
   storePassword=...
   keyAlias=kerstlampjes
   keyPassword=...
   ```
3. `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk`

Bewaar de keystore goed: zonder dezelfde sleutel kun je een geïnstalleerde app niet meer bijwerken.

## Vertalingen

Teksten staan als Nederlandse sleutel in de code (`Tr.t("…")`); de vertalingen staan in
`Translations.kt`. De test `TranslationsTest` controleert dat alles in alle talen is vertaald.
