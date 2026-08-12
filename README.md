# PharmaMobile

Aplicacion multiplataforma para la **gestion integral de una farmacia**: clientes,
productos, pedidos e inventario farmaceutico. Desarrollada con **Kotlin Multiplatform (KMP)**
y **Compose Multiplatform**, con estrategia *offline-first* y consumo de servicios REST.

> Universidad Peruana Union - Facultad de Ingenieria y Arquitectura
> Pregrado - VIII Ciclo - Semestre 2026-2

## Arquitectura y source sets

El codigo compartido vive en el modulo `shared`, organizado por *source sets*:

| Source set     | Rol principal                     | Ejemplos de contenido                                   |
|----------------|-----------------------------------|---------------------------------------------------------|
| `commonMain`   | Logica de negocio compartida      | Modelos (Cliente, Producto, Pedido), casos de uso, validaciones, calculos de stock |
| `androidMain`  | Implementacion especifica Android | Bindings y APIs del ecosistema Google Android (camara, GPS) |
| `iosMain`      | Implementacion especifica iOS     | Bindings y SDK nativo para dispositivos Apple           |

La regla practica: todo lo que sea **estructura de datos y reglas de negocio** va en
`commonMain` para escribirse una sola vez; el **acceso a hardware o APIs nativas** se
resuelve por plataforma en `androidMain` / `iosMain` mediante `expect`/`actual`.

## Estructura del proyecto

- `shared/` - codigo compartido (commonMain, androidMain, iosMain + tests).
- `androidApp/` - aplicacion Android (punto de entrada, `MainActivity`).
- `iosApp/` - aplicacion iOS (requiere macOS con Xcode para compilar).

## Como ejecutar

- **Android:** abrir el proyecto en Android Studio, seleccionar un emulador o dispositivo
  fisico y ejecutar la configuracion `androidApp`. Por linea de comandos:
  `./gradlew :androidApp:assembleDebug`
- **iOS:** abrir `iosApp/` en Xcode desde un equipo Mac y ejecutar.

## Estado

Linea base tecnica estable y versionada con Git desde el primer dia. La pantalla inicial
muestra un boton que despliega un mensaje de bienvenida, validando que la app compila y
se ejecuta correctamente sobre Android.

## Configuracion

- Package base: `pe.edu.upeu.pharmamobile`
- Kotlin `2.4.10` - Compose Multiplatform `1.11.1` - AGP `9.3.1`
- minSdk 24 / targetSdk 36
