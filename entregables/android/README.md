# APK Android de depuración

`PharmaMobil-debug.apk` se genera desde `:androidApp:assembleDebug` y se copia aquí después de una compilación correcta. El binario local no se incluye en Git para evitar agregar unos 22 MB a cada revisión. Si clonas el repositorio, vuelve a ejecutar:

```bash
./gradlew :androidApp:assembleDebug
mkdir -p entregables/android
cp androidApp/build/outputs/apk/debug/androidApp-debug.apk entregables/android/PharmaMobil-debug.apk
```

La APK de depuración permite configurar la URL del backend dentro de la aplicación. Usa la IP privada de la computadora en la red del laboratorio para un celular físico; el valor inicial `10.0.2.2` solo funciona en el emulador.
