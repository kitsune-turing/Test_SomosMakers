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

## Preguntas
1. Rendimiento y escalabilidad
Aplicaría concurrencia controlada mediante thread pools y programación asíncrona para evitar bloqueos. Usaría caché en memoria (por ejemplo, para saldos consultados frecuentemente) con políticas de expiración claras. A nivel de diseño, emplearía patrones como CQRS para separar lecturas y escrituras, y circuit breaker para aislar fallos y proteger el sistema bajo alta carga.

2. Seguridad en APIs financieras
a. Protección contra ataques comunes
Implementaría validación estricta de entradas y prepared statements para prevenir inyección SQL. Para CSRF, usaría tokens antifalsificación y deshabilitaría cookies en APIs REST cuando sea posible. Contra XSS, sanitizaría salidas y aplicaría Content Security Policy. Además, reforzaría la seguridad con autenticación fuerte (OAuth2/JWT), cifrado TLS y controles de acceso basados en roles.

3. Transacciones en sistemas distribuidos
a. Consistencia y manejo de errores
Usaría consistencia eventual con el patrón Saga para coordinar transferencias entre servicios, definiendo acciones compensatorias ante fallos. Implementaría idempotencia en las APIs para evitar duplicados y timeouts con reintentos controlados. El monitoreo y logging distribuido permitirían detectar y recuperar errores de forma confiable.

4. Pruebas unitarias y de integración
a. Diseño de la suite de pruebas
Separaría pruebas unitarias para la lógica de negocio, usando mocks de dependencias externas, y pruebas de integración para validar la interacción entre servicios y la base de datos. Automatizaría la ejecución en CI/CD y emplearía datos de prueba controlados. Esto asegura detección temprana de errores y estabilidad del sistema.

5. Front-end
a. Gestión de estado y autenticación
Centralizaría el estado con un store seguro y predecible, evitando duplicidad de datos. La autenticación se manejaría con tokens de corta duración almacenados de forma segura. Sincronizaría el estado con el backend y manejaría la expiración de sesión para mantener coherencia y proteger la información del usuario.

6. Spring Boot
a. @SpringBootApplication y arranque
Esta anotación agrupa la configuración principal, habilita el escaneo de componentes y activa la auto-configuración. Durante el arranque, Spring analiza el classpath, crea el contexto de aplicación y registra los beans, reduciendo la configuración manual y acelerando el inicio.
b. Ciclo de vida de un bean
El ciclo incluye instanciación, inyección de dependencias, inicialización y destrucción. Se puede intervenir usando métodos de inicialización personalizados, interfaces específicas o anotaciones que permiten ejecutar lógica antes o después de cada fase.
c. Personalización de la auto-configuración
Personalizaría mediante propiedades externas, profiles y configuraciones condicionales. También se pueden definir beans propios que sobrescriban los predeterminados. Así se mantiene la filosofía de “convención sobre configuración” sin perder flexibilidad.
