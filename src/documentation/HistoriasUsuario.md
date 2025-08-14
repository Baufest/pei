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
---
## 👨‍💻 Historia de Usuario #231
### 📝 Título  
Excepción para "Clientes Confiables"
---
### 📌 Descripción Breve  
Se implementa una lógica configurable para determinar si un cliente puede ser considerado confiable, permitiendo omitir ciertas validaciones en el sistema. Esta lógica se basa en criterios como antigüedad, historial de chargebacks y perfil del cliente. El objetivo es facilitar futuras modificaciones sin alterar múltiples partes del código.
---
### ⚙️ Detalles Técnicos  
#### 🧪 Clases/Métodos Afectados  
- `ClienteConfiableService`
  - Método: `esClienteConfiable(User cliente)`
  - Métodos internos: `filtroAntiguedad`, `filtroChargeback`, `filtroPerfil`
- `ClienteConfiableProperties`
  - Clase interna: `Antiguedad`
  - Propiedades: `medicion`, `minimoMedicion`, `perfilesNoConfiables`, `limiteChargeback`
- `User`
  - Métodos: `getCreationDate()`, `getChargebacks()`, `getProfile()`
#### 🌐 Endpoints Nuevos/Modificados  
_No se introdujeron endpoints en esta implementación._
#### 🗃️ Cambios en Base de Datos  
- No se realizaron cambios estructurales en la base de datos.
---
### 🔍 Impacto en el Sistema  
- Módulo afectado: `com.pei.service`
- Dependencias relevantes: `ClienteConfiableProperties`, `User`, `FiltroCliente`
---
### 💻 Ejemplo de Uso  
**Request**  
```java
User cliente = new User();
cliente.setCreationDate(LocalDate.of(2020, 1, 1));
cliente.setProfile("NORMAL");
cliente.addChargebacks(new ArrayList<>());
boolean confiable = clienteConfiableService.esClienteConfiable(cliente);
```

**Response**
```java
true // Si cumple con todos los filtros configurados
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testCargaDesdeYaml`: Verifica que las propiedades se cargan correctamente desde el archivo de configuración.
- `clienteCumpleTodosLosFiltros`: Cliente con más de 24 meses, sin chargebacks y perfil confiable → **confiable**.
- `clienteFallaPorAntiguedad`: Cliente con solo 12 meses de antigüedad → **no confiable**.
- `clienteFallaPorChargebacks`: Cliente con más de un chargeback → **no confiable**.
- `clienteFallaPorPerfil`: Cliente con perfil "IRRECUPERABLE" → **no confiable**.

### 🧪 Endpoints Probados
_No aplica, ya que no se expone vía API._

---

## ✅ Estado
✔️ Completado

---


---

## 🧑‍💻 Historia de Usuario #222

### 📝 Título
Alerta por transferencias a cuentas recién creadas

---

### 📌 Descripción Breve
Se implementa un endpoint que valida si una transferencia se realiza a una cuenta creada hace menos de 48 horas. Si la cuenta destino fue creada en ese rango, se genera una alerta.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `AlertController`
  - Método: `validateNewAccountTransfers(TransferRequest transferReq)`
- `AccountService`
  - Método: `validateNewAccountTransfers(Account destinationAccount, Transaction currentTransaction)`
- `TransferRequest` (DTO)
- `Alert` (DTO)

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                      | Parámetros (Body)         | Respuesta                      |
|-------------|--------------------------|---------------------------|--------------------------------|
| POST        | `/api/alerta-cuenta-nueva` | `TransferRequest`         | `Alert` con mensaje de alerta  |

#### 🗃️ Cambios en Base de Datos
- No aplica.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Alertas y cuentas
- Dependencias relevantes: `AccountService`, `TransferRequest`, `Alert`

---

