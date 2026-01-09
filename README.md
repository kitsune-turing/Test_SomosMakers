# BankLoad - Sistema de Gestión de Préstamos Bancarios

[![Java](https://img.shields.io/badge/Java-20-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Descripción

**BankLoad** es una solución empresarial completa para la gestión de préstamos bancarios, desarrollada con Spring Boot y siguiendo las mejores prácticas de arquitectura de software. El sistema proporciona una plataforma segura y escalable que permite a los usuarios solicitar préstamos de manera eficiente, mientras que los administradores pueden revisar, aprobar o rechazar solicitudes con un flujo de trabajo optimizado.

### Características Principales

- **Autenticación y Autorización Segura**: Implementación de JWT con Spring Security para protección de endpoints
- **Caché Distribuido**: Optimización de rendimiento mediante Redis para consultas frecuentes
- **📊 Panel de Control Interactivo**: Interfaz intuitiva para gestión de préstamos y estadísticas en tiempo real
- **🏗️ Arquitectura Hexagonal**: Separación clara de responsabilidades y alta mantenibilidad
- **🔄 Programación Reactiva**: Endpoints reactivos con Spring WebFlux para operaciones de alta concurrencia
- **📝 Registro de Auditoría**: Trazabilidad completa de todas las operaciones del sistema
- **🐳 Despliegue Containerizado**: Configuración Docker lista para producción
- **🎨 Templates Dinámicos**: Vistas renderizadas del lado del servidor con Thymeleaf

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 20 | Lenguaje de programación principal |
| Spring Boot | 4.0.1 | Framework de aplicación |
| Spring Security | - | Autenticación y autorización |
| Spring Data JPA | - | Persistencia de datos |
| Spring WebFlux | - | Programación reactiva |
| PostgreSQL | Latest | Base de datos relacional |
| Redis | Latest | Caché distribuido |
| Thymeleaf | - | Motor de plantillas |
| Lombok | - | Reducción de código boilerplate |
| Docker | - | Containerización |
| Gradle | - | Gestión de dependencias y build |

## 🏛️ Arquitectura

El proyecto implementa una **arquitectura hexagonal** (ports and adapters) que garantiza:

- ✅ Independencia de frameworks externos
- ✅ Facilidad de testing
- ✅ Separación entre lógica de negocio e infraestructura
- ✅ Flexibilidad para cambios futuros

```
BanckLoad/
├── application/              # Capa de Aplicación
│   ├── controller/          # Controladores REST y Reactivos
│   │   ├── AuthController.java
│   │   ├── LoanController.java
│   │   ├── ReactiveLoanController.java
│   │   ├── StatisticsController.java
│   │   └── ViewController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── LoanRequestDTO.java
│   │   ├── LoanResponseDTO.java
│   │   └── AuthResponseDTO.java
│   ├── service/             # Servicios de aplicación
│   │   ├── AuthService.java
│   │   └── LoanService.java
│   └── exception/           # Manejo global de excepciones
│       ├── GlobalExceptionHandler.java
│       └── ErrorResponse.java
├── domain/                   # Capa de Dominio
│   └── model/               # Entidades y lógica de negocio
│       ├── Loan.java
│       ├── User.java
│       └── Role.java
└── infrastructure/           # Capa de Infraestructura
    ├── config/              # Configuraciones del sistema
    │   ├── SecurityConfig.java
    │   └── RedisConfig.java
    ├── repository/          # Repositorios JPA
    │   ├── LoanRepository.java
    │   └── UserRepository.java
    └── security/            # Seguridad y JWT
        ├── JwtTokenProvider.java
        └── JwtAuthenticationFilter.java
```

## 🚀 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java JDK 20** o superior
- 🐘 **PostgreSQL 14+**
- 🔴 **Redis 6+**
- 🐳 **Docker & Docker Compose** (opcional, para despliegue containerizado)
- 📦 **Gradle 7+** (incluido en el wrapper del proyecto)

## 📦 Instalación y Configuración

### Opción 1: Despliegue con Docker (Recomendado)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/kitsune-turing/Test_SomosMakers.git
   cd BanckLoad
   ```

2. **Iniciar los servicios con Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **La aplicación estará disponible en:**
   - 🌐 Aplicación: `http://localhost:8080`
   - 🗄️ PostgreSQL: `localhost:5432`
   - 🔴 Redis: `localhost:6379`

### Opción 2: Instalación Manual

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/kitsune-turing/Test_SomosMakers.git
   cd BanckLoad
   ```

2. **Configurar PostgreSQL**
   ```sql
   CREATE DATABASE bankload_db;
   CREATE USER bankload_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE bankload_db TO bankload_user;
   ```

3. **Configurar Redis**
   ```bash
   redis-server
   ```

4. **Configurar las variables de entorno** en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/bankload_db
   spring.datasource.username=bankload_user
   spring.datasource.password=your_password
   
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   
   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000
   ```

5. **Compilar y ejecutar la aplicación**
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

## 📚 Documentación de la API

### Autenticación

#### Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@example.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez"
}
```

#### Inicio de Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "usuario",
  "roles": ["ROLE_USER"]
}
```

### Gestión de Préstamos

#### Solicitar Préstamo
```http
POST /api/loans
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 50000.00,
  "termMonths": 24,
  "purpose": "Compra de vehículo",
  "monthlyIncome": 3000.00
}
```

#### Listar Préstamos del Usuario
```http
GET /api/loans/my-loans
Authorization: Bearer {token}
```

#### Obtener Préstamo por ID
```http
GET /api/loans/{id}
Authorization: Bearer {token}
```

### Administración (Requiere rol ADMIN)

#### Listar Todos los Préstamos
```http
GET /api/loans
Authorization: Bearer {token}
```

#### Aprobar Préstamo
```http
PUT /api/loans/{id}/approve
Authorization: Bearer {token}
Content-Type: application/json

{
  "comments": "Préstamo aprobado - cumple requisitos"
}
```

#### Rechazar Préstamo
```http
PUT /api/loans/{id}/reject
Authorization: Bearer {token}
Content-Type: application/json

{
  "comments": "Ingreso mensual insuficiente"
}
```

### Endpoints Reactivos

#### Flujo de Préstamos en Tiempo Real
```http
GET /api/reactive/loans/stream
Authorization: Bearer {token}
Accept: text/event-stream
```

## 🖥️ Vistas Web

El sistema incluye una interfaz web completa accesible desde el navegador:

- 🏠 **Login**: `/login` - Página de inicio de sesión
- 📝 **Registro**: `/register` - Registro de nuevos usuarios
- 📊 **Dashboard**: `/dashboard` - Panel de control con:
  - Formulario de solicitud de préstamos
  - Lista de préstamos del usuario
  - Panel de administración (solo para administradores)
  - Estadísticas en tiempo real

## 🧪 Pruebas

### Ejecutar todas las pruebas
```bash
./gradlew test
```

### Ejecutar pruebas con reporte de cobertura
```bash
./gradlew test jacocoTestReport
```

### Ejecutar solo pruebas unitarias
```bash
./gradlew test --tests "*Test"
```

### Ejecutar solo pruebas de integración
```bash
./gradlew test --tests "*IntegrationTest"
```

## 🔒 Seguridad

El sistema implementa múltiples capas de seguridad:

- **🔐 JWT (JSON Web Tokens)**: Autenticación stateless
- **🛡️ Spring Security**: Protección de endpoints y control de acceso basado en roles
- **🔒 Encriptación de Contraseñas**: BCrypt con factor de trabajo configurable
- **🚫 Protección CSRF**: Tokens anti-falsificación
- **✅ Validación de Entrada**: Sanitización y validación en todas las capas
- **📜 Auditoría**: Registro de todas las operaciones críticas

## ⚡ Optimización y Rendimiento

- **Redis Cache**: Almacenamiento en caché de consultas frecuentes
- **Connection Pooling**: HikariCP para gestión eficiente de conexiones a BD
- **Lazy Loading**: Carga perezosa de relaciones JPA
- **Índices de Base de Datos**: Optimización de consultas frecuentes
- **Programación Reactiva**: Manejo eficiente de operaciones I/O

## 📊 Monitoreo

El sistema incluye endpoints de monitoreo:

```http
GET /actuator/health        # Estado de salud de la aplicación
GET /actuator/metrics       # Métricas de la aplicación
GET /actuator/info          # Información de la aplicación
```

## 🤝 Contribución

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Autores

- **Kitsune Turing** - *Desarrollo Inicial* - [GitHub](https://github.com/kitsune-turing)

## 📞 Soporte

Para reportar bugs o solicitar nuevas características, por favor abre un issue en el repositorio de GitHub.

---

## Preguntas

### 1. Rendimiento y escalabilidad
Aplicaría concurrencia controlada mediante thread pools y programación asíncrona para evitar bloqueos. Usaría caché en memoria (por ejemplo, para saldos consultados frecuentemente) con políticas de expiración claras. A nivel de diseño, emplearía patrones como CQRS para separar lecturas y escrituras, y circuit breaker para aislar fallos y proteger el sistema bajo alta carga.

### 2. Seguridad en APIs financieras

**a. Protección contra ataques comunes**

Implementaría validación estricta de entradas y prepared statements para prevenir inyección SQL. Para CSRF, usaría tokens antifalsificación y deshabilitaría cookies en APIs REST cuando sea posible. Contra XSS, sanitizaría salidas y aplicaría Content Security Policy. Además, reforzaría la seguridad con autenticación fuerte (OAuth2/JWT), cifrado TLS y controles de acceso basados en roles.

### 3. Transacciones en sistemas distribuidos

**a. Consistencia y manejo de errores**

Usaría consistencia eventual con el patrón Saga para coordinar transferencias entre servicios, definiendo acciones compensatorias ante fallos. Implementaría idempotencia en las APIs para evitar duplicados y timeouts con reintentos controlados. El monitoreo y logging distribuido permitirían detectar y recuperar errores de forma confiable.

### 4. Pruebas unitarias y de integración

**a. Diseño de la suite de pruebas**

Separaría pruebas unitarias para la lógica de negocio, usando mocks de dependencias externas, y pruebas de integración para validar la interacción entre servicios y la base de datos. Automatizaría la ejecución en CI/CD y emplearía datos de prueba controlados. Esto asegura detección temprana de errores y estabilidad del sistema.

### 5. Front-end

**a. Gestión de estado y autenticación**

Centralizaría el estado con un store seguro y predecible, evitando duplicidad de datos. La autenticación se manejaría con tokens de corta duración almacenados de forma segura. Sincronizaría el estado con el backend y manejaría la expiración de sesión para mantener coherencia y proteger la información del usuario.

### 6. Spring Boot

**a. @SpringBootApplication y arranque**

Esta anotación agrupa la configuración principal, habilita el escaneo de componentes y activa la auto-configuración. Durante el arranque, Spring analiza el classpath, crea el contexto de aplicación y registra los beans, reduciendo la configuración manual y acelerando el inicio.

**b. Ciclo de vida de un bean**

El ciclo incluye instanciación, inyección de dependencias, inicialización y destrucción. Se puede intervenir usando métodos de inicialización personalizados, interfaces específicas o anotaciones que permiten ejecutar lógica antes o después de cada fase.

**c. Personalización de la auto-configuración**

Personalizaría mediante propiedades externas, profiles y configuraciones condicionales. También se pueden definir beans propios que sobrescriban los predeterminados. Así se mantiene la filosofía de "convención sobre configuración" sin perder flexibilidad.

---

**⭐ Si este proyecto te ha sido útil, considera darle una estrella en GitHub!**
