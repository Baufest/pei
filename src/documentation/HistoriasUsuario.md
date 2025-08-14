# 📘 Historias de Usuario - Proyecto PEI

Este documento tiene como objetivo registrar las historias de usuario desarrolladas por el equipo. Cada sección corresponde a una historia completada, incluyendo su descripción funcional, técnica y ejemplos de uso.

---

## 🧑‍💻 Historia de Usuario #221

### 📝 Título
Implementación y pruebas de detección de cuentas intermediarias ("Money Mule")

---

### 📌 Descripción Breve
Se desarrolló un endpoint para detectar comportamientos sospechosos en cuentas que podrían estar actuando como intermediarias para lavado de dinero. Además, se implementaron pruebas unitarias para validar el comportamiento del controlador asociado.

---

### ⚙️ Detalles Técnicos

#### 🧩 Implementación
# 📄 Documentación de Cambio

## 📝 Título del Cambio
Implementación de endpoint para detección de cuentas intermediarias ("Money Mule")

---

## 📌 Resumen Breve
Se implementó un nuevo endpoint que permite detectar comportamientos sospechosos en cuentas que podrían estar actuando como intermediarias para lavado de dinero. La lógica se basa en identificar cuentas que reciben múltiples depósitos pequeños y luego realizan una transferencia significativa en un período de 24 horas.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - Método: `detectMoneyMule(List<Transaction> transactions)`
- `com.pei.service.AlertService`
    - Método: `verifyMoneyMule(List<Transaction> transactions)`
- `com.pei.domain.Transaction`
- `com.pei.dto.Alert`

### Endpoints Nuevos/Modificados
| Método HTTP | URL                     | Parámetros                          | Respuesta                                      |
|-------------|-------------------------|-------------------------------------|------------------------------------------------|
| POST        | /api/alerta-money-mule  | `List<Transaction>` en el body JSON | `Alert` con mensaje si se detecta actividad sospechosa |

### Cambios en Base de Datos
- No aplica. El análisis se realiza sobre datos ya existentes sin modificar la estructura de la base.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Dependencias relevantes: `AlertService`, `Transaction`, `User`

---

## 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-money-mule
Content-Type: application/json
[
  {
    "id": 1,
    "type": "DEPOSIT",
    "amount": 100.0,
    "timestamp": "2025-08-12T10:00:00",
    "user": {
      "id": 123
    }
  },
  {
    "id": 2,
    "type": "DEPOSIT",
    "amount": 150.0,
    "timestamp": "2025-08-12T11:00:00",
    "user": {
      "id": 123
    }
  },
  ...
  {
    "id": 10,
    "type": "TRANSFER",
    "amount": 800.0,
    "timestamp": "2025-08-12T20:00:00",
    "user": {
      "id": 123
    }
  }
]
```


---

#### 🧪 Pruebas Unitarias
# 📄 Documentación de Cambio

## 📝 Título del Cambio
Implementación de pruebas unitarias para el controlador `AlertController`

---

## 📌 Resumen Breve
Se desarrollaron pruebas unitarias utilizando `MockMvc` para validar el comportamiento de los endpoints del controlador `AlertController`. Las pruebas cubren distintos escenarios de detección de alertas, incluyendo cuentas intermediarias, cuentas recién creadas, clientes de alto riesgo y validación de perfil de usuario.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertControllerTest`
    - `Should_ReturnOkAlert_When_MoneyMuleDetected`
    - `Should_ReturnNotContent_When_MoneyMuleNotDetected`
    - `ValidarTransferenciasCuentasRecienCreadasTests`
        - `validarTransferenciasCuentasRecienCreadas_CuandoOk_RetornaAlerta`
        - `validarClienteAltoRiesgo_CuandoOk_RetornaAlerta`
        - `validateUserProfileTransaction`

### Endpoints Probados
| Método HTTP | URL                                 | Escenario de Test                                      | Resultado Esperado |
|-------------|--------------------------------------|--------------------------------------------------------|--------------------|
| POST        | /api/alerta-money-mule              | Detección positiva de Money Mule                      | 200 OK + JSON con alerta |
| POST        | /api/alerta-money-mule              | No se detecta Money Mule                              | 404 Not Found      |
| POST        | /api/alerta-cuenta-nueva            | Validación exitosa de cuenta recién creada            | 200 OK             |
| GET         | /api/alerta-cliente-alto-riesgo/{id}| Cliente de alto riesgo detectado                      | 200 OK             |
| POST        | /api/alerta-perfil                  | Validación de perfil de usuario para transacción      | 200 OK + JSON con descripción |

### Cambios en Base de Datos
- No aplica. Las pruebas se realizan con objetos simulados (`mock`) sin interacción con la base de datos.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Mejora la confiabilidad del sistema al validar el comportamiento esperado de los endpoints.
- Uso de `MockitoBean` para simular servicios dependientes (`AlertService`, `AccountService`).

---

## 💻 Ejemplo de Uso

**Ejemplo de test exitoso para Money Mule**
```java
mockMvc.perform(post("/api/alerta-money-mule")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(transactions)))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.userId").value(1))
    .andExpect(jsonPath("$.description").value("Alerta: Posible Money Mule detectado del usuario 1"));
```

