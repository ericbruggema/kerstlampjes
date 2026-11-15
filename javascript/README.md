# 🎄 Kerstlampjes voor websites (JavaScript)

Een snoer kerstlampjes langs de rand van je webpagina. **Eén los bestand ([kerstlampjes.js](kerstlampjes.js)), geen afhankelijkheden, ongeveer 9 kB.** Klikken gaan dwars door de lampjes heen, dus ze zitten nooit in de weg. Open [demo.html](demo.html) om het uit te proberen.

*English below.*

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

A string of Christmas lights along the edge of your web page. **One single file ([kerstlampjes.js](kerstlampjes.js)), no dependencies, about 9 kB.** Clicks pass straight through the lights, so they never get in the way. Open [demo.html](demo.html) to try it.

```html
<script src="kerstlampjes.js" data-sides="top"></script>
```

or

```html
<script src="kerstlampjes.js"></script>
<script>
  Kerstlampjes.start();                                   // all around the screen
  // Kerstlampjes.start({ sides: "top" });                // top only
  // Kerstlampjes.start({ sides: "top,left,right" });     // any combination
</script>
```

| Option | Default | What it does |
|---|---|---|
| `sides` | `"all"` | Edges with lights: `all`, or a list of `top`, `right`, `bottom`, `left` (commas or an array) |
| `palette` | `"classic"` | `classic`, `warm`, `redgreen`, `ice`, `pink`, or your own array: `["#f00", "#0f0"]` |
| `mode` | `"twinkle"` | `steady`, `twinkle`, `alternate`, `chase`, `wave` |
| `size` / `spacing` | `14` / `44` | Bulb size and distance between bulbs, in pixels |
| `speed` | `5` | 1 to 10 |
| `inset` / `sag` | `4` / `6` | Distance from the edge and sag of the wire |
| `opacity` | `1` | 0 to 1 |
| `wire` | `"#1f4d2b"` | Colour of the wire |
| `zIndex` | high | Stacking level |
| `target` | whole page | An element (selector or node): the lights then hang around that element only |

The same names work as `data-` attributes. Control them while running with `update({ ... })`, `stop()` and `toggle()`; make several strings with `Kerstlampjes.create({ ... }).start()`. The script pauses in hidden tabs, stays still for people who prefer reduced motion, and sends nothing anywhere. MIT licence.
