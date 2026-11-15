/*!
 * Kerstlampjes / Christmas Lights for websites – a string of Christmas lights along the edge of the page,
 * with optional decorations. One single file, no dependencies. MIT licence.
 * https://github.com/ericbruggema/kerstlampjes
 *
 * Usage (every option is optional) / Gebruik (alles is optioneel):
 *   <script src="christmaslights.js"></script>          (or / of kerstlampjes.js: the same file)
 *   <script>ChristmasLights.start({ sides: "top,left,right", palette: "warm", decor: "snow,garland" });</script>
 *   Dutch name / Nederlandse naam: Kerstlampjes.start({ ... })
 *
 * Or without code, with data attributes on the script tag / Of zonder code, met data-attributen:
 *   <script src="christmaslights.js" data-sides="top" data-bulb-style="star" data-decor="snow"></script>
 *
 * The marquee text defaults to English when <html lang="en"> is set, otherwise to Dutch.
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
  var BULBS = ["classic", "led", "ball", "flame", "star", "heart", "icicle", "snowflake"];
  var DECOR = ["snow", "stars", "shooting", "icicles", "garland", "train", "marquee"];
  var DEFAULTS = {
    sides: "all",          // "all" of een lijst: "top,left,right,bottom" (of een array)
    palette: "classic",    // classic | warm | redgreen | ice | pink, of eigen array met kleuren
    mode: "twinkle",       // steady | twinkle | alternate | chase | wave
    bulbStyle: "classic",  // classic | led | ball | flame | star | heart | icicle | snowflake | mix
    decor: "",             // versiering, bijvoorbeeld "snow,stars,garland" (zie README)
    garlandBalls: true,    // ballen in de slinger
    trainWagons: 3,        // aantal wagons van het treintje
    marquee: /^en/i.test(document.documentElement.lang || "") ? "Merry Christmas and a Happy New Year!" : "Vrolijk kerstfeest en een gelukkig nieuwjaar!", // text of the sign / tekst van de lichtkrant
    size: 14,              // grootte van een lampje in pixels
    spacing: 44,           // afstand tussen de lampjes in pixels
    speed: 5,              // 1..10
    inset: 4,              // afstand tot de rand in pixels
    sag: 6,                // doorhang van de draad
    opacity: 1,            // 0..1
    wire: "#1f4d2b",       // kleur van de draad
    zIndex: 2147483000,    // boven alles, klikken gaan er dwars doorheen
    target: null           // standaard de hele pagina; of een element (dan alleen daaromheen)
  };

  function list(v, all) {
    if (!v || v === "none") return [];
    if (v === "all") return all.slice();
    var src = Array.isArray(v) ? v : String(v).split(/[\s,]+/), out = [];
    src.forEach(function (s) {
      s = String(s).toLowerCase().trim();
      if (s === "all") out = all.slice();
      else if (all.indexOf(s) >= 0 && out.indexOf(s) < 0) out.push(s);
    });
    return out;
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
  function clamp(v) { return v < 0 ? 0 : v > 1 ? 1 : v; }
  function seeded(n) { return function () { n = (n * 1664525 + 1013904223) % 4294967296; return n / 4294967296; }; }

  /** Gesloten afgeronde rechthoek: punt en looprichting op afstand s (met de klok mee). */
  function rim(x0, y0, x1, y1, radius) {
    var R = Math.max(0, Math.min(radius, (x1 - x0) / 2, (y1 - y0) / 2));
    var sw = (x1 - x0) - 2 * R, sh = (y1 - y0) - 2 * R, arc = R * Math.PI / 2;
    var seg = [sw, arc, sh, arc, sw, arc, sh, arc];
    var total = 2 * sw + 2 * sh + 4 * arc;
    return {
      length: total,
      at: function (sIn) {
        var s = sIn % total; if (s < 0) s += total;
        var k = 0;
        while (k < 7 && s >= seg[k]) { s -= seg[k]; k++; }
        if (k === 0) return { x: x0 + R + s, y: y0, a: 0 };
        if (k === 2) return { x: x1, y: y0 + R + s, a: Math.PI / 2 };
        if (k === 4) return { x: x1 - R - s, y: y1, a: Math.PI };
        if (k === 6) return { x: x0, y: y1 - R - s, a: -Math.PI / 2 };
        var cx = (k === 1 || k === 3) ? x1 - R : x0 + R, cy = (k === 1 || k === 7) ? y0 + R : y1 - R;
        var th = -Math.PI / 2 + ((k - 1) >> 1) * Math.PI / 2 + (R > 0 ? s / R : 0);
        return { x: cx + R * Math.cos(th), y: cy + R * Math.sin(th), a: th + Math.PI / 2 };
      }
    };
  }

  function create(userOpts) {
    var o = {}, k;
    for (k in DEFAULTS) o[k] = DEFAULTS[k];
    var canvas = document.createElement("canvas");
    var ctx = canvas.getContext("2d");
    var colors, sides, decor, bulbs = [], sprites = {}, raf = 0, running = false, dpr = 1, W = 0, H = 0, target = null;
    var flakes = [], sparks = [], shoot = null, nextShoot = 3, icicles = [], rnd = seeded(7);
    var reduce = root.matchMedia && root.matchMedia("(prefers-reduced-motion: reduce)").matches;
    var api;

    function has(d) { return decor.indexOf(d) >= 0; }
    function animated() { return reduce ? false : (o.mode !== "steady" || decor.length > 0 || !!target); }

    function configure(opts) {
      for (k in opts) if (k in DEFAULTS) o[k] = opts[k];
      sides = list(o.sides, ["top", "right", "bottom", "left"]);
      decor = list(o.decor, DECOR);
      colors = Array.isArray(o.palette) ? o.palette : (PALETTES[o.palette] || PALETTES.classic);
      target = o.target ? (typeof o.target === "string" ? document.querySelector(o.target) : o.target) : null;
      canvas.style.cssText = "position:" + (target ? "absolute" : "fixed") + ";pointer-events:none;z-index:" + o.zIndex +
        ";opacity:" + o.opacity + ";left:0;top:0";
      layout();
    }

    // ---------- lampjes ----------
    function styleOf(b) { return o.bulbStyle === "mix" ? BULBS[(b.i * 3 + (b.i >> 3)) % 8] : (BULBS.indexOf(o.bulbStyle) >= 0 ? o.bulbStyle : "classic"); }

    function drawBulb(g, style, col, k2, z) {
      var shine = "rgba(255,255,255," + (0.25 + 0.55 * k2) + ")";
      function cap(l, t, w, h) { g.fillStyle = "#2b3a30"; g.fillRect(l, t, w, h); }
      function dot(x, y, w, h) { g.fillStyle = shine; g.beginPath(); g.ellipse(x + w / 2, y + h / 2, w / 2, h / 2, 0, 0, 6.2832); g.fill(); }
      g.fillStyle = col;
      switch (style) {
        case "led":
          cap(-z * 0.12, -z * 0.05, z * 0.24, z * 0.37);
          g.fillStyle = col; g.beginPath(); g.arc(0, z * 0.62, z * 0.34, 0, 6.2832); g.fill();
          dot(-z * 0.18, z * 0.44, z * 0.16, z * 0.16); break;
        case "ball":
          cap(-z * 0.22, -z * 0.05, z * 0.44, z * 0.43);
          g.fillStyle = col; g.beginPath(); g.arc(0, z * 1.02, z * 0.66, 0, 6.2832); g.fill();
          dot(-z * 0.42, z * 0.68, z * 0.3, z * 0.44); break;
        case "flame":
          cap(-z * 0.16, -z * 0.05, z * 0.32, z * 0.47);
          g.fillStyle = col; g.beginPath(); g.moveTo(0, z * 1.9);
          g.bezierCurveTo(z * 0.62, z * 1.25, z * 0.5, z * 0.6, 0, z * 0.42);
          g.bezierCurveTo(-z * 0.5, z * 0.6, -z * 0.62, z * 1.25, 0, z * 1.9); g.fill();
          dot(-z * 0.2, z * 0.8, z * 0.14, z * 0.45); break;
        case "star":
          cap(-z * 0.1, -z * 0.05, z * 0.2, z * 0.35);
          g.fillStyle = col; g.beginPath();
          for (var i = 0; i < 10; i++) {
            var r = i % 2 === 0 ? z * 0.78 : z * 0.34, a = -Math.PI / 2 + i * Math.PI / 5;
            g[i ? "lineTo" : "moveTo"](r * Math.cos(a), z * 1.05 + r * Math.sin(a));
          }
          g.fill(); dot(-z * 0.16, z * 0.87, z * 0.16, z * 0.16); break;
        case "heart":
          cap(-z * 0.1, -z * 0.05, z * 0.2, z * 0.35);
          var hr = z * 0.62, cy = z * 1.0;
          g.fillStyle = col; g.beginPath(); g.moveTo(0, cy + hr * 0.95);
          g.bezierCurveTo(-hr * 1.4, cy + hr * 0.05, -hr * 0.8, cy - hr * 0.95, 0, cy - hr * 0.35);
          g.bezierCurveTo(hr * 0.8, cy - hr * 0.95, hr * 1.4, cy + hr * 0.05, 0, cy + hr * 0.95); g.fill();
          dot(-hr * 0.58, cy - hr * 0.28, hr * 0.26, hr * 0.26); break;
        case "icicle":
          cap(-z * 0.14, -z * 0.05, z * 0.28, z * 0.35);
          g.fillStyle = col; g.beginPath();
          g.moveTo(-z * 0.3, z * 0.3); g.lineTo(z * 0.3, z * 0.3); g.lineTo(z * 0.06, z * 2.1); g.lineTo(0, z * 2.25); g.lineTo(-z * 0.06, z * 2.1); g.fill();
          g.fillStyle = shine; g.fillRect(-z * 0.2, z * 0.45, z * 0.1, z * 0.75); break;
        case "snowflake":
          cap(-z * 0.1, -z * 0.05, z * 0.2, z * 0.35);
          g.strokeStyle = col; g.lineWidth = Math.max(1.2, z * 0.14); g.lineCap = "round";
          for (var j = 0; j < 3; j++) {
            var an = j * Math.PI / 3, dx = Math.cos(an) * z * 0.7, dy = Math.sin(an) * z * 0.7;
            g.beginPath(); g.moveTo(-dx, z * 1.05 - dy); g.lineTo(dx, z * 1.05 + dy); g.stroke();
          }
          g.fillStyle = col; g.beginPath(); g.arc(0, z * 1.05, z * 0.16, 0, 6.2832); g.fill(); break;
        default: // klassiek
          cap(-z * 0.22, -z * 0.05, z * 0.44, z * 0.45);
          g.fillStyle = col; g.beginPath(); g.moveTo(0, z * 0.4);
          g.bezierCurveTo(z * 0.62, z * 0.4, z * 0.58, z * 1.15, 0, z * 1.75);
          g.bezierCurveTo(-z * 0.58, z * 1.15, -z * 0.62, z * 0.4, 0, z * 0.4); g.fill();
          dot(-z * 0.23, z * 0.63, z * 0.14, z * 0.44);
      }
    }

    // één lampje per kleur, helderheid en soort vooraf getekend; daarna alleen nog neerzetten
    function sprite(ci, level, style) {
      var key = style + ":" + ci + ":" + level;
      if (sprites[key]) return sprites[key];
      var z = o.size, d = Math.ceil(z * 5.2), c = document.createElement("canvas");
      c.width = c.height = Math.ceil(d * dpr);
      var g = c.getContext("2d");
      g.scale(dpr, dpr);
      g.translate(d / 2, d / 2 - z * 1.0);
      var k2 = level / 7, col = colors[ci % colors.length], cy = z * 1.1;
      var gr = g.createRadialGradient(0, cy, 0, 0, cy, z * 2.4);
      gr.addColorStop(0, rgba(col, 0.75 * k2 * k2));
      gr.addColorStop(1, rgba(col, 0));
      g.fillStyle = gr; g.beginPath(); g.arc(0, cy, z * 2.4, 0, 6.2832); g.fill();
      drawBulb(g, style, mix("#222a25", col, 0.18 + 0.82 * k2), k2, z);
      sprites[key] = c;
      return c;
    }

    function layout() {
      var box = target ? target.getBoundingClientRect() : { left: 0, top: 0, width: root.innerWidth, height: root.innerHeight };
      W = Math.round(box.width); H = Math.round(box.height);
      dpr = Math.min(root.devicePixelRatio || 1, 2);
      canvas.width = Math.round(W * dpr); canvas.height = Math.round(H * dpr);
      canvas.style.width = W + "px"; canvas.style.height = H + "px";
      if (target) {
        canvas.style.left = (box.left + (root.pageXOffset || 0)) + "px";
        canvas.style.top = (box.top + (root.pageYOffset || 0)) + "px";
      }
      sprites = {};
      bulbs = [];
      var z = o.size, edge = o.inset + z * 0.15, sp = Math.max(o.spacing, z * 1.2), r2 = seeded(42), idx = 0;
      sides.forEach(function (side) {
        var horiz = side === "top" || side === "bottom";
        var len = (horiz ? W : H) - 2 * edge, n = Math.max(2, Math.floor(len / sp)), step = len / n;
        for (var i = 0; i <= n; i++) {
          var pos = edge + i * step, b = { side: side, i: idx++, phase: r2() * 6.2832, freq: 0.35 + r2() * 0.9 };
          if (side === "top") { b.x = pos; b.y = edge; b.rot = 0; }
          else if (side === "bottom") { b.x = pos; b.y = H - edge; b.rot = Math.PI; }
          else if (side === "left") { b.x = edge; b.y = pos; b.rot = -Math.PI / 2; }
          else { b.x = W - edge; b.y = pos; b.rot = Math.PI / 2; }
          b.ci = b.i % colors.length;
          bulbs.push(b);
        }
      });
      // versiering: sneeuw, sterretjes, ijspegels
      flakes = []; sparks = []; icicles = [];
      for (var f = 0; f < Math.min(140, Math.round(W * H / 14000)); f++)
        flakes.push({ x: rnd() * W, y: rnd() * H, v: 18 + rnd() * 34, s: 1 + rnd() * 2.4, p: rnd() * 6.28 });
      for (var q = 0; q < 14; q++)
        sparks.push({ x: rnd() * W, y: rnd() * H, s: 4 + rnd() * 7, p: rnd() * 6.28, f: 0.4 + rnd() * 0.9 });
      for (var x = 10; x < W - 10; x += 14 + rnd() * 22)
        icicles.push({ x: x, w: 5 + rnd() * 6, l: 12 + rnd() * 34, p: rnd() * 6.28 });
    }

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

    function inward(side) {
      return side === "top" ? [0, 1] : side === "bottom" ? [0, -1] : side === "left" ? [1, 0] : [-1, 0];
    }

    function wire() {
      ctx.strokeStyle = o.wire; ctx.lineWidth = Math.max(1.5, o.size * 0.14); ctx.lineCap = "round";
      var prev = null;
      bulbs.forEach(function (b) {
        if (prev && prev.side === b.side) {
          var d = inward(b.side), mx = (prev.x + b.x) / 2 + d[0] * o.sag * 2, my = (prev.y + b.y) / 2 + d[1] * o.sag * 2;
          ctx.beginPath(); ctx.moveTo(prev.x, prev.y); ctx.quadraticCurveTo(mx, my, b.x, b.y); ctx.stroke();
        }
        prev = b;
      });
    }

    // ---------- versiering ----------
    function drawSnow(t) {
      ctx.fillStyle = "#fff";
      flakes.forEach(function (f) {
        var y = (f.y + t * f.v * (o.speed / 5)) % (H + 10), x = f.x + Math.sin(t * 0.6 + f.p) * 14;
        ctx.globalAlpha = 0.5 + 0.4 * Math.sin(f.p);
        ctx.beginPath(); ctx.arc((x + W) % W, y - 5, f.s, 0, 6.2832); ctx.fill();
      });
      ctx.globalAlpha = 1;
    }

    function drawStars(t) {
      sparks.forEach(function (s) {
        var a = Math.pow(0.5 + 0.5 * Math.sin(t * s.f * 2 + s.p), 3);
        if (a < 0.04) return;
        ctx.fillStyle = "rgba(255,244,200," + a + ")";
        ctx.beginPath();
        for (var i = 0; i < 8; i++) {
          var r = i % 2 ? s.s * 0.22 : s.s, an = i * Math.PI / 4;
          ctx[i ? "lineTo" : "moveTo"](s.x + r * Math.cos(an), s.y + r * Math.sin(an));
        }
        ctx.fill();
      });
    }

    function drawShoot(t, dt) {
      nextShoot -= dt;
      if (!shoot && nextShoot <= 0) {
        shoot = { x: rnd() * W, y: rnd() * H * 0.3, vx: 520 + rnd() * 260, vy: 260 + rnd() * 160, life: 0 };
        nextShoot = 4 + rnd() * 6;
      }
      if (!shoot) return;
      shoot.life += dt; shoot.x += shoot.vx * dt; shoot.y += shoot.vy * dt;
      var len = 90, n = Math.hypot(shoot.vx, shoot.vy), a = clamp(1 - shoot.life / 1.4);
      var g = ctx.createLinearGradient(shoot.x, shoot.y, shoot.x - shoot.vx / n * len, shoot.y - shoot.vy / n * len);
      g.addColorStop(0, "rgba(255,255,255," + a + ")"); g.addColorStop(1, "rgba(255,255,255,0)");
      ctx.strokeStyle = g; ctx.lineWidth = 2.4; ctx.lineCap = "round";
      ctx.beginPath(); ctx.moveTo(shoot.x, shoot.y); ctx.lineTo(shoot.x - shoot.vx / n * len, shoot.y - shoot.vy / n * len); ctx.stroke();
      ctx.fillStyle = "rgba(255,255,255," + a + ")"; ctx.beginPath(); ctx.arc(shoot.x, shoot.y, 2.6, 0, 6.2832); ctx.fill();
      if (shoot.life > 1.4 || shoot.x > W + 50 || shoot.y > H + 50) shoot = null;
    }

    function drawIcicles(t) {
      icicles.forEach(function (c) {
        var g = ctx.createLinearGradient(0, 0, 0, c.l);
        g.addColorStop(0, "rgba(220,240,255,0.95)"); g.addColorStop(1, "rgba(150,200,255,0.55)");
        ctx.fillStyle = g;
        ctx.beginPath(); ctx.moveTo(c.x - c.w / 2, 0); ctx.lineTo(c.x + c.w / 2, 0); ctx.lineTo(c.x, c.l); ctx.fill();
        var tw = Math.pow(0.5 + 0.5 * Math.sin(t * 1.6 + c.p), 6);
        if (tw > 0.1) { ctx.fillStyle = "rgba(255,255,255," + tw + ")"; ctx.beginPath(); ctx.arc(c.x, c.l, 1.8, 0, 6.2832); ctx.fill(); }
      });
    }

    function drawGarland() {
      var z = o.size, ball = ["#e5484d", "#f4c25b", "#4aa3ff", "#c9d1d9"], cnt = 0;
      var inn = Math.max(2, o.inset + z * 0.5);
      sides.concat(sides.length ? [] : []).forEach(function (side) {
        var horiz = side === "top" || side === "bottom", len = (horiz ? W : H), seg = 70, n = Math.max(2, Math.round(len / seg)), step = len / n;
        var d = inward(side);
        for (var i = 0; i < n; i++) {
          var a = i * step, b2 = (i + 1) * step, m = (a + b2) / 2;
          var pa = horiz ? [a, side === "top" ? inn : H - inn] : [side === "left" ? inn : W - inn, a];
          var pb = horiz ? [b2, pa[1]] : [pa[0], b2];
          var cx = (pa[0] + pb[0]) / 2 + d[0] * 26, cy = (pa[1] + pb[1]) / 2 + d[1] * 26;
          ctx.strokeStyle = "#1f6b3a"; ctx.lineWidth = 6; ctx.lineCap = "round";
          ctx.beginPath(); ctx.moveTo(pa[0], pa[1]); ctx.quadraticCurveTo(cx, cy, pb[0], pb[1]); ctx.stroke();
          ctx.strokeStyle = "#3bb273"; ctx.lineWidth = 2.4;
          ctx.beginPath(); ctx.moveTo(pa[0], pa[1]); ctx.quadraticCurveTo(cx, cy, pb[0], pb[1]); ctx.stroke();
          if (o.garlandBalls) {
            var bx = (pa[0] + pb[0]) / 2 + d[0] * 13, by = (pa[1] + pb[1]) / 2 + d[1] * 13;
            ctx.fillStyle = ball[cnt++ % ball.length];
            ctx.beginPath(); ctx.arc(bx + d[0] * 6, by + d[1] * 6, 5.5, 0, 6.2832); ctx.fill();
            ctx.fillStyle = "rgba(255,255,255,.55)"; ctx.beginPath(); ctx.arc(bx + d[0] * 6 - 1.6, by + d[1] * 6 - 1.6, 1.6, 0, 6.2832); ctx.fill();
          }
        }
      });
    }

    function drawTrain(t) {
      var gap = Math.max(36, o.size * 3);
      var path = rim(gap, gap, W - gap, H - gap, 34), speed = 70 * (o.speed / 5);
      var head = (t * speed) % path.length, cars = Math.max(1, Math.min(8, Math.round(o.trainWagons)));
      var cols = ["#4aa3ff", "#4cd964", "#f4c25b", "#ff4fd8", "#ff9500", "#7fd4ff", "#b36bff", "#e5484d"];
      // spoor
      ctx.strokeStyle = "rgba(255,255,255,.18)"; ctx.lineWidth = 5; ctx.setLineDash([2, 9]);
      ctx.beginPath(); for (var s = 0; s <= path.length; s += 6) { var p = path.at(s); ctx[s ? "lineTo" : "moveTo"](p.x, p.y); } ctx.closePath(); ctx.stroke(); ctx.setLineDash([]);
      for (var c = cars; c >= 0; c--) {
        var p2 = path.at(head - c * 34);
        ctx.save(); ctx.translate(p2.x, p2.y); ctx.rotate(p2.a);
        if (c === 0) {
          ctx.fillStyle = "#e5484d"; ctx.fillRect(-16, -9, 32, 18);
          ctx.fillStyle = "#2b3a30"; ctx.fillRect(4, -13, 8, 6);
          ctx.fillStyle = "#f6efe0"; ctx.fillRect(-12, -6, 9, 7);
        } else {
          ctx.fillStyle = cols[(c - 1) % cols.length]; ctx.fillRect(-14, -8, 28, 16);
          ctx.fillStyle = "#f6efe0"; ctx.fillRect(-6, -11, 12, 5);
          ctx.fillStyle = "#e5484d"; ctx.fillRect(-1, -11, 2, 5);
        }
        ctx.restore();
      }
    }

    function drawMarquee(t) {
      var txt = String(o.marquee || ""), h = 30, w = Math.min(W - 40, 560), x0 = (W - w) / 2, y0 = H - Math.max(44, o.size * 3.2) - h;
      ctx.fillStyle = "rgba(10,14,12,.78)"; ctx.beginPath();
      if (ctx.roundRect) ctx.roundRect(x0, y0, w, h, 7); else ctx.rect(x0, y0, w, h);
      ctx.fill();
      ctx.save(); ctx.beginPath(); ctx.rect(x0 + 4, y0, w - 8, h); ctx.clip();
      ctx.font = "600 15px system-ui, sans-serif"; ctx.textBaseline = "middle";
      var tw = ctx.measureText(txt).width + 60, off = (t * 60 * (o.speed / 5)) % tw;
      ctx.fillStyle = "#ffcf5a"; ctx.shadowColor = "#ff9500"; ctx.shadowBlur = 6;
      for (var x = x0 + w - off; x > x0 - tw; x -= tw) ctx.fillText(txt, x, y0 + h / 2 + 1);
      for (var x2 = x0 + w - off + tw; x2 < x0 + w; x2 += tw) ctx.fillText(txt, x2, y0 + h / 2 + 1);
      ctx.restore();
    }

    // ---------- tekenlus ----------
    var lastMs = 0;
    function frame(ms) {
      if (!running) return;
      if (target) { // het element kan verschuiven of van formaat veranderen
        var bx = target.getBoundingClientRect();
        if (Math.round(bx.width) !== W || Math.round(bx.height) !== H) layout();
        else { canvas.style.left = (bx.left + (root.pageXOffset || 0)) + "px"; canvas.style.top = (bx.top + (root.pageYOffset || 0)) + "px"; }
      }
      var t = ms / 1000, dt = Math.min(0.1, lastMs ? (ms - lastMs) / 1000 : 0.016); lastMs = ms;
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      ctx.clearRect(0, 0, W, H);
      if (has("train")) drawTrain(t);
      if (has("garland")) drawGarland();
      if (has("icicles")) drawIcicles(t);
      if (has("stars")) drawStars(t);
      wire();
      var half = Math.ceil(o.size * 5.2) / 2, dy = o.size * 1.0;
      for (var n = 0; n < bulbs.length; n++) {
        var b = bulbs[n], lvl = Math.max(0, Math.min(7, Math.round(intensity(b, t) * 7)));
        ctx.save(); ctx.translate(b.x, b.y); ctx.rotate(b.rot);
        ctx.drawImage(sprite(b.ci, lvl, styleOf(b)), -half, -half + dy, half * 2, half * 2);
        ctx.restore();
      }
      if (has("snow")) drawSnow(t);
      if (has("shooting")) drawShoot(t, dt);
      if (has("marquee")) drawMarquee(t);
      if (!animated()) { running = false; raf = 0; return; }
      raf = root.requestAnimationFrame(frame);
    }

    function kick() { if (api.active && !raf) { running = true; raf = root.requestAnimationFrame(frame); } }
    function onResize() { layout(); kick(); }
    function onVisible() {
      if (document.hidden) { running = false; if (raf) root.cancelAnimationFrame(raf); raf = 0; }
      else { lastMs = 0; kick(); }
    }

    api = {
      active: false,
      /** Toont de lampjes. */
      start: function (opts) {
        configure(opts || {});
        document.body.appendChild(canvas);
        if (!api.active) {
          root.addEventListener("resize", onResize);
          document.addEventListener("visibilitychange", onVisible);
        }
        api.active = true; kick();
        return api;
      },
      /** Past instellingen aan terwijl de lampjes aan zijn: update({ palette: "ice", sides: "top" }). */
      update: function (opts) { configure(opts || {}); kick(); return api; },
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
    configure(userOpts || {});
    return api;
  }

  var Kerstlampjes = {
    palettes: PALETTES, modes: MODES, bulbStyles: BULBS, decorations: DECOR,
    /** Maakt één snoer en zet het meteen aan. */
    start: function (opts) { return (Kerstlampjes.instance = create(opts).start()); },
    stop: function () { if (Kerstlampjes.instance) Kerstlampjes.instance.stop(); },
    /** Maakt een snoer zonder het aan te zetten (voor meerdere snoeren naast elkaar). */
    create: create
  };
  root.Kerstlampjes = Kerstlampjes;
  root.ChristmasLights = Kerstlampjes; // English name, same object

  // Automatisch starten via data-attributen: <script src="kerstlampjes.js" data-sides="top" data-bulb-style="star">
  var me = document.currentScript;
  if (me && (me.hasAttribute("data-auto") || Object.keys(me.dataset).length)) {
    var opts = {};
    Object.keys(me.dataset).forEach(function (key) {
      if (key === "auto" || !(key in DEFAULTS)) return;
      var v = me.dataset[key], d = DEFAULTS[key];
      opts[key] = typeof d === "number" ? Number(v) : typeof d === "boolean" ? v !== "false" && v !== "0" : v;
    });
    var go = function () { Kerstlampjes.start(opts); };
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", go); else go();
  }
})(window);
