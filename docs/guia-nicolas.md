# Guía para Nicolás: cómo hacer tu parte sin enredarte

Tu parte son las tarjetas **N-00 a N-37** del Trello. Cada tarjeta dice qué archivo tocar, qué ejemplo copiar y cuándo está lista. Esta guía es lo que se repite en todas: léela una vez completa y vuelve a la sección 5 cuando algo falle.

La parte de Federico ya está unida en `main`: pedir cita, Mis citas, Pendientes, la pestaña Seguro, afiliar y Urgencias. **Ninguna tarjeta tuya está bloqueada.**

## 1. Una sola vez: preparar el computador (tarjeta N-00)

1. Instala **Git** y **Android Studio**.
2. Clona el repositorio:

   ```
   git clone https://github.com/FedericoChalaca/huellitas.git
   ```

3. Entra a la carpeta `huellitas` y dile a Git quién eres. **Usa el mismo correo de tu cuenta de GitHub**: si pones otro, tus commits no salen a tu nombre.

   ```
   git config user.name "Nicolás Castrillón"
   ```

   ```
   git config user.email "el-correo-de-tu-github"
   ```

4. Abre la carpeta con Android Studio y **espera** a que termine "Gradle sync" (la barra de abajo). La primera vez tarda varios minutos.
5. Si se queja del Java: File → Settings → Build, Execution, Deployment → Build Tools → Gradle → **Gradle JDK: 21**.
6. Dale al botón verde ▶ con un emulador. Si la app abre y puedes crear una cuenta, quedaste listo.

## 2. La rutina de cada tarjeta (siempre igual)

1. En Trello, pasa la tarjeta a **En curso**.
2. Ponte al día con `main`:

   ```
   git checkout main
   ```

   ```
   git pull
   ```

3. Crea la rama con el nombre que dice la tarjeta:

   ```
   git checkout -b nicolas/n-08-catalogo
   ```

4. Haz **solo** lo que dice la tarjeta, en los archivos que dice la tarjeta.
5. Compila y prueba (sección 3).
6. Guarda tu trabajo con un mensaje que cuente qué hiciste, en español y en pasado:

   ```
   git add .
   ```

   ```
   git commit -m "Catálogo de 20 medicamentos con su categoría y precio"
   ```

7. Sube la rama:

   ```
   git push -u origin nicolas/n-08-catalogo
   ```

8. En GitHub abre un **Pull Request** hacia `main`. Título: el código y el nombre de la tarjeta (por ejemplo «N-08 · Catálogo de medicamentos»).
9. Pasa la tarjeta a **En revisión** y avísale a Federico. Cuando él lo una, la tarjeta pasa a **Hecho**.
10. Vuelve al paso 2 para la siguiente. **No empieces una tarjeta nueva encima de una rama vieja.**

## 3. Antes de pedir revisión

- [ ] La app compila: `.\gradlew.bat assembleDebug` (o el ▶ de Android Studio).
- [ ] Las pruebas pasan: `.\gradlew.bat testDebugUnitTest`.
- [ ] Abriste **tu** pantalla en el emulador y hace lo que dice «Listo cuando».
- [ ] Probaste el caso vacío (sin datos) y girar la pantalla: no se cierra.
- [ ] `git status` solo muestra los archivos de tu tarjeta.
- [ ] Borraste el `TODO(N-xx)` o el 🚧 de esa tarjeta.

## 4. En qué orden

| Orden | Tarjetas | Qué es |
|---|---|---|
| 1 | N-00, N-01 | Preparar el computador y tu primer commit (tu nombre en Créditos) |
| 2 | N-37 | Dibujar a mano los wireframes del Figma (no es código: hazlo cuando quieras) |
| 3 | N-02 a N-07 | Desparasitaciones |
| 4 | N-08 a N-20 | Medicamentos y cotización |
| 5 | N-21 a N-23 | Planes y póliza |
| 6 | N-24 a N-26 | Detalle de la cita, cancelar y reprogramar |
| 7 | N-27 a N-29 | Llamar, cómo llegar y primeros auxilios |
| 8 | N-30 a N-32 | Más, Ajustes, Créditos |
| 9 | N-33 a N-36 | README, capturas, política de privacidad y revisión de textos |

Dentro de cada grupo ve en orden: casi siempre la tarjeta siguiente usa lo de la anterior (lo dice el renglón «Antes»).

Cosas que **ya existen** y solo tienes que usar:

- `pesos(48_000)` devuelve `"$ 48.000"`. Está en `data/Logic.kt`. **No la vuelvas a escribir.**
- `EstadoCitaChip(estado)` dibuja la etiqueta de una cita. Está en `ui/citas/CitasScreen.kt`.
- `BackScaffold`, `EmptyState`, `ConfirmDialog`, `DateField`, `SpeciesBadge` en `ui/Components.kt`.
- `activityViewModel<...>()` y `MenuCard` en `ui/Placeholder.kt`.
- Para tener datos de prueba: crea una mascota, afíliala en la pestaña Seguro y pide una cita con el +.

