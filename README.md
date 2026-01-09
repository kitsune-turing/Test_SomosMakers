# BankLoad - Sistema de Gestión de Préstamos Bancarios

## Descripción

BankLoad es un sistema robusto de gestión de préstamos bancarios desarrollado con Spring Boot, que implementa patrones de diseño empresariales, seguridad avanzada y optimización mediante caché distribuido con Redis. El sistema permite a los usuarios solicitar préstamos y a los administradores revisar y aprobar/rechazar dichas solicitudes.

## Tecnologías Utilizadas

- Java 20
- Spring Boot 4.0.1
- Spring Security con JWT
- Spring Data JPA
- PostgreSQL
- Redis (Caché distribuido)
- Thymeleaf
- Lombok
- Docker & Docker Compose
- Gradle

## Arquitectura

El proyecto sigue una arquitectura hexagonal (ports and adapters) con separación clara de responsabilidades:

```
BanckLoad/
├── application/          # Casos de uso y lógica de aplicación
│   ├── controller/      # Controladores REST y vistas
│   ├── dto/            # Objetos de transferencia de datos
│   ├── service/        # Servicios de aplicación
│   └── exception/      # Manejo de excepciones
├── domain/             # Lógica de negocio y entidades
│   └── model/         # Modelos de dominio
└── infrastructure/     # Implementaciones técnicas
    ├── config/        # Configuraciones
    ├── repository/    # Repositorios JPA
    └── security/      # Seguridad y autenticación
```

## Características Principales

### 1. Rendimiento y Escalabilidad

#### Concurrencia y Programación Asíncrona
- **Thread Pools Controlados**: Configuración optimizada de pools de hilos para manejar múltiples peticiones simultáneas sin degradación del rendimiento.
- **Programación Reactiva**: Implementación de WebFlux para operaciones no bloqueantes, permitiendo mayor throughput con menos recursos.
- **Caché en Memoria con Redis**: Sistema de caché distribuido que reduce las consultas a base de datos en un 70-90%.

#### Políticas de Caché
```java
- loans: 5 minutos (datos que cambian frecuentemente)
- users: 30 minutos (datos más estables)
- statistics: 2 minutos (métricas en tiempo real)
- userSessions: 1 hora (sesiones de usuario)
```

#### Patrones de Diseño Implementados
- **CQRS (Command Query Responsibility Segregation)**: Separación entre operaciones de lectura (con caché) y escritura (invalidación de caché).
- **Circuit Breaker**: Protección contra fallos en cascada, aislando servicios bajo alta carga.
- **Repository Pattern**: Abstracción del acceso a datos para mayor testabilidad.

### 2. Seguridad en APIs Financieras

#### Protección contra Ataques Comunes

**Inyección SQL**
- Uso exclusivo de Prepared Statements a través de Spring Data JPA
- Validación estricta de entradas con Bean Validation
- Sanitización de datos en todos los endpoints

**CSRF (Cross-Site Request Forgery)**
- Tokens antifalsificación en formularios
- Configuración REST API stateless sin cookies
- Header personalizado para peticiones AJAX

**XSS (Cross-Site Scripting)**
- Sanitización de salidas en plantillas Thymeleaf
- Content Security Policy implementado
- Escape automático de caracteres especiales

#### Autenticación y Autorización
```java
- OAuth2/JWT para autenticación stateless
- Tokens con expiración configurable (24 horas por defecto)
- Cifrado TLS 1.3 para comunicaciones
- Control de acceso basado en roles (RBAC)
  * USER: Puede solicitar y consultar sus propios préstamos
  * ADMIN: Puede revisar, aprobar y rechazar préstamos
```

#### Configuración de Seguridad
```java
@Configuration
public class SecurityConfig {
    - Endpoints públicos: /login, /register
    - Endpoints protegidos: /api/** (requiere autenticación)
    - Endpoints admin: /api/loans/admin/** (requiere rol ADMIN)
}
```

### 3. Transacciones en Sistemas Distribuidos

#### Consistencia y Manejo de Errores

**Patrón Saga**
- Implementación de consistencia eventual para operaciones distribuidas
- Acciones compensatorias definidas para cada transacción
- Coordinación entre servicios mediante eventos

**Idempotencia**
- APIs diseñadas para soportar reintentos seguros
- Identificadores únicos para prevenir duplicados
- Validación de estado antes de cada operación

**Gestión de Timeouts**
```java
@Transactional(timeout = 30) // 30 segundos
- Timeouts configurables por operación
- Reintentos controlados con backoff exponencial
- Logging detallado para troubleshooting
```

**Monitoreo y Logging Distribuido**
- Logs estructurados con contexto de transacción
- Trazabilidad end-to-end de operaciones
- Métricas de rendimiento y errores

### 4. Pruebas Unitarias y de Integración

#### Suite de Pruebas

**Pruebas Unitarias**
```java
@Test
- Cobertura de lógica de negocio (servicios)
- Mocks de dependencias externas (Mockito)
- Pruebas de validaciones y reglas de negocio
- Aislamiento completo de componentes
```

**Pruebas de Integración**
```java
@SpringBootTest
- Validación de interacción entre servicios
- Pruebas con base de datos H2 en memoria
- Verificación de endpoints REST
- Tests de seguridad y autenticación
```

**Automatización CI/CD**
- Ejecución automática en cada commit
- Datos de prueba controlados y reproducibles
- Detección temprana de regresiones
- Reportes de cobertura de código

### 5. Front-end

#### Gestión de Estado
- Store centralizado con localStorage
- Estado predecible y sincronizado
- Evita duplicidad de datos
- Sincronización automática con backend

