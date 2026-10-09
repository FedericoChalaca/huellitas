# Huellitas: plan de trabajo

Entrega 3 de Aplicaciones Móviles (UPB): una app **nativa** de Android hecha con Kotlin, publicada en Google Play.
Equipo: Federico Martínez y Nicolás Castrillón.

Las tareas de cada uno están en el Trello del equipo, una tarjeta por tarea, con los pasos. Este documento es el mapa general.

## 1. Qué es la app

Huellitas es el control de la salud de las mascotas conectado a un seguro:

- **Mascotas y su salud:** fichas, vacunas y desparasitaciones, con aviso de lo que está por vencer o vencido.
- **Pendientes:** todo lo que falta aplicar, con un botón para agendar la cita ahí mismo.
- **Citas:** pedir cita (vacunación, desparasitación, consulta, especialista o urgencia), verla, cambiarla o cancelarla.
- **Urgencias 24 h:** clínicas abiertas ahora, llamar, cómo llegar, pedir atención inmediata y primeros auxilios.
- **Seguro:** póliza de cada mascota (plan, vigencia, coberturas, cuánto del límite anual se ha usado) y comparación de planes.
- **Medicamentos:** cotizar con el descuento del plan y guardar la cotización.

> **El seguro es de mentira.** No existe ninguna aseguradora detrás: "Huellitas Seguro", las clínicas, los planes y los precios son datos inventados que viven dentro de la app (`data/Insurer.kt`) y funcionan sin internet. Todo texto de la app y de la ficha de Play debe decirlo con claridad ("demostración académica"). Los teléfonos empiezan por `000` para que no llamen a nadie real.

## 2. Qué pide el taller y dónde lo cumplimos

| Lo que pide el taller | Dónde está en Huellitas |
|---|---|
| Android Studio y Kotlin | Proyecto en Kotlin con Jetpack Compose (`app/`) |
| Wireframe en papel y en Figma, en el repo | `docs/wireframes/` (24 pantallas) |
| Login y registro | `ui/AuthScreens.kt` |
| Menú | Barra de abajo con 5 pestañas (`ui/Components.kt`) |
| Listado | Mascotas, Pendientes, Citas, cotizaciones |
| Detalle | Detalle de la mascota, de la cita, de la póliza |
| Formulario | Mascota, vacuna, desparasitación, pedir cita, afiliar |
| Configuración | Ajustes |
| Créditos (datos del estudiante) | Créditos |
| Probar en un dispositivo físico | La tablet Redmi Pad SE |
| Firmar y generar el `.aab` | `./gradlew bundleRelease` con la llave de subida |
| Publicar en Google Play | Ficha y publicación (Federico) |
| Presentación: arquitectura, vistas, funciones, publicación | Presentación del equipo |

## 3. Cómo está hecha

```
app/src/main/java/com/huellitas/mascotas/
├─ data/                    Datos y lógica que no dibuja nada
│  ├─ Entities.kt           Las tablas (Pet, Vaccine, Deworming, Appointment, Policy, Quote…)
│  ├─ Database.kt           Las consultas (DAO) y la base de datos Room
│  ├─ Insurer.kt            La aseguradora de mentira: planes, clínicas, horarios
│  ├─ Medicamentos.kt       Catálogo y cálculo de cotizaciones (de Nicolás)
│  ├─ Logic.kt              Contraseñas, validaciones, fechas, estado de una vacuna
│  └─ Prefs.kt              Sesión y ajustes (DataStore)
├─ MainViewModel.kt         Cuenta, mascotas y vacunas
├─ OwnerViewModel.kt        Base de los demás ViewModels
├─ CitasViewModel.kt        Citas
├─ SeguroViewModel.kt       Pólizas
├─ DesparasitacionesViewModel.kt   (de Nicolás)
├─ MedicamentosViewModel.kt        (de Nicolás)
└─ ui/                      Las pantallas (Jetpack Compose), una carpeta por función
   ├─ App.kt                Todas las rutas de navegación
   ├─ Components.kt         Piezas que se repiten (barra, chips, campo de fecha…)
   ├─ Placeholder.kt        Pantallas "en construcción" y tarjeta de menú
   ├─ pendientes/ citas/ seguro/ urgencias/ desparasitacion/ medicamentos/ mas/
```

