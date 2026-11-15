/*!
 * Kerstlampjes voor websites – een snoer kerstlampjes langs de rand van de pagina.
 * Eén los bestand, geen afhankelijkheden. MIT-licentie.
 * https://github.com/ericbruggema/kerstlampjes
 *
 * Gebruik (alles is optioneel):
 *   <script src="kerstlampjes.js"></script>
 *   <script>Kerstlampjes.start({ sides: "top,left,right", palette: "warm", mode: "twinkle" });</script>
 *
 * Of zonder code, met data-attributen op de scripttag:
 *   <script src="kerstlampjes.js" data-sides="top" data-palette="ice"></script>
 */
(function (root) {
  "use strict";

  var PALETTES = {
    classic: ["#ff3b30", "#ffd54f", "#4cd964", "#40a9ff", "#ff9500", "#ff4fd8"],
    warm: ["#ffe3a8", "#ffd27f", "#ffebc4"],
    redgreen: ["#ff3b30", "#34c759", "#ffc94a"],
    ice: ["#7fd4ff", "#ffffff", "#3d7bff"],
    pink: ["#ff6fb5", "#b36bff", "#ffc2e2"]
  };
  var MODES = ["steady", "twinkle", "alternate", "chase", "wave"];
  var DEFAULTS = {
    sides: "all",        // "all" of een lijst: "top,left,right,bottom" (of een array)
    palette: "classic",  // classic | warm | redgreen | ice | pink, of eigen array met kleuren
    mode: "twinkle",     // steady | twinkle | alternate | chase | wave
    size: 14,            // grootte van een lampje in pixels
    spacing: 44,         // afstand tussen de lampjes in pixels
    speed: 5,            // 1..10
    inset: 4,            // afstand tot de rand in pixels
    sag: 6,              // doorhang van de draad
    opacity: 1,          // 0..1
    wire: "#1f4d2b",     // kleur van de draad
    zIndex: 2147483000,  // boven alles, klikken gaan er dwars doorheen
    target: null         // standaard de hele pagina; of een element (dan alleen daaromheen)
  };

  function parseSides(v) {
    var all = ["top", "right", "bottom", "left"];
    if (!v || v === "all") return all;
    var list = Array.isArray(v) ? v : String(v).split(/[\s,]+/);
    var out = [];
    list.forEach(function (s) {
      s = String(s).toLowerCase().trim();
      if (s === "all") out = all.slice();
      else if (all.indexOf(s) >= 0 && out.indexOf(s) < 0) out.push(s);
    });
    return out;
  }

  function create(userOpts) {
    var o = {}, k;
    for (k in DEFAULTS) o[k] = DEFAULTS[k];
    var canvas = document.createElement("canvas");
    var ctx = canvas.getContext("2d");
    var colors, sides, bulbs = [], sprites = {}, raf = 0, running = false, dpr = 1, W = 0, H = 0;
    var reduce = root.matchMedia && root.matchMedia("(prefers-reduced-motion: reduce)").matches;
    var target = null;

    function configure(opts) {
      for (k in opts) if (k in DEFAULTS) o[k] = opts[k];
      sides = parseSides(o.sides);
      colors = Array.isArray(o.palette) ? o.palette : (PALETTES[o.palette] || PALETTES.classic);
      target = o.target ? (typeof o.target === "string" ? document.querySelector(o.target) : o.target) : null;
      canvas.style.cssText = "position:" + (target ? "absolute" : "fixed") + ";pointer-events:none;z-index:" + o.zIndex +
        ";opacity:" + o.opacity + ";left:0;top:0";
      sprites = {};
      layout();
    }

    // vierkant sprite van één lampje (wijst omlaag), per kleur en helderheid één keer getekend
    function sprite(ci, level) {
      var key = ci + ":" + level;
      if (sprites[key]) return sprites[key];
      var z = o.size, d = Math.ceil(z * 5.2), c = document.createElement("canvas");
      c.width = c.height = Math.ceil(d * dpr);
      var g = c.getContext("2d");
      g.scale(dpr, dpr);
      g.translate(d / 2, d / 2 - z * 1.0);
      var k2 = level / 7, col = colors[ci % colors.length];
      var cy = z * 1.1;
      // gloed
      var gr = g.createRadialGradient(0, cy, 0, 0, cy, z * 2.4);
      gr.addColorStop(0, rgba(col, 0.75 * k2 * k2));
      gr.addColorStop(1, rgba(col, 0));
      g.fillStyle = gr; g.beginPath(); g.arc(0, cy, z * 2.4, 0, 6.2832); g.fill();
      // fitting
      g.fillStyle = "#2b3a30";
      g.fillRect(-z * 0.22, -z * 0.05, z * 0.44, z * 0.45);
      // glas: gedimd als het lampje uit is, fel als het aan is
      g.fillStyle = mix("#222a25", col, 0.18 + 0.82 * k2);
      g.beginPath();
      g.moveTo(0, z * 0.4);
      g.bezierCurveTo(z * 0.62, z * 0.4, z * 0.58, z * 1.15, 0, z * 1.75);
      g.bezierCurveTo(-z * 0.58, z * 1.15, -z * 0.62, z * 0.4, 0, z * 0.4);
      g.fill();
      g.fillStyle = "rgba(255,255,255," + (0.25 + 0.55 * k2) + ")";
      g.beginPath(); g.ellipse(-z * 0.16, z * 0.85, z * 0.07, z * 0.22, 0, 0, 6.2832); g.fill();
      sprites[key] = c;
      return c;
    }

    function rgb(hex) {
      hex = hex.replace("#", "");
      if (hex.length === 3) hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
      return [parseInt(hex.substr(0, 2), 16), parseInt(hex.substr(2, 2), 16), parseInt(hex.substr(4, 2), 16)];
    }
    function rgba(hex, a) { var c = rgb(hex); return "rgba(" + c[0] + "," + c[1] + "," + c[2] + "," + a + ")"; }
    function mix(a, b, f) {
      var x = rgb(a), y = rgb(b);
      return "rgb(" + Math.round(x[0] + (y[0] - x[0]) * f) + "," + Math.round(x[1] + (y[1] - x[1]) * f) + "," + Math.round(x[2] + (y[2] - x[2]) * f) + ")";
    }

    function layout() {
      var box = target ? target.getBoundingClientRect() : { left: 0, top: 0, width: root.innerWidth, height: root.innerHeight };
      W = Math.round(box.width); H = Math.round(box.height);
      dpr = Math.min(root.devicePixelRatio || 1, 2);
      canvas.width = Math.round(W * dpr); canvas.height = Math.round(H * dpr);
      canvas.style.width = W + "px"; canvas.style.height = H + "px";
      if (target) {
        // absoluut ten opzichte van de pagina (het canvas hangt direct in <body>)
        canvas.style.left = (box.left + (root.pageXOffset || 0)) + "px"; canvas.style.top = (box.top + (root.pageYOffset || 0)) + "px";
      }
      sprites = {};
      bulbs = [];
      var z = o.size, edge = o.inset + z * 0.15, sp = Math.max(o.spacing, z * 1.2), rnd = seeded(42), idx = 0;
      sides.forEach(function (side) {
        var horiz = side === "top" || side === "bottom";
        var len = (horiz ? W : H) - 2 * edge, n = Math.max(2, Math.floor(len / sp));
        var step = len / n;
        for (var i = 0; i <= n; i++) {
          var pos = edge + i * step, b = { side: side, i: idx++, phase: rnd() * 6.2832, freq: 0.35 + rnd() * 0.9 };
          if (side === "top") { b.x = pos; b.y = edge; b.rot = 0; }
          else if (side === "bottom") { b.x = pos; b.y = H - edge; b.rot = Math.PI; }
          else if (side === "left") { b.x = edge; b.y = pos; b.rot = -Math.PI / 2; }
          else { b.x = W - edge; b.y = pos; b.rot = Math.PI / 2; }
          b.ci = b.i % colors.length;
          bulbs.push(b);
        }
        // doorhangende draad tussen de lampjes
      });
      drawn = false;
    }

    function seeded(n) { return function () { n = (n * 1664525 + 1013904223) % 4294967296; return n / 4294967296; }; }

    var drawn = false;
    function intensity(b, t) {
      var sp = o.speed / 5;
      switch (o.mode) {
        case "steady": return 1;
        case "alternate":
          var v = 0.5 + 0.5 * Math.sin(6.2832 * t * sp * 0.5 + (b.i % 2) * Math.PI);
          return 0.12 + 0.88 * clamp((v - 0.3) / 0.4);
        case "chase":
          var q = (b.i - t * sp * 3) % 4; if (q < 0) q += 4;
          return 0.12 + 0.88 * Math.exp(-q * q * 1.2);
        case "wave":
          return 0.25 + 0.75 * (0.5 + 0.5 * Math.sin(b.i * 0.35 - t * sp * 2.5));
        default:
          var w = 0.5 + 0.5 * Math.sin(6.2832 * t * sp * b.freq + b.phase);
          return 0.2 + 0.8 * Math.pow(w, 2.2);
      }
    }
    function clamp(v) { return v < 0 ? 0 : v > 1 ? 1 : v; }

    function wire() {
      ctx.strokeStyle = o.wire; ctx.lineWidth = Math.max(1.5, o.size * 0.14); ctx.lineCap = "round";
      var prev = null;
      bulbs.forEach(function (b) {
        if (prev && prev.side === b.side) {
          var mx = (prev.x + b.x) / 2, my = (prev.y + b.y) / 2;
          var nx = Math.sin(b.rot) * -1, ny = Math.cos(b.rot); // naar binnen
          ctx.beginPath(); ctx.moveTo(prev.x, prev.y);
          ctx.quadraticCurveTo(mx + nx * o.sag * 2, my + ny * o.sag * 2, b.x, b.y);
          ctx.stroke();
        }
        prev = b;
      });
    }

    function frame(ms) {
      if (!running) return;
      var t = ms / 1000;
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      ctx.clearRect(0, 0, W, H);
      wire();
      var half = Math.ceil(o.size * 5.2) / 2, dy = o.size * 1.0;
      for (var n = 0; n < bulbs.length; n++) {
        var b = bulbs[n], lvl = Math.max(0, Math.min(7, Math.round(intensity(b, t) * 7)));
        var s = sprite(b.ci, lvl);
        ctx.save();
        ctx.translate(b.x, b.y); ctx.rotate(b.rot);
        ctx.drawImage(s, -half, -half + dy, half * 2, half * 2);
        ctx.restore();
      }
      if (o.mode === "steady" || reduce) { running = false; raf = 0; return; }
      raf = root.requestAnimationFrame(frame);
    }

    function onResize() { layout(); if (!running) { running = true; raf = root.requestAnimationFrame(frame); } }
    function onVisible() {
      if (document.hidden) { running = false; if (raf) root.cancelAnimationFrame(raf); raf = 0; }
      else if (api.active) { running = true; raf = root.requestAnimationFrame(frame); }
    }

    var api = {
      active: false,
      /** Toont de lampjes. */
      start: function (opts) {
        if (opts) configure(opts); else configure({});
        document.body.appendChild(canvas);
        if (!api.active) {
          root.addEventListener("resize", onResize);
          document.addEventListener("visibilitychange", onVisible);
        }
        api.active = true; running = true;
        if (!raf) raf = root.requestAnimationFrame(frame);
        return api;
      },
      /** Past instellingen aan terwijl de lampjes aan zijn: update({ palette: "ice", sides: "top" }). */
      update: function (opts) {
        configure(opts || {});
        if (api.active && !raf) { running = true; raf = root.requestAnimationFrame(frame); }
        return api;
      },
      /** Haalt de lampjes weg. */
      stop: function () {
        api.active = false; running = false;
        if (raf) root.cancelAnimationFrame(raf); raf = 0;
        root.removeEventListener("resize", onResize);
        document.removeEventListener("visibilitychange", onVisible);
        if (canvas.parentNode) canvas.parentNode.removeChild(canvas);
        return api;
      },
      toggle: function () { return api.active ? api.stop() : api.start(); }
    };
    if (userOpts) configure(userOpts); else configure({});
    return api;
  }

  var Kerstlampjes = {
    palettes: PALETTES,
    modes: MODES,
    /** Maakt één snoer en zet het meteen aan. */
    start: function (opts) { return Kerstlampjes.instance = create(opts).start(); },
    stop: function () { if (Kerstlampjes.instance) Kerstlampjes.instance.stop(); },
    /** Maakt een snoer zonder het aan te zetten (voor meerdere snoeren naast elkaar). */
    create: create
  };
  root.Kerstlampjes = Kerstlampjes;

  // Automatisch starten via data-attributen op de scripttag: <script src="kerstlampjes.js" data-sides="top">
  var me = document.currentScript;
  if (me && (me.hasAttribute("data-auto") || Object.keys(me.dataset).length)) {
    var opts = {};
    Object.keys(me.dataset).forEach(function (key) {
      if (key === "auto" || !(key in DEFAULTS)) return;
      var v = me.dataset[key];
      opts[key] = typeof DEFAULTS[key] === "number" ? Number(v) : v;
    });
    var go = function () { Kerstlampjes.start(opts); };
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", go); else go();
  }
})(window);
