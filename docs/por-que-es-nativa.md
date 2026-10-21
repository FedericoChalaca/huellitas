# Por qué Huellitas es una app nativa (y Entre Dos no)

Lo que pide la Entrega 3 es una aplicación **nativa con Android Studio y Kotlin**. Esta nota es la evidencia y sirve para la parte de arquitectura de la presentación. Los números salen del `.aab` y del `.apk` firmados que se compilaron el 21 de octubre de 2026.

## Qué quiere decir «nativa»

Una app es nativa cuando está escrita en el lenguaje oficial de la plataforma (Kotlin en Android), y cuando **cada pantalla la dibuja el propio sistema con sus componentes**: botones, listas, campos de texto, diálogos. No hay un navegador adentro mostrando una página web.

## Huellitas

| Qué se mira | Qué hay |
|---|---|
| Lenguaje | 39 archivos Kotlin (más de 3.200 líneas). Cero archivos Java, JavaScript, HTML o TypeScript en `app/src/main`. |
| Pantallas | Jetpack Compose con Material 3: cada pantalla es una función `@Composable` que Android dibuja con sus propios componentes. |
| Navegación | Navigation Compose, dentro de la misma actividad. |
| Datos | Room (SQLite) y DataStore en el celular. Sin servidor. |
| Permisos | **Ninguno.** Ni siquiera internet: la app no puede conectarse a nada. |
| `.apk` de release | 1,8 MB. Sin carpeta `assets` web, sin `.html`, sin `.js`. |
| WebView | La clase `android.webkit.WebView` no está en el código final (se comprobó en el release). |
| Seguridad de contraseñas | PBKDF2 con sal propia de cada cuenta, hecho con las APIs de Java/Android. |
| Firma | `.aab` firmado con la llave de subida del proyecto. |

## Entre Dos

Entre Dos es una **página web** (Next.js, React y Tailwind, escrita en TypeScript) alojada en Vercel. La app de Android es un contenedor de Capacitor: una sola actividad vacía de Java (`class MainActivity extends BridgeActivity {}`) que abre un **WebView** y le pone adentro `https://entredos-psi.vercel.app`.

| Qué se mira | Qué hay |
|---|---|
| Lenguaje | Casi todo TypeScript y CSS. En Android solo `MainActivity.java`, una clase vacía que hereda de Capacitor. |
| Pantallas | HTML y CSS que dibuja un navegador (WebView), no componentes de Android. |
| Contenido | Viene de internet cada vez que se abre. Solo se empaqueta una página de «sin conexión» (`index.html`, `cordova.js`). |
| Permisos | Internet, notificaciones, vibración y estado de red. Sin internet no funciona. |
| Actualizaciones | Cambia el sitio web y la app cambia sola, sin subir otra versión a Play. |

Es una app **híbrida**: sirve, se publica en Play y puede usar funciones del celular (notificaciones, compartir, vibración) a través de plugins, pero no es nativa. Por eso la Entrega 3 pedía otra aplicación.

## Cómo lo puede comprobar quien revise

1. Abrir `app/src/main/java/com/huellitas/mascotas/ui/PetScreens.kt`: se ven funciones `@Composable` con `Card`, `LazyColumn`, `Button`, no HTML.
2. Abrir `app/src/main/AndroidManifest.xml`: no hay `uses-permission`.
3. Compilar con `.\gradlew.bat assembleRelease` y abrir el `.apk` como `.zip`: solo trae `classes.dex`, recursos y bibliotecas nativas.
