# Sistema Hospitalario con Arquitectura SOA
**Programación Orientada a Servicios - Universidad de Montemorelos**

Este proyecto implementa un sistema hospitalario utilizando **Arquitectura Orientada a Servicios (SOA)** con Spring Boot, demostrando los principios fundamentales de SOA: reutilización, composición, bajo acoplamiento y abstracción.

---

## 📋 Tabla de Contenidos
- [Descripción General](#descripción-general)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Servicios Implementados](#servicios-implementados)
- [Aspecto de Logging (AOP)](#aspecto-de-logging-aop-⭐)
- [Requisitos](#requisitos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Pruebas](#pruebas)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## 📖 Descripción General

El sistema está compuesto por **4 microservicios independientes** que se comunican entre sí mediante REST APIs:

1. **auth-service** (Puerto 8081) - Autenticación y validación de tokens
2. **patient-service** (Puerto 8082) - Gestión de pacientes (CRUD)
3. **notification-service** (Puerto 8083) - Envío de notificaciones (Email, SMS, Alertas)
4. **hospital-gateway** (Puerto 8080) - API Gateway que orquesta los 3 servicios anteriores

### Principios SOA Implementados

✅ **Reutilización** - Cada servicio puede ser consumido por múltiples clientes
✅ **Composición** - El gateway combina servicios simples para crear operaciones complejas
✅ **Bajo Acoplamiento** - Los servicios son independientes y se comunican vía HTTP/REST
✅ **Abstracción** - Cada servicio oculta su implementación interna
✅ **Contrato Estandarizado** - APIs REST con JSON como formato de intercambio

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    HOSPITAL GATEWAY (8080)                  │
│              API Gateway con Aspecto de Logging             │
│           Orquesta y consume los 3 microservicios           │
└────────────────┬──────────────┬──────────────┬──────────────┘
                 │              │              │
        ┌────────▼─────┐ ┌─────▼──────┐ ┌────▼─────────┐
        │ Auth Service │ │  Patient   │ │Notification  │
        │   (8081)     │ │  Service   │ │  Service     │
        │              │ │   (8082)   │ │   (8083)     │
        │ - Login      │ │ - CRUD     │ │ - Email      │
        │ - Validate   │ │ - Register │ │ - SMS        │
        │   Token      │ │ - Query    │ │ - Alerts     │
        └──────────────┘ └────────────┘ └──────────────┘
```

---

## 🔧 Servicios Implementados

### 1️⃣ Auth Service (Puerto 8081)
**Ubicación:** `auth-servie/`

Servicio de autenticación reutilizable para todo el sistema.

**Endpoints:**
- `POST /api/auth/login` - Iniciar sesión
- `GET /api/auth/validate/{token}` - Validar token
- `GET /api/auth/user/{token}` - Obtener usuario desde token

**Usuarios de prueba:**
- admin / admin123
- doctor / doctor123
- nurse / nurse123

---

### 2️⃣ Patient Service (Puerto 8082)
**Ubicación:** `patient-service/`

Servicio de gestión de pacientes con CRUD completo.

**Endpoints:**
- `POST /api/patients/register` - Registrar nuevo paciente
- `GET /api/patients` - Obtener todos los pacientes
- `GET /api/patients/{id}` - Obtener paciente por ID
- `PUT /api/patients/{id}` - Actualizar paciente
- `DELETE /api/patients/{id}` - Eliminar paciente

**Modelo de Paciente:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "phone": "555-1234",
  "age": 35,
  "address": "Calle Principal 123",
  "bloodType": "O+"
}
```

---

### 3️⃣ Notification Service (Puerto 8083)
**Ubicación:** `notification-service/`

Servicio de notificaciones con soporte para múltiples canales.

**Endpoints:**
- `POST /api/notifications/send` - Enviar notificación genérica
- `POST /api/notifications/send/email` - Enviar email
- `POST /api/notifications/send/sms` - Enviar SMS
- `POST /api/notifications/send/alert` - Enviar alerta
- `GET /api/notifications` - Obtener todas las notificaciones
- `GET /api/notifications/{id}` - Obtener por ID
- `GET /api/notifications/recipient/{recipient}` - Obtener por destinatario

---

### 4️⃣ Hospital Gateway (Puerto 8080)
**Ubicación:** `hospital-gateway/`

**API Gateway que orquesta los 3 servicios** - Punto de entrada único al sistema.

#### Endpoints Proxy (acceso directo a servicios)
- `POST /api/gateway/auth/login` - Proxy a auth-service
- `GET /api/gateway/patients` - Proxy a patient-service
- `POST /api/gateway/notifications/send` - Proxy a notification-service

#### **Endpoints de COMPOSICIÓN SOA** ⭐
Estos endpoints demuestran el principio de **composición de servicios**:

**1. Registro Seguro con Notificación:**
```
POST /api/gateway/secure-patient-registration
```
**¿Qué hace?**
1. Autentica al usuario (auth-service)
2. Registra al paciente (patient-service)
3. Envía email de bienvenida (notification-service)

**2. Notificar a un Paciente:**
```
POST /api/gateway/notify-patient/{patientId}?subject=X&message=Y
```
**¿Qué hace?**
1. Obtiene datos del paciente (patient-service)
2. Envía notificación personalizada (notification-service)

**3. Notificación Masiva:**
```
POST /api/gateway/notify-all-patients?subject=X&message=Y
```
**¿Qué hace?**
1. Obtiene TODOS los pacientes (patient-service)
2. Envía email a cada uno (notification-service)

---

## ⭐ Aspecto de Logging (AOP)

### 🎯 ¿Qué es AOP?

**Aspect-Oriented Programming (Programación Orientada a Aspectos)** permite separar funcionalidades transversales (cross-cutting concerns) del código de negocio.

### 📍 Ubicación del Aspecto

```
hospital-gateway/src/main/java/com/example/hospital_gateway/aspect/LoggingAspect.java
```

### 🔍 ¿Qué hace el Aspecto?

El aspecto **intercepta automáticamente TODAS las peticiones** al `GatewayController` y registra:

1. **@Before** - Información de la petición entrante
   - Timestamp
   - Endpoint llamado
   - Parámetros recibidos

2. **@AfterReturning** - Información de respuesta exitosa
   - Tipo de respuesta
   - Estado exitoso ✓

3. **@AfterThrowing** - Captura de errores
   - Mensaje de error
   - Estado fallido ✗

4. **@Around** - Medición de performance
   - Tiempo de ejecución en milisegundos
   - Detección de operaciones lentas (> 1 segundo)

5. **Logging de Servicios** - Trazabilidad de llamadas
   - Registra llamadas a microservicios externos
   - Marca cuando responden exitosamente

### 📊 Ejemplo de Log Generado

Cuando haces una petición al gateway, verás en consola:

```
╔════════════════════════════════════════════════════════════════
║ [AUDIT] Nueva petición recibida
║ Timestamp: 2026-03-26 13:30:45
║ Endpoint: GatewayController.securePatientRegistration
║ Parámetros: [SecurePatientRegistrationRequest(...)]
║ Ejecutando...
    ➜ Llamando a servicio externo: login
    ✓ Servicio login respondió exitosamente
    ➜ Llamando a servicio externo: registerPatient
    ✓ Servicio registerPatient respondió exitosamente
    ➜ Llamando a servicio externo: sendEmail
    ✓ Servicio sendEmail respondió exitosamente
║ [AUDIT] Petición completada exitosamente
║ Método: securePatientRegistration
║ Respuesta: RegistrationWithNotificationResponse
║ Estado: ✓ EXITOSO
║ Tiempo de ejecución: 234 ms
╚════════════════════════════════════════════════════════════════
```

### 🎓 Conceptos AOP Demostrados

✅ **Separación de Responsabilidades** - El logging no contamina el código de negocio
✅ **Reutilización** - Un solo aspecto intercepta TODOS los endpoints
✅ **Cross-cutting Concerns** - Funcionalidad transversal aplicada automáticamente
✅ **No Invasivo** - No modifica el código existente del controller

### 🔧 Configuración del Aspecto

**1. Dependencias en `pom.xml`:**
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

**2. Habilitar AOP en `HospitalGatewayApplication.java`:**
```java
@SpringBootApplication
@EnableAspectJAutoProxy
public class HospitalGatewayApplication { ... }
```

**3. Clase del Aspecto:**
- Anotada con `@Aspect` y `@Component`
- Define `@Pointcut` para especificar dónde aplicar
- Usa `@Before`, `@After`, `@Around` para interceptar

---

## 💻 Requisitos

- **Java 17** o superior
- **Maven 3.6+**
- **cURL** o **Postman** para pruebas
- **4 terminales** para ejecutar los servicios

---

## 🚀 Instalación y Ejecución

### 1. Clonar/Descargar el Proyecto

```bash
cd /Users/hithancrispin/Documents/Paradigmas_24/
```

### 2. Iniciar los Servicios (en orden)

Abre **4 terminales** y ejecuta cada comando:

**Terminal 1 - Auth Service:**
```bash
cd auth-servie
./mvnw spring-boot:run
```

**Terminal 2 - Patient Service:**
```bash
cd patient-service
./mvnw spring-boot:run
```

**Terminal 3 - Notification Service:**
```bash
cd notification-service
./mvnw spring-boot:run
```

**Terminal 4 - Hospital Gateway:**
```bash
cd hospital-gateway
./mvnw spring-boot:run
```

Espera a que cada servicio muestre `Started Application...` antes de continuar.

---

## 🧪 Pruebas

### Verificar que todo está corriendo

```bash
curl http://localhost:8080/api/gateway/health
# Respuesta: "Hospital Gateway is running on port 8080"
```

### Prueba 1: Login
```bash
curl -X POST http://localhost:8080/api/gateway/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Prueba 2: Ver Pacientes
```bash
curl http://localhost:8080/api/gateway/patients
```

### Prueba 3: **Composición SOA** (La más importante)
```bash
curl -X POST http://localhost:8080/api/gateway/secure-patient-registration \
  -H "Content-Type: application/json" \
  -d '{
    "credentials": {
      "username": "admin",
      "password": "admin123"
    },
    "patientData": {
      "firstName": "Ana",
      "lastName": "García",
      "email": "ana.garcia@email.com",
      "phone": "555-7777",
      "age": 25,
      "address": "Avenida Principal 100",
      "bloodType": "A+"
    }
  }'
```

**¡Observa la Terminal 4 (hospital-gateway) para ver el aspecto en acción!**

### Prueba 4: Notificar Paciente
```bash
curl -X POST "http://localhost:8080/api/gateway/notify-patient/1?subject=Recordatorio&message=Tiene%20cita%20mañana"
```

### Prueba 5: Notificación Masiva
```bash
curl -X POST "http://localhost:8080/api/gateway/notify-all-patients?subject=Aviso&message=Horario%20especial%20este%20viernes"
```

---

## 📁 Estructura del Proyecto

```
Paradigmas_24/
├── auth-servie/                    # Servicio de Autenticación (8081)
│   └── src/main/java/.../
│       ├── controller/
│       │   └── AuthController.java
│       ├── service/
│       │   └── AuthService.java
│       ├── model/
│       │   └── User.java
│       └── dto/
│           ├── LoginRequest.java
│           └── AuthResponse.java
│
├── patient-service/                # Servicio de Pacientes (8082)
│   └── src/main/java/.../
│       ├── controller/
│       │   └── PatientController.java
│       ├── service/
│       │   └── PatientService.java
│       ├── model/
│       │   └── Patient.java
│       └── dto/
│           └── PatientDTO.java
│
├── notification-service/           # Servicio de Notificaciones (8083)
│   └── src/main/java/.../
│       ├── controller/
│       │   └── NotificationController.java
│       ├── service/
│       │   └── NotificationService.java
│       ├── model/
│       │   └── Notification.java
│       └── dto/
│           ├── NotificationRequest.java
│           └── NotificationResponse.java
│
├── hospital-gateway/               # API Gateway (8080) ⭐
│   └── src/main/java/.../
│       ├── controller/
│       │   └── GatewayController.java
│       ├── service/
│       │   └── HospitalGatewayService.java
│       ├── aspect/                 # ⭐ ASPECTO DE LOGGING (AOP)
│       │   └── LoggingAspect.java  # ← AQUÍ ESTÁ EL ASPECTO
│       ├── config/
│       │   └── RestTemplateConfig.java
│       └── dto/
│           ├── LoginRequest.java
│           ├── AuthResponse.java
│           ├── Patient.java
│           ├── PatientDTO.java
│           ├── NotificationRequest.java
│           ├── NotificationResponse.java
│           ├── SecurePatientRegistrationRequest.java
│           └── RegistrationWithNotificationResponse.java
│
├── README.md                       # Este archivo
└── .gitignore
```

---

## 🎯 Conceptos Clave Implementados

### SOA (Service-Oriented Architecture)
- ✅ Servicios independientes y reutilizables
- ✅ Comunicación mediante REST/HTTP
- ✅ Composición de servicios para operaciones complejas
- ✅ Bajo acoplamiento entre servicios
- ✅ Abstracción de implementación interna

### AOP (Aspect-Oriented Programming)
- ✅ Logging y auditoría transversal
- ✅ Separación de responsabilidades
- ✅ Interceptación transparente (no invasiva)
- ✅ Medición de performance automática
- ✅ Trazabilidad de llamadas entre servicios

### REST APIs
- ✅ Endpoints bien definidos
- ✅ JSON como formato de intercambio
- ✅ Códigos HTTP apropiados (200, 201, 404, 500)
- ✅ Operaciones CRUD estándar

### Patrones de Diseño
- ✅ **Gateway Pattern** - Punto de entrada único
- ✅ **Service Layer** - Lógica de negocio separada
- ✅ **DTO Pattern** - Transferencia de datos
- ✅ **Repository Pattern** (simulado en memoria)

---

## 👨‍💻 Autores

**Universidad de Montemorelos**
Facultad de Ingeniería y Tecnología (FITEC)
Ingeniería en Sistemas Computacionales
Materia: Programación Orientada a Servicios

---

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AOP Documentation](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [REST API Best Practices](https://restfulapi.net/)
- [SOA Principles](https://www.ibm.com/topics/soa)

---

## 📝 Notas Importantes

1. **Base de Datos:** Los servicios usan almacenamiento en memoria (HashMap). Los datos se pierden al reiniciar.
2. **Seguridad:** Autenticación simulada - NO usar en producción.
3. **Tokens:** Generados con UUID - en producción usar JWT.
4. **Puertos:** Asegúrate que los puertos 8080-8083 estén libres.
5. **Logs del Aspecto:** Solo visibles en la consola del hospital-gateway (Terminal 4).

---

## ✨ Características Destacadas

🎯 **Aspecto de Logging** - Auditoría completa de todas las operaciones
🔄 **Composición SOA** - Operaciones que combinan múltiples servicios
🏗️ **Arquitectura Modular** - Servicios independientes y reutilizables
📊 **Medición de Performance** - Detección automática de operaciones lentas
🔍 **Trazabilidad** - Seguimiento de llamadas entre microservicios

---

**"Educar es redimir"** - Universidad de Montemorelos
