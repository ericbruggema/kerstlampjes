/* Easter eggs van Kerstlampjes.
   De hoofdpagina (index.html) laadt alleen het ei dat bij de datum van vandaag hoort.
   De verborgen pagina toont ze allemaal. */
window.KL_EGGS = [
  {
    id: "sinterklaas",
    title: "Pakjesavond & Sinterklaasfeest",
    when: "5 en 6 december",
    banner: "🎁 Pakjesavond! Is je schoen al gezet?",
    text: "Onder de boom verschijnen cadeautjes.",
    test: (d) => d.getMonth() === 11 && (d.getDate() === 5 || d.getDate() === 6),
  },
  {
    id: "kerstavond",
    title: "Kerstavond",
    when: "24 december",
    banner: "✨ Kerstavond — stille nacht, heilige nacht",
    text: "Het sneeuwt zachtjes en er liggen cadeautjes onder de boom.",
    test: (d) => d.getMonth() === 11 && d.getDate() === 24,
  },
  {
    id: "kerstmorgen",
    title: "Kerstochtend",
    when: "25 december, vóór 12:00",
    banner: "🎅 Goedemorgen! Vrolijk kerstfeest",
    text: "De lucht kleurt als bij zonsopgang en de cadeautjes staan klaar.",
    test: (d) => d.getMonth() === 11 && d.getDate() === 25 && d.getHours() < 12,
  },
  {
    id: "kerstdag",
    title: "Eerste kerstdag",
    when: "25 december, vanaf 12:00",
    banner: "🎄 Eerste kerstdag — vrolijk kerstfeest!",
    text: "Sneeuw en cadeautjes voor de rest van de dag.",
    test: (d) => d.getMonth() === 11 && d.getDate() === 25 && d.getHours() >= 12,
  },
  {
    id: "tweede-kerstdag",
    title: "Tweede kerstdag",
    when: "26 december",
    banner: "🎄 Tweede kerstdag — geniet van de rust",
    text: "Rustig sneeuwen, zonder haast.",
    test: (d) => d.getMonth() === 11 && d.getDate() === 26,
  },
  {
    id: "oudejaarsavond",
    title: "Oudejaarsavond",
    when: "31 december",
    banner: "🎆 Nog 03:12:45 tot het nieuwe jaar",
    text: "Vuurwerk achter de boom, met een live aftelklok tot middernacht.",
    test: (d) => d.getMonth() === 11 && d.getDate() === 31,
  },
  {
    id: "nieuwjaar",
    title: "Nieuwjaarsdag",
    when: "1 januari",
    banner: "🥳 Gelukkig nieuwjaar!",
    text: "Vuurwerk en confetti, met het nieuwe jaartal.",
    test: (d) => d.getMonth() === 0 && d.getDate() === 1,
  },
  {
    id: "driekoningen",
    title: "Driekoningen",
    when: "6 januari",
    banner: "⭐ Driekoningen — volg de ster",
    text: "De ster wordt groter en er schiet af en toe een vallende ster langs.",
    test: (d) => d.getMonth() === 0 && d.getDate() === 6,
  },
];

window.KL_eggForDate = function (d) {
  return window.KL_EGGS.find((e) => e.test(d)) || null;
};
