<div align="center">

# 🎄 Weihnachtslichter (Kerstlampjes)

**Eine Lichterkette am Rand deines Bildschirms, auch über deinem Startbildschirm, deinen Icons und Widgets. Dazu als Bonus: eigene Weihnachtsbäume, Kamine, Szenen und ein Editor für deine eigene Szene. Für Android.**

[⬇️ APK herunterladen](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.4)

🌍 **Lies dies in:** [Nederlands](README.md) · [English](README.en.md) · **Deutsch** · [Français](README.fr.md) · [Español](README.es.md) · [中文](README.zh.md)

💻 **Neu: Weihnachtslichter für Windows!** Nur die Lichter, immer sichtbar im Infobereich. [⬇️ Download (100 kB)](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.2/Kerstlampjes-Windows.exe) · [Mehr Infos](#-windows)

</div>

> 📷 Diese Übersetzung enthält keine Screenshots, damit sie kurz bleibt. Alle Bilder findest du in der [niederländischen README](README.md) und auf der Website. Die App selbst gibt es in allen sechs Sprachen.
> Die Menünamen unten stehen so, wie sie erscheinen, wenn die App auf Deutsch eingestellt ist; die App folgt der Sprache deines Telefons (siehe [Sprachen](#-sprachen)).

---

## Inhalt

1. Was ist das?
2. Installation
3. Erste Benutzung
4. Funktionen
5. Dekoration rund um deinen Rand
5. Windows-Version
5. Bedienung
6. Sprachen
7. Berechtigungen und Datenschutz
8. Akku und Leistung
9. Häufige Fragen
10. Aus dem Quellcode bauen
11. Projektstruktur
12. Technik
13. Tests und CI
14. Quellen und Lizenzen

---

## 🎅 Was ist das?

Weihnachtslichter ist eine Android-App, die dein Handy in eine Weihnachtsdekoration verwandelt. **Die Hauptsache sind die Lichter
im Hintergrund deines Handys**: eine Lichterkette um den Rand deines Bildschirms, die auch über deinem Startbildschirm bleibt.
Alles andere ist Bonus.

**Hauptsache**

- eine **Lichterkette am Bildschirmrand**, die auch über deinem Startbildschirm, deinen Icons, Widgets und anderen Apps bleibt, mit 5 Effekten, 5 Farbschemata, Ein/Aus über eine Schnelleinstellungs-Kachel, Rückkehr nach einem Neustart und (einstellbar) Reaktion auf Geräusche;

**Bonus**

- **19 Weihnachtsbäume** (14 gezeichnet, 5 als Foto) plus dein **eigenes Foto**, in das du selbst Lichter hängst;
- **12 Kamine** mit echtem Partikelfeuer oder Fotos eines knisternden Kaminfeuers;
- **9 interaktive Szenen**: Schneekugel, Adventskalender, Adventskranz, Weihnachtsdorf mit Zug, Schlitten, Schneemann, Eisbahn, Krippe und Geschenke;
- ein **Szenen-Editor**: eigener Hintergrund (auch dein eigenes Foto), Feuer in deiner Farbe, Schnee, Feuerwerk, Sticker mit eigenen Animationen, selbst gezeichnete Lichterketten und Teilen mit anderen;
- **8 Lichtarten**, **3 Widgets**, **Live-Hintergrund**, **4 Weihnachtslieder**, ein Schlaf-Timer und Lieblings-Looks;
- Überraschungen an **besonderen Tagen** (Nikolaus, Heiligabend, Silvester, Dreikönig …);
- **6 Sprachen**: Niederländisch, Englisch, Deutsch, Französisch, Spanisch und Chinesisch.

Alles wird in der App selbst gezeichnet und synthetisiert (Canvas, keine Game-Engine, kein AndroidX). Die einzigen
Bilder sind die Fotos echter Bäume und Kamine von Wikimedia Commons ([Quellen](#-quellen-und-lizenzen)).
Es gibt **keine Werbung und keine Konten**. Das Internet wird nur benutzt, wenn du selbst auf *Nach Updates suchen* tippst.

| | |
|---|---|
| Paketname | `nl.kerstlampjes.app` |
| Version | 2.4 (versionCode 6) |
| Android | 8.0 (API 26) oder neuer |
| Sprache | Kotlin |
| Größe | etwa 3 MB |

---

## 📲 Installation

### Option 1: die APK von GitHub (empfohlen)

1. Öffne auf deinem Handy das [Release v2.1](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.4).
2. Lade **`Kerstlampjes-2.4.apk`** herunter.
3. Öffne die heruntergeladene Datei. Fragt Android nach der Erlaubnis, *Apps aus unbekannten Quellen zu installieren*, erlaube das für deinen Browser oder Dateimanager.
4. Tippe auf **Installieren** und danach auf **Öffnen**.

> **Eine ältere Version aktualisieren?** Das Release ist mit einem eigenen Schlüssel signiert. Ist auf deinem Handy eine Version
> mit anderer Signatur installiert (zum Beispiel ein Debug-Build), deinstalliere sie zuerst. Deine
> Einstellungen gehen dabei verloren.

### Option 2: die APK im Repository

Dieselbe signierte APK liegt auch im Repo: [`website/Kerstlampjes.apk`](website/Kerstlampjes.apk).

### Option 3: selbst bauen

Siehe [Aus dem Quellcode bauen](#-aus-dem-quellcode-bauen).

---

## 🚀 Erste Benutzung

1. **Öffne die App.** Du siehst die Einstellungen der Randlichter: Effekt, Farben, Geschwindigkeit, Helligkeit, Aussehen und Passform.
2. **Tippe auf „Lichter einschalten“.** Die App fragt einmalig nach der Erlaubnis, *über anderen Apps zu zeichnen*. Schalte sie ein.
   Gehe dann zurück: Die Lichter laufen jetzt am Bildschirmrand entlang, auch wenn du zum Startbildschirm gehst.
3. **Tippe auf „🎄 Baue deinen Weihnachtsbaum“**, um die Ansicht für Bäume, Kamine und Szenen zu öffnen. Wische nach links oder rechts, um zwischen Bäumen, Kaminen und Szenen zu wechseln.
4. **Tippe auf den Baum**, um ein Licht aufzuhängen. Tippe auf ein Licht, um die Farbe zu ändern. Halte ein Licht gedrückt, um es zu entfernen.
5. **Öffne das ⋯-Menü** für Ton, Musik, Widgets, Favoriten, Schlaf-Timer, Sprache und mehr.

---

## ✨ Funktionen

### 💡 Lichter um den Bildschirmrand

Eine animierte Lichterkette folgt dem Rand und den abgerundeten Ecken deines Bildschirms. Berührungen gehen
einfach an die App darunter weiter, die Kette stört also nicht.

| Einstellung | Möglichkeiten |
|---|---|
| **Effekt** | Dauerlicht, Funkeln, abwechselndes Blinken, Lauflicht, Farbwelle und *Zur Musik* |
| **Farben** | 5 Farbschemata |
| **Geschwindigkeit** | Langsam bis schnell |
| **Helligkeit** | 30 bis 100 % |
| **Aussehen** | Größe der Lichter, Abstand dazwischen und Durchhang des Drahts |
| **Passform** | Rundung der Ecken und Abstand zum Rand, damit es zu deinem Bildschirm passt |

- **Über allem.** Die Lichter liegen über deinem Startbildschirm, deinen Icons, deinen Widgets und anderen Apps.
- **Ein und aus.** Über die Schaltfläche in der App, über das ⋯-Menü (*Lichter um den Bildschirm*) oder über die **Schnelleinstellungs-Kachel** im Benachrichtigungsbereich.
- **Nach einem Neustart.** Waren die Lichter an, als du das Handy ausgeschaltet hast, kommen sie von selbst zurück. Voraussetzung ist, dass die Erlaubnis zum Zeichnen über anderen Apps noch aktiv ist.
- **Zum Mikrofon.** Schalte im ⋯-Menü *Randlichter reagieren auf Geräusche* ein (oder wähle den Effekt *Zur Musik*). Je lauter das Geräusch, desto mehr Lichter leuchten auf. In der Stille glimmen sie leise. Die App fragt dann nach der Mikrofon-Berechtigung. Siehe [Berechtigungen und Datenschutz](#-berechtigungen-und-datenschutz).
- **Vordergrund-Benachrichtigung.** Android zeigt eine Benachrichtigung, solange die Lichter an sind. Android verlangt das von Apps, die im Hintergrund laufen.

### 🌲 Weihnachtsbäume

19 Bäume zum Durchwischen, jeder mit eigenem Schmuck, plus dein eigenes Foto.

- **14 gezeichnete Bäume:** von klassisch und Tanne bis Neon, Eis und Cartoon-3D.
- **5 echte Weihnachtsbäume auf Foto:** Kapitol, Baum im Zimmer, Fichte im Park, Stadtbaum und Lichterbaum.
- **Dein eigenes Foto:** Wähle ein Foto deines eigenen Baums oder Zimmers und hänge Lichter darüber. Das funktioniert auch als Hintergrund.
- **Jeder Baum merkt sich seine eigenen Lichter.** Wechselst du zu einem anderen Baum und zurück, hängen sie noch an derselben Stelle.
- **Lichtart pro Baum** oder eine Mischung aller Arten.

### 🔥 Kamine

12 Kamine mit echtem Feuer. Die 7 gezeichneten Kamine verwenden Hunderte glühende Partikel. Die 5 Foto-Kamine
verwenden ein Foto mit bewegtem Feuer darüber. Tippe auf das Feuer, und es lodert auf. Deine eigene
Lichterkette hängt am Kaminsims oder zwischen den Pfosten.

### ✨ Interaktive Szenen

Nicht nur zuschauen: tippen, schütteln und bauen.

| Szene | Was tust du? |
|---|---|
| **Schneekugel** | Schüttle dein Handy oder tippe: Es beginnt zu schneien. |
| **Adventskalender** | Jeden Tag ein Türchen mit einer Überraschung. |
| **Adventskranz** | Zünde die Kerzen an, Sonntag für Sonntag. |
| **Weihnachtsdorf mit Zug** | Tippe auf Häuschen für Licht, tippe auf den Zug für die Hupe. Halte die Schiene gedrückt, damit der Zug anhält. Tippe auf den Himmel für ein Sternchen. |
| **Schlitten des Weihnachtsmanns** | Tippe auf den Schlitten, und es regnet Geschenke. Halte lange gedrückt für eine Turbo-Fahrt. |
| **Schneemann bauen** | Tippe, und er erscheint Schritt für Schritt. |
| **Eisbahn** | Tippe aufs Eis für einen neuen Schlittschuhläufer. Die Läufer drehen Kreise umeinander. Tippe auf einen Läufer, und er fällt und rutscht vom Eis. |
| **Krippe** | Tippe auf den Sternenhimmel für einen Wunsch. Tippe auf Maria, Josef, die Könige, den Ochsen oder den Esel, und sie sagen etwas. |
| **Geschenke** | Eigene Botschaften, die erst an einem gewählten Tag geöffnet werden dürfen. |

### 🎨 Eigene Szene erstellen

Du baust deine eigene Weihnachtsszene genau so, wie du sie willst. Öffne **⋯ → 🎨 Eigene Szene erstellen**. Oben
siehst du genau, was die Szene später zeigt, unten fünf Registerkarten.

| Registerkarte | Was kannst du tun? |
|---|---|
| 🖼️ **Hintergrund** | Wähle aus acht Farbverläufen oder nimm dein **eigenes Foto**. Schalte einen Schneeboden ein oder aus. |
| ✨ **Effekte** | Schnee, fallende Dinge (Geschenke, Sterne, Herzen …), Konfetti, funkelnde Sterne, Feuerwerk, Sternschnuppen, ein sanftes Glühen und **Feuer**. Jeder Effekt hat eigene Regler (Menge, Geschwindigkeit, Größe) und eine Farbe. |
| 🎅 **Sticker** | 32 Bilder (Weihnachtsbaum, Weihnachtsmann, Schneemann, Schlitten, Zug …). Ziehe sie, mache sie größer oder kleiner und drehe sie. |
| 💡 **Lichter** | **Zeichne mit dem Finger eine Lichterkette** an einem Fenster, Dach oder Baum entlang, oder wähle *Oben*, *Bogen* oder *Rundherum*. Wähle pro Kette die Lichtart, die Farben und den Effekt. |
| 💾 **Mehr** | Name, neue Szene, duplizieren, alle deine Szenen, **teilen**, einfügen und löschen. |

**Feuer in deiner Farbe.** Setze das Feuer, wohin du willst, und wähle Orange, Blau, Grün, Lila oder Weiß. Stelle Höhe,
Breite und Geschwindigkeit ein. Tippe in der Szene auf das Feuer, und es lodert auf.

**Eigene Animationen.** Jeder Sticker hat eine Animation und eine Reaktion auf einen Tipp:

| Animation | Reaktion auf einen Tipp |
|---|---|
| Still, Schweben, Winken, Pulsieren, Blinken, Hüpfen, Drehen, Vorbeifahren, Kreisen | Nichts, Hüpfen, Funken, Drehen, Glöckchen oder **etwas sagen** (eine Sprechblase mit deinem eigenen Text) |

**Speichern und verwenden.** Alles wird automatisch gespeichert. Deine Szene findest du als Szene **Meine Szene** (die
letzte in der Reihe der Szenen). Du kannst sie als **Live-Hintergrund** einstellen und in deinen Favoriten speichern. Du kannst
mehrere Szenen aufbewahren und über *Mehr → Meine Szenen* zwischen ihnen wechseln.

**Teilen.** Wähle *Mehr → Teilen* und sende den Text per WhatsApp, E-Mail oder eine andere App. Der Empfänger kopiert den
Text und wählt im Editor *Mehr → Einfügen*. Ein eigenes Foto als Hintergrund wird nicht mitgeschickt.

### 💡 Lichtarten

Wähle unter dem Baum aus acht Arten oder eine **Mischung** aus allem. Du kannst die Auswahl pro Baum oder Kamin speichern
oder für alles gelten lassen, einschließlich deines Hintergrunds.

Klassisch · Mini-LEDs · Glaskugeln · Flämmchen · Sternchen · Herzen · Eiszapfen · Schneeflocken

### 📱 Widgets

Drei Widgets für deinen Startbildschirm. Halte eine leere Stelle auf deinem Startbildschirm gedrückt, wähle *Widgets* und suche *Weihnachtslichter*.

| Widget | Was siehst du? |
|---|---|
| **Countdown bis Weihnachten** | Die Anzahl der Tage bis Weihnachten, an den Feiertagen selbst mit einem Text. |
| **Szene von heute** | Die Szene, die zum heutigen Datum passt (Advent, Krippe, Geschenke …). Tippe, um die App mit dieser Szene zu öffnen. |
| **Weihnachtsbaum mit Lichtern** | Dein eigener Baum mit deinen Lichtern, mit abgerundeten Ecken. Tippe, um die App zu öffnen. |

Das Baum-Widget aktualisiert sich nur, wenn sich dein Baum wirklich verändert hat, damit es deinen Akku nicht belastet.

### 🖼️ Live-Hintergrund

Wähle in der App einen Baum, Kamin oder eine Szene und stelle sie als **Live-Hintergrund** ein. Jedes Mal, wenn du zu deinem
Startbildschirm gehst, siehst du zum Beispiel das Feuer oder den fahrenden Zug, mit deinen Lichtern darauf.

1. Wähle in der App den gewünschten Baum, Kamin oder die Szene.
2. Tippe auf die Schaltfläche **Hintergrund** unter dem Baum.
3. Bestätige auf dem Android-Bildschirm.

### 🔊 Ton, Musik und Mikrofon

Der gesamte Ton wird **in der App selbst synthetisiert**. Es gibt keine Audiodateien.

- **Kaminfeuer:** knisterndes Feuer bei den Kaminen.
- **Glöckchen** bei einem Tipp und eine **Spieluhr-Melodie** in den Szenen.
- **Vier Weihnachtslieder:** *Jingle Bells*, *Stille Nacht*, *We wish you a merry Christmas* und *Deck the halls*, jeweils mit eigenem Tempo, Spieluhr-Klang und leichtem Echo. Wähle das Lied über *Musik* im ⋯-Menü.
- **Zur Musik:** ein Lichter-Effekt, bei dem sich die Lichter mit dem Klang bewegen. Er folgt sowohl den eingebauten Liedern als auch dem Mikrofon.

### ⋯ Das ⋯-Menü

| Eintrag | Was macht er? |
|---|---|
| 💡 **Lichter um den Bildschirm** | Schaltet die Randlichter ein oder aus. |
| 🎤 **Randlichter reagieren auf Geräusche** | Die Randlichter bewegen sich zum Ton des Mikrofons. |
| 🔊 **Ton** | Kaminfeuer, Glöckchen und Melodien ein oder aus. |
| 🎵 **Musik** | Wähle eines der vier Weihnachtslieder. |
| 📅 **Automatisch zur Szene des Tages** | In der Saison öffnet die App die Szene von heute. |
| 🔔 **Tägliche Benachrichtigung** | Jeden Tag um 18:00 Uhr eine Benachrichtigung mit dem Countdown. |
| 🎁 **Zur Szene von heute** | Direkt zur Szene, die zum Datum passt. |
| ⭐ **Lieblings-Looks** | Speichere Baum oder Kamin mit Lichtern, Effekt und Lichtart unter einem eigenen Namen. |
| 😴 **Schlaf-Timer** | 15 Minuten bis 2 Stunden: Die Lichter dimmen ruhig und gehen aus. |
| 🔋 **Bildrate** | Normal (30 fps), sparsam (15 fps) oder automatisch. |
| 🖼️ **Eigenes Foto** | Verwende ein eigenes Foto als Hintergrund. |
| 🔄 **Nach Updates suchen** | Sucht auf GitHub nach einer neueren Version und öffnet die Download-Seite. |
| 🌍 **Sprache** | Automatisch oder wähle selbst eine der sechs Sprachen. |

### 📅 Besondere Tage

- **Szene des Tages:** Vom 15. November bis zum 6. Januar öffnen die App, das Widget und die Benachrichtigung die Szene, die zu heute passt: Weihnachtsdorf im November, der Adventskranz an den Adventssonntagen, der Adventskalender im Dezember, Krippe und Geschenke rund um Weihnachten.
- **Countdown bis Weihnachten:** oben auf dem Baum-Bildschirm und im Widget.
- **Überraschungen:** An einigen besonderen Tagen passiert etwas in der App (Nikolaus, Heiligabend, erster und zweiter Weihnachtstag, Silvester, Neujahr und Dreikönig). Komm an einem besonderen Tag noch einmal wieder. 👀

---

## 👆 Bedienung

| Geste | Wo | Wirkung |
|---|---|---|
| Wischen links/rechts | Baum, Kamin oder Szene | Nächste oder vorherige |
| Tippen | Baum oder Kamin | Licht aufhängen |
| Auf ein Licht tippen | Baum oder Kamin | Andere Farbe |
| Ein Licht lange drücken | Baum oder Kamin | Licht entfernen |
| Tippen | Feuer | Das Feuer lodert auf |
| Schütteln | Schneekugel | Es schneit |
| Tippen | Dorf: Häuschen | Licht an oder aus |
| Tippen | Dorf: Himmel | Sternchen |
| Schiene lange drücken | Dorf | Zug hält an oder fährt wieder |
| Tippen / lange drücken | Schlitten | Geschenke / Turbo |
| Tippen | Eisbahn | Neuer Läufer oder ein Läufer fällt |
| Auf eine Figur tippen | Krippe | Die Figur sagt etwas |

---

## 🌍 Sprachen

| Sprache | Code |
|---|---|
| Nederlands | `nl` |
| English | `en` |
| Deutsch | `de` |
| Français | `fr` |
| Español | `es` |
| 中文 | `zh` |

Die App folgt standardmäßig der Sprache deines Telefons. Wählst du über **⋯ → Sprache** selbst eine Sprache, gilt sie für die
ganze App, die Widgets und die Benachrichtigung. Ist dein Telefon auf eine andere Sprache eingestellt, ist die App auf Englisch.

---

## 🔐 Berechtigungen und Datenschutz

Die App nutzt das Internet **nur**, wenn du im ⋯-Menü auf *Nach Updates suchen* tippst. Dann wird bei GitHub der Name der neuesten Version abgefragt und nichts über dich gesendet. Sonst sendet die App nichts. Alles bleibt auf deinem Handy.

| Berechtigung | Wofür | Erforderlich? |
|---|---|---|
| Über anderen Apps zeichnen (`SYSTEM_ALERT_WINDOW`) | Die Randlichter über Startbildschirm und anderen Apps | Nur für die Randlichter |
| Vordergrunddienst (`FOREGROUND_SERVICE`, `…_SPECIAL_USE`) | Die Randlichter im Hintergrund laufen lassen | Nur für die Randlichter |
| Mikrofon (`RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE`) | Lichter, die auf Geräusche reagieren | Nein, nur wenn du diesen Effekt wählst |
| Internet (`INTERNET`) | Nur die Update-Prüfung im ⋯-Menü | Nein, nur wenn du darauf tippst |
| Nutzungsdaten (`PACKAGE_USAGE_STATS`) | Nur für die Option „Nur auf dem Startbildschirm“: erkennen, welche App vorne ist | Ja, du erlaubst es in den Systemeinstellungen; nichts wird gespeichert oder gesendet |
| Benachrichtigungen (`POST_NOTIFICATIONS`) | Tägliche Benachrichtigung und die Benachrichtigung des Vordergrunddienstes | Nein |
| Start (`RECEIVE_BOOT_COMPLETED`) | Randlichter und Benachrichtigung nach einem Neustart wiederherstellen | Nein |

- **Mikrofon:** Die App misst nur den Geräuschpegel, damit sich die Lichter bewegen. Es wird nichts aufgenommen, gespeichert oder gesendet. Android zeigt selbst die Mikrofonanzeige, solange das Mikrofon verwendet wird.
- **Fotos:** Ein eigenes Foto wird nur lokal in der App verwendet.
- **Daten:** Einstellungen, Lichter und Favoriten liegen im eigenen Speicher der App (`SharedPreferences`). Entfernst du die App, sind sie weg.

---

## 🔋 Akku und Leistung

- Die App zeichnet mit Canvas. Statische Teile (der Baum) werden einmalig in einer Bitmap gespeichert.
- **Bildrate:** normal 30 fps, *sparsam* 15 fps, und *automatisch* schaltet bei Energiesparmodus oder unter 20 % Akku auf 15 fps.
- **Im Hintergrund** wird nichts gezeichnet, wenn die App nicht sichtbar ist. Randlichter mit Dauerlicht (*Dauerlicht*) stehen still und kosten keine Frames.
- Die **Widgets** aktualisieren sich nur, wenn sich wirklich etwas geändert hat.
- Das **Mikrofon** verbraucht zusätzlich Akku. Deshalb ist es standardmäßig aus.
- Auf dem Emulator sank die mittlere Zeit zum Aufbauen eines Bildes mit den letzten Optimierungen von etwa 30 ms auf etwa 23 ms. Echte Messungen auf einem Handy wurden noch nicht gemacht.

---

## ❓ Häufige Fragen

**Die Lichter verschwinden nach einer Weile.**
Manche Handymarken (Xiaomi, Huawei, Samsung, OnePlus …) beenden Hintergrund-Apps aggressiv. Schalte in den
Akku-Einstellungen die *Akkuoptimierung* für Weihnachtslichter aus und erlaube den *Autostart*.

**Ich sehe keine Lichter über meinem Startbildschirm.**
Prüfe unter *Einstellungen → Apps → Weihnachtslichter → Über anderen Apps anzeigen*, ob die Berechtigung aktiv ist.

**Die Lichter kommen nach einem Neustart nicht zurück.**
Die App muss noch die Berechtigung haben, über anderen Apps zu zeichnen, und die Lichter müssen vor dem Neustart an
gewesen sein. Manche Hersteller blockieren Start-Empfänger; erlaube den *Autostart*.

**Das Widget zeigt nichts oder ist leer.**
Entferne das Widget und füge es neu hinzu. Das Baum-Widget braucht mindestens einmal die geöffnete App.

**Die Installation schlägt fehl („App nicht installiert“).**
Ist schon eine Version mit anderer Signatur installiert, deinstalliere sie zuerst. Prüfe auch, ob dein Handy
Android 8.0 oder neuer hat.

**Funktioniert die App ohne Internet?**
Ja, vollständig. Das Internet wird nur für die Update-Prüfung benutzt, die du selbst startest.

**Kann ich die App im Play Store bekommen?**
Noch nicht. Die APK ist zum selbst Installieren gedacht.

---

## 🛠️ Aus dem Quellcode bauen

### Voraussetzungen

- JDK 17
- Android SDK (Plattform 34/35 und Build-Tools; `ANDROID_HOME` gesetzt)
- Kein Android Studio nötig: Der Gradle-Wrapper erledigt alles

### Debug-Build und Tests

```bash
cd Kerstlampjes
./gradlew testDebugUnitTest assembleDebug
```

Die APK landet in `Kerstlampjes/app/build/outputs/apk/debug/app-debug.apk`.

Auf einem Gerät oder Emulator installieren:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release (signiert)

1. Erstelle einen Keystore:

   ```bash
   keytool -genkeypair -keystore release.keystore -alias kerstlampjes -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Erstelle `Kerstlampjes/keystore.properties` (steht in `.gitignore`):

   ```
   storeFile=release.keystore
   storePassword=...
   keyAlias=kerstlampjes
   keyPassword=...
   ```
3. Baue das Release:

   ```bash
   ./gradlew assembleRelease
   ```

   Ergebnis: `app/build/outputs/apk/release/app-release.apk`. Der Release-Build nutzt Minify und Resource-Shrinking.

Bewahre den Keystore gut auf: Ohne denselben Schlüssel kannst du eine installierte App nicht mehr aktualisieren.
Der Keystore und die Passwörter gehören **nicht** in Git.

### Ein Release auf GitHub veröffentlichen

```bash
gh release create v2.4 app-release.apk --title "Kerstlampjes 2.4" --notes "..."
```

---

## 🗂️ Projektstruktur

```
KerstApp/
├── Kerstlampjes/                  Android-Projekt (Gradle, Kotlin)
│   └── app/src/
│       ├── main/java/nl/kerstlampjes/app/
│       │   ├── MainActivity.kt        Einstellungen der Randlichter
│       │   ├── TreeActivity.kt        Baum-/Kamin-/Szenen-Bildschirm, Auswahl und ⋯-Menü
│       │   ├── TreeRenderer.kt        Zeichnen von Bäumen, Lichtern, Kette, TreeStore
│       │   ├── Hearths.kt             Kamine und Partikelfeuer
│       │   ├── Scenes.kt              Die 9 interaktiven Szenen
│       │   ├── MyScene.kt             Eigene Szene: Rezept, Speicherung, Zeichnen von Effekten, Stickern und Lichterketten
│       │   ├── SceneEditorActivity.kt Editor für eigene Szenen
│       │   ├── LightsService.kt       Vordergrunddienst für die Randlichter
│       │   ├── LightsView.kt          Overlay-View mit der Lichterkette
│       │   ├── TreeWallpaperService.kt Live-Hintergrund
│       │   ├── Widgets.kt             Die drei Widgets
│       │   ├── Extras.kt              Ton, Musik, Kalender, Benachrichtigung, Start
│       │   ├── Tr.kt / Translations.kt Übersetzungen
│       │   ├── Perf.kt                Bildrate und Akku
│       │   ├── Prefs.kt               Einstellungen
│       │   └── Credits.kt             Foto-Quellen in der App
│       ├── main/res/                  Layouts, Widgets, Strings (nl/en/de/fr/es/zh)
│       └── test/                      Unit-Tests
├── website/                       Statische Website
│   ├── index.html, style.css      Hauptseite
│   ├── eggs.js + versteckte Seite Überraschungen zu besonderen Tagen
│   ├── media/                     Screenshots und kurze Videos
│   └── Kerstlampjes.apk           Signierte Release-APK
├── CREDITS.md                     Quellen und Lizenzen der Fotos
├── .github/workflows/android.yml  CI
└── README.md
```

---

## 🧩 Technik

- **Sprache und Plattform:** Kotlin, `minSdk 26`, kein AndroidX. Alles ist normales Android SDK.
- **Zeichnen:** Ein gemeinsamer `TreeRenderer` zeichnet für den Baum-Bildschirm, das Widget und den Live-Hintergrund.
  `SceneHost` verwaltet die Szenen und `HearthPainter` die Kamine.
- **Randlichter:** ein `TYPE_APPLICATION_OVERLAY`-Fenster in einem Vordergrunddienst (`specialUse`, mit `microphone`, wenn der Musik-Effekt an ist). Das Fenster ist nicht berührbar, Berührungen gehen also durch.
- **Widgets:** `AppWidgetProvider` mit `RemoteViews`. Das Baum-Widget rendert eine Bitmap und skaliert sie.
- **Speicher:** `SharedPreferences` mit dem Namen `kerstlampjes`. Lichter pro Baum liegen unter einem Schlüssel mit dem Namen des Baums, damit sich die Reihenfolge der Bäume später ändern kann, ohne deine Lichter zu verlieren.
- **Ton:** Lieder und Effekte werden synthetisiert; `AudioLevel` kombiniert Mikrofon und Synthesizer. Das Mikrofon wird nur eingeschaltet, wenn ein Teil (App, Hintergrund oder Randlichter) es braucht.
- **Übersetzungen:** Der niederländische Text ist der Schlüssel (`Tr.t("…")`, `Tr.f(fmt, …)`). `Translations.kt` enthält die fünf anderen Sprachen.
- **Leistung:** Cache für die Baum-Ebene, Wiederverwendung von Shadern und Pfaden, kein Zeichnen im Hintergrund, `Perf.frameDelay` für 30/15 fps.
- **Release:** Minify und Shrink mit `proguard-rules.pro`, Signierung aus `keystore.properties`.

### Übersetzen

Texte stehen im Code als niederländischer Schlüssel (`Tr.t("…")`). Die Übersetzungen stehen in `Translations.kt`.
Fügst du einen neuen Text hinzu, trage die fünf Übersetzungen ein. Der Test `TranslationsTest` prüft, dass
alles in allen Sprachen übersetzt ist und die Platzhalter (`%d`, `%s`) übereinstimmen.

---

## ✅ Tests und CI

- **Unit-Tests** (`./gradlew testDebugUnitTest`):
  - `TranslationsTest`: Jeder Schlüssel ist in allen Sprachen übersetzt, mit denselben Platzhaltern.
  - `CalendarTest`: Die Szene des Tages stimmt für die Adventssonntage, Weihnachten, Silvester und die übrigen Daten.
- **Lint:** `./gradlew lintDebug`.
- **GitHub Actions** (`.github/workflows/android.yml`): Bei jedem Push auf `main` und jedem Pull-Request laufen Lint, Unit-Tests und ein Debug- und Release-Build. Die APKs werden als Artefakt aufbewahrt.

Was **nicht** getestet ist: das Verhalten auf einem echten Handy (Akku, echtes Mikrofon, Klang der Lieder) und die
Rückkehr der Randlichter nach einem echten Neustart.

---

## 🎁 Dekoration rund um deinen Rand

Neben den Lichtern kannst du unter **Dekoration** auf dem Hauptbildschirm vieles dazuschalten. Alles lässt sich einzeln ein- oder ausschalten:

- 🚂 **Kleiner Zug**, der Runden dreht: von oben gesehen am Rand entlang oder von der Seite am unteren Rand. 1 bis 8 Wagen voller Geschenke, auf Wunsch mit Lichtern. Tippe auf den Zug, dann pfeift er.
- ❄️ **Schneeflocken und Sternchen**, ⭐ **Sternschnuppen** (tippe eine an für Feuerwerk) und 🎆 **Feuerwerk** (tippe irgendwo für einen Knall).
- ☃️ **Liegenbleibender Schnee** am unteren Rand. Schüttle dein Handy, und der Schnee fliegt weg; danach baut er sich wieder auf.
- 🧊 **Eiszapfen** am oberen Rand. Tippe darauf, dann fallen sie; danach wachsen sie nach.
- 🎄 **Girlande** am Rand, mit oder ohne Kugeln und mit oder ohne Lichter (*Lichter anzeigen*).
- 🛷 **Schlitten mit Rentieren**, 🐧 **Pinguine und Schneemann** (tippe darauf, dann hüpfen sie) und eine **Laufschrift** mit deinem eigenen Text.
- **Nur auf dem Startbildschirm:** eine Option, damit nichts über deinen Apps erscheint. Dafür fragt die App nach Zugriff auf die Nutzungsdaten, um zu sehen, welche App vorne ist. Das bleibt auf deinem Handy; nichts wird gespeichert oder gesendet.

Tippen funktioniert im Spielbildschirm (**🎮 Spiel mit deinem Rand**). Das Overlay über anderen Apps lässt alle Tipper zur App darunter durch, aber Schütteln funktioniert überall.

## 💻 Windows

Es gibt auch eine kleine Windows-Version nur mit den Lichtern: eine Kette am Rand deines Bildschirms (über der Taskleiste), immer sichtbar, und du kannst einfach hindurchklicken.

**Erste Schritte**

1. Lade [Kerstlampjes-Windows.exe](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.2/Kerstlampjes-Windows.exe) herunter und doppelklicke darauf. Nichts zu installieren; Windows 10 oder 11 genügt.
2. Suche das Glühbirnen-Symbol unten rechts neben der Uhr (manchmal hinter dem Pfeil ^).
3. **Linksklick** auf das Symbol schaltet die Lichter an oder aus. **Rechtsklick** öffnet das Menü:

| Menüpunkt | Was er macht |
|---|---|
| Lichter an | Schaltet die Lichter an oder aus |
| Einstellungen… | Effekt, Farben, Dekoration und mehr |
| Feuerwerk zünden · Eiszapfen fallen lassen · Schnee wegfegen | Schnellaktionen für die Dekoration |
| Mit Windows starten | Startet das Programm automatisch beim Hochfahren |
| Nach Updates suchen | Prüft auf GitHub, ob es eine neuere Version gibt |
| Über Weihnachtslichter… | Erklärung, Version und Lizenz |
| Beenden | Beendet das Programm |


- **Download:** [Kerstlampjes-Windows.exe](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.2/Kerstlampjes-Windows.exe) (100 kB, [Release](https://github.com/ericbruggema/kerstlampjes/releases/tag/windows-v1.2)). Nichts zu installieren: Doppelklick, und das Symbol erscheint im Infobereich, unten rechts bei der Uhr.
- **An/aus:** Linksklick auf das Symbol. **Rechtsklick** öffnet das Menü: Einstellungen, mit Windows starten, nach Updates suchen, Über und Beenden.
- **Einstellungen:** Effekt (Dauerlicht, Funkeln, abwechselnd, Lauflicht, Farbwelle), Farben, Geschwindigkeit, Größe, Abstand, Eckenrundung, Randabstand, Durchhang, Deckkraft. Auch alle Bildschirme oder nur der Hauptbildschirm.
- **Dekoration:** dieselbe wie auf Android (kleiner Zug, Schnee, Feuerwerk, Eiszapfen, Girlande, Schlitten, Pinguine, Laufschrift), einzeln in den Einstellungen einzuschalten. Klicke auf Eiszapfen, Sternschnuppen, den Zug oder die Pinguine, dann reagieren sie; dein Klick geht trotzdem ans Fenster darunter.
- **Feuerwerksshow:** wähle zufälliges Feuerwerk oder eine Art: Raketen, Fontänen, Mörser, Römische Lichter, Feuerräder oder eine choreografierte große Show (etwa 32 Sekunden, danach eine Pause). Es geht an zufälligen Stellen am unteren Rand los; von Hand zünden geht über das Menü.
- **Schnee:** fällt standardmäßig an den Seiten; du kannst ihn auch über den ganzen Bildschirm fallen lassen. Die Option Kugeln an der Girlande ist erst wählbar, wenn die Girlande an ist.
- **Schnee entfernen:** fahre mit der Maus durch den Schnee, schüttle die Maus hin und her oder wähle im Menü *Schnee wegfegen*.
- **Nur auf dem Desktop:** eine Option, die Lichter nur zu zeigen, wenn du den Desktop siehst, nicht über Programmen.
- **Blendet sich aus** bei Vollbild-Apps (Spiele, Filme, Präsentationen).
- **Datenschutz:** nichts geht über das Internet, außer du wählst „Nach Updates suchen“. Einstellungen liegen in `%APPDATA%\Kerstlampjes`.
- **Sprachen:** folgt der Windows-Sprache (Nederlands, English, Deutsch, Français, Español, 中文).
- **Windows SmartScreen:** die Datei ist nicht digital signiert; bei der Warnung *Weitere Informationen* → *Trotzdem ausführen* wählen.
- **Bauen:** Ordner `Windows/`, `dotnet build -c Release` (.NET Framework 4.8, in Windows 10/11 enthalten).

## 🤝 Open Source und Weiterverwendung

Kerstlampjes ist **Open Source und für alle frei nutzbar** unter der [MIT-Lizenz](LICENSE). Du darfst es kopieren,
ändern, teilen und sogar in deiner eigenen App verwenden. Der Code liegt hier: https://github.com/ericbruggema/kerstlampjes.

**Bitte nenne die Quelle.** Verwendest du Code oder Ideen aus diesem Projekt (zum Beispiel den Szenen-Editor, die
Randlichter oder das Partikelfeuer)? Dann verlinke bitte https://github.com/ericbruggema/kerstlampjes in deiner App, README oder Website. Die
MIT-Lizenz verlangt mindestens, dass der Copyright-Hinweis in [`LICENSE`](LICENSE) erhalten bleibt. Eine kurze
Zeile wie „Basiert auf Kerstlampjes, https://github.com/ericbruggema/kerstlampjes“ genügt.

Verbesserungen, Übersetzungen und Ideen sind willkommen über [Issues und Pull Requests](https://github.com/ericbruggema/kerstlampjes/issues). Die Fotos in
der App haben eigene Lizenzen, siehe unten.

---

## 📄 Quellen und Lizenzen

Die Fotos echter Bäume und Kamine stammen von **Wikimedia Commons** (abgerufen am 14. Oktober 2026). Sie wurden
verkleinert und zugeschnitten. Die vollständige Liste mit Autoren und Lizenzen steht in [`CREDITS.md`](CREDITS.md) und
in der App unter *Quellen*.

| Foto | Autor | Lizenz |
|---|---|---|
| Fire burning in a fireplace | Pöllö | CC BY 3.0 |
| Cozy Christmas Fireplace with stockings | Lindsey Turner | CC BY 2.0 |
| Fireplace, Wood fire, Rostov-on-Don | Vyacheslav Argenberg | CC BY 4.0 |
| Fireplace Burning | unbekannt | Gemeinfrei |
| 2020 Capitol Christmas Tree with Lights | USCapitol | Gemeinfrei |
| Weihnachtsbaum im China Garten | PantheraLeo1359531 | CC BY 4.0 |
| Weihnachtsbaum am Lorenzpark | PantheraLeo1359531 | CC BY 4.0 |
| Christmas Tree in the Park | Corey Leopold | CC BY 2.0 |
| White Christmas Tree Lights at Night | photos-public-domain.com | CC0 |

Musik und Ton sind selbst synthetisiert; die Lieder sind traditionell und frei von Rechten.

---

<div align="center">

Gemacht mit ganz vielen Lichtern. 🎄✨

</div>
