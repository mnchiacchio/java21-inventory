# Java21-Inventory API

## 📋 Descripción del Proyecto

**Java21-Inventory API** es una API REST desarrollada en Spring Boot para la gestión de inventario de productos. Este proyecto funciona como un **sandbox experimental** para explorar y experimentar con las últimas características de Java 21, incluyendo:

- **Virtual Threads** de Java 21 para manejo eficiente de concurrencia
- **Spring WebFlux** para programación reactiva
- **Concurrencia** y manejo de múltiples operaciones simultáneas
- **Idempotencia** en operaciones de inventario
- **Resiliencia** y patrones de recuperación ante fallos

## 🏗️ Estructura del Proyecto

```
src/main/java/com/example/inventory/
├── controller/          # Controladores REST para manejar endpoints de la API
├── service/            # Lógica de negocio y servicios de aplicación
├── model/              # Entidades y DTOs del dominio de inventario
├── repository/         # Capa de acceso a datos y persistencia
└── InventoryApplication.java  # Clase principal de Spring Boot
```

### Propósito de cada capa:

- **Controller**: Maneja las peticiones HTTP y define los endpoints de la API
- **Service**: Contiene la lógica de negocio y orquesta las operaciones
- **Model**: Define las entidades del dominio (Product, Inventory, etc.) y DTOs
- **Repository**: Abstrae el acceso a datos y define operaciones de persistencia

## 🛠️ Tecnologías Utilizadas

- **Java 21 HotSpot** - Última versión LTS con Virtual Threads
- **Spring Boot 3.5.5** - Framework principal para desarrollo de microservicios
- **Spring WebFlux** - Programación reactiva y no bloqueante
- **Maven Wrapper** - Gestión de dependencias y build del proyecto
- **Cursor IDE** - Entorno de desarrollo integrado
- **Git** - Control de versiones
- **ConcurrentHashMap** - Simulación de base de datos en memoria con soporte para concurrencia segura


## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos
- Java 21 instalado en el sistema
- Terminal o línea de comandos

### Pasos para ejecutar:

1. **Clonar el repositorio** (si no lo has hecho):
   ```bash
   git clone <url-del-repositorio>
   cd java21-inventory
   ```

2. **Abrir terminal** en la raíz del proyecto:
   ```bash
   # Navegar a la carpeta del proyecto
   cd java21-inventory
   ```

3. **Ejecutar la aplicación**:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   En Windows también puedes usar:
   ```powershell
   .\mvnw spring-boot:run  
   ```

4. **Verificar que la aplicación esté corriendo**:
   - La aplicación estará disponible en `http://localhost:8080`
   - Revisa la consola para confirmar que Spring Boot se inició correctamente

## 🎯 Objetivos de Experimentación

Este proyecto está diseñado para experimentar con conceptos avanzados de desarrollo en Java:

### 🔄 Virtual Threads
- Probar el rendimiento de los Virtual Threads de Java 21
- Comparar con threads tradicionales en operaciones de I/O
- Medir el impacto en el manejo de concurrencia

### ⚡ Endpoints Reactivos
- Implementar endpoints usando Spring WebFlux
- Manejar streams de datos de forma reactiva
- Optimizar el rendimiento con programación no bloqueante

### 🔀 Concurrencia
- Gestionar operaciones simultáneas de inventario
- Implementar patrones de concurrencia segura
- Manejar condiciones de carrera en operaciones críticas

### 🔒 Idempotencia
- Garantizar que las operaciones repetidas no causen efectos secundarios
- Implementar claves de idempotencia para operaciones de inventario
- Manejar reintentos seguros en operaciones de red

### 🛡️ Resiliencia
- Implementar circuit breakers y timeouts
- Manejar fallos de servicios externos
- Aplicar patrones de retry y fallback

---

*Este proyecto es un laboratorio de experimentación para explorar las capacidades más avanzadas de Java 21 y Spring Boot en el contexto de APIs de inventario.*
