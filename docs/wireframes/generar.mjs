// Wireframes de Huellitas en escala de grises, en SVG (sin dependencias).
// Uso: node docs/wireframes/generar.mjs
// Escribe wireframe.svg (las 10 pantallas, para importar en Figma con File > Import)
// y hoja-1.svg, hoja-2.svg, hoja-3.svg (cuatro pantallas por hoja, para el boceto en papel).
import { writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const OUT = dirname(fileURLToPath(import.meta.url));
const W = 300;
const H = 620;
const GAP = 60;
const PAD = 60;

const F = "Verdana, DejaVu Sans, sans-serif";
const LINE = "#9aa0a6";
const FILL = "#f1f3f4";
const DARK = "#e0e0e0";
const TEXT = "#3c4043";

const box = (x, y, w, h, r = 8, fill = FILL) => `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${r}" fill="${fill}" stroke="${LINE}" stroke-width="1.5"/>`;
const bar = (x, y, w, h = 8) => `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${h / 2}" fill="${LINE}" opacity="0.45"/>`;
const text = (x, y, t, size = 13, anchor = "start", weight = "normal") =>
  `<text x="${x}" y="${y}" font-family="${F}" font-size="${size}" font-weight="${weight}" fill="${TEXT}" text-anchor="${anchor}">${t}</text>`;
const circle = (cx, cy, r, fill = FILL) => `<circle cx="${cx}" cy="${cy}" r="${r}" fill="${fill}" stroke="${LINE}" stroke-width="1.5"/>`;
const chip = (x, y, w, t) => box(x, y, w, 22, 11, DARK) + text(x + w / 2, y + 15, t, 10, "middle", "bold");
const field = (y, label) => box(24, y, 252, 42, 8) + text(38, y + 26, label, 12);
const button = (y, label) => box(24, y, 252, 44, 22, DARK) + text(150, y + 28, label, 13, "middle", "bold");

// Menú de abajo: las cuatro secciones, con la actual marcada.
function tabBar(active) {
  const tabs = ["Mascotas", "Vacunas", "Ajustes", "Créditos"];
  const y = H - 62;
  let s = box(12, y, W - 24, 50, 16, "#ffffff");
  tabs.forEach((t, i) => {
    const cx = 12 + ((W - 24) / 4) * (i + 0.5);
    s += circle(cx, y + 18, 9, i === active ? DARK : FILL) + text(cx, y + 40, t, 9, "middle", i === active ? "bold" : "normal");
  });
  return s;
}

const title = (t, sub) => text(20, 46, t, 19, "start", "bold") + (sub ? text(20, 68, sub, 11) : "");
const back = (t) => text(20, 44, "&lt;", 18, "start", "bold") + text(46, 45, t, 16, "start", "bold");

const petRow = (y, name, status) =>
  box(20, y, W - 40, 72, 12) + circle(52, y + 36, 22) + text(88, y + 32, name, 13, "start", "bold") + bar(88, y + 42, 90, 7) + chip(W - 100, y + 25, 70, status);

const vaccineRow = (y, name, status) =>
  box(20, y, W - 40, 78, 12) +
  text(34, y + 24, name, 12, "start", "bold") +
  text(34, y + 44, "Aplicada: 12 de marzo de 2026", 10) +
  text(34, y + 62, "Próxima: 12 de marzo de 2027", 10) +
  chip(W - 104, y + 48, 70, status) +
  circle(W - 40, y + 22, 9) + text(W - 40, y + 26, "x", 10, "middle", "bold");

const infoCard = (y, h, t) => box(20, y, W - 40, h, 12, "#ebebeb") + text(34, y + 24, t, 12, "start", "bold");

const SCREENS = [
  {
    name: "Splash",
    body: () => circle(150, 250, 44, DARK) + text(150, 340, "Huellitas", 24, "middle", "bold") + text(150, 364, "Control de mascotas y vacunas", 11, "middle"),
  },
  {
    name: "Login",
    body: () =>
      circle(150, 96, 34, DARK) +
      text(150, 170, "Bienvenido de nuevo", 18, "middle", "bold") +
      text(150, 192, "Entra para ver a tus mascotas", 11, "middle") +
      field(222, "Correo") +
      field(278, "Contraseña") +
      text(254, 304, "Ver", 10, "middle", "bold") +
      button(346, "Entrar") +
      text(150, 414, "¿No tienes cuenta? Crear una", 11, "middle"),
  },
  {
    name: "Registro",
    body: () =>
      circle(150, 80, 30, DARK) +
      text(150, 146, "Crea tu cuenta", 18, "middle", "bold") +
      text(150, 168, "Se guarda solo en este celular", 11, "middle") +
      field(194, "Tu nombre") +
      field(246, "Correo") +
      field(298, "Contraseña (mínimo 8)") +
      field(350, "Repite la contraseña") +
      button(416, "Crear cuenta") +
      text(150, 484, "Ya tengo cuenta", 11, "middle"),
  },
  {
    name: "Mis mascotas (listado)",
    body: () =>
      title("Mis mascotas") +
      petRow(84, "Luna", "Al día") +
      petRow(166, "Rocky", "Pronto") +
      petRow(248, "Michi", "Vencida") +
      circle(254, 500, 24, DARK) +
      text(254, 509, "+", 24, "middle", "bold") +
      tabBar(0),
  },
  {
    name: "Detalle de la mascota",
    body: () =>
      back("Luna") +
      circle(238, 40, 11) + circle(270, 40, 11) +
      box(20, 70, W - 40, 118, 14) +
      circle(60, 112, 28, DARK) +
      text(104, 106, "Luna", 16, "start", "bold") +
      text(104, 126, "Perro · Criollo", 11) +
      bar(34, 152, 200) +
      bar(34, 168, 140) +
      text(20, 220, "Vacunas", 16, "start", "bold") +
      box(180, 202, 100, 30, 15, DARK) + text(230, 222, "+ Registrar", 11, "middle", "bold") +
      vaccineRow(244, "Rabia", "Al día") +
      vaccineRow(330, "Triple", "Pronto") +
      vaccineRow(416, "Parvovirus", "Vencida"),
  },
  {
    name: "Nueva mascota (formulario)",
    body: () =>
      back("Nueva mascota") +
      field(70, "Nombre") +
      text(24, 140, "Especie", 11, "start", "bold") +
      chip(24, 150, 56, "Perro") + chip(86, 150, 56, "Gato") + chip(148, 150, 46, "Ave") + chip(200, 150, 64, "Conejo") +
      chip(24, 180, 46, "Pez") + chip(76, 180, 60, "Reptil") + chip(142, 180, 50, "Otro") +
      field(222, "Raza (opcional)") +
      field(278, "Fecha de nacimiento (opcional)") +
      circle(254, 299, 9) +
      box(24, 334, 252, 96, 8) + text(38, 358, "Notas (alergias, veterinario…)", 12) +
      button(456, "Guardar"),
  },
  {
    name: "Registrar vacuna (formulario)",
    body: () =>
      back("Registrar vacuna") +
      field(70, "Vacuna") +
      chip(24, 124, 46, "Rabia") + chip(76, 124, 50, "Triple") + chip(132, 124, 80, "Parvovirus") + chip(218, 124, 58, "Moquillo") +
      field(168, "Fecha de aplicación") + circle(254, 189, 9) +
      field(224, "Próxima dosis (opcional)") + circle(254, 245, 9) +
      text(24, 292, "Calcular la próxima dosis desde la aplicación:", 10, "start", "bold") +
      chip(24, 304, 70, "En 1 mes") + chip(100, 304, 86, "En 6 meses") + chip(192, 304, 76, "En 1 año") +
      box(24, 346, 252, 72, 8) + text(38, 370, "Notas (lote, veterinario…)", 12) +
      button(446, "Guardar"),
  },
  {
    name: "Próximas vacunas",
    body: () =>
      title("Próximas vacunas") +
      text(20, 82, "1 vacuna vencida.", 12, "start", "bold") +
      [
        ["Parvovirus", "Michi · 2 de octubre", "Vencida"],
        ["Triple", "Rocky · 14 de octubre", "Pronto"],
        ["Rabia", "Luna · 12 de marzo", "Al día"],
        ["Moquillo", "Luna · 3 de abril", "Al día"],
      ]
        .map(([n, sub, st], i) => {
          const y = 98 + i * 82;
          return box(20, y, W - 40, 70, 12) + circle(46, y + 35, 18) + text(76, y + 30, n, 12, "start", "bold") + text(76, y + 48, sub, 10) + chip(W - 100, y + 24, 70, st);
        })
        .join("") +
      tabBar(1),
  },
  {
    name: "Ajustes (configuración)",
    body: () =>
      title("Ajustes") +
      infoCard(84, 96, "Cuenta") + text(34, 120, "Ana Pérez", 14, "start", "bold") + text(34, 138, "ana@correo.com", 11) + bar(34, 152, 190, 7) +
      infoCard(192, 74, "Apariencia") + chip(34, 232, 70, "Sistema") + chip(110, 232, 60, "Claro") + chip(176, 232, 66, "Oscuro") +
      infoCard(278, 92, "Aviso de vacunas") + text(34, 316, "Marcar «Pronto» cuando falten:", 10) + chip(34, 330, 62, "3 días") + chip(102, 330, 62, "7 días") + chip(170, 330, 66, "14 días") +
      box(20, 388, W - 40, 42, 21, "#ffffff") + text(150, 414, "Cerrar sesión", 12, "middle", "bold") +
      text(150, 456, "Borrar mi cuenta y mis datos", 11, "middle") +
      tabBar(2),
  },
  {
    name: "Créditos",
    body: () =>
      title("Créditos") +
      box(20, 84, W - 40, 84, 12) + circle(58, 126, 24, DARK) + text(96, 120, "Huellitas", 15, "start", "bold") + text(96, 138, "Versión 1.0.0", 10) +
      infoCard(182, 196, "Trabajo académico") +
      ["Estudiante", "Programa", "Universidad", "Materia", "Docente", "Periodo"].map((t, i) => text(34, 222 + i * 24, t, 10) + bar(140, 214 + i * 24, 110)).join("") +
      infoCard(392, 100, "Con qué está hecha") +
      [0, 1, 2].map((i) => text(34, 436 + i * 22, "•", 10) + bar(48, 429 + i * 22, 170)).join("") +
      tabBar(3),
  },
];

function sheet(screens, cols, firstNumber = 1) {
  const rows = Math.ceil(screens.length / cols);
  const totalW = PAD * 2 + cols * W + (cols - 1) * GAP;
  const totalH = PAD * 2 + rows * (H + 46) + (rows - 1) * GAP;
  const frames = screens
    .map((s, i) => {
      const x = PAD + (i % cols) * (W + GAP);
      const y = PAD + Math.floor(i / cols) * (H + 46 + GAP);
      return `<g id="pantalla-${firstNumber + i}">
    ${text(x, y + 14, `${firstNumber + i}. ${s.name}`, 14, "start", "bold")}
    <g transform="translate(${x} ${y + 30})">
      <rect width="${W}" height="${H}" rx="24" fill="#ffffff" stroke="#5f6368" stroke-width="2"/>
      <rect x="110" y="10" width="80" height="16" rx="8" fill="${LINE}" opacity="0.3"/>
      ${s.body()}
    </g>
  </g>`;
    })
    .join("\n  ");
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${totalW}" height="${totalH}" viewBox="0 0 ${totalW} ${totalH}">
  <rect width="${totalW}" height="${totalH}" fill="#ffffff"/>
  ${frames}
</svg>
`;
}

writeFileSync(join(OUT, "wireframe.svg"), sheet(SCREENS, 5));
for (let i = 0; i * 4 < SCREENS.length; i++) {
  writeFileSync(join(OUT, `hoja-${i + 1}.svg`), sheet(SCREENS.slice(i * 4, i * 4 + 4), 4, i * 4 + 1));
}
console.log(`OK: ${SCREENS.length} pantallas -> wireframe.svg y ${Math.ceil(SCREENS.length / 4)} hojas`);
