<<<<<<<
# DSMEventos
>>>>>>>
# Bienvenido a mi aplicación para la gestión de eventos

Esta aplicación fue desarrollada con Kotlin en el IDE de [`Android Studio`](https://developer.android.com/studio).
Además, implementa la herramienta de FireBase y FireStore, por lo que necesitará una cuenta activa en [`FireBase Console`](https://console.firebase.google.com/).

## Para iniciar

1. Se deben agregar las dependencias básicas en el archivo gradle/libs.versions.toml agregar versions, libraries y plugins necesarios:, para utilizar FireBase y FireStore

   ```bash
   [versions]
   google-services = "4.4.4"
   firebase-bom = "34.12.0"
   credentials = "1.7.0-alpha02"
   googleid = "1.1.1"
   [libraries]
   firebase-bom = { module = "com.google.firebase:firebase-bom", version.ref = "firebase-bom" }
   firebase-auth = { module = "com.google.firebase:firebase-auth" }
   firebase-firestore = { module = "com.google.firebase:firebase-firestore" }
   androidx-credentials = { module = "androidx.credentials:credentials", version.ref = "credentials" }
   androidx-credentials-play-services-auth = { module = "androidx.credentials:credentials-play-services-auth", version.ref = "credentials" }
   googleid = { module = "com.google.android.libraries.identity.googleid:googleid", version.ref = "googleid" }
   [plugins]
   google-services = { id = "com.google.gms.google-services", version.ref = "google-services" }
   ```

En el archivo app/build.gradle.kts (:app) llamar los plugins y colocar las dependencias

   ```bash
   plugins {
   alias(libs.plugins.google.services)
   }
   dependencias {
   implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    }
   ```

2. Pasos para la instalación:  
   Instalar Android Studio  
   Abrir emulador de Android Studio, se recomienda el modelo Google Pixel 7 por eficiencia, fluidez y launcher más limpio para no sobrecargar los recursos de la PC
   
4. Para obtener las huellas digitales SHA-1 y SHA-256 porque se deben incluir en FireBase Console antes de descargar el archivo google-services.json se usa el siguiente comando para detener el daemon actual y con el segundo se genera uno nuevo que devuelve los valores SHA

   ```bash
   .\gradlew --stop
   .\gradlew signingReport

   Resultado:
   > Task :app:signingReport
   Variant: debug
   SHA1: 56: :38
   SHA-256: B7: :AB
   Valid until: date
   ```

Nota: para que Android Studio no se queje de la versión de Java 8, en la variable de entorno JAVA_HOME se debe cambiar la ruta hacia una versión más reciente, por ejemplo, a esta: C:\Program Files\Java\jdk-17

Ir a FireBase Console para incluir las SHA en la App Android "AndroidProyecto2", luego descargar el archivo google-services.json y colocarlo en la ruta C:/Users/Desarrollo2/AndroidStudioProjects/DSMEvento/app 

Para ver los resultados:

- [Emulador en Android Studio](https://docs.expo.dev/workflow/android-studio-emulator/)

## Para conocer más sobre desarrollo en Kotlin en Android Studio
Para obtener más información sobre cómo desarrollar un proyecto con Kotlin, consulte los siguientes recursos:

- [Documentación de Android Studio](https://developer.android.com/studio/intro)
- Para aprender los conceptos básicos o profundizar en temas avanzados con la información [(Guías)](https://developer.android.com/samples)

## Licenciamiento

El proyecto utiliza la licencia Atribución-NoComercial-CompartirIgual 4.0 Internacional (CC BY-NC-SA 4.0), la cual establece que:
El interesado es libre de: compartir, copiar y redistribuir el material en cualquier medio o formato, de adaptar o remezclar, transformar y construir a partir del material. A cambio, se debe cumplir estrictamente con las siguientes 3 reglas básicas: "BY - dar crédito al autor", "NC - no lucrar con el material" y "SA - compartir tus adaptaciones bajo esta misma licencia". La licenciante no puede revocar estas libertades en tanto usted siga los términos establecidos con anterioridad.

## Autor/es
   Josué Esaú Cruz Mejía - Estudiante UDB Virtual - CM221973

## Documentación solicitada
- [Reporte escrito](https://drive.google.com/file/d/19J8DfCqn4xJNx4Wp5gvPs3gct1cANXNd/view?usp=sharing)
- [Mock Ups](https://drive.google.com/file/d/1way6mP9gVsa0_oX3nRkOIwoOrk38MuPU/view?usp=sharing)

>>>>>>> Para futuras referencias, el commit inicial de este proyecto fue: f0fe3cb
