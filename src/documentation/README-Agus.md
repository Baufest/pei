# 📄 Documentación de Cambio

## 📝 Título del Cambio
Implementación de endpoint para alerta de aprobaciones sospechosas en transacciones

---

## 🧑‍💻 Historia de Usuario #225

---

## 📌 Resumen Breve
Se agregó un nuevo endpoint que permite evaluar si una transacción requiere alerta por aprobaciones sospechosas. El sistema analiza el nivel de riesgo y la criticidad de la transacción, generando una alerta si se detecta un patrón inusual o riesgoso en el proceso de aprobación.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - Método: `evaluateApprovals(Long transactionId)`
- `com.pei.service.AlertService` (interfaz y/o implementación)
    - Método: `approvalAlert(Long transactionId)`
- `com.pei.dto.Alert`
- `com.pei.domain.Transaction` (usado indirectamente)

### Endpoints Nuevos/Modificados
| Método HTTP | URL                      | Parámetros                        | Respuesta                                      |
|-------------|--------------------------|-----------------------------------|------------------------------------------------|
| POST        | /api/alerta-aprobaciones | `transactionId` en el body (JSON) | `Alert` con mensaje si se detecta actividad sospechosa |

### Cambios en Base de Datos
- No aplica. El endpoint realiza análisis sobre transacciones existentes, sin modificar la estructura ni los datos de la base.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Dependencias relevantes: `AlertService`, `Transaction`

---

## 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-aprobaciones
Content-Type: application/json

123
```

**Response (caso positivo)**
```json
{
  "userId": 123,
  "description": "Transacción con ID = 123 tiene más de 2 aprobaciones"
}
```

**Response (caso negativo)**
```http
404 Not Found
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenario Cubierto
- `shouldReturnAlertWhenTransactionHasMoreThanTwoApprovals`: Genera alerta si la transacción tiene más de dos aprobaciones.

### 🧪 Endpoints Probados
| Método HTTP | URL                      | Escenario de Test                       | Resultado Esperado |
|-------------|--------------------------|-----------------------------------------|--------------------|
| POST        | /api/alerta-aprobaciones | Transacción con más de dos aprobaciones | Alerta generada    |

---

## ✅ Estado
✔️ Completado

---

## 📦 Documentación de Integraciones Externas

_No aplica para este endpoint. No se utilizan servicios externos._

---

## 📝 Título del Cambio
Implementación de endpoint para alerta de comportamiento fuera del rango horario promedio

---
# 📄 Documentación de Cambio

## 🧑‍💻 Historia de Usuario #219

---

## 📌 Resumen Breve
Se implementó el endpoint `/alerta-horario` que permite detectar si una transacción se realiza fuera del rango horario habitual del usuario. El sistema compara la hora de la nueva transacción con el rango promedio calculado a partir del historial de transacciones del usuario (usando la clase `TimeRange` embebida en el usuario). Si la transacción está fuera del rango, se genera una alerta.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - Método: `evaluateTransactionOutOfTimeRange(TimeRangeRequest request)`
- `com.pei.service.AlertService` (interfaz y/o implementación)
    - Método: `timeRangeAlert(List<Transaction> historial, Transaction nuevaTransaccion)`
- `com.pei.dto.TimeRangeRequest`
- `com.pei.dto.Alert`
- `com.pei.domain.Transaction`
- `com.pei.domain.TimeRange` (embebida en el usuario)

### Endpoints Nuevos/Modificados
| Método HTTP | URL                | Parámetros (Body)         | Respuesta                                      |
|-------------|--------------------|---------------------------|------------------------------------------------|
| POST        | `/api/alerta-horario` | `TimeRangeRequest`        | `Alert` con mensaje si la transacción está fuera del rango horario |

### Cambios en Base de Datos
- No aplica. El análisis se realiza sobre datos existentes y la clase `TimeRange` está embebida en el usuario.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Dependencias relevantes: `AlertService`, `Transaction`, `TimeRange`, `TimeRangeRequest`

---

## 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-horario
Content-Type: application/json
{
  "transactions": [
    { "id": 1, "dateHour": "2025-08-13T09:30:00" },
    { "id": 2, "dateHour": "2025-08-13T14:20:00" }
  ],
  "newTransaction": { "id": 3, "dateHour": "2025-08-13T23:10:00" }
}
```

**Response (caso positivo)**
```json
{
  "userId": 3,
  "description": "Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14"
}
```

**Response (caso negativo)**
```http
404 Not Found
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenario Cubierto
- `shouldReturnAlertWhenTransactionIsOutOfTimeRange`: Genera alerta si la nueva transacción está fuera del rango horario promedio.

