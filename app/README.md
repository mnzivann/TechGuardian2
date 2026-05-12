TechGuardian

TechGuardian es una aplicación móvil nativa para Android diseñada para la gestión eficiente de tickets de mantenimiento y control de activos tecnológicos (computadoras, consolas, smartphones, etc.).

El sistema está construido bajo los principios de Clean Architecture y el patrón de diseño MVVM, garantizando un código escalable, testeable y fácil de mantener. Destaca por su enfoque Offline-First, permitiendo a los técnicos registrar y consultar mantenimientos incluso sin conexión a internet.
Arquitectura del Proyecto

El proyecto está modularizado en capas lógicas estrictas para cumplir con el principio de responsabilidad única (SOLID):

UI Layer (Capa de Presentación): Construida 100% con Jetpack Compose (UI Declarativa). Utiliza ViewModels para gestionar el estado de la interfaz mediante StateFlow, reaccionando a los cambios de datos en tiempo real.

Data Layer (Capa de Datos): Implementa el patrón Repository como única fuente de la verdad (Single Source of Truth). Decide dinámicamente si obtener los datos de la base de datos local (Room) o sincronizarlos con la API REST.

DI Layer (Inyección de Dependencias): Utiliza Hilt para proveer instancias centralizadas (como la base de datos y clientes de red), eliminando el acoplamiento fuerte entre clases.

Stack Tecnológico y Librerías

Lenguaje: Kotlin

UI: Jetpack Compose

Arquitectura: MVVM (Model-View-ViewModel)

Inyección de Dependencias: Dagger Hilt

Asincronía y Reactividad: Kotlin Coroutines & Flow

Persistencia Local: Room Database (SQLite)

Networking: Retrofit2 & OkHttp3 (con Interceptores para autenticación JWT)

Persistencia de Sesión: Preferences DataStore

📂 Estructura de Paquetes
Plaintext
com.example.techguardian2
│
├── data/               # Capa de manejo de datos
│   ├── local/          # Base de datos offline (Room, Entities, DAOs)
│   ├── remote/         # Consumo de API REST (Retrofit, DTOs)
│   └── repository/     # Lógica de sincronización de datos
│
├── di/                 # Módulos de Inyección de Dependencias (Hilt)
│   └── AppModule.kt    # Proveedores de Room y Retrofit
│
└── ui/                 # Capa de Interfaz de Usuario
├── screens/        # Vistas de Compose (Lista de Activos, Tickets)
├── theme/          # Tipografía, Colores y Temas
└── viewmodels/     # Gestión de estados (StateFlow)
🚀 Instalación y Ejecución
Clonar el repositorio:

Bash
git clone https://github.com/mnzivann/TechGuardian2.git
Abrir el proyecto en Android Studio (Ladybug o superior).

Sincronizar los archivos de Gradle.

Para la conexión local con el servidor backend (API REST), asegúrate de que el puerto de escucha en el servidor Java esté configurado correctamente (ej. http://localhost:5001/api) para evitar el error Address already in use del puerto 5000.

Compilar y ejecutar en un emulador (API 24+) o dispositivo físico.

👨‍💻 Autor
Jorge Ivan Muñiz Samano

Estudiante de Ingeniería en Sistemas.

GitHub: @mnzivann