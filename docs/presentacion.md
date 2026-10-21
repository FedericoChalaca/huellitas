# Guion de la presentación (10 a 12 minutos, dos voces)

El taller pide resaltar: **arquitectura, vistas, funcionalidades y publicación**.

1. **Qué es Huellitas (1 min).** La salud de las mascotas en un solo lugar, conectada a un seguro de demostración. Aviso: el seguro y las clínicas son inventados.
2. **Por qué es nativa (1 min).** Kotlin y Jetpack Compose, sin WebView, sin permisos, `.apk` de 1,8 MB. Comparación con Entre Dos (híbrida, un WebView que carga una página). Ver `por-que-es-nativa.md`.
3. **Arquitectura (2 min).** Pantalla (Compose) → ViewModel → DAO (Room) → SQLite. Datos y reglas en `data/` sin dibujar nada, con pruebas. Cada consulta lleva el dueño de la cuenta. Contraseñas con PBKDF2. Navegación con rutas.
4. **Vistas (2 min).** Mostrar las hojas de wireframe y la app: login, registro, menú de 5 pestañas, listados, detalle, formularios, configuración, créditos.
5. **Funcionalidades (3 min), demo en la tablet.** Registrar mascota y vacuna → Pendientes → agendar cita → afiliar a un plan → cotizar medicamentos → urgencias. Cada persona explica las suyas.
6. **Trabajo en equipo (1 min).** Repositorio, Trello con tarjetas por tarea, ramas y Pull Requests, commits de los dos.
7. **Publicación (1 min).** Llave de firma, `.aab`, ficha de Play (ícono, gráfico, capturas, descripción, política de privacidad).
8. **Qué aprendimos (30 s).**

Preguntas que pueden hacer, con respuesta corta:

- *¿Por qué Room y no una API?* La app funciona sin internet y los datos son de cada persona.
- *¿Dónde está la contraseña?* No se guarda: solo su hash PBKDF2 con una sal propia por cuenta.
- *¿Por qué un ViewModel por función?* Para que cada persona trabaje en su archivo sin pisarse.
- *¿Qué es real y qué no?* Todo lo de la aseguradora es inventado; lo demás es real y vive en el celular.
