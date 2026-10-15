<div align="center">

# 🎄 Luces de Navidad (Kerstlampjes)

**Una guirnalda de luces a lo largo del borde de tu pantalla, también por encima de tu pantalla de inicio, tus iconos y tus widgets. Además, como extra: tus propios árboles de Navidad, chimeneas, escenas y un editor para tu propia escena. Para Android.**

[⬇️ Descargar el APK](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.3)

🌍 **Léelo en:** [Nederlands](README.md) · [English](README.en.md) · [Deutsch](README.de.md) · [Français](README.fr.md) · **Español** · [中文](README.zh.md)

💻 **Novedad: ¡Luces de Navidad para Windows!** Solo las luces, siempre visibles desde la bandeja del sistema. [⬇️ Descargar (60 kB)](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.0/Kerstlampjes-Windows.exe) · [Más información](#-windows)

</div>

> 📷 Esta traducción no incluye capturas de pantalla, para mantenerla corta. Todas las imágenes están en el [README neerlandés](README.md) y en el sitio web. La propia aplicación está disponible en los seis idiomas.
> Los nombres de menú de abajo son los que aparecen cuando la aplicación está en español; la aplicación sigue el idioma de tu teléfono (consulta [Idiomas](#-idiomas)).

---

## Contenido

1. ¿Qué es?
2. Instalación
3. Primer uso
4. Funciones
5. Controles
6. Idiomas
7. Permisos y privacidad
8. Batería y rendimiento
9. Preguntas frecuentes
10. Compilar desde el código fuente
11. Estructura del proyecto
12. Técnica
13. Pruebas y CI
14. Fuentes y licencias

---

## 🎅 ¿Qué es?

Luces de Navidad es una aplicación de Android que convierte tu teléfono en una decoración navideña. **Lo principal son las luces
en el fondo de tu teléfono**: una guirnalda alrededor del borde de tu pantalla que también se queda por encima de tu pantalla de inicio.
Todo lo demás es un extra.

**Lo principal**

- una **guirnalda de luces a lo largo del borde de la pantalla** que también se queda por encima de tu pantalla de inicio, tus iconos, widgets y otras aplicaciones, con 5 efectos, 5 esquemas de color, encendido y apagado mediante un icono de ajustes rápidos, vuelta tras reiniciar y (configurable) reacción al sonido;

**Extras**

- **19 árboles de Navidad** (14 dibujados, 5 en foto) más tu **propia foto**, donde tú mismo cuelgas las luces;
- **12 chimeneas** con fuego real de partículas o fotos de un fuego que crepita;
- **9 escenas interactivas**: bola de nieve, calendario de Adviento, corona de Adviento, pueblo navideño con tren, trineo, muñeco de nieve, pista de hielo, belén y regalos;
- un **editor de escenas**: tu propio fondo (también tu propia foto), fuego de tu propio color, nieve, fuegos artificiales, pegatinas con sus propias animaciones, guirnaldas dibujadas a mano y compartir con otros;
- **8 tipos de luces**, **3 widgets**, **fondo de pantalla animado**, **4 villancicos**, un temporizador de sueño y looks favoritos;
- sorpresas en **días especiales** (San Nicolás, Nochebuena, Nochevieja, Reyes …);
- **6 idiomas**: neerlandés, inglés, alemán, francés, español y chino.

Todo se dibuja y sintetiza en la propia aplicación (Canvas, sin motor de juegos, sin AndroidX). Las únicas
imágenes son las fotos de árboles y chimeneas reales, de Wikimedia Commons ([fuentes](#-fuentes-y-licencias)).
**No hay anuncios ni cuentas**. Internet solo se usa cuando tú mismo tocas *Buscar actualizaciones*.

| | |
|---|---|
| Nombre del paquete | `nl.kerstlampjes.app` |
| Versión | 2.3 (versionCode 5) |
| Android | 8.0 (API 26) o más reciente |
| Lenguaje | Kotlin |
| Tamaño | unos 3 MB |

---

## 📲 Instalación

### Opción 1: el APK desde GitHub (recomendada)

1. En tu teléfono, ve a la [release v2.3](https://github.com/ericbruggema/kerstlampjes/releases/tag/v2.3).
2. Descarga **`Kerstlampjes-2.3.apk`**.
3. Abre el archivo descargado. Si Android te pide permiso para *instalar desde orígenes desconocidos*, concédelo a tu navegador o gestor de archivos.
4. Toca **Instalar** y después **Abrir**.

> **¿Actualizas una versión anterior?** La release está firmada con una clave propia. Si en tu teléfono hay una versión
> con otra firma (por ejemplo una compilación de depuración), desinstálala primero. Tus
> ajustes se perderán.

### Opción 2: el APK en el repositorio

El mismo APK firmado también está en el repo: [`website/Kerstlampjes.apk`](website/Kerstlampjes.apk).

### Opción 3: compilarlo tú mismo

Consulta [Compilar desde el código fuente](#-compilar-desde-el-código-fuente).

---

## 🚀 Primer uso

1. **Abre la aplicación.** Ves la pantalla de ajustes de las luces del borde: efecto, colores, velocidad, brillo, aspecto y ajuste.
2. **Toca «Encender luces».** La aplicación pide una vez permiso para *dibujar sobre otras aplicaciones*. Actívalo.
   Vuelve atrás: las luces ya corren por el borde de tu pantalla, también cuando vas a la pantalla de inicio.
3. **Toca «🎄 Construye tu árbol de Navidad»** para abrir la vista de árboles, chimeneas y escenas. Desliza a izquierda o derecha para cambiar entre árboles, chimeneas y escenas.
4. **Toca el árbol** para colgar una luz. Toca una luz para cambiar su color. Mantén pulsada una luz para quitarla.
5. **Abre el menú ⋯** para sonido, música, widgets, favoritos, temporizador de sueño, idioma y más.

---

## ✨ Funciones

### 💡 Luces alrededor del borde de tu pantalla

Una guirnalda animada sigue el borde y las esquinas redondeadas de tu pantalla. Los toques pasan
directamente a la aplicación de debajo, así que no estorba.

| Ajuste | Posibilidades |
|---|---|
| **Efecto** | Fijo, parpadeo suave, parpadeo alterno, luz corredora, onda de color y *Al ritmo de la música* |
| **Colores** | 5 esquemas de color |
| **Velocidad** | De lenta a rápida |
| **Brillo** | 30 a 100 % |
| **Aspecto** | Tamaño de las luces, distancia entre ellas y caída del cable |
| **Ajuste** | Redondeo de las esquinas y distancia al borde, para que encaje con tu pantalla |

- **Por encima de todo.** Las luces están sobre tu pantalla de inicio, tus iconos, tus widgets y otras aplicaciones.
- **Encender y apagar.** Con el botón de la aplicación, con el menú ⋯ (*Luces alrededor de la pantalla*) o con el **icono de ajustes rápidos** del panel de notificaciones.
- **Tras reiniciar.** Si las luces estaban encendidas cuando apagaste el teléfono, vuelven solas. Para ello, el permiso para dibujar sobre otras aplicaciones debe seguir activado.
- **Con el micrófono.** En el menú ⋯ activa *Las luces del borde reaccionan al sonido* (o elige el efecto *Al ritmo de la música*). Cuanto más fuerte es el sonido, más luces se encienden. En silencio brillan suavemente. La aplicación pedirá entonces permiso para el micrófono. Consulta [Permisos y privacidad](#-permisos-y-privacidad).
- **Notificación en primer plano.** Android muestra una notificación mientras las luces están encendidas. Es un requisito de Android para las aplicaciones que se ejecutan en segundo plano.

### 🌲 Árboles de Navidad

19 árboles entre los que deslizar, cada uno con su propia decoración, más tu propia foto.

- **14 árboles dibujados:** desde el clásico y el abeto hasta neón, hielo y 3D de dibujos animados.
- **5 árboles de Navidad reales en foto:** Capitolio, árbol de interior, abeto en el parque, árbol de ciudad y árbol de lucecitas.
- **Tu propia foto:** elige una foto de tu propio árbol o habitación y cuelga luces encima. También funciona como fondo de pantalla.
- **Cada árbol recuerda sus propias luces.** Si cambias de árbol y vuelves, siguen colgadas en el mismo sitio.
- **Tipo de luz por árbol**, o una mezcla de todos los tipos.

### 🔥 Chimeneas

12 chimeneas con fuego real. Las 7 chimeneas dibujadas usan cientos de partículas incandescentes. Las 5 chimeneas
con foto usan una foto con un fuego en movimiento por encima. Toca el fuego y se aviva. Tu propia
guirnalda cuelga a lo largo de la repisa o entre los postes.

### ✨ Escenas interactivas

No solo mirar: toca, agita y construye.

| Escena | ¿Qué haces? |
|---|---|
| **Bola de nieve** | Agita tu teléfono o toca: empieza a nevar. |
| **Calendario de Adviento** | Cada día una ventanita con una sorpresa. |
| **Corona de Adviento** | Enciende las velas, domingo a domingo. |
| **Pueblo navideño con tren** | Toca las casitas para dar luz, toca el tren para el claxon. Mantén pulsada la vía para detener el tren. Toca el cielo para una estrellita. |
| **Trineo de Papá Noel** | Toca el trineo y llueven regalos. Mantén pulsado para un viaje turbo. |
| **Construir un muñeco de nieve** | Toca y aparece paso a paso. |
| **Pista de hielo** | Toca el hielo para un patinador nuevo. Los patinadores giran unos alrededor de otros. Toca un patinador y se cae y se desliza fuera del hielo. |
| **Belén** | Toca el cielo estrellado para un deseo. Toca a María, José, los reyes, el buey o el burro y dicen algo. |
| **Regalos** | Tus propios mensajes que solo se pueden abrir un día elegido. |

### 🎨 Crear tu propia escena

Construyes tu propia escena navideña exactamente como tú quieras. Abre **⋯ → 🎨 Crear tu propia escena**. Arriba
ves exactamente lo que mostrará la escena más adelante y abajo cinco pestañas.

| Pestaña | ¿Qué puedes hacer? |
|---|---|
| 🖼️ **Fondo** | Elige entre ocho degradados de color o usa tu **propia foto**. Activa o desactiva un suelo nevado. |
| ✨ **Efectos** | Nieve, cosas que caen (regalos, estrellas, corazones …), confeti, estrellas centelleantes, fuegos artificiales, estrellas fugaces, un suave resplandor y **fuego**. Cada efecto tiene sus propios deslizadores (cantidad, velocidad, tamaño) y color. |
| 🎅 **Pegatinas** | 32 imágenes (árbol de Navidad, Papá Noel, muñeco de nieve, trineo, tren …). Arrástralas, hazlas más grandes o más pequeñas y gíralas. |
| 💡 **Luces** | **Dibuja con el dedo una guirnalda** a lo largo de una ventana, un tejado o un árbol, o elige *Arriba*, *Arco* o *Alrededor*. Para cada guirnalda elige el tipo de luces, los colores y el efecto. |
| 💾 **Más** | Nombre, nueva escena, duplicar, todas tus escenas, **compartir**, pegar y eliminar. |

**Fuego de tu propio color.** Coloca el fuego donde quieras y elige naranja, azul, verde, morado o blanco. Ajusta la altura,
la anchura y la velocidad. Toca el fuego en la escena y se aviva.

**Tus propias animaciones.** Cada pegatina tiene una animación y una reacción a un toque:

| Animación | Reacción a un toque |
|---|---|
| Quieta, flotar, saludar, pulsar, parpadear, saltar, girar, pasar de largo, dar vueltas | Nada, saltar, chispas, girar, campanita o **decir algo** (un bocadillo con tu propio texto) |

**Guardar y usar.** Todo se guarda automáticamente. Encontrarás tu escena como la escena **Mi escena** (la
última de la fila de escenas). Puedes ponerla como **fondo de pantalla animado** y guardarla en tus favoritos. Puedes
guardar varias escenas y cambiar entre ellas con *Más → Mis escenas*.

**Compartir.** Elige *Más → Compartir* y envía el texto por WhatsApp, correo u otra aplicación. El destinatario copia el
texto y elige en el editor *Más → Pegar*. Una foto propia usada como fondo no se envía.

### 💡 Tipos de luces

Debajo del árbol, elige entre ocho tipos, o una **mezcla** de todos. Puedes guardar la elección por árbol o chimenea,
o aplicarla a todo, incluido tu fondo de pantalla.

Clásicas · Mini LED · Bolas de cristal · Llamitas · Estrellitas · Corazones · Carámbanos · Copos de nieve

### 📱 Widgets

Tres widgets para tu pantalla de inicio. Mantén pulsado un hueco vacío de tu pantalla de inicio, elige *Widgets* y busca *Luces de Navidad*.

| Widget | ¿Qué ves? |
|---|---|
| **Cuenta atrás para Navidad** | El número de días que faltan para Navidad, con un texto en los propios días de fiesta. |
| **Escena de hoy** | La escena que corresponde a la fecha de hoy (Adviento, belén, regalos …). Toca para abrir la aplicación en esa escena. |
| **Árbol de Navidad con luces** | Tu propio árbol con tus luces, con esquinas redondeadas. Toca para abrir la aplicación. |

El widget del árbol solo se actualiza cuando tu árbol ha cambiado de verdad, para no cargar tu batería.

### 🖼️ Fondo de pantalla animado

En la aplicación, elige un árbol, una chimenea o una escena y ponlo como **fondo de pantalla animado**. Cada vez que vas a tu
pantalla de inicio ves, por ejemplo, el fuego o el tren en marcha, con tus luces encima.

1. En la aplicación, elige el árbol, la chimenea o la escena que quieras.
2. Toca el botón **Fondo de pantalla** debajo del árbol.
3. Confirma en la pantalla de Android.

### 🔊 Sonido, música y micrófono

Todo el sonido se **sintetiza en la propia aplicación**. No hay archivos de audio.

- **Fuego de chimenea:** fuego que crepita junto a las chimeneas.
- **Campanitas** al tocar, y una **melodía de caja de música** en las escenas.
- **Cuatro villancicos:** *Jingle Bells*, *Noche de paz*, *We wish you a merry Christmas* y *Deck the halls*, cada uno con su propio tempo, un sonido de caja de música y un ligero eco. Elige el villancico con *Música* en el menú ⋯.
- **Al ritmo de la música:** un efecto de luces en el que las luces se mueven con el sonido. Sigue tanto a los villancicos incorporados como al micrófono.

### ⋯ El menú ⋯

| Elemento | ¿Qué hace? |
|---|---|
| 💡 **Luces alrededor de la pantalla** | Enciende o apaga las luces del borde. |
| 🎤 **Las luces del borde reaccionan al sonido** | Las luces del borde se mueven con el sonido del micrófono. |
| 🔊 **Sonido** | Fuego de chimenea, campanitas y melodías activados o desactivados. |
| 🎵 **Música** | Elige uno de los cuatro villancicos. |
| 📅 **Ir automáticamente a la escena del día** | En temporada, la aplicación se abre en la escena de hoy. |
| 🔔 **Notificación diaria** | Cada día a las 18:00 una notificación con la cuenta atrás. |
| 🎁 **Ir a la escena de hoy** | Directo a la escena que corresponde a la fecha. |
| ⭐ **Looks favoritos** | Guarda árbol o chimenea con luces, efecto y tipo de luces bajo un nombre propio. |
| 😴 **Temporizador de sueño** | De 15 minutos a 2 horas: las luces se atenúan con calma y se apagan. |
| 🔋 **Velocidad de imagen** | Normal (30 fps), económica (15 fps) o automática. |
| 🖼️ **Foto propia** | Usa una foto propia como fondo. |
| 🔄 **Buscar actualizaciones** | Busca en GitHub una versión más reciente y abre la página de descarga. |
| 🌍 **Idioma** | Automático, o elige tú mismo uno de los seis idiomas. |

### 📅 Días especiales

- **Escena del día:** del 15 de noviembre al 6 de enero la aplicación, el widget y la notificación se abren en la escena que corresponde a hoy: pueblo navideño en noviembre, la corona de Adviento los domingos de Adviento, el calendario de Adviento en diciembre, belén y regalos en torno a Navidad.
- **Cuenta atrás para Navidad:** en la parte superior de la pantalla del árbol y en el widget.
- **Sorpresas:** en algunos días especiales ocurre algo en la aplicación (San Nicolás, Nochebuena, primer y segundo día de Navidad, Nochevieja, Año Nuevo y Reyes). Vuelve en un día especial. 👀

---

## 👆 Controles

| Gesto | Dónde | Efecto |
|---|---|---|
| Deslizar izquierda/derecha | Árbol, chimenea o escena | Siguiente o anterior |
| Tocar | Árbol o chimenea | Colgar una luz |
| Tocar una luz | Árbol o chimenea | Otro color |
| Mantener pulsada una luz | Árbol o chimenea | Quitar la luz |
| Tocar | Fuego | El fuego se aviva |
| Agitar | Bola de nieve | Nieva |
| Tocar | Pueblo: casita | Luz encendida o apagada |
| Tocar | Pueblo: cielo | Estrellita |
| Mantener pulsada la vía | Pueblo | El tren se detiene o vuelve a andar |
| Tocar / mantener pulsado | Trineo | Regalos / turbo |
| Tocar | Pista de hielo | Patinador nuevo, o un patinador se cae |
| Tocar una figura | Belén | La figura dice algo |

---

## 🌍 Idiomas

| Idioma | Código |
|---|---|
| Nederlands | `nl` |
| English | `en` |
| Deutsch | `de` |
| Français | `fr` |
| Español | `es` |
| 中文 | `zh` |

Por defecto la aplicación sigue el idioma de tu teléfono. Si eliges tú mismo un idioma en **⋯ → Idioma**, se aplica a
toda la aplicación, los widgets y la notificación. Si tu teléfono está en otro idioma, la aplicación está en inglés.

---

## 🔐 Permisos y privacidad

La aplicación usa internet **solo** cuando tocas *Buscar actualizaciones* en el menú ⋯. Entonces pregunta a GitHub por el nombre de la última versión y no envía nada sobre ti. Aparte de eso, la aplicación no envía nada. Todo se queda en tu teléfono.

| Permiso | Para qué | ¿Obligatorio? |
|---|---|---|
| Dibujar sobre otras aplicaciones (`SYSTEM_ALERT_WINDOW`) | Las luces del borde sobre la pantalla de inicio y otras aplicaciones | Solo para las luces del borde |
| Servicio en primer plano (`FOREGROUND_SERVICE`, `…_SPECIAL_USE`) | Mantener las luces del borde en segundo plano | Solo para las luces del borde |
| Micrófono (`RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE`) | Luces que reaccionan al sonido | No, solo si eliges ese efecto |
| Internet (`INTERNET`) | Solo la búsqueda de actualizaciones del menú ⋯ | No, solo si la tocas |
| Notificaciones (`POST_NOTIFICATIONS`) | Notificación diaria y la notificación del servicio en primer plano | No |
| Inicio (`RECEIVE_BOOT_COMPLETED`) | Restablecer las luces del borde y la notificación tras reiniciar | No |

- **Micrófono:** la aplicación solo mide el nivel de sonido, para que las luces se muevan. No se graba, almacena ni envía nada. Android muestra por sí mismo el indicador de micrófono mientras se usa.
- **Fotos:** una foto propia solo se usa localmente en la aplicación.
- **Datos:** los ajustes, las luces y los favoritos están en el almacenamiento propio de la aplicación (`SharedPreferences`). Si eliminas la aplicación, desaparecen.

---

## 🔋 Batería y rendimiento

- La aplicación dibuja con Canvas. Las partes estáticas (el árbol) se guardan una sola vez en un bitmap.
- **Velocidad de imagen:** normal 30 fps, *económica* 15 fps, y *automática* pasa a 15 fps con el ahorro de batería o con menos del 20 % de batería.
- **En segundo plano** no se dibuja nada cuando la aplicación no está visible. Las luces del borde con luz fija (*Fijo*) se quedan quietas y no cuestan fotogramas.
- Los **widgets** solo se actualizan cuando algo ha cambiado de verdad.
- El **micrófono** consume batería adicional. Por eso está desactivado por defecto.
- En el emulador, el tiempo mediano de construcción de un fotograma bajó de unos 30 ms a unos 23 ms con las últimas optimizaciones. Aún no se han hecho mediciones reales en un teléfono.

---

## ❓ Preguntas frecuentes

**Las luces desaparecen al cabo de un rato.**
Algunas marcas de teléfonos (Xiaomi, Huawei, Samsung, OnePlus …) detienen de forma agresiva las aplicaciones en segundo plano. En los
ajustes de batería desactiva la *optimización de batería* para Luces de Navidad y permite el *inicio automático*.

**No veo luces sobre mi pantalla de inicio.**
Comprueba en *Ajustes → Aplicaciones → Luces de Navidad → Mostrar sobre otras aplicaciones* que el permiso está activado.

**Las luces no vuelven tras reiniciar.**
La aplicación debe seguir teniendo permiso para dibujar sobre otras aplicaciones, y las luces tienen que haber estado encendidas
antes del reinicio. Algunos fabricantes bloquean los receptores de arranque; permite el *inicio automático*.

**El widget no muestra nada o está vacío.**
Elimina el widget y añádelo de nuevo. El widget del árbol necesita que la aplicación se haya abierto al menos una vez.

**La instalación falla («aplicación no instalada»).**
Si ya hay instalada una versión con otra firma, desinstálala primero. Comprueba también que tu teléfono
tenga Android 8.0 o más reciente.

**¿Funciona la aplicación sin internet?**
Sí, por completo. Internet solo se usa para la búsqueda de actualizaciones que inicias tú mismo.

**¿Puedo conseguir la aplicación en Play Store?**
Todavía no. El APK está pensado para instalarlo tú mismo.

---

## 🛠️ Compilar desde el código fuente

### Requisitos

- JDK 17
- SDK de Android (plataforma 34/35 y build-tools; `ANDROID_HOME` definido)
- No hace falta Android Studio: el wrapper de Gradle lo hace todo

### Compilación de depuración y pruebas

```bash
cd Kerstlampjes
./gradlew testDebugUnitTest assembleDebug
```

El APK queda en `Kerstlampjes/app/build/outputs/apk/debug/app-debug.apk`.

Instalar en un dispositivo o emulador:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Release (firmada)

1. Crea un keystore:

   ```bash
   keytool -genkeypair -keystore release.keystore -alias kerstlampjes -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Crea `Kerstlampjes/keystore.properties` (está en `.gitignore`):

   ```
   storeFile=release.keystore
   storePassword=...
   keyAlias=kerstlampjes
   keyPassword=...
   ```
3. Compila la release:

   ```bash
   ./gradlew assembleRelease
   ```

   Resultado: `app/build/outputs/apk/release/app-release.apk`. La compilación release usa minify y resource-shrinking.

Guarda bien el keystore: sin la misma clave ya no podrás actualizar una aplicación instalada.
El keystore y las contraseñas **no** deben estar en git.

### Publicar una release en GitHub

```bash
gh release create v2.3 app-release.apk --title "Kerstlampjes 2.3" --notes "..."
```

---

## 🗂️ Estructura del proyecto

```
KerstApp/
├── Kerstlampjes/                  Proyecto Android (Gradle, Kotlin)
│   └── app/src/
│       ├── main/java/nl/kerstlampjes/app/
│       │   ├── MainActivity.kt        Ajustes de las luces del borde
│       │   ├── TreeActivity.kt        Pantalla árbol/chimenea/escena, selectores y menú ⋯
│       │   ├── TreeRenderer.kt        Dibujo de árboles, luces, guirnalda, TreeStore
│       │   ├── Hearths.kt             Chimeneas y fuego de partículas
│       │   ├── Scenes.kt              Las 9 escenas interactivas
│       │   ├── MyScene.kt             Escena propia: receta, almacenamiento, dibujo de efectos, pegatinas y guirnaldas
│       │   ├── SceneEditorActivity.kt Editor de escenas propias
│       │   ├── LightsService.kt       Servicio en primer plano para las luces del borde
│       │   ├── LightsView.kt          Vista overlay con la guirnalda
│       │   ├── TreeWallpaperService.kt Fondo de pantalla animado
│       │   ├── Widgets.kt             Los tres widgets
│       │   ├── Extras.kt              Sonido, música, calendario, notificación, arranque
│       │   ├── Tr.kt / Translations.kt Traducciones
│       │   ├── Perf.kt                Velocidad de imagen y batería
│       │   ├── Prefs.kt               Ajustes
│       │   └── Credits.kt             Fuentes de las fotos en la aplicación
│       ├── main/res/                  Layouts, widgets, cadenas (nl/en/de/fr/es/zh)
│       └── test/                      Pruebas unitarias
├── website/                       Sitio web estático
│   ├── index.html, style.css      Página principal
│   ├── eggs.js + página oculta    Sorpresas de días especiales
│   ├── media/                     Capturas de pantalla y vídeos cortos
│   └── Kerstlampjes.apk           APK release firmado
├── CREDITS.md                     Fuentes y licencias de las fotos
├── .github/workflows/android.yml  CI
└── README.md
```

---

## 🧩 Técnica

- **Lenguaje y plataforma:** Kotlin, `minSdk 26`, sin AndroidX. Todo es SDK de Android normal.
- **Dibujo:** un `TreeRenderer` compartido dibuja para la pantalla del árbol, el widget y el fondo de pantalla animado.
  `SceneHost` gestiona las escenas y `HearthPainter` las chimeneas.
- **Luces del borde:** una ventana `TYPE_APPLICATION_OVERLAY` en un servicio en primer plano (`specialUse`, con `microphone` cuando el efecto de música está activo). La ventana no es táctil, así que los toques pasan a través.
- **Widgets:** `AppWidgetProvider` con `RemoteViews`. El widget del árbol renderiza un bitmap y lo escala.
- **Almacenamiento:** `SharedPreferences` con el nombre `kerstlampjes`. Las luces de cada árbol se guardan bajo una clave con el nombre del árbol, de modo que el orden de los árboles pueda cambiar más adelante sin perder tus luces.
- **Sonido:** los villancicos y efectos se sintetizan; `AudioLevel` combina micrófono y sintetizador. El micrófono solo se activa cuando un componente (aplicación, fondo de pantalla o luces del borde) lo necesita.
- **Traducciones:** el texto en neerlandés es la clave (`Tr.t("…")`, `Tr.f(fmt, …)`). `Translations.kt` contiene los otros cinco idiomas.
- **Rendimiento:** caché para la capa del árbol, reutilización de shaders y rutas, sin dibujo en segundo plano, `Perf.frameDelay` para 30/15 fps.
- **Release:** minify y shrink con `proguard-rules.pro`, firma desde `keystore.properties`.

### Traducir

Los textos están en el código como clave en neerlandés (`Tr.t("…")`). Las traducciones están en `Translations.kt`.
Si añades un texto nuevo, rellena las cinco traducciones. La prueba `TranslationsTest` comprueba que
todo está traducido en todos los idiomas y que los marcadores (`%d`, `%s`) coinciden.

---

## ✅ Pruebas y CI

- **Pruebas unitarias** (`./gradlew testDebugUnitTest`):
  - `TranslationsTest`: cada clave está traducida en todos los idiomas, con los mismos marcadores.
  - `CalendarTest`: la escena del día es correcta para los domingos de Adviento, Navidad, Nochevieja y el resto de fechas.
- **Lint:** `./gradlew lintDebug`.
- **GitHub Actions** (`.github/workflows/android.yml`): en cada push a `main` y en cada pull request se ejecutan lint, pruebas unitarias y una compilación debug y release. Los APK se guardan como artefacto.

Lo que **no** se ha probado: el comportamiento en un teléfono real (batería, micrófono real, sonido de los villancicos) y la
vuelta de las luces del borde tras un reinicio real.

---

## 💻 Windows

También hay una pequeña versión para Windows solo con las luces: una guirnalda a lo largo del borde de tu pantalla (sobre la barra de tareas), siempre visible, y puedes hacer clic a través de ella.

- **Descarga:** [Kerstlampjes-Windows.exe](https://github.com/ericbruggema/kerstlampjes/releases/download/windows-v1.0/Kerstlampjes-Windows.exe) (60 kB, [release](https://github.com/ericbruggema/kerstlampjes/releases/tag/windows-v1.0)). Nada que instalar: doble clic y el icono aparece en la bandeja del sistema, abajo a la derecha junto al reloj.
- **Encender/apagar:** clic izquierdo en el icono. **Clic derecho** abre el menú: ajustes, iniciar con Windows, buscar actualizaciones, acerca de y salir.
- **Ajustes:** efecto (fijo, parpadeo suave, alterno, luz corredora, ola de color), colores, velocidad, tamaño, separación, redondeo de esquinas, distancia al borde, caída del cable, opacidad. También todas las pantallas o solo la principal.
- **Se oculta** con apps a pantalla completa (juegos, películas, presentaciones).
- **Privacidad:** nada pasa por internet, salvo que elijas «Buscar actualizaciones». Los ajustes están en `%APPDATA%\Kerstlampjes`.
- **Idiomas:** sigue el idioma de Windows (Nederlands, English, Deutsch, Français, Español, 中文).
- **Windows SmartScreen:** el archivo no está firmado digitalmente; en la advertencia elige *Más información* → *Ejecutar de todas formas*.
- **Compilar:** carpeta `Windows/`, `dotnet build -c Release` (.NET Framework 4.8, incluido en Windows 10/11).

## 🤝 Código abierto y reutilización

Kerstlampjes es de **código abierto y de uso libre para todos** bajo la [licencia MIT](LICENSE). Puedes copiarlo,
modificarlo, compartirlo e incluso usarlo en tu propia aplicación. El código está aquí: https://github.com/ericbruggema/kerstlampjes.

**Por favor, menciona la fuente.** ¿Usas código o ideas de este proyecto (por ejemplo el editor de escenas, las
luces del borde o el fuego de partículas)? Entonces enlaza https://github.com/ericbruggema/kerstlampjes en tu aplicación, README o sitio web. La licencia
MIT exige al menos conservar el aviso de copyright en [`LICENSE`](LICENSE). Una línea breve como «Basado en
Kerstlampjes, https://github.com/ericbruggema/kerstlampjes» es suficiente.

Mejoras, traducciones e ideas son bienvenidas mediante [issues y pull requests](https://github.com/ericbruggema/kerstlampjes/issues). Las fotos de la
aplicación tienen su propia licencia, véase abajo.

---

## 📄 Fuentes y licencias

Las fotos de árboles y chimeneas reales proceden de **Wikimedia Commons** (consultadas el 14 de octubre de 2026). Se han
reducido y recortado. La lista completa de autores y licencias está en [`CREDITS.md`](CREDITS.md) y
en la aplicación, en *Fuentes*.

| Foto | Autor | Licencia |
|---|---|---|
| Fire burning in a fireplace | Pöllö | CC BY 3.0 |
| Cozy Christmas Fireplace with stockings | Lindsey Turner | CC BY 2.0 |
| Fireplace, Wood fire, Rostov-on-Don | Vyacheslav Argenberg | CC BY 4.0 |
| Fireplace Burning | desconocido | Dominio público |
| 2020 Capitol Christmas Tree with Lights | USCapitol | Dominio público |
| Weihnachtsbaum im China Garten | PantheraLeo1359531 | CC BY 4.0 |
| Weihnachtsbaum am Lorenzpark | PantheraLeo1359531 | CC BY 4.0 |
| Christmas Tree in the Park | Corey Leopold | CC BY 2.0 |
| White Christmas Tree Lights at Night | photos-public-domain.com | CC0 |

La música y el sonido están sintetizados por la propia aplicación; los villancicos son tradicionales y libres de derechos.

---

<div align="center">

Hecho con muchas luces. 🎄✨

</div>
