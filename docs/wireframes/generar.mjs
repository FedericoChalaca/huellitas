// Wireframes de Huellitas en escala de grises, en SVG (sin dependencias).
// Uso: node docs/wireframes/generar.mjs
// Escribe wireframe.svg (las 24 pantallas, para importar en Figma con File > Import)
// y hoja-1.svg ... hoja-6.svg (cuatro pantallas por hoja, para el boceto).
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
const chip = (x, y, w, t, on = false) => box(x, y, w, 22, 11, on ? "#c9cdd1" : DARK) + text(x + w / 2, y + 15, t, 10, "middle", "bold");
const field = (y, label) => box(24, y, 252, 42, 8) + text(38, y + 26, label, 12);
const button = (y, label) => box(24, y, 252, 44, 22, DARK) + text(150, y + 28, label, 13, "middle", "bold");
const small = (x, y, w, label) => box(x, y, w, 28, 14, DARK) + text(x + w / 2, y + 19, label, 10, "middle", "bold");

// Menú de abajo: las cinco secciones, con la actual marcada.
function tabBar(active) {
  const tabs = ["Mascotas", "Pendientes", "Citas", "Seguro", "Más"];
  const y = H - 62;
  let s = box(12, y, W - 24, 50, 16, "#ffffff");
  tabs.forEach((t, i) => {
    const cx = 12 + ((W - 24) / 5) * (i + 0.5);
    s += circle(cx, y + 18, 9, i === active ? DARK : FILL) + text(cx, y + 40, t, 8, "middle", i === active ? "bold" : "normal");
  });
  return s;
}

const title = (t, sub) => text(20, 46, t, 19, "start", "bold") + (sub ? text(20, 68, sub, 11) : "");
const back = (t) => text(20, 44, "&lt;", 18, "start", "bold") + text(46, 45, t, 16, "start", "bold");
const infoCard = (y, h, t) => box(20, y, W - 40, h, 12, "#ebebeb") + text(34, y + 24, t, 12, "start", "bold");

const petRow = (y, name, status) =>
  box(20, y, W - 40, 72, 12) + circle(52, y + 36, 22) + text(88, y + 32, name, 13, "start", "bold") + bar(88, y + 42, 90) + chip(W - 100, y + 25, 70, status);

const doseRow = (y, name, status) =>
  box(20, y, W - 40, 66, 12) +
  text(34, y + 22, name, 12, "start", "bold") +
  text(34, y + 40, "Aplicada: 12 de marzo de 2026", 9) +
  text(34, y + 55, "Próxima: 12 de marzo de 2027", 9) +
  chip(W - 104, y + 36, 70, status) +
  circle(W - 40, y + 18, 9) + text(W - 40, y + 22, "x", 10, "middle", "bold");

const clinicRow = (y, name, extra) =>
  box(20, y, W - 40, 96, 12) +
  circle(46, y + 30, 18) +
  text(76, y + 26, name, 11, "start", "bold") +
  text(76, y + 42, "Calle 10 # 20-30 · a 1,2 km", 9) +
  chip(W - 78, y + 8, 50, "24 h") +
  box(34, y + 60, 108, 28, 14, DARK) + text(88, y + 79, "Llamar", 10, "middle", "bold") +
  box(150, y + 60, 118, 28, 14, DARK) + text(209, y + 79, "Cómo llegar", 10, "middle", "bold") +
  (extra ?? "");