Cómo fluye todo: **pantalla (Compose) → ViewModel → DAO (Room) → base de datos**. La pantalla nunca habla con la base de datos directamente.

Tres ideas que se repiten en todo el código:

1. **Cada consulta lleva el dueño** (`owner`): una cuenta nunca ve ni toca los datos de otra. Mira cualquier DAO.
2. **Las fechas son números** (`LocalDate.toEpochDay()`): sin horas ni zonas horarias. El dinero son pesos enteros (COP).
3. **Cada persona tiene sus propios archivos** (ver la sección 5). Así casi nunca habrá conflictos al unir el trabajo.

El mejor ejemplo para copiar es el de las vacunas: `MainViewModel.kt` (funciones de vacunas), `ui/PetScreens.kt` (`VaccineRow`, `VaccineFormScreen`) y `data/Database.kt` (`VaccineDao`).

## 4. Las 24 pantallas y de quién es cada una

Los dibujos están en `docs/wireframes/hoja-1.png` a `hoja-6.png` (cuatro por hoja) y en [Figma](https://www.figma.com/design/AuM0H9mTpmj8dgmwznI7ku/Huellitas---Wireframes).

| # | Pantalla | Archivo | Estado / dueño |
|---|---|---|---|
| 1 | Splash | `ui/App.kt` | Hecha |
| 2-3 | Login, Registro | `ui/AuthScreens.kt` | Hechas |
| 4 | Mis mascotas | `ui/PetScreens.kt` | Hecha |
| 5 | Detalle de la mascota | `ui/PetScreens.kt` | Hecha (Nicolás agrega las desparasitaciones) |
| 6-7 | Nueva mascota, Registrar vacuna | `ui/PetScreens.kt` | Hechas |
| 8 | Registrar desparasitación | `ui/desparasitacion/` | **Nicolás** |
| 9 | Pendientes | `ui/pendientes/` | Hecha |
| 10-11 | Mis citas, Pedir cita | `ui/citas/` | Hechas |
| 12 | Detalle de la cita | `ui/citas/` | **Nicolás** |
| 13 | Seguro (inicio) | `ui/seguro/` | Hecha |
| 14-15 | Póliza, Planes | `ui/seguro/` | **Nicolás** |
| 16 | Afiliar mascota | `ui/seguro/` | Hecha |
| 17 | Urgencias 24 h | `ui/urgencias/` | Hecha (botones de la clínica: Nicolás) |
| 18 | Primeros auxilios | `ui/urgencias/` | **Nicolás** |
| 19-21 | Cotizar medicamentos, Mi cotización, Mis cotizaciones | `ui/medicamentos/` | **Nicolás** |
| 22 | Más | `ui/mas/` | **Nicolás** |
| 23-24 | Ajustes, Créditos | `ui/OtherScreens.kt` | Hechas (Nicolás las pule) |

## 5. Reparto del trabajo

Los códigos son los de las tarjetas de Trello. **F** = Federico, **N** = Nicolás.

**Nicolás (38 tarjetas, todas pequeñas, de 20 a 60 minutos).** Su guía paso a paso, con los errores más comunes, está en [`guia-nicolas.md`](guia-nicolas.md).

| Área | Tarjetas |
|---|---|
| Empezar | N-00 preparar el computador, N-01 tu nombre en Créditos (tu primer commit) |
| Desparasitaciones | N-02 a N-07 |
| Medicamentos y cotización | N-08 a N-20 |
| Póliza y planes | N-21 a N-23 |
| Citas (detalle, cancelar, reprogramar) | N-24 a N-26 |
| Urgencias (llamar, cómo llegar, primeros auxilios) | N-27 a N-29 |
| Más, Ajustes y Créditos | N-30 a N-32 |
| Calidad y publicación | N-33 README, N-34 capturas en la tablet, N-35 política de privacidad, N-36 revisar textos |
| Diseño en papel | N-37 dibujar a mano los wireframes del Figma |

**Federico (15 tarjetas, las más largas).** De F-01 a F-10 ya están hechas y unidas en `main`; falta la publicación.

| Área | Tarjetas |
|---|---|
| Pendientes unificados | F-01, F-02 |
| Citas | F-03 a F-05, F-10 |
| Datos de la aseguradora y horarios | F-06 |
| Seguro | F-07, F-08 |
| Urgencias | F-09 |
| Publicación | F-11 llave y `.aab`, F-12 ficha de Play, F-13 presentación, F-14 Figma (hecho), F-15 pruebas en la tablet |

## 6. Cómo trabajamos con Git

Cada quien hace sus commits con **su propio usuario de Git**. Cada commit cuenta lo que esa persona hizo.

**Una sola vez:**

1. Instala Git y Android Studio (el proyecto compila con Java 21; ya viene configurado en `gradle/gradle-daemon-jvm.properties`).
2. Clona el repositorio: `git clone <URL del repositorio>`
3. Dile a Git quién eres: `git config user.name "Tu nombre"` y `git config user.email "tu@correo"`.
4. Abre la carpeta con Android Studio y espera a que termine "Gradle sync".

**Por cada tarjeta (siempre igual):**

1. Mueve la tarjeta de Trello a **En curso**.
2. Ponte al día: `git checkout main` y `git pull`.
3. Crea tu rama con el código de la tarjeta: `git checkout -b nicolas/n-08-catalogo`.
4. Haz el cambio, **compila y pruébalo** (sección 7).
5. Commit con un mensaje que diga qué hiciste, en español, en pasado y sin el código: `git add .` y `git commit -m "Catálogo de 20 medicamentos con su categoría y precio"`.
6. Sube la rama: `git push -u origin nicolas/n-08-catalogo`.
7. En GitHub abre un **Pull Request** hacia `main`, con el código de la tarjeta en el título.
8. Mueve la tarjeta a **En revisión**. Federico lo revisa, pide cambios si hacen falta y lo une.
9. Cuando se una, la tarjeta pasa a **Hecho** y vuelves al paso 2.

Reglas de oro:

- **Un commit (o pocos) por tarjeta.** Es mejor un commit pequeño y claro que uno enorme que mezcla todo.
- **Nunca trabajes directo en `main`.**
- **Solo toca los archivos de tu tarjeta.** Si necesitas cambiar uno de otra persona, avísale antes.
- **Nunca subas llaves, contraseñas ni archivos `.jks` o `keystore.properties`.** Ya están en `.gitignore`.
- Si Git dice que hay un conflicto, no borres nada: llama a Federico.

## 7. Cómo compilar y probar

- **Desde Android Studio:** botón verde ▶ con el emulador o con la tablet conectada.
- **Desde la terminal** (PowerShell, dentro de la carpeta del proyecto, un comando por vez):
  - `.\gradlew.bat assembleDebug` compila la app.
  - `.\gradlew.bat testDebugUnitTest` corre las pruebas.
- **En la tablet:** actívale las opciones de desarrollador y la depuración por USB, conéctala y dale permiso al computador. Android Studio la muestra arriba como dispositivo.
- Antes de pedir revisión: la app **abre**, tu pantalla funciona, y las pruebas pasan.

Si Android Studio se queja del Java, abre File → Settings → Build → Gradle y elige **JDK 21** en "Gradle JDK".

## 8. Reglas de código

- El texto de la app va en **español**, con tildes.
- Copia el estilo del código que ya existe: nombres, orden, cómo se arma una pantalla.
- Las pantallas "en construcción" (`PantallaPendiente`, `PestanaPendiente`) se reemplazan por la pantalla de verdad. Borra el marcador `TODO(N-xx)` de la tarjeta que termines.
- Lo que sea un cálculo o una regla (totales de una cotización, horarios, estados) va en una **función pura** con su prueba en `app/src/test/`. Mira `LogicTest.kt`.
- Los textos de ejemplo deben ser obviamente inventados. Nada de nombres, teléfonos ni direcciones reales.

## 9. Antes de entregar

- [ ] Todas las tarjetas en **Hecho** y ningún `TODO(` ni `🚧` en pantalla.
- [ ] La app abre y se recorre completa en la tablet, sin cerrarse.
- [ ] Las pruebas pasan: `.\gradlew.bat testDebugUnitTest`.
- [ ] Wireframe en papel (boceto) y en Figma subidos a `docs/wireframes/`.
- [ ] `.aab` firmado, ficha de Play completa, política de privacidad publicada.
- [ ] Las dos personas tienen commits en el repositorio.
- [ ] Presentación lista: arquitectura, vistas, funciones y publicación.
