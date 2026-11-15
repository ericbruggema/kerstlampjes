# 🎄 Kerstlampjes voor websites (JavaScript)

Een snoer kerstlampjes langs de rand van je webpagina, met acht soorten lampjes en optionele versiering (sneeuw, sterretjes, vallende sterren, ijspegels, slinger, treintje en lichtkrant). **Eén los bestand ([kerstlampjes.js](kerstlampjes.js)), geen afhankelijkheden, 25 kB (8 kB ingepakt).** Klikken gaan dwars door de lampjes heen, dus ze zitten nooit in de weg. Open [demo.html](demo.html) om alles uit te proberen.

*English below. In het Engels heet het script `christmaslights.js` (`ChristmasLights.start(...)`), met [Engelse demo](demo.en.html); het is hetzelfde bestand als `kerstlampjes.js`.*

## Het kortst mogelijk

```html
<script src="kerstlampjes.js" data-sides="top"></script>
```

Meer is niet nodig. De lampjes verschijnen meteen.

## Met een aanroep

```html
<script src="kerstlampjes.js"></script>
<script>
  Kerstlampjes.start();                                   // rondom het hele scherm
  // Kerstlampjes.start({ sides: "top" });                // alleen boven
  // Kerstlampjes.start({ sides: "top,left,right" });     // een combinatie
  // Kerstlampjes.start({ sides: "bottom", palette: "warm", mode: "steady" });
</script>
```

## Instellingen

Alles is optioneel. Dezelfde namen werken als `data-`-attribuut (`data-sides="top"`) en in `start({ ... })`.