### 🧪 Endpoints Probados
| Método HTTP | URL                | Escenario de Test                       | Resultado Esperado |
|-------------|--------------------|-----------------------------------------|--------------------|
| POST        | `/api/alerta-horario` | Transacción fuera del rango horario     | Alerta generada    |

---

## ✅ Estado
✔️ Completado

---

# 📄 Documentación de Cambio

## 📝 Título del Cambio
Implementación de endpoint para escalado de alerta por canal de comunicación

---

## 🧑‍💻 Historia de Usuario #236

---

## 📌 Resumen Breve
Se desarrolló el endpoint `/alerta-canales` que permite escalar alertas de transacciones sospechosas a través de diferentes canales de comunicación (email, SMS, etc.). El sistema utiliza dependencias externas para el envío de notificaciones, como JavaMail, configuradas en el `pom.xml` y en `application.properties`. La lógica determina el canal adecuado según la criticidad de la alerta y los datos del usuario.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - Método: `escalateAlertByChannel(ChannelAlertRequest request)`
- `com.pei.service.AlertService` (interfaz y/o implementación)
    - Método: `escalateAlertByChannel(Transaction transaction, User user)`
- `com.pei.service.NotificationService` (envío de notificaciones)
    - Métodos: `sendEmail(Alert alert, User user)`, `sendSms(Alert alert, User user)`
- `com.pei.dto.ChannelAlertRequest`
- `com.pei.dto.Alert`
- `com.pei.domain.Transaction`
- `com.pei.domain.User`

### Endpoints Nuevos/Modificados
| Método HTTP | URL                  | Parámetros (Body)         | Respuesta                                      |
|-------------|----------------------|---------------------------|------------------------------------------------|
| POST        | `/api/alerta-canales` | `ChannelAlertRequest`     | `Alert` con mensaje y canal utilizado          |

### Cambios en Base de Datos
- No aplica. El endpoint utiliza datos existentes y no modifica la estructura ni los datos de la base.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`, `NotificationService`
- Dependencias relevantes: `AlertService`, `NotificationService`, `Transaction`, `User`
- Integraciones externas: JavaMail (email), proveedor SMS

---

## 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-canales
Content-Type: application/json
{
  "transactionId": 456,
  "userId": 789,
  "channel": "EMAIL"
}
```

**Response (caso positivo)**
```json
{
  "userId": 789,
  "description": "Alerta escalada por EMAIL para la transacción 456"
}
```

**Response (caso negativo)**
```http
404 Not Found
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenario Cubierto
- `escalateAlertByChannel_CuandoTransaccionYUsuarioValidos_EnvioPorCanalCorrecto`: Genera alerta y envía notificación por el canal especificado si la transacción y el usuario existen.

### 🧪 Endpoints Probados
| Método HTTP | URL                  | Escenario de Test                       | Resultado Esperado |
|-------------|----------------------|-----------------------------------------|--------------------|
| POST        | `/api/alerta-canales` | Transacción y usuario válidos           | Alerta y notificación enviada |

---

## ✅ Estado
✔️ Completado

---

## 📦 Documentación de Integraciones Externas

- **Servicio de Email (JavaMail)**: Configurado en `pom.xml` y `application.properties` para envío de alertas por correo electrónico.
- **Servicio SMTP (Ethereal)**: Configurado para pruebas de envío de correos electrónicos con credenciales en `application.properties`.
---

### Uso del servicio de correos con Ethereal

Para probar correctamente el sistema de envío de mails se utilizó Ethereal, un servicio SMTP de prueba. Para verificar que los correos se envían correctamente, deben seguir los siguientes pasos:

1. Acceder a [https://ethereal.email/create](https://ethereal.email/create).
2. Seleccionar la opción **Login** en la esquina superior derecha.
3. Iniciar sesión con las siguientes credenciales:
    - Usuario: `karine.koepp98@ethereal.email`
    - Contraseña: `stVCqRSSE3vfrVDDKq`
4. Una vez dentro, dirigirse a la sección **Messages** en la parte superior.
5. Aquí se podrán ver los correos recibidos que hayan sido enviados desde la aplicación.
6. Levantar la aplicación `PeiApplication`.
7. Enviar un correo de prueba desde Postman o cualquier otro cliente que use la aplicación.
8. El mensaje enviado debería aparecer en la sección **Messages** de Ethereal si todo funcionó correctamente.

---
