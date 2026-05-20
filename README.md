# TechGuardian

Sistema integral (Aplicación Móvil + API RESTful) diseñado para la gestión eficiente de tickets de mantenimiento de equipos tecnológicos institucionales. Utiliza una arquitectura limpia y moderna, separando las vistas por roles de usuario para garantizar la seguridad y fluidez del proceso de soporte técnico.

## Características y Roles
* **Administrador:** Acceso a un panel CRUD para registrar, editar y dar de baja a técnicos y personal de oficina.
* **Personal de Oficina (Usuarios):** Creación de tickets de mantenimiento, seleccionando el equipo afectado desde el inventario.
* **Técnicos:** Recepción de tickets de falla para su posterior diagnóstico y reparación.

## Arquitectura y Tecnologías

### Frontend (Aplicación Móvil - Android)
El proyecto móvil sigue los principios de **Clean Architecture** y el patrón **MVVM** (Model-View-ViewModel).
* **UI:** Jetpack Compose (Declarativa)
* **Lenguaje:** Kotlin
* **Consumo de API:** Retrofit2 + Gson
* **Inyección de Dependencias:** Dagger-Hilt
* **Navegación:** Jetpack Navigation Compose
* **Almacenamiento Local:** Room Database / DataStore (Manejo de tokens de sesión)

### Backend (API RESTful)
Construida sin frameworks pesados para un rendimiento ágil y control total sobre las peticiones de red.
* **Lenguaje:** Java puro
* **Servidor HTTP:** `com.sun.net.httpserver.HttpServer` (Nativo de Java)
* **Arquitectura:** REST (Representational State Transfer)
* **Comunicación:** JSON

## Configuración Inicial
Para ejecutar este proyecto en tu entorno local:

1. **Clonar el repositorio:** Descarga el código en tu máquina local.
2. **Iniciar el Servidor (Backend):**
   * Abre el archivo `TechGuardianAPI.java` en tu IDE (IntelliJ, VS Code, etc.) y ejecútalo. El servidor iniciará en el puerto `5001`.
   * Para conectar con la aplicación móvil fuera de la red local, abre una terminal y ejecuta Ngrok: `ngrok http 5001`.
3. **Configurar la App (Frontend):**
   * Abre el proyecto de Android en **Android Studio**.
   * Ve al archivo `AppModule.kt` (ubicado en la carpeta `di/`) y actualiza la variable `baseUrl` con tu URL actual de Ngrok o tu IP local Wi-Fi:
     ```kotlin
     val baseUrl = "[https://tu-url-de-ngrok.ngrok-free.dev/api/](https://tu-url-de-ngrok.ngrok-free.dev/api/)" 
     ```
4. Sincroniza el proyecto con Gradle y ejecuta la aplicación en tu dispositivo físico o emulador.

##  Equipo de Desarrollo
Proyecto desarrollado para la asignatura de Desarrollo de Aplicaciones Moviles.
* Jorge Ivan Muñiz Samano
* Hazziel Enrique Ramirez Vilches
