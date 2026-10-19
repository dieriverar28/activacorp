# ActivaCorp - Aplicación de Gestión de Oportunidades Comerciales

**ActivaCorp** es una aplicación nativa para Android desarrollada en Kotlin utilizando **Jetpack Compose** y **Clean Architecture** bajo el patrón **MVVM (Model-View-ViewModel)**. La solución está diseñada para la gestión operativa y supervisión estratégica de oportunidades comerciales corporativas, organizaciones y contactos.

---

## 🚀 Tecnologías y Herramientas

- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
- **Persistencia Local:** Room Database (SQLite)
- **Manejo de Estado y Reactividad:** Kotlin Coroutines, `StateFlow`, `collectAsStateWithLifecycle()`
- **Inyección / Manejo de ViewModel:** AndroidViewModel & `SavedStateHandle`
- **Captura de Imágenes:** Android Camera / ActivityResultContracts con `FileProvider`
- **Control de Versiones:** Git con flujo modular de ramas (`vale_v2`, `vale_supervisor_edicion`, `vale_ui_theme`, `vale_comentarios`, `vale_entrega_final`)

---

## 🏛️ Arquitectura del Proyecto

El proyecto implementa una arquitectura limpia (*Clean Architecture*) organizada en tres capas estructurales:

```text
com.example.activacorp/
├── data/           # Base de datos Room (Entities, DAOs, Database, Repositories)
├── model/          # Modelos de dominio y UI (Organizacion, Oportunidad, Contacto, Enums)
├── pantallas/      # Vistas Compose y ViewModels (Login, Dashboard, Oportunidades, Panel Supervisor)
├── ui/theme/       # Tema gráfico, paleta de colores, tipografías y dimensiones (Material3)
└── util/           # Clases auxiliares (Sesión Singleton, Validadores, CamaraHelper)

👥 Roles de Usuario y Funcionalidades
1. Ejecutivo Comercial
Autenticación: Inicio de sesión local seguro validado contra base de datos Room.

Gestión de Cartera: Visualización y filtrado de oportunidades asignadas.

Registro y Evidencia: Creación de nuevas oportunidades comerciales con captura fotográfica en tiempo real mediante cámara y FileProvider.

Relaciones: Consulta y vinculación de organizaciones y contactos asociados.

2. Supervisor
Panel Global del Supervisor: Visión consolidada de toda la cartera comercial.

Supervisión de Estado: Monitoreo de oportunidades activas, gestiones atrasadas y registros sin asignar.

Reasignación Estratégica: Capacidad de reasignar ejecutivos responsables y modificar prioridades en tiempo real.

🧪 Cuentas de Prueba Precargadas
Rol	Correo Electrónico	Contraseña
Ejecutivo	camila.rojas@agencia.cl	ejecutivo123
Ejecutivo	diego.soto@agencia.cl	ejecutivo123
Supervisor	marcos.fuentes@agencia.cl	supervisor123


🛠️ Instrucciones de Compilación y Ejecución
Clonar el repositorio:

Bash
git clone https://github.com/tu-usuario/activacorp.git
Abrir el proyecto: Cargar la carpeta en Android Studio (Ladybug o superior).

Sincronización: Ejecutar Sync Project with Gradle Files.

Despliegue: Seleccionar un emulador o dispositivo físico con API 26+ (Android 8.0 o superior).

Ejecución: Presionar Run (Shift + F10).
1. **Abrir el proyecto:** Cargar la carpeta en Android Studio (Ladybug o superior).
2. **Sincronización:** Ejecutar *Sync Project with Gradle Files*.
3. **Despliegue:** Seleccionar un emulador o dispositivo físico con API 26+ (Android 8.0 o superior).
4. **Ejecución:** Presionar **Run** (`Shift + F10`).
