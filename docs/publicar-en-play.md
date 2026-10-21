# Cómo publicar Huellitas en Google Play (paso a paso, con los datos listos)

Cuenta: la de desarrollador del profesor (`holamundo.co`), desde el Chrome donde Federico ya entra a Play Console. Todo lo que se pega está en [`play-store.md`](play-store.md).

**Archivo a subir:** `D:\hobby\huellitas\release\huellitas-1.0.0.aab` (firmado, versionCode 1). Si se cambia el código, se regenera con `.\gradlew.bat bundleRelease` y el nuevo queda en `app\build\outputs\bundle\release\`; en ese caso se sube `versionCode` en `app/build.gradle.kts`.

## 1. Crear la aplicación (Todas las aplicaciones → Crear aplicación)

| Campo | Valor |
|---|---|
| Nombre | `Huellitas: salud de mascotas` |
| Idioma predeterminado | Español (Latinoamérica) |
| Aplicación o juego | Aplicación |
| Gratuita o de pago | Gratuita |
| Declaraciones | Aceptar las políticas de contenido y las leyes de exportación de EE. UU. |

## 2. Ficha de la tienda (Presencia en Google Play → Ficha de Play Store principal)

- Descripción corta y completa: copiarlas de `play-store.md`.
- Ícono: `docs/play/icono-512.png`.
- Gráfico de funciones: `docs/play/grafico-funciones-1024x500.png`.
- Capturas de teléfono: las 6 de `docs/play/capturas/`.
- Categoría: Salud y bienestar. Correo de contacto: federicoml2004@gmail.com.

## 3. Contenido de la aplicación (Política → Contenido de la aplicación)

Respuestas en la tabla de `play-store.md`. En resumen:

- **Política de privacidad:** https://federicochalaca.github.io/huellitas/politica-de-privacidad
- **Anuncios:** no.
- **Acceso a la aplicación:** se puede usar creando una cuenta dentro de la app, sin restricciones.
- **Público objetivo:** 18 años o más.
- **Clasificación de contenido:** cuestionario sin violencia, sexo, drogas ni apuestas.
- **Seguridad de los datos:** no recopila ni comparte datos; todo queda en el dispositivo.
- **Servicios financieros:** no ofrece servicios financieros reales (el seguro es una simulación y la ficha lo dice).
- **Salud:** no es una aplicación médica.
- **Eliminación de cuenta:** dentro de la app (Más → Ajustes → Borrar mi cuenta y mis datos); URL: la misma política.

## 4. Subir el paquete (Probar y publicar → Producción → Crear versión)

1. Subir `huellitas-1.0.0.aab`.
2. Si Play ofrece **Play App Signing**, aceptarlo (Google guarda la llave final; la de `C:\Users\feder\.keystores\huellitas-release.jks` queda como llave de subida).
3. Nombre de la versión: `1.0.0`. Notas: «Primera versión: mascotas, vacunas, desparasitaciones, pendientes, citas, urgencias, póliza de demostración y cotización de medicamentos».
4. Países: Colombia (o todos).
5. Revisar y **Enviar a revisión**. Play puede tardar de unas horas a varios días.

Si la cuenta es nueva y exige una prueba cerrada antes de producción (12 testers durante 14 días), el profesor ya dijo que ese requisito es opcional para el curso: se puede publicar en prueba interna o cerrada primero.

## Si Play rechaza

Lo más probable por la naturaleza de la app: «seguro» (servicios financieros) o «salud». La respuesta es la misma: la app dice en la ficha, en Seguro, Planes, Medicamentos y «Acerca de la demo» que es una demostración académica y no ofrece nada real.
