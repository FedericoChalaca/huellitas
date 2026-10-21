// Icono (512x512) y gráfico de funciones (1024x500) de la ficha de Play, dibujados a partir del
// mismo ícono de la app (res/drawable/ic_launcher_foreground.xml, colores de res/values/colors.xml).
// Uso: instalar sharp en cualquier carpeta (npm i sharp) y correr: node docs/play/generar.mjs
import sharp from "sharp";

const VERDE = "#2F6F5E";
const CREMA = "#FFF8F0";
// La huella, en las mismas coordenadas del vector de 108x108 de la app.
const huella = `
  <ellipse cx="54" cy="68" rx="16" ry="13" fill="${CREMA}"/>
  <ellipse cx="36" cy="50" rx="6.5" ry="7.5" fill="${CREMA}"/>
  <ellipse cx="47" cy="39" rx="6.5" ry="7.5" fill="${CREMA}"/>
  <ellipse cx="61" cy="39" rx="6.5" ry="7.5" fill="${CREMA}"/>
  <ellipse cx="72" cy="50" rx="6.5" ry="7.5" fill="${CREMA}"/>`;

// Icono: cuadrado completo, sin transparencia (Play le pone las esquinas redondeadas).
const icono = `<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 108 108">
  <rect width="108" height="108" fill="${VERDE}"/>${huella}</svg>`;
await sharp(Buffer.from(icono)).flatten({ background: VERDE }).png().toFile(new URL("./icono-512.png", import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, "$1"));

// Gráfico de funciones: sin transparencia tampoco.
const grafico = `<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="500">
  <defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#2F6F5E"/><stop offset="100%" stop-color="#1F4D41"/></linearGradient></defs>
  <rect width="1024" height="500" fill="url(#g)"/>
  <circle cx="900" cy="80" r="160" fill="${CREMA}" opacity="0.07"/>
  <circle cx="980" cy="440" r="120" fill="${CREMA}" opacity="0.07"/>
  <g transform="translate(70 120) scale(2.6)">${huella}</g>
  <text x="380" y="235" font-family="Verdana, DejaVu Sans, sans-serif" font-size="84" font-weight="bold" fill="${CREMA}">Huellitas</text>
  <text x="383" y="300" font-family="Verdana, DejaVu Sans, sans-serif" font-size="25" fill="#CFE5DC">La salud de tus mascotas en un solo lugar</text>
  <text x="383" y="352" font-family="Verdana, DejaVu Sans, sans-serif" font-size="24" fill="#9CC9BB">Vacunas · citas · urgencias · medicamentos</text>
</svg>`;
await sharp(Buffer.from(grafico)).flatten({ background: VERDE }).png().toFile(new URL("./grafico-funciones-1024x500.png", import.meta.url).pathname.replace(/^\/([A-Za-z]:)/, "$1"));
console.log("OK: icono-512.png y grafico-funciones-1024x500.png");
