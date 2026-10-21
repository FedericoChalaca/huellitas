# Entrega 3 (APP nativa): lo que pide el profesor contra lo que hay

Enunciado: https://xacarana.com/cursos/aplicaciones_moviles/#/6. Revisado el 21 de octubre de 2026.

**Lo único que falta es la parte de Nicolás.** Todo lo demás está hecho o listo para el día de la entrega.

## Lo que pide el taller

| Pide el enunciado | Estado | Dónde se ve |
|---|---|---|
| Cambiar de grupo | Hecho: Federico y Nicolás | Créditos |
| Un tema de la lista: «Control mascota (vacunación…)» | Hecho | La app entera |
| **Kotlin y Android Studio (nativa)** | Hecho | [`por-que-es-nativa.md`](por-que-es-nativa.md) |
| Wireframe en papel | **Falta (Nicolás, N-37)** | `docs/wireframes/boceto/` |
| Wireframe digital en Figma, subido al repo | Hecho (falta abrir el acceso del enlace y exportar el PNG, lo hace Federico en F-14) | [Figma](https://www.figma.com/design/AuM0H9mTpmj8dgmwznI7ku/Huellitas---Wireframes) y `docs/wireframes/` |
| Sección **login** | Hecho | `ui/AuthScreens.kt` |
| Sección **registro** | Hecho | `ui/AuthScreens.kt` |
| Sección **menú** | Hecho: barra de 5 pestañas | `ui/Components.kt` |
| Sección **listado** | Hecho: mascotas, pendientes, citas; faltan cotizaciones (Nicolás) | `ui/PetScreens.kt`, `ui/citas/` |
| Sección **detalle** | Hecho: mascota y cita parcialmente; faltan cita, póliza (Nicolás) | `ui/PetScreens.kt` |
| Sección **formulario** | Hecho: mascota, vacuna, pedir cita, afiliar; falta desparasitación (Nicolás) | `ui/PetScreens.kt`, `ui/citas/`, `ui/seguro/` |
| Sección **configuración** | Hecho | `ui/OtherScreens.kt` |
| Sección **créditos** (información del estudiante) | Hecho para Federico; falta el nombre de Nicolás (N-01) | `ui/OtherScreens.kt` |
| Montaje en Android Studio | Hecho | `app/` |
| Implementar las funcionalidades | Parte de Federico hecha (ver abajo); **falta la de Nicolás** | |
| Probar en un dispositivo físico | **Falta (Federico):** conectar la tablet y correr el recorrido | tarjeta F-15 |
| Nombre, recursos gráficos y descripción para el lanzamiento | Hecho | [`play-store.md`](play-store.md), `docs/play/` |
| Firmar y generar el `.aab` | Hecho (se vuelve a generar al final) | `app\build\outputs\bundle\release\` |
| Publicar en Google Play | **Falta, y va al final** | tarjeta F-12 |
| Presentación: arquitectura, vistas, funcionalidades y publicación | Guion listo | [`presentacion.md`](presentacion.md) |

## Criterios de evaluación

| Criterio | % | Estado |
|---|---|---|
| Asistencia a clase | 10 | De cada quien |
| Uso de repositorio y colaboración en el nuevo equipo | 10 | Repo y Trello al día; **faltan los commits de Nicolás** |
| Diseño en papel | 10 | **Falta (Nicolás, N-37)** |
| Diseño en Figma | 10 | Hecho |
| Montaje en Android Studio | 20 | Hecho |
| Funcionalidades | 20 | **Mitad hecha; falta la de Nicolás** |
| Publicación en Google Play | 20 | Listo para subir cuando la app esté completa |

## Funcionalidades hechas (Federico)

Cuentas locales (registro, login, cerrar sesión, borrar cuenta), mascotas, vacunas con estado (al día, pronto, vencida), pendientes unificados con botón de agendar, pedir cita (mascota, motivo, especialidad, clínica, día y hora, con reglas), mis citas (próximas e historial), afiliar a un plan y cambiar de plan, pestaña Seguro con la póliza de cada mascota, urgencias 24 h con atención inmediata, temas claro y oscuro, aviso de vacunas configurable.

## Lo que falta de Nicolás (en Trello, tarjetas N-00 a N-37)

Desparasitaciones (N-02 a N-07), medicamentos y cotización (N-08 a N-20), planes y póliza (N-21 a N-23), detalle, cancelar y reprogramar cita (N-24 a N-26), botones de llamar y cómo llegar y primeros auxilios (N-27 a N-29), menú Más, Ajustes y Créditos (N-30 a N-32), README, capturas, política de privacidad y revisión de textos (N-33 a N-36) y los wireframes a mano (N-37).

## Qué se hace cuando él termine

1. Unir sus Pull Requests y comprobar que ya no queda ningún `TODO(` ni 🚧.
2. Recorrer la app completa en la tablet (F-15).
3. Subir `versionCode` si hace falta y generar el `.aab` final.
4. Capturas y política de privacidad en la ficha, y publicar en Play (F-12).
5. Abrir el acceso del Figma y subir el PNG (F-14).
6. Presentar con el guion de `presentacion.md`.
