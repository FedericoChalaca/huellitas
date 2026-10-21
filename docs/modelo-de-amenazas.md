# Modelo de amenazas

## Alcance y datos protegidos

Huellitas es una demostración académica local: no tiene servidor ni sincroniza datos. La base Room guarda cuentas, hashes de contraseña, mascotas, vacunas, desparasitaciones, citas, pólizas y cotizaciones. Los ajustes y el id de la sesión activa se guardan en DataStore.

La app no declara permisos de red ni otros permisos de Android en `app/src/main/AndroidManifest.xml`. La copia de seguridad de Android está desactivada (`android:allowBackup="false"`).

## Quién puede ver qué

- La persona que inicia sesión puede ver los datos asociados a su cuenta.
- Las consultas de mascotas, salud, citas, pólizas y cotizaciones filtran por el id de la cuenta propietaria; las relaciones de Room eliminan los datos dependientes al borrar su cuenta o mascota.
- Otra cuenta dentro de la app no debería poder consultar ni modificar esos registros mediante las pantallas y los DAO.
- Los datos de clínicas, planes, precios y seguro son ficticios; la app no contacta clínicas ni procesa pagos.

## Controles actuales

- Las contraseñas no se guardan en texto claro: se deriva un hash PBKDF2-HMAC-SHA-256 con sal aleatoria por cuenta (`data/Logic.kt`).
- Las consultas que acceden a datos de una persona incluyen el propietario (`data/Database.kt`).
- Las llaves de firma y credenciales de Firebase se excluyen del repositorio mediante `.gitignore`.
- La migración de Room de la versión 1 a la 2 crea las tablas nuevas sin borrar las cuentas, mascotas ni vacunas existentes.

## Límites conocidos

- La base de datos y DataStore no están cifrados con una llave propia de la app. El aislamiento del sistema Android protege el almacenamiento privado en un dispositivo normal, pero alguien con acceso root, un dispositivo comprometido o una compilación de depuración podría extraer datos y probar contraseñas contra sus hashes sin conexión.
- El hash ralentiza esos intentos, pero no protege una contraseña débil ni permite recuperar la cuenta si se pierde el dispositivo; no hay recuperación remota.
- Esta app local no proporciona aislamiento frente a quien ya controla el dispositivo desbloqueado ni frente a cambios hechos directamente en sus archivos de datos.
- No hay protección de servidor, sincronización ni auditoría remota; las cuentas y los registros solo existen en ese dispositivo.

## Revisión antes de publicar

No incluir llaves de firma ni archivos de credenciales en Git. Probar las consultas con más de una cuenta y verificar que una cuenta no ve ni modifica mascotas o registros de otra. Si la app incorpora red, sincronización o copias de seguridad en el futuro, revisar este modelo antes de habilitarlas.
