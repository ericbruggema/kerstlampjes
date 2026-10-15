<div align="center">

# 🎄 Lumières de Noël (Kerstlampjes)

**Une guirlande lumineuse le long du bord de ton écran, aussi par-dessus ton écran d'accueil, tes icônes et tes widgets. En bonus : tes propres sapins, cheminées, scènes et un éditeur pour ta propre scène. Pour Android.**

[⬇️ Télécharger l'APK](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.3)

🌍 **Lire en :** [Nederlands](README.md) · [English](README.en.md) · [Deutsch](README.de.md) · **Français** · [Español](README.es.md) · [中文](README.zh.md)

</div>

> 📷 Cette traduction ne contient pas de captures d'écran, pour rester courte. Toutes les images se trouvent dans le [README néerlandais](README.md) et sur le site web. L'application existe dans les six langues.
> Les noms de menu ci-dessous sont ceux qui s'affichent quand l'application est réglée en français ; l'application suit la langue de ton téléphone (voir [Langues](#-langues)).

---

## Sommaire

1. Qu'est-ce que c'est ?
2. Installation
3. Première utilisation
4. Fonctions
5. Commandes
6. Langues
7. Autorisations et vie privée
8. Batterie et performances
9. Questions fréquentes
10. Compiler depuis les sources
11. Structure du projet
12. Technique
13. Tests et CI
14. Sources et licences

---

## 🎅 Qu'est-ce que c'est ?

Lumières de Noël est une application Android qui transforme ton téléphone en décoration de Noël. **L'essentiel, ce sont les lumières
à l'arrière-plan de ton téléphone** : une guirlande autour du bord de ton écran qui reste aussi par-dessus ton écran d'accueil.
Tout le reste est un bonus.

**L'essentiel**

- une **guirlande lumineuse le long du bord de l'écran** qui reste aussi par-dessus ton écran d'accueil, tes icônes, tes widgets et les autres applications, avec 5 effets, 5 palettes de couleurs, activation/désactivation via une tuile des réglages rapides, retour après un redémarrage et (au choix) réaction au son ;

**Bonus**

- **19 sapins de Noël** (14 dessinés, 5 en photo) plus ta **propre photo**, où tu accroches toi-même des lumières ;
- **12 cheminées** avec un vrai feu à particules ou des photos d'un feu qui crépite ;
- **9 scènes interactives** : boule à neige, calendrier de l'Avent, couronne de l'Avent, village de Noël avec train, traîneau, bonhomme de neige, patinoire, crèche et cadeaux ;
- un **éditeur de scène** : ton propre fond (aussi ta propre photo), feu dans ta couleur, neige, feux d'artifice, autocollants avec leurs propres animations, guirlandes dessinées à la main et partage avec d'autres ;
- **8 types de lumières**, **3 widgets**, **fond d'écran animé**, **4 chants de Noël**, une minuterie de sommeil et des looks favoris ;
- des surprises lors de **jours spéciaux** (Saint-Nicolas, réveillon de Noël, Nouvel An, Épiphanie …) ;
- **6 langues** : néerlandais, anglais, allemand, français, espagnol et chinois.

Tout est dessiné et synthétisé dans l'application elle-même (Canvas, pas de moteur de jeu, pas d'AndroidX). Les seules
images sont les photos de vrais sapins et cheminées, issues de Wikimedia Commons ([sources](#-sources-et-licences)).
Il n'y a **ni publicité, ni compte**. Internet n'est utilisé que si tu appuies toi-même sur *Rechercher des mises à jour*.

| | |
|---|---|
| Nom du paquet | `nl.kerstlampjes.app` |
| Version | 2.3 (versionCode 5) |
| Android | 8.0 (API 26) ou plus récent |
| Langage | Kotlin |
| Taille | environ 3 Mo |

---

## 📲 Installation

### Option 1 : l'APK depuis GitHub (recommandé)

1. Sur ton téléphone, va sur la [release v2.3](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.3).
2. Télécharge **`Kerstlampjes-2.3.apk`**.
3. Ouvre le fichier téléchargé. Si Android demande l'autorisation d'*installer depuis des sources inconnues*, autorise-le pour ton navigateur ou ton gestionnaire de fichiers.
4. Appuie sur **Installer** puis sur **Ouvrir**.

> **Mettre à jour une ancienne version ?** La release est signée avec une clé propre. Si une version
> avec une autre signature se trouve sur ton téléphone (par exemple une build de débogage), désinstalle-la d'abord. Tes
> réglages seront alors perdus.

### Option 2 : l'APK dans le dépôt

La même APK signée se trouve aussi dans le dépôt : [`website/Kerstlampjes.apk`](website/Kerstlampjes.apk).

### Option 3 : compiler soi-même

Voir [Compiler depuis les sources](#-compiler-depuis-les-sources).

---

## 🚀 Première utilisation

1. **Ouvre l'application.** Tu vois l'écran de réglages des lumières de bord : effet, couleurs, vitesse, luminosité, apparence et ajustement.
2. **Appuie sur « Allumer les lumières ».** L'application demande une fois l'autorisation de *dessiner par-dessus d'autres applications*. Active-la.
   Reviens ensuite en arrière : les lumières courent maintenant le long du bord de ton écran, aussi quand tu vas sur l'écran d'accueil.
3. **Appuie sur « 🎄 Construis ton sapin »** pour ouvrir l'affichage des sapins, cheminées et scènes. Balaie vers la gauche ou la droite pour passer d'un sapin, d'une cheminée ou d'une scène à l'autre.
4. **Appuie sur le sapin** pour accrocher une lumière. Appuie sur une lumière pour changer sa couleur. Maintiens une lumière appuyée pour la supprimer.
5. **Ouvre le menu ⋯** pour le son, la musique, les widgets, les favoris, la minuterie de sommeil, la langue et plus encore.

---

## ✨ Fonctions

### 💡 Lumières autour du bord de ton écran

Une guirlande animée suit le bord et les coins arrondis de ton écran. Les touchers passent simplement
à l'application en dessous, elle ne gêne donc pas.

| Réglage | Possibilités |
|---|---|
| **Effet** | Fixe, scintillement, clignotement en alternance, lumière chenillard, vague de couleurs et *Sur la musique* |
| **Couleurs** | 5 palettes de couleurs |
| **Vitesse** | De lente à rapide |
| **Luminosité** | 30 à 100 % |
| **Apparence** | Taille des lumières, espacement entre elles et affaissement du fil |
| **Ajustement** | Arrondi des coins et distance au bord, pour que ça s'adapte à ton écran |

- **Par-dessus tout.** Les lumières se trouvent au-dessus de ton écran d'accueil, de tes icônes, de tes widgets et des autres applications.
- **Marche et arrêt.** Via le bouton dans l'application, via le menu ⋯ (*Lumières autour de l'écran*) ou via la **tuile des réglages rapides** dans ton panneau de notifications.
- **Après un redémarrage.** Si les lumières étaient allumées quand tu as éteint le téléphone, elles reviennent toutes seules. Il faut pour cela que l'autorisation de dessiner par-dessus d'autres applications soit toujours active.
- **Sur le micro.** Dans le menu ⋯, active *Les lumières de bord réagissent au son* (ou choisis l'effet *Sur la musique*). Plus le son est fort, plus il y a de lumières allumées. Dans le silence, elles brillent doucement. L'application demande alors l'autorisation du micro. Voir [Autorisations et vie privée](#-autorisations-et-vie-privée).
- **Notification de premier plan.** Android affiche une notification tant que les lumières sont allumées. C'est une exigence d'Android pour les applications qui tournent en arrière-plan.

### 🌲 Sapins de Noël

19 sapins à parcourir en balayant, chacun avec sa propre décoration, plus ta propre photo.

- **14 sapins dessinés :** du classique et du pin au néon, à la glace et au 3D cartoon.
- **5 vrais sapins de Noël en photo :** Capitole, sapin d'intérieur, épicéa dans le parc, sapin de ville et sapin lumineux.
- **Ta propre photo :** choisis une photo de ton sapin ou de ta pièce et accroche des lumières dessus. Ça marche aussi comme fond d'écran.
- **Chaque sapin retient ses propres lumières.** Si tu passes à un autre sapin et reviens, elles sont toujours au même endroit.
- **Type de lumière par sapin**, ou un mélange de tous les types.

### 🔥 Cheminées

12 cheminées avec un vrai feu. Les 7 cheminées dessinées utilisent des centaines de particules incandescentes. Les 5 cheminées
en photo utilisent une photo avec un feu animé par-dessus. Appuie sur le feu et il s'embrase. Ta propre
guirlande s'accroche le long du manteau ou entre les montants.

### ✨ Scènes interactives

Pas seulement regarder : touche, secoue et construis.

| Scène | Que fais-tu ? |
|---|---|
| **Boule à neige** | Secoue ton téléphone ou touche : il se met à neiger. |
| **Calendrier de l'Avent** | Chaque jour une petite porte avec une surprise. |
| **Couronne de l'Avent** | Allume les bougies, dimanche après dimanche. |
| **Village de Noël avec train** | Touche les maisons pour la lumière, touche le train pour le klaxon. Maintiens la voie appuyée pour arrêter le train. Touche le ciel pour une petite étoile. |
| **Traîneau du Père Noël** | Touche le traîneau et il pleut des cadeaux. Maintiens appuyé pour une course turbo. |
| **Construire un bonhomme de neige** | Touche et il apparaît étape par étape. |
| **Patinoire** | Touche la glace pour un nouveau patineur. Les patineurs tournent les uns autour des autres. Touche un patineur et il tombe et glisse hors de la glace. |
| **Crèche** | Touche le ciel étoilé pour un vœu. Touche Marie, Joseph, les rois, le bœuf ou l'âne et ils disent quelque chose. |
| **Cadeaux** | Tes propres messages qui ne peuvent être ouverts qu'un jour choisi. |

### 🎨 Créer ta propre scène

Tu construis ta propre scène de Noël exactement comme tu la veux. Ouvre **⋯ → 🎨 Créer ta propre scène**. En haut,
tu vois exactement ce que la scène montrera plus tard, et en bas cinq onglets.

| Onglet | Que peux-tu faire ? |
|---|---|
| 🖼️ **Arrière-plan** | Choisis parmi huit dégradés de couleurs ou utilise ta **propre photo**. Active ou désactive un sol enneigé. |
| ✨ **Effets** | Neige, choses qui tombent (cadeaux, étoiles, cœurs …), confettis, étoiles scintillantes, feux d'artifice, étoiles filantes, une douce lueur et du **feu**. Chaque effet a ses propres curseurs (quantité, vitesse, taille) et sa couleur. |
| 🎅 **Autocollants** | 32 images (sapin de Noël, Père Noël, bonhomme de neige, traîneau, train …). Déplace-les, agrandis-les ou rétrécis-les et fais-les pivoter. |
| 💡 **Lumières** | **Dessine une guirlande avec ton doigt** le long d'une fenêtre, d'un toit ou d'un sapin, ou choisis *Haut*, *Arc* ou *Tout autour*. Pour chaque guirlande, choisis le type de lumières, les couleurs et l'effet. |
| 💾 **Plus** | Nom, nouvelle scène, dupliquer, toutes tes scènes, **partager**, coller et supprimer. |

**Du feu dans ta couleur.** Place le feu où tu veux et choisis orange, bleu, vert, violet ou blanc. Règle la hauteur,
la largeur et la vitesse. Touche le feu dans la scène et il s'embrase.

**Tes propres animations.** Chaque autocollant a une animation et une réaction à un toucher :

| Animation | Réaction à un toucher |
|---|---|
| Immobile, flotter, saluer, pulser, clignoter, sautiller, tourner sur soi-même, passer en roulant, tourner en rond | Rien, sautiller, étincelles, tourner, clochette ou **dire quelque chose** (une bulle avec ton propre texte) |

**Enregistrer et utiliser.** Tout est enregistré automatiquement. Tu retrouves ta scène sous le nom **Ma scène** (la
dernière de la rangée de scènes). Tu peux la définir comme **fond d'écran animé** et la garder dans tes favoris. Tu peux
conserver plusieurs scènes et passer de l'une à l'autre via *Plus → Mes scènes*.

**Partager.** Choisis *Plus → Partager* et envoie le texte par WhatsApp, e-mail ou une autre application. Le destinataire copie le
texte et choisit *Plus → Coller* dans l'éditeur. Une photo personnelle utilisée comme arrière-plan n'est pas envoyée.

### 💡 Types de lumières

Sous le sapin, choisis parmi huit types, ou un **mélange** de tout. Tu peux enregistrer le choix par sapin ou par cheminée,
ou l'appliquer à tout, y compris à ton fond d'écran.

Classique · Mini-LED · Boules de verre · Petites flammes · Petites étoiles · Cœurs · Glaçons · Flocons de neige

### 📱 Widgets

Trois widgets pour ton écran d'accueil. Maintiens appuyé un endroit vide de ton écran d'accueil, choisis *Widgets* et cherche *Lumières de Noël*.

| Widget | Que vois-tu ? |
|---|---|
| **Compte à rebours jusqu'à Noël** | Le nombre de jours avant Noël, avec un texte les jours de fête eux-mêmes. |
| **Scène du jour** | La scène qui correspond à la date d'aujourd'hui (Avent, crèche, cadeaux …). Touche pour ouvrir l'application sur cette scène. |
| **Sapin de Noël avec lumières** | Ton propre sapin avec tes lumières, avec des coins arrondis. Touche pour ouvrir l'application. |

Le widget du sapin ne se rafraîchit que si ton sapin a vraiment changé, pour ne pas peser sur ta batterie.

### 🖼️ Fond d'écran animé

Dans l'application, choisis un sapin, une cheminée ou une scène et définis-le comme **fond d'écran animé**. Chaque fois que tu vas sur ton
écran d'accueil, tu vois par exemple le feu ou le train qui roule, avec tes lumières dessus.

1. Dans l'application, choisis le sapin, la cheminée ou la scène souhaité.
2. Appuie sur le bouton **Fond d'écran** sous le sapin.
3. Confirme sur l'écran d'Android.

### 🔊 Son, musique et micro

Tout le son est **synthétisé dans l'application elle-même**. Il n'y a pas de fichiers audio.

- **Feu de cheminée :** feu qui crépite près des cheminées.
- **Clochettes** au toucher, et une **mélodie de boîte à musique** dans les scènes.
- **Quatre chants de Noël :** *Jingle Bells*, *Douce nuit*, *We wish you a merry Christmas* et *Deck the halls*, chacun avec son propre tempo, un son de boîte à musique et un léger écho. Choisis le chant via *Musique* dans le menu ⋯.
- **Sur la musique :** un effet de lumières où les lumières bougent avec le son. Il suit aussi bien les chants intégrés que le micro.

### ⋯ Le menu ⋯

| Élément | Que fait-il ? |
|---|---|
| 💡 **Lumières autour de l'écran** | Active ou désactive les lumières de bord. |
| 🎤 **Les lumières de bord réagissent au son** | Les lumières de bord bougent sur le son du micro. |
| 🔊 **Son** | Feu de cheminée, clochettes et mélodies activés ou non. |
| 🎵 **Musique** | Choisis l'un des quatre chants de Noël. |
| 📅 **Aller automatiquement à la scène du jour** | En saison, l'application s'ouvre sur la scène d'aujourd'hui. |
| 🔔 **Notification quotidienne** | Chaque jour à 18 h 00 une notification avec le compte à rebours. |
| 🎁 **Aller à la scène d'aujourd'hui** | Directement à la scène qui correspond à la date. |
| ⭐ **Looks favoris** | Enregistre sapin ou cheminée avec lumières, effet et type de lumières sous un nom de ton choix. |
| 😴 **Minuterie de sommeil** | De 15 minutes à 2 heures : les lumières s'atténuent doucement et s'éteignent. |
| 🔋 **Fréquence d'images** | Normale (30 fps), économique (15 fps) ou automatique. |
| 🖼️ **Photo personnelle** | Utilise ta propre photo comme arrière-plan. |
| 🔄 **Rechercher des mises à jour** | Cherche sur GitHub une version plus récente et ouvre la page de téléchargement. |
| 🌍 **Langue** | Automatique, ou choisis toi-même l'une des six langues. |

### 📅 Jours spéciaux

- **Scène du jour :** du 15 novembre au 6 janvier, l'application, le widget et la notification s'ouvrent sur la scène qui correspond à aujourd'hui : village de Noël en novembre, la couronne de l'Avent les dimanches de l'Avent, le calendrier de l'Avent en décembre, crèche et cadeaux autour de Noël.
- **Compte à rebours jusqu'à Noël :** en haut de l'écran du sapin et dans le widget.
- **Surprises :** certains jours spéciaux, il se passe quelque chose dans l'application (Saint-Nicolas, réveillon de Noël, premier et deuxième jour de Noël, Saint-Sylvestre, Nouvel An et Épiphanie). Reviens un jour spécial. 👀

---

## 👆 Commandes

| Geste | Où | Effet |
|---|---|---|
| Balayer gauche/droite | Sapin, cheminée ou scène | Suivant ou précédent |
| Toucher | Sapin ou cheminée | Accrocher une lumière |
| Toucher une lumière | Sapin ou cheminée | Autre couleur |
| Appui long sur une lumière | Sapin ou cheminée | Supprimer la lumière |
| Toucher | Feu | Le feu s'embrase |
| Secouer | Boule à neige | Il neige |
| Toucher | Village : maison | Lumière allumée ou éteinte |
| Toucher | Village : ciel | Petite étoile |
| Appui long sur la voie | Village | Le train s'arrête ou repart |
| Toucher / appui long | Traîneau | Cadeaux / turbo |
| Toucher | Patinoire | Nouveau patineur, ou un patineur tombe |
| Toucher un personnage | Crèche | Le personnage dit quelque chose |

---

## 🌍 Langues

| Langue | Code |
|---|---|
| Nederlands | `nl` |
| English | `en` |
| Deutsch | `de` |
| Français | `fr` |
| Español | `es` |
| 中文 | `zh` |

Par défaut, l'application suit la langue de ton téléphone. Si tu choisis toi-même une langue via **⋯ → Langue**, elle s'applique à
toute l'application, aux widgets et à la notification. Si ton téléphone est réglé sur une autre langue, l'application est en anglais.

---

## 🔐 Autorisations et vie privée

L'application n'utilise internet **que** si tu appuies sur *Rechercher des mises à jour* dans le menu ⋯. Elle demande alors à GitHub le nom de la dernière version et n'envoie rien te concernant. Pour le reste, l'application n'envoie rien. Tout reste sur ton téléphone.

| Autorisation | Pour quoi | Obligatoire ? |
|---|---|---|
| Dessiner par-dessus d'autres applications (`SYSTEM_ALERT_WINDOW`) | Les lumières de bord par-dessus l'écran d'accueil et les autres applications | Seulement pour les lumières de bord |
| Service de premier plan (`FOREGROUND_SERVICE`, `…_SPECIAL_USE`) | Faire tourner les lumières de bord en arrière-plan | Seulement pour les lumières de bord |
| Micro (`RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE`) | Lumières qui réagissent au son | Non, seulement si tu choisis cet effet |
| Internet (`INTERNET`) | Uniquement la recherche de mises à jour du menu ⋯ | Non, seulement si tu appuies dessus |
| Notifications (`POST_NOTIFICATIONS`) | Notification quotidienne et notification du service de premier plan | Non |
| Démarrage (`RECEIVE_BOOT_COMPLETED`) | Rétablir les lumières de bord et la notification après un redémarrage | Non |

- **Micro :** l'application ne mesure que le niveau sonore, pour faire bouger les lumières. Rien n'est enregistré, stocké ou envoyé. Android affiche lui-même l'indicateur de micro tant que le micro est utilisé.
- **Photos :** une photo personnelle n'est utilisée que localement dans l'application.
- **Données :** les réglages, lumières et favoris se trouvent dans le stockage propre à l'application (`SharedPreferences`). Si tu supprimes l'application, ils disparaissent.

---

## 🔋 Batterie et performances

- L'application dessine avec Canvas. Les éléments statiques (le sapin) sont conservés une seule fois dans une bitmap.
- **Fréquence d'images :** normale 30 fps, *économique* 15 fps, et *automatique* passe à 15 fps en mode économie d'énergie ou sous 20 % de batterie.
- **En arrière-plan**, rien n'est dessiné quand l'application n'est pas visible. Les lumières de bord à lumière fixe (*Fixe*) restent immobiles et ne coûtent aucune image.
- Les **widgets** ne se rafraîchissent que si quelque chose a vraiment changé.
- Le **micro** consomme de la batterie supplémentaire. C'est pourquoi il est désactivé par défaut.
- Sur l'émulateur, le temps médian de construction d'une image est passé d'environ 30 ms à environ 23 ms avec les dernières optimisations. Aucune mesure réelle sur un téléphone n'a encore été faite.

---

## ❓ Questions fréquentes

**Les lumières disparaissent au bout d'un moment.**
Certaines marques de téléphones (Xiaomi, Huawei, Samsung, OnePlus …) arrêtent agressivement les applications en arrière-plan. Dans les
réglages de batterie, désactive l'*optimisation de la batterie* pour Lumières de Noël et autorise le *démarrage automatique*.

**Je ne vois pas de lumières par-dessus mon écran d'accueil.**
Vérifie dans *Réglages → Applications → Lumières de Noël → Afficher par-dessus d'autres applications* que l'autorisation est activée.

**Les lumières ne reviennent pas après un redémarrage.**
L'application doit encore avoir l'autorisation de dessiner par-dessus d'autres applications, et les lumières doivent avoir été allumées
avant le redémarrage. Certains fabricants bloquent les récepteurs de démarrage ; autorise le *démarrage automatique*.

**Le widget n'affiche rien ou est vide.**
Supprime le widget et ajoute-le à nouveau. Le widget du sapin nécessite que l'application ait été ouverte au moins une fois.

**L'installation échoue (« application non installée »).**
Si une version avec une autre signature est déjà installée, désinstalle-la d'abord. Vérifie aussi que ton téléphone
a Android 8.0 ou plus récent.

**L'application fonctionne-t-elle sans internet ?**
Oui, entièrement. Internet n'est utilisé que pour la recherche de mises à jour que tu lances toi-même.

**Puis-je avoir l'application sur le Play Store ?**
Pas encore. L'APK est destinée à être installée soi-même.

---

## 🛠️ Compiler depuis les sources

### Prérequis

- JDK 17
- SDK Android (plateforme 34/35 et build-tools ; `ANDROID_HOME` défini)
- Pas besoin d'Android Studio : le wrapper Gradle fait tout

### Build de débogage et tests

```bash
cd Kerstlampjes
./gradlew testDebugUnitTest assembleDebug
```

L'APK se trouve dans `Kerstlampjes/app/build/outputs/apk/debug/app-debug.apk`.

Installer sur un appareil ou un émulateur :

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release (signée)

1. Crée un keystore :

   ```bash
   keytool -genkeypair -keystore release.keystore -alias kerstlampjes -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Crée `Kerstlampjes/keystore.properties` (présent dans `.gitignore`) :

   ```
   storeFile=release.keystore
   storePassword=...
   keyAlias=kerstlampjes
   keyPassword=...
   ```
3. Compile la release :

   ```bash
   ./gradlew assembleRelease
   ```

   Résultat : `app/build/outputs/apk/release/app-release.apk`. La build release utilise minify et resource-shrinking.

Garde bien le keystore : sans la même clé, tu ne peux plus mettre à jour une application installée.
Le keystore et les mots de passe n'ont **pas** leur place dans git.

### Publier une release sur GitHub

```bash
gh release create v2.3 app-release.apk --title "Kerstlampjes 2.3" --notes "..."
```

---

## 🗂️ Structure du projet

```
KerstApp/
├── Kerstlampjes/                  Projet Android (Gradle, Kotlin)
│   └── app/src/
│       ├── main/java/nl/kerstlampjes/app/
│       │   ├── MainActivity.kt        Réglages des lumières de bord
│       │   ├── TreeActivity.kt        Écran sapin/cheminée/scène, sélecteurs et menu ⋯
│       │   ├── TreeRenderer.kt        Dessin des sapins, lumières, guirlande, TreeStore
│       │   ├── Hearths.kt             Cheminées et feu à particules
│       │   ├── Scenes.kt              Les 9 scènes interactives
│       │   ├── MyScene.kt             Scène perso : recette, stockage, dessin des effets, autocollants et guirlandes
│       │   ├── SceneEditorActivity.kt Éditeur de scènes perso
│       │   ├── LightsService.kt       Service de premier plan pour les lumières de bord
│       │   ├── LightsView.kt          Vue overlay avec la guirlande
│       │   ├── TreeWallpaperService.kt Fond d'écran animé
│       │   ├── Widgets.kt             Les trois widgets
│       │   ├── Extras.kt              Son, musique, calendrier, notification, démarrage
│       │   ├── Tr.kt / Translations.kt Traductions
│       │   ├── Perf.kt                Fréquence d'images et batterie
│       │   ├── Prefs.kt               Réglages
│       │   └── Credits.kt             Sources des photos dans l'application
│       ├── main/res/                  Layouts, widgets, chaînes (nl/en/de/fr/es/zh)
│       └── test/                      Tests unitaires
├── website/                       Site web statique
│   ├── index.html, style.css      Page principale
│   ├── eggs.js + page cachée      Surprises des jours spéciaux
│   ├── media/                     Captures d'écran et courtes vidéos
│   └── Kerstlampjes.apk           APK release signée
├── CREDITS.md                     Sources et licences des photos
├── .github/workflows/android.yml  CI
└── README.md
```

---

## 🧩 Technique

- **Langage et plateforme :** Kotlin, `minSdk 26`, pas d'AndroidX. Tout est du SDK Android ordinaire.
- **Dessin :** un `TreeRenderer` partagé dessine pour l'écran du sapin, le widget et le fond d'écran animé.
  `SceneHost` gère les scènes et `HearthPainter` les cheminées.
- **Lumières de bord :** une fenêtre `TYPE_APPLICATION_OVERLAY` dans un service de premier plan (`specialUse`, avec `microphone` quand l'effet musique est actif). La fenêtre n'est pas tactile, les touchers passent donc au travers.
- **Widgets :** des `AppWidgetProvider` avec `RemoteViews`. Le widget du sapin rend une bitmap et la met à l'échelle.
- **Stockage :** `SharedPreferences` nommées `kerstlampjes`. Les lumières par sapin sont stockées sous une clé portant le nom du sapin, de sorte que l'ordre des sapins puisse changer plus tard sans perdre tes lumières.
- **Son :** les chants et effets sont synthétisés ; `AudioLevel` combine micro et synthétiseur. Le micro n'est activé que si un élément (application, fond d'écran ou lumières de bord) en a besoin.
- **Traductions :** le texte néerlandais est la clé (`Tr.t("…")`, `Tr.f(fmt, …)`). `Translations.kt` contient les cinq autres langues.
- **Performances :** cache pour la couche du sapin, réutilisation des shaders et des chemins, pas de dessin en arrière-plan, `Perf.frameDelay` pour 30/15 fps.
- **Release :** minify et shrink avec `proguard-rules.pro`, signature depuis `keystore.properties`.

### Traduire

Les textes sont dans le code sous forme de clé néerlandaise (`Tr.t("…")`). Les traductions sont dans `Translations.kt`.
Si tu ajoutes un nouveau texte, remplis les cinq traductions. Le test `TranslationsTest` vérifie que
tout est traduit dans toutes les langues et que les espaces réservés (`%d`, `%s`) correspondent.

---

## ✅ Tests et CI

- **Tests unitaires** (`./gradlew testDebugUnitTest`) :
  - `TranslationsTest` : chaque clé est traduite dans toutes les langues, avec les mêmes espaces réservés.
  - `CalendarTest` : la scène du jour est correcte pour les dimanches de l'Avent, Noël, le Nouvel An et les autres dates.
- **Lint :** `./gradlew lintDebug`.
- **GitHub Actions** (`.github/workflows/android.yml`) : à chaque push sur `main` et à chaque pull request, lint, tests unitaires et une build debug et release s'exécutent. Les APK sont conservées comme artefact.

Ce qui n'a **pas** été testé : le comportement sur un vrai téléphone (batterie, vrai micro, son des chants) et le
retour des lumières de bord après un vrai redémarrage.

---

## 💻 Windows

Il existe aussi une petite version Windows avec seulement les lumières : une guirlande le long du bord de votre écran (au-dessus de la barre des tâches), toujours visible, et vous pouvez cliquer à travers.

- **Téléchargement :** [Kerstlampjes-Windows.exe](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.0/Kerstlampjes-Windows.exe) (60 ko, [release](https://github.com/ericbruggema/kerstlampjes/releases/tag/windows-v1.0)). Rien à installer : double-cliquez et l'icône apparaît dans la zone de notification, en bas à droite près de l'horloge.
- **Marche/arrêt :** clic gauche sur l'icône. **Clic droit** ouvre le menu : paramètres, démarrer avec Windows, rechercher des mises à jour, à propos et quitter.
- **Paramètres :** effet (fixe, scintillement, alterné, chenillard, vague de couleurs), couleurs, vitesse, taille, espacement, arrondi des coins, distance du bord, affaissement, opacité. Aussi tous les écrans ou seulement l'écran principal.
- **Se masque** pendant les applis plein écran (jeux, films, présentations).
- **Confidentialité :** rien ne passe par internet, sauf si vous choisissez « Rechercher des mises à jour ». Les paramètres sont dans `%APPDATA%\Kerstlampjes`.
- **Langues :** suit la langue de Windows (Nederlands, English, Deutsch, Français, Español, 中文).
- **Windows SmartScreen :** le fichier n'est pas signé numériquement ; à l'avertissement, choisissez *Informations complémentaires* → *Exécuter quand même*.
- **Compilation :** dossier `Windows/`, `dotnet build -c Release` (.NET Framework 4.8, inclus dans Windows 10/11).

## 🤝 Open source et réutilisation

Kerstlampjes est **open source et libre pour tous** sous la [licence MIT](LICENSE). Tu peux le copier, le
modifier, le partager et même l'utiliser dans ta propre application. Le code se trouve ici : https://github.com/ericbruggema/kerstlampjes.

**Merci de citer la source.** Tu utilises du code ou des idées de ce projet (par exemple l'éditeur de scènes, les
lumières de bord ou le feu à particules) ? Alors mets un lien vers https://github.com/ericbruggema/kerstlampjes dans ton application, ton README ou ton
site web. La licence MIT exige au minimum de conserver la mention de copyright dans [`LICENSE`](LICENSE). Une
courte ligne comme « Basé sur Kerstlampjes, https://github.com/ericbruggema/kerstlampjes » suffit.

Améliorations, traductions et idées sont les bienvenues via les [issues et pull requests](https://github.com/ericbruggema/kerstlampjes/issues). Les
photos de l'application ont leur propre licence, voir ci-dessous.

---

## 📄 Sources et licences

Les photos de vrais sapins et cheminées proviennent de **Wikimedia Commons** (récupérées le 14 octobre 2026). Elles ont été
réduites et recadrées. La liste complète des auteurs et licences figure dans [`CREDITS.md`](CREDITS.md) et
dans l'application sous *Sources*.

| Photo | Auteur | Licence |
|---|---|---|
| Fire burning in a fireplace | Pöllö | CC BY 3.0 |
| Cozy Christmas Fireplace with stockings | Lindsey Turner | CC BY 2.0 |
| Fireplace, Wood fire, Rostov-on-Don | Vyacheslav Argenberg | CC BY 4.0 |
| Fireplace Burning | inconnu | Domaine public |
| 2020 Capitol Christmas Tree with Lights | USCapitol | Domaine public |
| Weihnachtsbaum im China Garten | PantheraLeo1359531 | CC BY 4.0 |
| Weihnachtsbaum am Lorenzpark | PantheraLeo1359531 | CC BY 4.0 |
| Christmas Tree in the Park | Corey Leopold | CC BY 2.0 |
| White Christmas Tree Lights at Night | photos-public-domain.com | CC0 |

La musique et le son sont synthétisés par l'application elle-même ; les chants sont traditionnels et libres de droits.

---

<div align="center">

Fait avec plein de lumières. 🎄✨

</div>