| Instelling | Standaard | Wat het doet |
|---|---|---|
| `sides` | `"all"` | Zijden met lampjes: `all`, of een lijst van `top`, `right`, `bottom`, `left` (komma's of een array) |
| `palette` | `"classic"` | `classic` (bont), `warm`, `redgreen`, `ice`, `pink`, of je eigen array: `["#f00", "#0f0"]` |
| `mode` | `"twinkle"` | `steady` (vast aan), `twinkle` (fonkelen), `alternate` (om en om), `chase` (lopend licht), `wave` (kleurgolf) |
| `bulbStyle` | `"classic"` | `classic`, `led` (mini-ledje), `ball` (glazen bol), `flame` (vlammetje), `star`, `heart`, `icicle`, `snowflake`, of `mix` (van alles door elkaar) |
| `decor` | `""` | Versiering, een lijst of `all`: `snow` (sneeuw), `stars` (sterretjes), `shooting` (vallende sterren), `icicles` (ijspegels), `garland` (slinger), `train` (treintje langs de rand), `marquee` (lichtkrant) |
| `garlandBalls` | `true` | Ballen in de slinger |
| `trainWagons` | `3` | Aantal wagons van het treintje, 1 tot 8 |
| `marquee` | "Vrolijk kerstfeest…" | Tekst van de lichtkrant |
| `glow` | `true` | Gloed rond de lampjes aan of uit |
| `respectReducedMotion` | `true` | Staat in het systeem "minder beweging" aan, dan blijven de lampjes stilstaan. Met `false` animeren ze altijd (de demo's doen dat) |
| `size` | `14` | Grootte van een lampje in pixels |
| `spacing` | `44` | Afstand tussen de lampjes in pixels |
| `speed` | `5` | Snelheid, 1 tot 10 |
| `inset` | `4` | Afstand tot de rand |
| `sag` | `6` | Doorhang van de draad |
| `opacity` | `1` | Doorzichtigheid, 0 tot 1 |
| `wire` | `"#1f4d2b"` | Kleur van de draad |
| `zIndex` | hoog | Laag boven andere elementen |
| `target` | hele pagina | Een element (selector of element): dan hangen de lampjes alleen om dat element |

## Aanpassen, uitzetten, meerdere snoeren

```js
var lampjes = Kerstlampjes.start({ sides: "top" });
lampjes.update({ palette: "ice", sides: "top,left" });   // terwijl ze aan zijn
lampjes.stop();                                          // weghalen
lampjes.toggle();                                        // aan/uit

// Alles erop en eraan:
Kerstlampjes.start({ bulbStyle: "mix", decor: "snow,stars,garland,train,marquee", marquee: "Fijne feestdagen!" });

// Twee snoeren met eigen instellingen, bijvoorbeeld om een kaart:
Kerstlampjes.create({ sides: "top", palette: "warm" }).start();
Kerstlampjes.create({ target: "#kaart", mode: "chase" }).start();
```

## Goed om te weten

- Met `mode: "steady"` wordt er maar één keer getekend: geen animatie, dus geen belasting.
- Het script pauzeert als het tabblad niet zichtbaar is en volgt het formaat van het venster.
- Heeft iemand in zijn systeem "minder beweging" ingesteld, dan blijven de lampjes stilstaan.
- Er wordt niets geladen of verzonden: alles gebeurt in de pagina zelf.
- Vrij te gebruiken onder de MIT-licentie. Een verwijzing naar [github.com/ericbruggema/kerstlampjes](https://github.com/ericbruggema/kerstlampjes) wordt gewaardeerd.

---

# 🎄 Christmas lights for websites (JavaScript)

A string of Christmas lights along the edge of your web page, with eight bulb styles and optional decorations (snow, sparkles, shooting stars, icicles, garland, little train and a scrolling text sign). **One single file ([christmaslights.js](christmaslights.js)), no dependencies, 25 kB (8 kB gzipped).** Clicks pass straight through the lights, so they never get in the way. Open [demo.en.html](demo.en.html) to try it.

```html
<script src="christmaslights.js" data-sides="top"></script>
```

or

```html
<script src="christmaslights.js"></script>
<script>
  ChristmasLights.start();                                   // all around the screen
  // ChristmasLights.start({ sides: "top" });                // top only
  // ChristmasLights.start({ sides: "top,left,right" });     // any combination
</script>
```

| Option | Default | What it does |
|---|---|---|
| `sides` | `"all"` | Edges with lights: `all`, or a list of `top`, `right`, `bottom`, `left` (commas or an array) |
| `palette` | `"classic"` | `classic`, `warm`, `redgreen`, `ice`, `pink`, or your own array: `["#f00", "#0f0"]` |
| `mode` | `"twinkle"` | `steady`, `twinkle`, `alternate`, `chase`, `wave` |
| `bulbStyle` | `"classic"` | `classic`, `led`, `ball`, `flame`, `star`, `heart`, `icicle`, `snowflake`, or `mix` |
| `decor` | `""` | Decorations, a list or `all`: `snow`, `stars`, `shooting`, `icicles`, `garland`, `train`, `marquee` |
| `garlandBalls` / `trainWagons` / `marquee` | `true` / `3` / text | Baubles in the garland, number of wagons (1 to 8), text of the sign |
| `glow` | `true` | Glow around the bulbs on or off |
| `respectReducedMotion` | `true` | If the system asks for reduced motion (e.g. Windows: animation effects off), the lights stay still. Use `false` to always animate (the demos do) |
| `size` / `spacing` | `14` / `44` | Bulb size and distance between bulbs, in pixels |
| `speed` | `5` | 1 to 10 |
| `inset` / `sag` | `4` / `6` | Distance from the edge and sag of the wire |
| `opacity` | `1` | 0 to 1 |
| `wire` | `"#1f4d2b"` | Colour of the wire |
| `zIndex` | high | Stacking level |
| `target` | whole page | An element (selector or node): the lights then hang around that element only |

The Dutch name keeps working too: `Kerstlampjes` and `kerstlampjes.js` are the same file and object. The same option names work as `data-` attributes. Control them while running with `update({ ... })`, `stop()` and `toggle()`; make several strings with `ChristmasLights.create({ ... }).start()`. The script pauses in hidden tabs, stays still for people who prefer reduced motion, and sends nothing anywhere. MIT licence.