const SCREENS = [
  {
    name: "Splash",
    body: () => circle(150, 250, 44, DARK) + text(150, 340, "Huellitas", 24, "middle", "bold") + text(150, 364, "Control de mascotas y seguro", 11, "middle"),
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
      box(20, 68, W - 40, 96, 14) +
      circle(58, 106, 26, DARK) +
      text(98, 100, "Luna", 15, "start", "bold") +
      text(98, 118, "Gato · Criollo · 2 años", 10) +
      bar(34, 140, 200) +
      box(20, 174, W - 40, 34, 17, DARK) + text(150, 196, "Pedir cita para Luna", 12, "middle", "bold") +
      text(20, 236, "Vacunas", 14, "start", "bold") + small(190, 218, 90, "+ Registrar") +
      doseRow(254, "Rabia", "Al día") +
      doseRow(326, "Triple", "Pronto") +
      text(20, 424, "Desparasitaciones", 14, "start", "bold") + small(190, 406, 90, "+ Registrar") +
      doseRow(442, "Antiparasitario", "Vencida"),
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
    name: "Registrar desparasitación (formulario)",
    body: () =>
      back("Registrar desparasitación") +
      field(70, "Producto") +
      text(24, 140, "Tipo", 11, "start", "bold") +
      chip(24, 150, 70, "Interna", true) + chip(100, 150, 70, "Externa") +
      field(190, "Fecha de aplicación") + circle(254, 211, 9) +
      field(246, "Próxima dosis (opcional)") + circle(254, 267, 9) +
      text(24, 314, "Calcular la próxima dosis desde la aplicación:", 10, "start", "bold") +
      chip(24, 326, 70, "En 1 mes") + chip(100, 326, 86, "En 3 meses") + chip(192, 326, 76, "En 6 meses") +
      box(24, 368, 252, 72, 8) + text(38, 392, "Notas", 12) +
      button(468, "Guardar"),
  },
  {
    name: "Pendientes",
    body: () =>
      title("Pendientes") +
      chip(20, 80, 52, "Todas", true) + chip(78, 80, 60, "Vacunas") + chip(144, 80, 88, "Desparasit.") + chip(238, 80, 44, "Venc.") +
      text(20, 128, "2 vencidas.", 12, "start", "bold") +
      [
        ["Parvovirus", "Michi · 2 de octubre", "Vencida"],
        ["Antiparasitario", "Luna · 5 de octubre", "Vencida"],
        ["Triple", "Rocky · 14 de octubre", "Pronto"],
        ["Pipeta", "Rocky · 30 de octubre", "Al día"],
      ]
        .map(([n, sub, st], i) => {
          const y = 140 + i * 96;
          return box(20, y, W - 40, 86, 12) + circle(46, y + 30, 18) + text(76, y + 26, n, 12, "start", "bold") + text(76, y + 42, sub, 10) + chip(W - 100, y + 14, 70, st) + small(34, y + 52, 100, "Agendar cita");
        })
        .join("") +
      tabBar(1),
  },
  {
    name: "Mis citas",
    body: () =>
      title("Mis citas") +
      chip(20, 78, 80, "Próximas", true) + chip(106, 78, 80, "Historial") +
      [
        ["Jue 8", "09:30", "Vacunación · Luna", "Clínica Huellitas Centro", "Confirmada"],
        ["Mar 13", "15:00", "Especialista · Rocky", "Hospital Veterinario 24 h", "Solicitada"],
        ["Vie 16", "08:30", "Consulta · Michi", "Veterinaria Patitas Norte", "Solicitada"],
      ]
        .map(([d, h, t, c, st], i) => {
          const y = 118 + i * 100;
          return box(20, y, W - 40, 88, 12) + box(30, y + 10, 54, 68, 10, DARK) + text(57, y + 40, d, 11, "middle", "bold") + text(57, y + 58, h, 10, "middle") +
            text(96, y + 28, t, 11, "start", "bold") + text(96, y + 46, c, 9) + chip(96, y + 56, 80, st);
        })
        .join("") +
      circle(254, 500, 24, DARK) + text(254, 509, "+", 24, "middle", "bold") +
      tabBar(2),
  },
  {
    name: "Pedir cita (formulario)",
    body: () =>
      back("Pedir cita") +
      text(24, 80, "Mascota", 10, "start", "bold") + chip(24, 86, 56, "Luna", true) + chip(86, 86, 60, "Rocky") + chip(152, 86, 56, "Michi") +
      text(24, 128, "Motivo", 10, "start", "bold") + chip(24, 134, 80, "Vacunación", true) + chip(110, 134, 96, "Desparasitación") + chip(212, 134, 64, "Consulta") +
      chip(24, 162, 84, "Especialista") + chip(114, 162, 66, "Urgencia") +
      text(24, 206, "Especialidad", 10, "start", "bold") + chip(24, 212, 108, "Medicina general", true) + chip(138, 212, 100, "Dermatología") +
      chip(24, 240, 90, "Cardiología") + chip(120, 240, 96, "Odontología") +
      text(24, 280, "Clínica", 10, "start", "bold") +
      box(24, 286, 252, 50, 10) + circle(46, 311, 12) + text(66, 307, "Clínica Huellitas Centro", 10, "start", "bold") + text(66, 322, "a 1,2 km · 24 h", 9) +
      box(24, 342, 252, 50, 10) + circle(46, 367, 12) + text(66, 363, "Veterinaria Patitas Norte", 10, "start", "bold") + text(66, 378, "a 3,4 km", 9) +
      field(404, "Fecha") + circle(254, 425, 9) +
      text(24, 468, "Hora", 10, "start", "bold") +
      chip(24, 474, 50, "08:00") + chip(80, 474, 50, "08:30") + chip(136, 474, 50, "09:30", true) + chip(192, 474, 50, "10:00") +
      button(512, "Pedir cita"),
  },
  {
    name: "Detalle de la cita",
    body: () =>
      back("Cita") +
      box(20, 68, W - 40, 150, 14) +
      chip(34, 82, 86, "Confirmada") +
      text(34, 130, "Vacunación · Luna", 15, "start", "bold") +
      text(34, 152, "Jueves 8 de octubre de 2026", 11) +
      text(34, 170, "09:30 a. m.", 11) +
      text(34, 196, "Especialidad: Medicina general", 10) +
      box(20, 232, W - 40, 80, 12) + circle(46, 262, 16) + text(74, 258, "Clínica Huellitas Centro", 11, "start", "bold") + text(74, 274, "Calle 10 # 20-30 · Medellín", 9) + text(74, 290, "604 000 0001", 9) +
      box(20, 326, W - 40, 56, 12) + text(34, 350, "Notas", 10, "start", "bold") + bar(34, 360, 200) +
      box(24, 412, 252, 44, 22, DARK) + text(150, 440, "Reprogramar", 13, "middle", "bold") +
      box(24, 468, 252, 44, 22, "#ffffff") + text(150, 496, "Cancelar cita", 13, "middle", "bold"),
  },
  {
    name: "Seguro (inicio)",
    body: () =>
      title("Huellitas Seguro") +
      box(20, 78, W - 40, 100, 14, "#ebebeb") + circle(50, 112, 20, DARK) + text(82, 106, "Luna · Plan Plus", 13, "start", "bold") + text(82, 124, "Vigente hasta 8 oct 2027", 9) +
      box(34, 146, 232, 10, 5, "#ffffff") + box(34, 146, 90, 10, 5, DARK) + text(34, 170, "Usado: $ 1.200.000 de $ 5.000.000", 9) +
      box(20, 190, W - 40, 56, 14) + circle(50, 218, 18) + text(82, 214, "Rocky · Sin póliza", 11, "start", "bold") + small(208, 205, 60, "Afiliar") +
      [
        ["Urgencias 24 h", "Clínicas abiertas ahora"],
        ["Cotizar medicamentos", "Con el descuento de tu plan"],
        ["Mis cotizaciones", "Lo que ya cotizaste"],
        ["Planes del seguro", "Compara Básico, Plus y Premium"],
        ["Primeros auxilios", "Qué hacer mientras llegas"],
      ]
        .map(([t, s], i) => box(20, 260 + i * 56, W - 40, 48, 12) + circle(44, 284 + i * 56, 14) + text(68, 280 + i * 56, t, 11, "start", "bold") + text(68, 296 + i * 56, s, 9))
        .join("") +
      tabBar(3),
  },
  {
    name: "Póliza de la mascota",
    body: () =>
      back("Póliza de Luna") +
      box(20, 68, W - 40, 112, 14, "#ebebeb") + text(34, 94, "Plan Plus", 16, "start", "bold") + text(34, 114, "Póliza Nº 0001-2026", 10) + text(34, 132, "Vigente: 8 oct 2026 a 8 oct 2027", 10) + chip(34, 146, 90, "Activa") +
      text(20, 208, "Coberturas", 13, "start", "bold") +
      ["Consultas generales", "Especialistas", "Desparasitaciones", "Medicamentos con descuento"]
        .map((t, i) => circle(34, 232 + i * 30, 8, DARK) + text(52, 236 + i * 30, t, 11))
        .join("") +
      text(20, 368, "Uso del límite anual", 13, "start", "bold") +
      box(20, 380, W - 40, 12, 6, "#ffffff") + box(20, 380, 70, 12, 6, DARK) +
      text(20, 408, "$ 1.200.000 de $ 5.000.000", 10) +
      box(20, 430, W - 40, 56, 12) + text(34, 454, "Cobertura 70% · Deducible $ 50.000", 10, "start", "bold") + bar(34, 464, 180) +
      button(520, "Cambiar de plan"),
  },
  {
    name: "Planes del seguro",
    body: () =>
      back("Planes del seguro") +
      [
        ["Básico", "$ 39.900 al mes", "Cobertura 50%", "Límite $ 2.000.000"],
        ["Plus", "$ 69.900 al mes", "Cobertura 70%", "Límite $ 5.000.000"],
        ["Premium", "$ 109.900 al mes", "Cobertura 90%", "Límite $ 10.000.000"],
      ]
        .map(([n, p, c, l], i) => {
          const y = 66 + i * 170;
          return box(20, y, W - 40, 158, 14, i === 1 ? "#ebebeb" : FILL) + text(34, y + 28, n, 15, "start", "bold") + text(34, y + 48, p, 11) +
            text(34, y + 72, c, 10) + text(34, y + 88, l, 10) +
            circle(40, y + 112, 5, DARK) + bar(52, y + 108, 160) + circle(40, y + 132, 5, DARK) + bar(52, y + 128, 130) +
            (i === 1 ? chip(W - 112, y + 12, 80, "Popular") : "");
        })
        .join(""),
  },
  {
    name: "Afiliar mascota",
    body: () =>
      back("Afiliar a Rocky") +
      text(24, 84, "Elige un plan", 12, "start", "bold") +
      box(20, 96, W - 40, 62, 12) + text(34, 120, "Básico · $ 39.900 al mes", 11, "start", "bold") + text(34, 138, "Cobertura 50%", 10) + circle(W - 44, 127, 9) +
      box(20, 168, W - 40, 62, 12, "#ebebeb") + text(34, 192, "Plus · $ 69.900 al mes", 11, "start", "bold") + text(34, 210, "Cobertura 70%", 10) + circle(W - 44, 199, 9, DARK) +
      box(20, 240, W - 40, 62, 12) + text(34, 264, "Premium · $ 109.900 al mes", 11, "start", "bold") + text(34, 282, "Cobertura 90%", 10) + circle(W - 44, 271, 9) +
      box(20, 322, W - 40, 110, 12) + text(34, 346, "Resumen", 12, "start", "bold") + text(34, 368, "Mascota: Rocky", 10) + text(34, 386, "Plan: Plus", 10) + text(34, 404, "Vigencia: 1 año desde hoy", 10) +
      text(150, 466, "Demostración: no es un seguro real", 9, "middle") +
      button(484, "Afiliar"),
  },
  {
    name: "Urgencias 24 horas",
    body: () =>
      back("Urgencias 24 h") +
      box(20, 66, W - 40, 56, 28, "#c9cdd1") + text(150, 100, "Pedir atención de urgencia ahora", 12, "middle", "bold") +
      text(20, 150, "Clínicas abiertas ahora", 12, "start", "bold") +
      clinicRow(162, "Huellitas Centro") +
      clinicRow(268, "Hospital 24 horas") +
      clinicRow(374, "Mascotas del Sur"),
  },
  {
    name: "Primeros auxilios",
    body: () =>
      back("Primeros auxilios") +
      box(20, 66, W - 40, 150, 12) + text(34, 92, "Atragantamiento", 12, "start", "bold") + circle(W - 40, 88, 8) +
      [0, 1, 2, 3].map((i) => circle(38, 118 + i * 24, 4, DARK) + bar(50, 114 + i * 24, 190 - i * 10)).join("") +
      ["Intoxicación", "Golpe de calor", "Heridas y sangrado", "Fracturas", "Picaduras"]
        .map((t, i) => box(20, 228 + i * 56, W - 40, 46, 12) + text(34, 256 + i * 56, t, 12, "start", "bold") + circle(W - 40, 251 + i * 56, 8))
        .join(""),
  },
  {
    name: "Cotizar medicamentos",
    body: () =>
      back("Cotizar medicamentos") +
      circle(262, 40, 13) + text(262, 44, "3", 10, "middle", "bold") +
      box(20, 66, W - 40, 40, 20) + circle(42, 86, 9) + text(60, 90, "Buscar medicamento", 11) +
      chip(20, 118, 52, "Todos", true) + chip(78, 118, 100, "Antiparasit.") + chip(184, 118, 84, "Antibióticos") +
      [
        ["Antiparasitario oral", "Caja x 3 tabletas", "$ 48.000"],
        ["Pipeta antipulgas", "Caja x 2", "$ 36.000"],
        ["Collar antiparasitario", "Unidad", "$ 72.000"],
        ["Desparasitante líquido", "Frasco 50 ml", "$ 28.000"],
      ]
        .map(([n, p, pr], i) => {
          const y = 156 + i * 84;
          return box(20, y, W - 40, 74, 12) + circle(46, y + 30, 18) + text(74, y + 26, n, 11, "start", "bold") + text(74, y + 42, p, 9) + text(74, y + 60, pr, 11, "start", "bold") + small(W - 108, y + 36, 80, "+ Agregar");
        })
        .join(""),
  },
  {
    name: "Mi cotización",
    body: () =>
      back("Mi cotización") +
      text(24, 80, "¿Para qué mascota?", 10, "start", "bold") + chip(24, 86, 56, "Luna", true) + chip(86, 86, 60, "Rocky") + chip(152, 86, 96, "Sin mascota") +
      [
        ["Antiparasitario oral", "$ 48.000"],
        ["Pipeta antipulgas", "$ 36.000"],
        ["Desparasitante líquido", "$ 28.000"],
      ]
        .map(([n, p], i) => {
          const y = 128 + i * 62;
          return box(20, y, W - 40, 54, 12) + text(34, y + 24, n, 11, "start", "bold") + text(34, y + 42, p, 10) +
            circle(W - 112, y + 27, 11) + text(W - 112, y + 31, "-", 12, "middle", "bold") + text(W - 82, y + 31, "1", 12, "middle", "bold") + circle(W - 52, y + 27, 11) + text(W - 52, y + 31, "+", 12, "middle", "bold");
        })
        .join("") +
      box(20, 322, W - 40, 112, 14, "#ebebeb") +
      text(34, 350, "Subtotal", 11) + text(W - 34, 350, "$ 112.000", 11, "end") +
      text(34, 374, "Seguro paga (70%)", 11) + text(W - 34, 374, "- $ 78.400", 11, "end") +
      box(34, 386, W - 68, 1, 0, LINE) +
      text(34, 414, "Tú pagas", 13, "start", "bold") + text(W - 34, 414, "$ 33.600", 13, "end", "bold") +
      button(456, "Guardar cotización"),
  },
  {
    name: "Mis cotizaciones",
    body: () =>
      back("Mis cotizaciones") +
      [
        ["8 de octubre de 2026", "3 medicamentos · Luna", "$ 112.000", "Tú pagas $ 33.600"],
        ["2 de octubre de 2026", "1 medicamento · Rocky", "$ 72.000", "Tú pagas $ 21.600"],
        ["20 de septiembre de 2026", "2 medicamentos", "$ 64.000", "Tú pagas $ 64.000"],
      ]
        .map(([d, n, t, p], i) => {
          const y = 68 + i * 96;
          return box(20, y, W - 40, 86, 12) + text(34, y + 24, d, 11, "start", "bold") + text(34, y + 42, n, 10) + text(34, y + 62, p, 10) + text(W - 34, y + 24, t, 12, "end", "bold") + chip(W - 112, y + 50, 78, "Solicitada");
        })
        .join(""),
  },
  {
    name: "Más",
    body: () =>
      title("Más") +
      [
        ["Ajustes", "Tema, avisos y tu cuenta"],
        ["Créditos", "Quién hizo esta app"],
        ["Acerca de la demo", "La aseguradora es de mentira"],
      ]
        .map(([t, s], i) => box(20, 84 + i * 66, W - 40, 56, 12) + circle(46, 112 + i * 66, 16) + text(74, 108 + i * 66, t, 12, "start", "bold") + text(74, 124 + i * 66, s, 9))
        .join("") +
      box(20, 296, W - 40, 42, 21, "#ffffff") + text(150, 322, "Cerrar sesión", 12, "middle", "bold") +
      tabBar(4),
  },
  {
    name: "Ajustes (configuración)",
    body: () =>
      back("Ajustes") +
      infoCard(66, 96, "Cuenta") + text(34, 118, "Ana Pérez", 14, "start", "bold") + text(34, 136, "ana@correo.com", 11) + bar(34, 148, 190, 7) +
      infoCard(174, 74, "Apariencia") + chip(34, 214, 70, "Sistema", true) + chip(110, 214, 60, "Claro") + chip(176, 214, 66, "Oscuro") +
      infoCard(260, 92, "Aviso de pendientes") + text(34, 298, "Marcar «Pronto» cuando falten:", 10) + chip(34, 312, 62, "3 días") + chip(102, 312, 62, "7 días", true) + chip(170, 312, 66, "14 días") +
      box(20, 372, W - 40, 42, 21, "#ffffff") + text(150, 398, "Cerrar sesión", 12, "middle", "bold") +
      text(150, 440, "Borrar mi cuenta y mis datos", 11, "middle"),
  },
  {
    name: "Créditos",
    body: () =>
      back("Créditos") +
      box(20, 66, W - 40, 80, 12) + circle(58, 106, 24, DARK) + text(96, 100, "Huellitas", 15, "start", "bold") + text(96, 118, "Versión 1.0.0", 10) +
      infoCard(160, 230, "Trabajo académico") +
      ["Estudiante", "Estudiante", "Programa", "Universidad", "Materia", "Docente"].map((t, i) => text(34, 200 + i * 28, t, 10) + bar(140, 192 + i * 28, 110)).join("") +
      infoCard(404, 100, "Con qué está hecha") +
      [0, 1, 2].map((i) => text(34, 448 + i * 22, "•", 10) + bar(48, 441 + i * 22, 170)).join(""),
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

writeFileSync(join(OUT, "wireframe.svg"), sheet(SCREENS, 6));
const hojas = Math.ceil(SCREENS.length / 4);
for (let i = 0; i < hojas; i++) {
  writeFileSync(join(OUT, `hoja-${i + 1}.svg`), sheet(SCREENS.slice(i * 4, i * 4 + 4), 4, i * 4 + 1));
}
console.log(`OK: ${SCREENS.length} pantallas -> wireframe.svg y ${hojas} hojas`);