---

## ✅ Estado
✔️ Completado


# 📘 Historias de Usuario - Proyecto PEI

---

## 🧑‍💻 Historia de Usuario #123

### 📝 Título
Alerta de fraude por chargebacks en usuarios

---

### 📌 Descripción Breve
Implementa la detección automática de fraude por chargebacks. Si un usuario tiene chargebacks sin compras, o una proporción de chargebacks sobre compras mayor al 10%, se genera una alerta de fraude.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `TransactionService`
  - Método: `getChargebackFraudAlert(Long userId)`

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP |               URL                 |  Parámetros  |        Respuesta        |
|-------------|-----------------------------------|--------------|-------------------------|
| GET         | `/api/alerts/chargeback/{userId}` | Path: userId | Alerta de fraude o null |

#### 🗃️ Cambios en Base de Datos
- No aplica

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Transacciones y alertas
- Dependencias relevantes: `ChargebackRepository`, `PurchaseRepository`

---

### 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerts/chargeback/123
```

**Response**
```json
{
  "userId": 123,
  "message": "Fraude detectado: chargebacks sin compras o proporción elevada"
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `getChargebackFraudAlert_CuandoNoHayComprasYHayChargebacks_GeneraAlerta`: Detecta alerta cuando hay chargebacks y cero compras.
- `getChargebackFraudAlert_CuandoProporcionChargebacksMayor10_GeneraAlerta`: Detecta alerta cuando la proporción supera el 10%.
- `getChargebackFraudAlert_CuandoNoCumpleCondiciones_NoGeneraAlerta`: No genera alerta si no se cumplen condiciones.

### 🧪 Endpoints Probados
| Método HTTP | URL | Escenario de Test | Resultado Esperado |
|-------------|-----|-------------------|---------------------|
| GET         | `/api/alerts/chargeback/{userId}` | Usuario con fraude | Alerta generada |

---

## ✅ Estado
- ✔️ Completado

---

## 🧑‍💻 Historia de Usuario #124

### 📝 Título
Alerta por múltiples transacciones rápidas

---

### 📌 Descripción Breve
Detecta y alerta cuando un usuario realiza más de 10 transacciones en menos de una hora, indicando posible actividad fraudulenta.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `TransactionService`
  - Método: `getFastMultipleTransactionAlert(Long userId)`

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP |                  URL                     |  Parámetros  |    Respuesta    |
|-------------|------------------------------------------|--------------|-----------------|
| GET         | `/api/alerts/fast-transactions/{userId}` | Path: userId | Alerta de fraude o null |

#### 🗃️ Cambios en Base de Datos
- No aplica

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Transacciones y alertas
- Dependencias relevantes: `TransactionRepository`

---

### 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerts/fast-transactions/123
```

**Response**
```json
{
  "userId": 123,
  "message": "Más de 10 transacciones en la última hora"
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `getFastMultipleTransactionAlert_CuandoSuperaLimite_GeneraAlerta`: Genera alerta si hay más de 10 transacciones en una hora.
- `getFastMultipleTransactionAlert_CuandoNoSuperaLimite_NoGeneraAlerta`: No genera alerta si no supera el límite.

### 🧪 Endpoints Probados
| Método HTTP | URL | Escenario de Test | Resultado Esperado |
|-------------|-----|-------------------|---------------------|
| GET         | `/api/alerts/fast-transactions/{userId}` | Usuario con actividad sospechosa | Alerta generada |

---

## ✅ Estado
- ✔️ Bloqueado

---

## 🧑‍💻 Historia de Usuario #125

### 📝 Título
Alerta por logins desde múltiples países

---

### 📌 Descripción Breve
Detecta si un usuario inicia sesión desde dos o más países diferentes en la última hora y genera una alerta de posible acceso no autorizado.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `GeolocalizationService`
  - Método: `getLoginAlert(Long userId)`

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP |                URL                   |  Parámetros  |    Respuesta    |
|-------------|--------------------------------------|--------------|-----------------|
| GET         | `/api/alerts/login-country/{userId}` | Path: userId | Alerta de login sospechoso o null |

#### 🗃️ Cambios en Base de Datos
- No aplica

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Seguridad y alertas
- Dependencias relevantes: `LoginsRepository`

---

### 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerts/login-country/123
```

**Response**
```json
{
  "userId": 123,
  "message": "Multiple countries logins detected for user 123"
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `getLoginAlert_CuandoHayLoginsDeVariosPaises_GeneraAlerta`: Genera alerta si hay logins desde más de un país.
- `getLoginAlert_CuandoTodosLosLoginsSonDelMismoPais_NoGeneraAlerta`: No genera alerta si todos los logins son del mismo país.

### 🧪 Endpoints Probados
| Método HTTP | URL | Escenario de Test | Resultado Esperado |
|-------------|-----|-------------------|---------------------|
| GET         | `/api/alerts/login-country/{userId}` | Usuario con logins sospechosos | Alerta generada |

---

## ✅ Estado
-

