# Ficha de Google Play (borrador listo para pegar)

Estado (21 de octubre de 2026): **todo listo para subir.** La app ya no tiene pantallas en construcción; el `.aab` firmado está en `release/huellitas-1.0.0.aab`. Los pasos exactos están en [`publicar-en-play.md`](publicar-en-play.md).

## Datos básicos

- **Nombre (máx. 30):** `Huellitas: salud de mascotas`
- **Paquete:** `com.huellitas.mascotas`
- **Idioma predeterminado:** Español (Latinoamérica)
- **Tipo / categoría:** Aplicación · Estilo de vida (con "Salud y fitness" Play exige declarar funciones de salud)
- **Contacto:** federicoml2004@gmail.com
- **Precio:** gratis · **Anuncios:** no

## Descripción corta (máx. 80)

`Vacunas, citas y urgencias de tus mascotas. Seguro de demostración.`

## Descripción completa

```
Huellitas es el control de salud de tus mascotas: vacunas, desparasitaciones, citas, urgencias y cotización de medicamentos, todo en un solo lugar y sin internet.

🐾 Mascotas y su salud
Registra a cada mascota con su especie, raza y fecha de nacimiento. Anota sus vacunas y desparasitaciones y la app te dice cuáles están al día, cuáles vencen pronto y cuáles ya vencieron.

🔔 Pendientes
Todo lo que falta aplicar, en una sola lista y ordenado por fecha, con un botón para agendar la cita ahí mismo.

📅 Citas
Pide cita para vacunación, desparasitación, consulta, especialista o urgencia. Elige la clínica, el día y la hora; luego puedes verla, cambiarla o cancelarla.

🚨 Urgencias 24 horas
Clínicas abiertas ahora, con botón para llamar y para ver cómo llegar, y primeros auxilios para los casos más comunes.

🛡️ Seguro y medicamentos (demostración)
Compara tres planes, afilia a tu mascota y cotiza medicamentos con el descuento de su plan.

IMPORTANTE: el seguro «Huellitas Seguro», sus planes, precios, clínicas y teléfonos son datos inventados para una demostración académica. No existe ninguna aseguradora real, no se hacen cobros ni se agendan citas de verdad.

🔒 Tus datos se quedan contigo
Todo se guarda solo en tu celular. La app no usa internet, no tiene anuncios y no comparte nada con nadie.

Hecha con Kotlin y Jetpack Compose como proyecto de la materia Aplicaciones Móviles de la Universidad Pontificia Bolivariana.
```

## Gráficos (en `docs/play/`)

- Ícono 512×512: `icono-512.png` (sin transparencia).
- Gráfico de funciones 1024×500: `grafico-funciones-1024x500.png` (sin transparencia).
- Se regeneran con `docs/play/generar.mjs`.
- **Capturas de teléfono:** 8 listas en `docs/play/capturas/` (1080×2160, sin transparencia, con una cuenta de prueba y datos inventados): Mis mascotas, Pendientes, Mis citas, Seguro, Afiliar a un plan, Urgencias, Póliza y Cotización. Play pide una relación de aspecto de 2:1 como máximo; por eso están recortadas del 1080×2400 del emulador. Son de pantallas que ya están terminadas. Si al final cambian mucho, se repiten con `docs/guia-diseno/`.

## Contenido de la aplicación (respuestas)

| Sección | Respuesta |
|---|---|
| Anuncios | No contiene anuncios |
| Acceso a la aplicación | La app se puede usar creando una cuenta dentro de la app (sin restricciones de acceso para el revisor: basta con registrarse) |
| Público objetivo | 18 años o más (la app maneja cuentas con contraseña) |
| Clasificación de contenido | Cuestionario sin violencia, sexo, drogas ni apuestas: sale «Para todos» |
| Seguridad de los datos | No recopila ni comparte datos: todo queda en el dispositivo. Sin permisos |
| Servicios financieros | Declarar que **no** ofrece servicios financieros reales; el seguro es una simulación y la ficha lo dice |
| Salud | No es una app médica: no diagnostica ni da tratamientos (los primeros auxilios son consejos generales con aviso) |
| Política de privacidad | **https://federicochalaca.github.io/huellitas/politica-de-privacidad** (ya publicada; el texto está en `docs/politica-de-privacidad.md`) |
| Eliminación de cuenta | Dentro de la app: Más → Ajustes → «Borrar mi cuenta y mis datos». Play también pide una URL: la misma página de la política, sección «Cómo borrar tus datos» (https://federicochalaca.github.io/huellitas/politica-de-privacidad#cómo-borrar-tus-datos) |

## Riesgo a vigilar

Una app que muestra un «seguro» puede ser revisada con la política de servicios financieros. Para evitarlo: que el aviso de «demostración académica» salga en la ficha, en la pantalla Seguro, en Planes, en Medicamentos y en «Acerca de la demo» (tarjetas N-32 y N-36), y que los teléfonos empiecen por `000`.

## Firma

- Llave de subida: `C:\Users\feder\.keystores\huellitas-release.jks`, alias `huellitas`. La contraseña está en `keystore.properties` (ignorado por git).
- **Respaldar la llave y esa contraseña en dos lugares** (por ejemplo el gestor de contraseñas y una memoria USB). Si se pierden no se puede actualizar la app.
- Huella SHA-256 del certificado: `44:77:F4:9B:D2:E5:8B:DA:B8:B3:E8:CC:8C:E3:20:38:5A:32:82:CC:87:3B:CF:6A:67:2A:11:EE:EB:AD:5B:95`
- Generar el paquete: `.\gradlew.bat bundleRelease` → `app\build\outputs\bundle\release\app-release.aab`. Antes de la subida final, subir `versionCode` en `app/build.gradle.kts` si ya se subió una versión.