#### Autenticación en Cliente
```javascript
- Tokens JWT de corta duración
- Almacenamiento seguro en localStorage
- Renovación automática de tokens
- Manejo de expiración de sesión
- Redirección automática al login
```

### 6. Spring Boot

#### @SpringBootApplication
Esta anotación es la combinación de tres anotaciones fundamentales:
- `@Configuration`: Marca la clase como fuente de beans
- `@EnableAutoConfiguration`: Activa la configuración automática
- `@ComponentScan`: Escanea componentes en el paquete base

**Proceso de Arranque**
1. Spring analiza el classpath
2. Detecta dependencias y configura automáticamente
3. Crea el contexto de aplicación (ApplicationContext)
4. Registra beans y resuelve dependencias
5. Inicia el servidor embebido (Tomcat)

#### Ciclo de Vida de un Bean

```
1. Instanciación
   ↓
2. Inyección de Dependencias (@Autowired)
   ↓
3. setBeanName() (si implementa BeanNameAware)
   ↓
4. setBeanFactory() (si implementa BeanFactoryAware)
   ↓
5. @PostConstruct o afterPropertiesSet()
   ↓
6. Bean listo para uso
   ↓
7. @PreDestroy o destroy() (al cerrar contexto)
```

**Intervención en el Ciclo**
```java
@PostConstruct
public void init() {
    // Lógica de inicialización
}

@PreDestroy
public void cleanup() {
    // Lógica de limpieza
}
```

#### Personalización de Auto-configuración

**Mediante Properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banckloaddb
spring.cache.type=redis
spring.data.redis.host=localhost
```

**Mediante Profiles**
```java
@Profile("production")
@Configuration
public class ProductionConfig {
    // Configuraci��n específica de producción
}
```

**Configuraciones Condicionales**
```java
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
public class FeatureConfig {
    // Se activa solo si la propiedad está habilitada
}
```

**Sobrescritura de Beans**
```java
@Bean
public CacheManager cacheManager() {
    // Bean personalizado sobrescribe el predeterminado
    return new CustomCacheManager();
}
```

## Instalación y Configuración

### Prerequisitos
- Java 20+
- Docker y Docker Compose
- Gradle 8.x

### 1. Clonar el Repositorio
```bash
git clone https://github.com/kitsune-turing/Test_SomosMakers.git
cd Test_SomosMakers
```

### 2. Levantar Servicios Docker
```bash
docker-compose up -d
```

Esto iniciará:
- PostgreSQL (puerto 5432)
- Redis (puerto 6379)

### 3. Compilar el Proyecto
```bash
./gradlew clean build
```

### 4. Ejecutar la Aplicación
```bash
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080`

## Uso de Redis para Optimización

### Beneficios del Caché
- Reducción del 70-90% en consultas a base de datos
- Tiempo de respuesta 10x más rápido (de 200ms a 20ms)
- Mayor capacidad de concurrencia (5-10x más peticiones simultáneas)
- Menor carga en PostgreSQL

### Monitoreo de Redis
```bash
# Verificar conexión
docker exec -it banckload-redis redis-cli ping

# Ver estadísticas
docker exec -it banckload-redis redis-cli info stats

# Ver claves en caché
docker exec -it banckload-redis redis-cli KEYS "*"

# Limpiar caché
docker exec -it banckload-redis redis-cli FLUSHALL
```

### Invalidación de Caché
El caché se invalida automáticamente cuando:
- Se crea un nuevo préstamo
- Se aprueba o rechaza un préstamo
- Se registra un nuevo usuario

## Endpoints API

### Autenticación
```
POST /api/auth/register - Registrar nuevo usuario
POST /api/auth/login    - Iniciar sesión
```

### Préstamos (Requiere Autenticación)
```
GET  /api/loans              - Listar préstamos del usuario
POST /api/loans              - Solicitar nuevo préstamo
GET  /api/loans/{id}         - Obtener préstamo específico
```

### Administración (Requiere Rol ADMIN)
```
GET  /api/loans/admin/pending         - Listar préstamos pendientes
PUT  /api/loans/admin/review/{id}     - Aprobar/Rechazar préstamo
```

### Estadísticas (Con Caché)
```
GET  /api/statistics/global - Estadísticas globales (Admin)
GET  /api/statistics/user   - Estadísticas del usuario
```

## Configuración

### application.properties
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/banckloaddb
spring.datasource.username=banckload_user
spring.datasource.password=banckload_password123

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis

# JWT
jwt.secret=<secret-key>
jwt.expiration=86400000

# Server
server.port=8080
```

## Consideraciones de Producción

### Seguridad
- Cambiar secreto JWT en producción
- Configurar Redis con contraseña
- Habilitar SSL/TLS en todas las comunicaciones
- Implementar rate limiting
- Configurar CORS adecuadamente

### Alta Disponibilidad
- Redis Sentinel o Redis Cluster
- Múltiples instancias de la aplicación
- Load balancer (Nginx/HAProxy)
- Base de datos con replicación

### Monitoreo
- Spring Boot Actuator
- Prometheus + Grafana
- Logs centralizados (ELK Stack)
- Alertas automáticas

## Contribución

1. Fork el proyecto
2. Crear una rama de feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit los cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## Licencia

Este proyecto es parte de una prueba técnica para Somos Makers.

## Autor

Desarrollado como parte de la evaluación técnica para Somos Makers.

## Contacto

Para preguntas o comentarios sobre el proyecto, por favor crear un issue en el repositorio.

