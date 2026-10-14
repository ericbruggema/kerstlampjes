# Kerstlampjes 🎄

Android-app die een snoer geanimeerde kerstlampjes langs de rand van je scherm tekent, boven alle apps. Tikken gaan gewoon door naar de app eronder.

## Functies
- Overlay boven alle apps (ook over statusbalk en notch)
- 5 effecten: vast, fonkelen, om-en-om knipperen, lopend licht, kleurgolf
- 5 kleurenschema's: klassiek bont, warm wit, rood/groen/goud, ijsblauw/wit, roze/paars
- Instelbaar: snelheid, grootte, afstand, doorhang draad, helderheid, hoekafronding, randafstand
- Live voorbeeld in de app
- Snelle-instellingen-tegel om aan/uit te zetten
- Melding met "Uitzetten"-knop
- Animatie pauzeert als het scherm uit staat (accu)

## Bouwen
1. Open de map in Android Studio (Ladybug of nieuwer).
2. Laat Gradle synchroniseren.
3. Run op je telefoon, of **Build → Build APK(s)**.

Vereist Android 8.0+ (API 26). Getest op target API 35.

## Eerste keer
1. Open de app → **Toestemming geven** → zet "Weergeven boven andere apps" aan.
2. Terug in de app → **Lampjes aanzetten**.
3. Stel **Afronding hoeken** in zodat het snoer je schermhoeken volgt.

## Opmerking Android 12+
Android blokkeert aanrakingen door overlays die te ondoorzichtig zijn. Daarom staat de venster-dekking daar op maximaal 80%; anders zou je telefoon onbedienbaar worden zolang de lampjes branden.