### 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-cuenta-nueva
Content-Type: application/json
{
  "destinationAccount": {
    "id": 123,
    "creationDate": "2025-08-12T10:00:00",
    "type": "Ahorros"
  },
  "currentTransaction": {
    "id": 456,
    "date": "2025-08-12T12:00:00"
  }
}
```

**Response**
```json
{
  "description": "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas."
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testCuentaCreadaHaceMenosDe48Horas`: Alerta si la cuenta fue creada hace menos de 48 horas.
- `testCuentaCreadaHaceMasDe48Horas`: Permite transferencia si la cuenta fue creada hace más de 48 horas.
- `testCuentaCreadaExactamente48Horas`: Permite transferencia si la cuenta fue creada exactamente hace 48 horas.
- `testCuentaCreadaDespuesDeTransaccion`: Permite transferencia si la cuenta fue creada después de la transacción.

### 🧪 Endpoints Probados
| Método HTTP | URL                      | Escenario de Test                       | Resultado Esperado |
|-------------|--------------------------|-----------------------------------------|--------------------|
| POST        | `/api/alerta-cuenta-nueva` | Cuenta creada hace menos de 48 horas    | Alerta generada    |

---

## ✅ Estado
✔️ Completado

---

## 🧑‍💻 Historia de Usuario #227

### 📝 Título
Alerta por cliente de alto riesgo

---

### 📌 Descripción Breve
Se implementa un endpoint que verifica si un cliente es de alto riesgo consultando su información. Si el cliente tiene la etiqueta "alto" en el campo de riesgo, se genera una alerta.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `AlertController`
  - Método: `validateHighRiskClient(Long userId)`
- `AccountService`
  - Método: `validateHighRiskClient(Long userId)`
- `ClienteService`
  - Método: `obtenerClienteJson(Long idCliente)`
- `Alert` (DTO)

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                                 | Parámetros (Path) | Respuesta                      |
|-------------|-------------------------------------|-------------------|-------------------------------|
| GET         | `/api/alerta-cliente-alto-riesgo/{userId}` | `userId`          | `Alert` con mensaje de alerta |

#### 🗃️ Cambios en Base de Datos
- No aplica.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Alertas y clientes
- Dependencias relevantes: `AccountService`, `ClienteService`, `Alert`

---

### 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerta-cliente-alto-riesgo/1
```

**Response**
```json
{
  "userId": 1,
  "description": "Alerta: El cliente es de alto riesgo."
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testHighRiskUser`: Alerta si el usuario es de alto riesgo.
- `testLowRiskUser`: Mensaje de verificado si el usuario es de bajo riesgo.
- `testNotFoundUser`: Alerta si el usuario no es encontrado.

### 🧪 Endpoints Probados
| Método HTTP | URL                                 | Escenario de Test         | Resultado Esperado |
|-------------|-------------------------------------|--------------------------|--------------------|
| GET         | `/api/alerta-cliente-alto-riesgo/1` | Usuario de alto riesgo   | Alerta generada    |

---

## ✅ Estado
✔️ Completado

---

## 🧑‍💻 Historia de Usuario #217

### 📝 Título
Alerta por validación de perfil de usuario en transacción

---

### 📌 Descripción Breve
Se implementa un endpoint que valida si el perfil del usuario corresponde al monto de la transacción realizada. Si el monto excede tres veces el promedio mensual y el perfil es "ahorrista", se genera una alerta.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `AlertController`
  - Método: `validateUserProfileTransaction(UserTransaction userTransaction)`
- `AccountService`
  - Método: `validateUserProfileTransaction(User user, Transaction transaction)`
- `UserTransaction` (DTO)
- `Alert` (DTO)

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                  | Parámetros (Body)      | Respuesta                      |
|-------------|----------------------|------------------------|-------------------------------|
| POST        | `/api/alerta-perfil` | `UserTransaction`      | `Alert` con mensaje de alerta |

#### 🗃️ Cambios en Base de Datos
- No aplica.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: Alertas y usuarios
- Dependencias relevantes: `AccountService`, `UserTransaction`, `Alert`

---

### 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-perfil
Content-Type: application/json
{
  "user": {
    "profile": "ahorrista",
    "averageMonthlySpending": 1000.0
  },
  "transaction": {
    "amount": 3500.0
  }
}
```

**Response**
```json
{
  "description": "Alerta: Monto inusual para perfil."
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testUserNull`: Alerta si el usuario es null.
- `testUserProfileNull`: Alerta si el perfil del usuario es null.
- `testTransactionNull`: Alerta si la transacción es null.
- `testTransactionAmountNull`: Alerta si el monto de la transacción es null.
- `testAmountExceedsThresholdAndProfileAhorrista`: Alerta si el monto excede el umbral y el perfil es "ahorrista".
- `testValidAmountAndProfileAhorrista`: Validación correcta si el monto está dentro del rango y el perfil es "ahorrista".
- `testValidAmountAndProfileOther`: Validación correcta para cualquier perfil distinto de "ahorrista".

### 🧪 Endpoints Probados
| Método HTTP | URL                  | Escenario de Test                  | Resultado Esperado |
|-------------|----------------------|------------------------------------|--------------------|
| POST        | `/api/alerta-perfil` | Monto inusual para perfil ahorrista| Alerta generada    |

---

## ✅ Estado
✔️ Completado

---

## 👨‍💻 Historia de Usuario #223

### 📝 Título
Detección de "Account Takeover" (Secuestro de Cuenta)

---

### 📌 Descripción Breve
Se implementa una lógica de detección de posibles secuestros de cuenta (Account Takeover) basada en la ocurrencia de eventos críticos (como cambios de email o teléfono) seguidos de una transacción en menos de una hora. El objetivo es generar una alerta automática ante este patrón sospechoso.

---

### ⚙️ Detalles Técnicos

#### 🧪 Clases/Métodos Afectados
- `AccountTakeoverController`
    - Método: `evaluateAccountTakeover(List<UserEvent> userEvents)`
- `TransactionService`
    - Métodos: `getMostRecentTransferByUserId(Long userId)`, `isLastTransferInLastHour(Transaction, LocalDateTime)`
- `User`
    - Campos agregados:
        - `email: String`
        - `phoneNumber: String`

#### 🌐 Endpoints Nuevos/Modificados

| Método HTTP | URL                          | Parámetros                  | Respuesta Esperada |
|-------------|------------------------------|-----------------------------|---------------------|
| POST        | `/api/alerta-account-takeover` | `List<UserEvent>` (JSON)    | `Alert` con `userId` y `description` |

#### 🗃️ Cambios en Base de Datos
- Se agregaron los campos `email` y `phoneNumber` a la entidad `User`.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: `com.pei.controller`, `com.pei.service`
- Dependencias relevantes: `UserEvent`, `Transaction`, `Alert`, `User`, `TransactionService`

---

### 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-account-takeover
Content-Type: application/json

[
  {
    "id": 1,
    "user": { "id": 1 },
    "type": "CHANGE_EMAIL",
    "eventDateHour": "2025-08-13T10:00:00"
  },
  {
    "id": 2,
    "user": { "id": 2 },
    "type": "CHANGE_PASSWORD",
    "eventDateHour": "2025-08-13T10:30:00"
  }
]
```

**Response**
```json
{
  "userId": 2,
  "description": "Alerta: Posible Account Takeover detectado para el usuario 2"
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `evaluateAccountTakeover_CuandoOk_RetornaAlerta`: Verifica que se genera una alerta cuando hay eventos críticos y una transacción en la última hora.
- `evaluateAccountTakeover_CuandoNoHayEventos_RetornaBadRequest`: Verifica que se retorna error cuando no se proporcionan eventos de usuario.

### 🧪 Endpoints Probados

| Método HTTP | URL                          | Escenario de Test                                | Resultado Esperado |
|-------------|------------------------------|--------------------------------------------------|---------------------|
| POST        | `/api/alerta-account-takeover` | Eventos críticos + transacción reciente          | `200 OK` con alerta |
| POST        | `/api/alerta-account-takeover` | Sin eventos de usuario                           | `400 Bad Request`   |

---

## ✅ Estado
✔️ Completado

---
