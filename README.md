# Huellitas

App nativa de Android (Kotlin y Jetpack Compose) para llevar la salud de las mascotas: vacunas y desparasitaciones, citas, urgencias, póliza de un seguro y cotización de medicamentos.

Trabajo de la materia Aplicaciones Móviles (UPB, 2026-2) de Federico Martínez y Nicolás Castrillón.

> **Demostración académica.** La aseguradora "Huellitas Seguro", sus clínicas, planes y precios son datos inventados que viven dentro de la app. No hay ningún seguro real ni conexión a internet.

## Cómo compilar

Necesitas Android Studio y el SDK de Android (compileSdk 37). El proyecto usa Java 21 para Gradle (ver `gradle/gradle-daemon-jvm.properties`).

```
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

## Para el equipo

Todo el plan, el reparto del trabajo y las reglas de Git están en [`docs/plan-de-trabajo.md`](docs/plan-de-trabajo.md). Las tareas, en el Trello del equipo. La guía de Nicolás, con los pasos de cada tarjeta y los errores comunes, está en [`docs/guia-nicolas.md`](docs/guia-nicolas.md).

Los dibujos de las pantallas están en [`docs/wireframes/`](docs/wireframes/) y en Figma: [Huellitas - Wireframes](https://www.figma.com/design/AuM0H9mTpmj8dgmwznI7ku/Huellitas---Wireframes) (24 pantallas en escala de grises).
