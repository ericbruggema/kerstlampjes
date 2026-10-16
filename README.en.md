<div align="center">

# 🎄 Christmas Lights (Kerstlampjes)

**A string of Christmas lights along the edge of your screen, also on top of your home screen, your icons and your widgets. Plus bonus: your own Christmas trees, fireplaces, scenes and an editor for your own scene. For Android.**

[⬇️ Download the APK](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.4)

🌍 **Read this in:** [Nederlands](README.md) · **English** · [Deutsch](README.de.md) · [Français](README.fr.md) · [Español](README.es.md) · [中文](README.zh.md)

💻 **New: Christmas Lights for Windows!** Just the lights, always visible from the system tray. [⬇️ Download (100 kB)](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.1/Kerstlampjes-Windows.exe) · [More info](#-windows)

</div>

> 📷 This translation has no screenshots, to keep it short. You can find all images in the [Dutch README](README.md) and on the website. The app itself is available in all six languages.
> Menu names below are given as they appear when the app is set to English; the app follows your phone's language (see [Languages](#-languages)).

---

## Contents

1. What is it?
2. Installing
3. First use
4. Features
5. Controls
6. Languages
7. Permissions and privacy
8. Battery and performance
9. Frequently asked questions
10. Building from source
11. Project structure
12. Technology
13. Tests and CI
14. Sources and licences

---

## 🎅 What is it?

Christmas Lights is an Android app that turns your phone into a Christmas decoration. **The main point is the lights
in the background of your phone**: a string of lights around the edge of your screen that also stays on top of your home screen.
Everything else is a bonus.

**Main feature**

- a **string of lights along the edge of your screen** that also stays on top of your home screen, your icons, widgets and other apps, with 5 effects, 5 colour schemes, on/off via a Quick Settings tile, coming back after a restart and (optionally) reacting to sound;

**Bonus**

- **19 Christmas trees** (14 drawn, 5 photos) plus your **own photo**, on which you hang your own lights;
- **12 fireplaces** with real particle fire or photos of a crackling fire;
- **9 interactive scenes**: snow globe, advent calendar, advent wreath, Christmas village with train, sleigh, snowman, ice rink, nativity scene and presents;
- a **scene editor**: your own background (also your own photo), fire in your own colour, snow, fireworks, stickers with their own animations, hand-drawn light strings and sharing with others;
- **8 kinds of lights**, **3 widgets**, **live wallpaper**, **4 Christmas songs**, a sleep timer and favourite looks;
- surprises on **special days** (Sinterklaas, Christmas Eve, New Year's Eve, Epiphany …);
- **6 languages**: Dutch, English, German, French, Spanish and Chinese.

Everything is drawn and synthesised inside the app itself (Canvas, no game engine, no AndroidX). The only
images are the photos of real trees and fireplaces, from Wikimedia Commons ([sources](#-sources-and-licences)).
There are **no ads and no accounts**. Internet is only used when you yourself tap *Check for updates*.

| | |
|---|---|
| Package name | `nl.kerstlampjes.app` |
| Version | 2.4 (versionCode 6) |
| Android | 8.0 (API 26) or newer |
| Language | Kotlin |
| Size | about 3 MB |

---

## 📲 Installing

### Option 1: the APK from GitHub (recommended)

1. On your phone, go to [release v2.4](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.4).
2. Download **`Kerstlampjes-2.4.apk`**.
3. Open the downloaded file. If Android asks for permission to *install from unknown sources*, allow it for your browser or file manager.
4. Tap **Install** and then **Open**.

> **Updating an older version?** The release is signed with its own key. If a version with a different
> signature is on your phone (for example a debug build), uninstall it first. Your settings will be lost.

### Option 2: the APK in the repository

The same signed APK is also in the repo: [`website/Kerstlampjes.apk`](website/Kerstlampjes.apk).

### Option 3: build it yourself

See [Building from source](#-building-from-source).

---

## 🚀 First use

1. **Open the app.** You see the settings screen for the edge lights: effect, colours, speed, brightness, appearance and fit.
2. **Tap "Turn lights on".** The app asks once for permission to *draw over other apps*. Turn it on.
   Then go back: the lights now run along the edge of your screen, also when you go to your home screen.
3. **Tap "🎄 Build your Christmas tree"** to open the tree, fireplace and scene view. Swipe left or right to switch between trees, fireplaces and scenes.
4. **Tap the tree** to hang a light. Tap a light to change its colour. Long-press a light to remove it.
5. **Open the ⋯ menu** for sound, music, widgets, favourites, sleep timer, language and more.

---

## ✨ Features

### 💡 Lights around the edge of your screen

An animated string of lights follows the edge and rounded corners of your screen. Touches pass straight through
to the app underneath, so it never gets in the way.

| Setting | Options |
|---|---|
| **Effect** | Steady, twinkle, alternate blinking, chasing light, colour wave and *To the music* |
| **Colours** | 5 colour schemes |
| **Speed** | Slow to fast |
| **Brightness** | 30 to 100% |
| **Appearance** | Size of the lights, spacing between them and sag of the wire |
| **Fit** | Corner rounding and distance from the edge, so it fits your screen |

- **On top of everything.** The lights sit above your home screen, your icons, your widgets and other apps.
- **On and off.** Via the button in the app, via the ⋯ menu (*Lights around the screen*) or via the **Quick Settings tile** in your notification panel.
- **After a restart.** If the lights were on when you switched your phone off, they come back by themselves. This requires that the permission to draw over other apps is still on.
- **To the microphone.** In the ⋯ menu turn on *Edge lights react to sound* (or choose the effect *To the music*). The louder the sound, the more lights light up. In silence they glow softly. The app then asks for microphone permission. See [Permissions and privacy](#-permissions-and-privacy).
- **Foreground notification.** Android shows a notification as long as the lights are on. Android requires this for apps that run in the background.

### 🌲 Christmas trees

19 trees to swipe between, each with its own decoration, plus your own photo.

- **14 drawn trees:** from classic and fir to neon, ice and cartoon 3D.
- **5 real Christmas trees as photos:** Capitol, indoor tree, spruce in the park, city tree and fairy-light tree.
- **Your own photo:** pick a photo of your own tree or room and hang lights over it. This also works as a wallpaper.
- **Every tree remembers its own lights.** Switch to another tree and back, and they hang in the same place.
- **Light type per tree,** or a mix of all types.

### 🔥 Fireplaces

12 fireplaces with real fire. The 7 drawn fireplaces use hundreds of glowing particles. The 5 photo
fireplaces use a photo with moving fire over it. Tap the fire and it flares up. Your own
string of lights hangs along the mantel or between the posts.

### ✨ Interactive scenes

Don't just watch: tap, shake and build.

| Scene | What do you do? |
|---|---|
| **Snow globe** | Shake your phone or tap: it starts snowing. |
| **Advent calendar** | A door with a surprise every day. |
| **Advent wreath** | Light the candles, Sunday by Sunday. |
| **Christmas village with train** | Tap houses for light, tap the train for the horn. Long-press the track to stop the train. Tap the sky for a little star. |
| **Santa's sleigh** | Tap the sleigh and it rains presents. Long-press for a turbo ride. |
| **Build a snowman** | Tap and he appears step by step. |
| **Ice rink** | Tap the ice for a new skater. Skaters circle around each other. Tap a skater and they fall and slide off the ice. |
| **Nativity scene** | Tap the starry sky for a wish. Tap Mary, Joseph, the kings, the ox or the donkey and they say something. |
| **Presents** | Your own messages that may only be opened on a chosen day. |

### 🎨 Make your own scene

You build your own Christmas scene exactly the way you want it. Open **⋯ → 🎨 Make your own scene**. At the
top you see exactly what the scene will show later, and at the bottom five tabs.

| Tab | What can you do? |
|---|---|
| 🖼️ **Background** | Choose from eight colour gradients or use your **own photo**. Turn a snowy ground on or off. |
| ✨ **Effects** | Snow, falling things (presents, stars, hearts …), confetti, twinkling stars, fireworks, shooting stars, a soft glow and **fire**. Each effect has its own sliders (amount, speed, size) and colour. |
| 🎅 **Stickers** | 32 pictures (Christmas tree, Santa, snowman, sleigh, train …). Drag them, make them bigger or smaller and rotate them. |
| 💡 **Lights** | **Draw a string of lights with your finger** along a window, roof or tree, or choose *Top*, *Arc* or *All around*. Per string, choose the type of lights, the colours and the effect. |
| 💾 **More** | Name, new scene, duplicate, all your scenes, **share**, paste and delete. |

**Fire in your own colour.** Place the fire where you want it and choose orange, blue, green, purple or white. Set height,
width and speed. Tap the fire in the scene and it flares up.

**Your own animations.** Every sticker has an animation and a reaction to a tap:

| Animation | Reaction to a tap |
|---|---|
| Still, floating, waving, pulsing, blinking, hopping, spinning, driving past, circling | Nothing, hop, sparkles, spin, little bell or **say something** (a speech bubble with your own text) |

**Saving and using.** Everything is saved automatically. You will find your scene as the scene **My scene** (the
last in the row of scenes). You can set it as **live wallpaper** and keep it in your favourites. You can
keep several scenes and switch between them via *More → My scenes*.

**Sharing.** Choose *More → Share* and send the text via WhatsApp, e-mail or another app. The recipient copies the
text and chooses *More → Paste* in the editor. A photo used as a background is not sent along.

### 💡 Types of lights

Under the tree, choose from eight types, or a **mix** of everything. You can save the choice per tree or fireplace,
or apply it to everything, including your wallpaper.

Classic · Mini LEDs · Glass baubles · Little flames · Little stars · Hearts · Icicles · Snowflakes

### 📱 Widgets

Three widgets for your home screen. Long-press an empty spot on your home screen, choose *Widgets* and look for *Christmas Lights*.

| Widget | What do you see? |
|---|---|
| **Countdown to Christmas** | The number of days until Christmas, with a message on the holidays themselves. |
| **Today's scene** | The scene that fits today's date (advent, nativity, presents …). Tap to open the app on that scene. |
| **Christmas tree with lights** | Your own tree with your lights, with rounded corners. Tap to open the app. |

The tree widget only refreshes when your tree has really changed, so it doesn't drain your battery.

### 🖼️ Live wallpaper

In the app, choose a tree, fireplace or scene and set it as **live wallpaper**. Every time you go to your
home screen you will see, for example, the fire or the driving train, with your lights on it.

1. In the app, choose the tree, fireplace or scene you want.
2. Tap the **Wallpaper** button under the tree.
3. Confirm on the Android screen.

### 🔊 Sound, music and microphone

All sound is **synthesised inside the app itself**. There are no audio files.

- **Fireplace:** crackling fire at the fireplaces.
- **Bells** on a tap, and a **music-box melody** in the scenes.
- **Four Christmas songs:** *Jingle Bells*, *Silent Night*, *We Wish You a Merry Christmas* and *Deck the Halls*, each with its own tempo, a music-box sound and a light echo. Pick the song via *Music* in the ⋯ menu.
- **To the music:** a lights effect in which the lights move with the sound. It follows both the built-in songs and the microphone.

### ⋯ The ⋯ menu

| Item | What does it do? |
|---|---|
| 💡 **Lights around the screen** | Turns the edge lights on or off. |
| 🎤 **Edge lights react to sound** | The edge lights move to the sound from the microphone. |
| 🔊 **Sound** | Fireplace, bells and melodies on or off. |
| 🎵 **Music** | Choose one of the four Christmas songs. |
| 📅 **Automatically go to scene of the day** | In season, the app opens on today's scene. |
| 🔔 **Daily notification** | Every day at 18:00 a notification with the countdown. |
| 🎁 **Go to today's scene** | Straight to the scene that fits the date. |
| ⭐ **Favourite looks** | Save tree or fireplace with lights, effect and light type under your own name. |
| 😴 **Sleep timer** | 15 minutes to 2 hours: the lights dim quietly and go out. |
| 🔋 **Frame rate** | Normal (30 fps), economical (15 fps) or automatic. |
| 🖼️ **Own photo** | Use your own photo as background. |
| 🔄 **Check for updates** | Looks on GitHub for a newer version and opens the download page. |
| 🌍 **Language** | Automatic, or choose one of the six languages yourself. |

### 📅 Special days

- **Scene of the day:** from 15 November to 6 January the app, the widget and the notification open on the scene that fits today: Christmas village in November, the advent wreath on the Sundays of Advent, the advent calendar in December, nativity and presents around Christmas.
- **Countdown to Christmas:** at the top of the tree screen and in the widget.
- **Surprises:** on a number of special days something happens in the app (Sinterklaas, Christmas Eve, first and second Christmas Day, New Year's Eve, New Year and Epiphany). Come back on a special day. 👀

---

## 👆 Controls

| Gesture | Where | Effect |
|---|---|---|
| Swipe left/right | Tree, fireplace or scene | Next or previous |
| Tap | Tree or fireplace | Hang a light |
| Tap a light | Tree or fireplace | Different colour |
| Long-press a light | Tree or fireplace | Remove the light |
| Tap | Fire | The fire flares up |
| Shake | Snow globe | It snows |
| Tap | Village: house | Light on or off |
| Tap | Village: sky | Little star |
| Long-press the track | Village | Train stops or runs again |
| Tap / long-press | Sleigh | Presents / turbo |
| Tap | Ice rink | New skater, or a skater falls |
| Tap a figure | Nativity | The figure says something |

---

## 🌍 Languages

| Language | Code |
|---|---|
| Nederlands | `nl` |
| English | `en` |
| Deutsch | `de` |
| Français | `fr` |
| Español | `es` |
| 中文 | `zh` |

By default the app follows your phone's language. If you choose a language yourself via **⋯ → Language**, it applies to the
whole app, the widgets and the notification. If your phone is set to another language, the app is in English.

---

## 🔐 Permissions and privacy

The app uses the internet **only** when you tap *Check for updates* in the ⋯ menu. It then asks GitHub for the name of the latest version and sends nothing about you. Apart from that the app sends nothing. Everything stays on your phone.

| Permission | Purpose | Required? |
|---|---|---|
| Draw over other apps (`SYSTEM_ALERT_WINDOW`) | The edge lights on top of the home screen and other apps | Only for the edge lights |
| Foreground service (`FOREGROUND_SERVICE`, `…_SPECIAL_USE`) | Keep the edge lights running in the background | Only for the edge lights |
| Microphone (`RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE`) | Lights that react to sound | No, only if you choose that effect |
| Internet (`INTERNET`) | Only the update check in the ⋯ menu | No, only when you tap it |
| Usage data (`PACKAGE_USAGE_STATS`) | Only for the "Only on the home screen" option: seeing which app is in front | Yes, you grant it in the system settings; nothing is stored or sent |
| Notifications (`POST_NOTIFICATIONS`) | Daily notification and the foreground service notification | No |
| Startup (`RECEIVE_BOOT_COMPLETED`) | Restore edge lights and notification after a restart | No |

- **Microphone:** the app only measures the sound level, to make the lights move. Nothing is recorded, stored or sent. Android itself shows the microphone indicator while the microphone is in use.
- **Photos:** your own photo is only used locally in the app.
- **Data:** settings, lights and favourites are in the app's own storage (`SharedPreferences`). If you remove the app, they are gone.

---

## 🔋 Battery and performance

- The app draws with Canvas. Static parts (the tree) are kept once in a bitmap.
- **Frame rate:** normally 30 fps, *economical* 15 fps, and *automatic* switches to 15 fps when battery saver is on or below 20% battery.
- **In the background** nothing is drawn when the app is not visible. Edge lights with steady light (*Steady*) stand still and cost no frames.
- The **widgets** only refresh when something has really changed.
- The **microphone** uses extra battery. That's why it is off by default.
- On the emulator, the median frame build time went from about 30 ms to about 23 ms with the latest optimisations. Real measurements on a phone have not been done yet.

---

## ❓ Frequently asked questions

**The lights disappear after a while.**
Some phone brands (Xiaomi, Huawei, Samsung, OnePlus …) stop background apps aggressively. In the
battery settings turn off *battery optimisation* for Christmas Lights and allow *auto-start*.

**I don't see any lights above my home screen.**
Check in *Settings → Apps → Christmas Lights → Display over other apps* that the permission is on.

**The lights don't come back after a restart.**
The app must still have permission to draw over other apps, and the lights must have been on
before the restart. Some manufacturers block start-up receivers; allow *auto-start*.

**The widget shows nothing or is empty.**
Remove the widget and add it again. The tree widget needs the app to have been opened at least once.

**The installation fails ("App not installed").**
If a version with a different signature is already installed, uninstall it first. Also check that your phone
has Android 8.0 or newer.

**Does the app work without internet?**
Yes, completely. The internet is only used for the update check that you start yourself.

**Can I get the app in the Play Store?**
Not yet. The APK is meant for installing yourself.

---

## 🛠️ Building from source

### Requirements

- JDK 17
- Android SDK (platform 34/35 and build-tools; `ANDROID_HOME` set)
- No Android Studio needed: the Gradle wrapper does everything

### Debug build and tests

```bash
cd Kerstlampjes
./gradlew testDebugUnitTest assembleDebug
```

The APK ends up in `Kerstlampjes/app/build/outputs/apk/debug/app-debug.apk`.

Install on a device or emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release (signed)

1. Create a keystore:

   ```bash
   keytool -genkeypair -keystore release.keystore -alias kerstlampjes -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Create `Kerstlampjes/keystore.properties` (it is in `.gitignore`):

   ```
   storeFile=release.keystore
   storePassword=...
   keyAlias=kerstlampjes
   keyPassword=...
   ```
3. Build the release:

   ```bash
   ./gradlew assembleRelease
   ```

   Result: `app/build/outputs/apk/release/app-release.apk`. The release build uses minify and resource shrinking.

Keep the keystore safe: without the same key you can no longer update an installed app.
The keystore and the passwords do **not** belong in git.

### Publishing a release on GitHub

```bash
gh release create v2.4 app-release.apk --title "Kerstlampjes 2.4" --notes "..."
```

---

## 🗂️ Project structure

```
KerstApp/
├── Kerstlampjes/                  Android project (Gradle, Kotlin)
│   └── app/src/
│       ├── main/java/nl/kerstlampjes/app/
│       │   ├── MainActivity.kt        Edge light settings
│       │   ├── TreeActivity.kt        Tree/fireplace/scene screen, pickers and ⋯ menu
│       │   ├── TreeRenderer.kt        Drawing trees, lights, string, TreeStore
│       │   ├── Hearths.kt             Fireplaces and particle fire
│       │   ├── Scenes.kt              The 9 interactive scenes
│       │   ├── MyScene.kt             Own scene: recipe, storage, drawing effects, stickers and light strings
│       │   ├── SceneEditorActivity.kt Editor for own scenes
│       │   ├── LightsService.kt       Foreground service for the edge lights
│       │   ├── LightsView.kt          Overlay view with the string of lights
│       │   ├── TreeWallpaperService.kt Live wallpaper
│       │   ├── Widgets.kt             The three widgets
│       │   ├── Extras.kt              Sound, music, calendar, notification, boot
│       │   ├── Tr.kt / Translations.kt Translations
│       │   ├── Perf.kt                Frame rate and battery
│       │   ├── Prefs.kt               Settings
│       │   └── Credits.kt             Photo sources in the app
│       ├── main/res/                  Layouts, widgets, strings (nl/en/de/fr/es/zh)
│       └── test/                      Unit tests
├── website/                       Static website
│   ├── index.html, style.css      Main page
│   ├── eggs.js + hidden page      Special-days surprises
│   ├── media/                     Screenshots and short videos
│   └── Kerstlampjes.apk           Signed release APK
├── CREDITS.md                     Sources and licences of the photos
├── .github/workflows/android.yml  CI
└── README.md
```

---

## 🧩 Technology

- **Language and platform:** Kotlin, `minSdk 26`, no AndroidX. Everything is plain Android SDK.
- **Drawing:** one shared `TreeRenderer` draws for the tree screen, the widget and the live wallpaper.
  `SceneHost` manages the scenes and `HearthPainter` the fireplaces.
- **Edge lights:** a `TYPE_APPLICATION_OVERLAY` window in a foreground service (`specialUse`, plus `microphone` when the music effect is on). The window is not touchable, so touches pass through.
- **Widgets:** `AppWidgetProvider`s with `RemoteViews`. The tree widget renders a bitmap and scales it.
- **Storage:** `SharedPreferences` named `kerstlampjes`. Lights per tree are stored under a key with the tree's name, so the order of trees can change later without losing your lights.
- **Sound:** songs and effects are synthesised; `AudioLevel` combines microphone and synthesiser. The microphone is only switched on when a component (app, wallpaper or edge lights) needs it.
- **Translations:** the Dutch text is the key (`Tr.t("…")`, `Tr.f(fmt, …)`). `Translations.kt` contains the five other languages.
- **Performance:** cache for the tree layer, reuse of shaders and paths, no drawing in the background, `Perf.frameDelay` for 30/15 fps.
- **Release:** minify and shrink with `proguard-rules.pro`, signing from `keystore.properties`.

### Translating

Texts are in the code as a Dutch key (`Tr.t("…")`). The translations are in `Translations.kt`.
If you add a new text, fill in the five translations. The `TranslationsTest` test checks that
everything is translated in all languages and that the placeholders (`%d`, `%s`) match.

---

## ✅ Tests and CI

- **Unit tests** (`./gradlew testDebugUnitTest`):
  - `TranslationsTest`: every key is translated in all languages, with the same placeholders.
  - `CalendarTest`: the scene of the day is right for the Sundays of Advent, Christmas, New Year and the other dates.
- **Lint:** `./gradlew lintDebug`.
- **GitHub Actions** (`.github/workflows/android.yml`): on every push to `main` and every pull request, lint, unit tests and a debug and release build run. The APKs are kept as an artifact.

What has **not** been tested: behaviour on a real phone (battery, real microphone, sound of the songs) and the
edge lights coming back after a real restart.

---

## 🎁 Decorations around your edge

Besides the lights, under **Decorations** on the main screen you can add all sorts of extras. Each one can be switched on or off separately:

- 🚂 **Little train** going round and round: seen from above along your edge, or from the side along the bottom edge. 1 to 8 wagons full of presents, optionally with lights. Tap the train and it whistles.
- ❄️ **Snowflakes and sparkles**, ⭐ **shooting stars** (tap one for fireworks) and 🎆 **fireworks** (tap anywhere for a bang).
- ☃️ **Settling snow** on the bottom edge. Shake your phone and the snow flies away; then it builds up again.
- 🧊 **Icicles** along the top edge. Tap them and they fall; then they grow back.
- 🎄 **Garland** along the edge, with or without baubles and with or without lights (*Show lights*).
- 🛷 **Sleigh with reindeer**, 🐧 **penguins and snowman** (tap them and they jump) and a **scrolling text sign** with your own text.
- **Only on the home screen:** an option to keep everything from showing over your apps. For that the app asks for access to usage data, to see which app is in front. It stays on your phone; nothing is stored or sent.

Tapping works in the play screen (**🎮 Play with your edge**). The overlay over other apps lets every tap through to the app underneath, but shaking works everywhere.

## 💻 Windows

There is also a small Windows version with just the lights: a string along the edge of your screen (above the taskbar), always visible, and you can click straight through it.

- **Download:** [Kerstlampjes-Windows.exe](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.1/Kerstlampjes-Windows.exe) (100 kB, [release](https://github.com/ericbruggema/kerstlampjes/releases/tag/windows-v1.1)). Nothing to install: double-click and the icon appears in the system tray, bottom right next to the clock.
- **On/off:** left-click the icon. **Right-click** opens the menu: settings, start with Windows, check for updates, about and exit.
- **Settings:** effect (steady, twinkle, alternate, chasing light, colour wave), colours, speed, size, spacing, corner rounding, distance from the edge, sag, opacity. Also all screens or just the main one.
- **Decorations:** the same as on Android (little train, snow, fireworks, icicles, garland, sleigh, penguins, scrolling text), each switched on separately in the settings. Click icicles, shooting stars, the train or the penguins and they react; your click still passes through to the window underneath.
- **Clearing the snow:** move your mouse through the snow, shake your mouse back and forth, or choose *Sweep away the snow* in the menu.
- **Only on the desktop:** an option to show the lights only when you see the desktop, not over programs.
- **Hides itself** during full-screen apps (games, films, presentations).
- **Privacy:** nothing goes over the internet unless you choose "Check for updates". Settings live in `%APPDATA%\Kerstlampjes`.
- **Languages:** follows the Windows language (Nederlands, English, Deutsch, Français, Español, 中文).
- **Windows SmartScreen:** the file is not digitally signed; at the warning choose *More info* → *Run anyway*.
- **Building:** folder `Windows/`, `dotnet build -c Release` (.NET Framework 4.8, included in Windows 10/11).

## 🤝 Open source and reuse

Kerstlampjes is **open source and free for everyone to use** under the [MIT licence](LICENSE). You may copy,
modify, share and even use it in your own app. The code is here: https://github.com/ericbruggema/kerstlampjes.

**Please give credit.** Do you use code or ideas from this project (for example the scene editor, the edge
lights or the particle fire)? Then please link to https://github.com/ericbruggema/kerstlampjes in your app, README or website. The MIT licence
requires at least that you keep the copyright notice in [`LICENSE`](LICENSE). A short line such as "Based on
Kerstlampjes, https://github.com/ericbruggema/kerstlampjes" is enough.

Improvements, translations and ideas are welcome through [issues and pull requests](https://github.com/ericbruggema/kerstlampjes/issues). The photos in
the app have their own licences, see below.

---

## 📄 Sources and licences

The photos of real trees and fireplaces come from **Wikimedia Commons** (retrieved on 14 October 2026). They have been
reduced and cropped. The full list of authors and licences is in [`CREDITS.md`](CREDITS.md) and
in the app under *Sources*.

| Photo | Author | Licence |
|---|---|---|
| Fire burning in a fireplace | Pöllö | CC BY 3.0 |
| Cozy Christmas Fireplace with stockings | Lindsey Turner | CC BY 2.0 |
| Fireplace, Wood fire, Rostov-on-Don | Vyacheslav Argenberg | CC BY 4.0 |
| Fireplace Burning | unknown | Public domain |
| 2020 Capitol Christmas Tree with Lights | USCapitol | Public domain |
| Weihnachtsbaum im China Garten | PantheraLeo1359531 | CC BY 4.0 |
| Weihnachtsbaum am Lorenzpark | PantheraLeo1359531 | CC BY 4.0 |
| Christmas Tree in the Park | Corey Leopold | CC BY 2.0 |
| White Christmas Tree Lights at Night | photos-public-domain.com | CC0 |

Music and sound are synthesised by the app itself; the songs are traditional and free of rights.

---

<div align="center">

Made with lots of lights. 🎄✨

</div>