## 5. Errores comunes y cómo salir

| Lo que ves | Qué pasó | Qué haces |
|---|---|---|
| `Unresolved reference: Card` (o cualquier nombre en rojo) | Falta el `import` | Pon el cursor sobre la palabra roja y oprime **Alt+Enter** → Import |
| `This material API is experimental` / `This foundation API is experimental` | Usaste algo marcado como experimental (`Card(onClick=…)`, `FlowRow`) | Primera línea del archivo: `@file:OptIn(ExperimentalMaterial3Api::class)` (y `ExperimentalLayoutApi::class` para `FlowRow`). Mira cómo empieza `ui/PetScreens.kt` |
| `Conflicting overloads` o `Redeclaration` | Escribiste una función que ya existe (por ejemplo `pesos`) | Borra la tuya y usa la que ya está |
| Gradle sync falla con `Unsupported class file major version` o `Unexpected lock protocol` | Gradle está usando otro Java | Settings → Gradle → **Gradle JDK: 21** y vuelve a sincronizar |
| La app se cierra al abrir una pantalla | Un error mientras corre | Abre **Logcat** abajo, busca `FATAL EXCEPTION` y lee la primera línea que diga `com.huellitas` |
| `An operation is not implemented: N-16` | Llamaste a una función que todavía es un `TODO(...)` | Haz primero esa tarjeta |
| `Cannot access database on the main thread` | Llamaste a la base de datos sin corrutina | La función del ViewModel debe ser `suspend` o ir dentro de `viewModelScope.launch { }`; en la pantalla, `scope.launch { }` |
| La pantalla no se actualiza al guardar | Leíste el dato una sola vez | Usa el `Flow` con `collectAsStateWithLifecycle`, como `PetDetailScreen` |
| La app muestra datos raros o se cierra después de cambiar de rama | Quedó instalada una versión distinta | Desinstala Huellitas del emulador y vuelve a darle ▶ |
| `warning: LF will be replaced by CRLF` | Aviso de Windows sobre saltos de línea | Nada: no es un error |
| `git push` dice `rejected` | En GitHub hay algo que tú no tienes | `git pull` y otra vez `git push` |
| GitHub dice que el Pull Request tiene conflictos | `main` cambió en el mismo archivo | **No borres nada.** Llama a Federico |
| Hiciste commits en `main` por error (y todavía no hiciste push) | Olvidaste crear la rama | `git checkout -b nicolas/n-xx-nombre` (la rama nueva se lleva tus commits) y sube esa rama. Después, para dejar tu `main` limpio: `git checkout main` y `git reset --hard origin/main` |
| Tus commits salen con otro nombre en GitHub | El correo de Git no es el de tu GitHub | Repite el paso 3 de la sección 1. Los commits nuevos ya salen bien |

## 6. Reglas que no se rompen

1. **Nunca trabajes directo en `main`.** Siempre una rama por tarjeta.
2. **Solo toca los archivos de tu tarjeta.** Si necesitas cambiar otro, avísale a Federico antes.
3. **No cambies `data/Entities.kt` ni `data/Database.kt`.** Las tablas ya tienen todo lo que necesitas; cambiarlas borra los datos de la app instalada.
4. **Nunca subas llaves ni contraseñas** (`.jks`, `keystore.properties`). Ya están en `.gitignore`.
5. **Todo dato de ejemplo es inventado**: nada de nombres, teléfonos ni marcas reales. La aseguradora es de mentira y la app lo dice.
6. **Escribe tú el código y entiéndelo.** En la presentación te pueden preguntar por cualquier pantalla tuya. Si copias un ejemplo, que sea de este mismo proyecto y léelo antes.
7. Un commit pequeño y claro vale más que uno gigante. Si una tarjeta te queda grande, haz dos commits en la misma rama.

## 7. Tus archivos

- `DesparasitacionesViewModel.kt`, `ui/desparasitacion/`
- `MedicamentosViewModel.kt`, `data/Medicamentos.kt`, `ui/medicamentos/`
- `ui/seguro/PolizaScreen.kt`, `ui/seguro/PlanesScreen.kt`
- `ui/citas/CitaDetalleScreen.kt` y, en `CitasViewModel.kt`, **solo** las funciones `cancelar` y `reprogramar`
- `ui/urgencias/ClinicaCard.kt`, `ui/urgencias/PrimerosAuxiliosScreen.kt`
- `ui/mas/`, y en `ui/OtherScreens.kt` lo que digan N-01, N-31 y N-36
- Tus pruebas en `app/src/test/`: `MedicamentosTest.kt` y `CotizacionTest.kt`
- `docs/capturas/`, `docs/politica-de-privacidad.md`, `docs/wireframes/boceto/`, `README.md` (N-33)

Si te trabas más de 20 minutos en lo mismo, escribe en la tarjeta de Trello qué intentaste y qué error salió (copia el texto del error), y pregúntale a Federico.
