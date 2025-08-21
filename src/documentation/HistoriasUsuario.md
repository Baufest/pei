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

## 🧑‍💻 Historia de Usuario #226

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
| GET         | `/api/alerts-chargeback/{userId}` | Path: userId | Alerta de fraude o null |

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

## 🧑‍💻 Historia de Usuario #235  (Se implemento nueva funcionalidad en Task #251)

Implementación de endpoint para alerta de transacciones rápidas por tipo de cliente

---

## 📌 Resumen Breve
Se implementó el endpoint `/alerta-fast-multiple-transaction/{userId}` que permite detectar si un usuario realiza más transacciones de las permitidas en un rango de tiempo corto, según su tipo de cliente ("individuo" o "empresa"). El sistema consulta el tipo de cliente y, si es válido, analiza la cantidad de transacciones recientes. Si supera el límite configurado, se genera una alerta.

---

## ⚙️ Detalles Técnicos

### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - Método: `getFastMultipleTransactionsAlert(Long userId)`
- `com.pei.service.ClienteService`
    - Método: `getClientType(Long userId)`
- `com.pei.service.TransactionService`
    - Método: `getFastMultipleTransactionAlert(Long userId, String clientType)`
- `com.pei.dto.Alert`

### Endpoints Nuevos/Modificados
| Método HTTP | URL                                         | Parámetros (Path) | Respuesta                                      |
|-------------|---------------------------------------------|-------------------|------------------------------------------------|
| GET         | `/api/alerta-fast-multiple-transaction/{userId}` | `userId`          | `Alert` con mensaje si se detecta actividad sospechosa |

### Cambios en Base de Datos
- No aplica. El endpoint realiza análisis sobre transacciones existentes, sin modificar la estructura ni los datos de la base.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Dependencias relevantes: `ClienteService`, `TransactionService`, configuración de límites en `application.yml`

---

## 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerta-fast-multiple-transaction/123
```

**Response (caso positivo)**
```json
{
  "userId": 123,
  "description": "Más de 10 transacciones en la última hora para usuario tipo individuo"
}
```

**Response (caso negativo)**
```http
404 Not Found
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `getFastMultipleTransactionsAlert_CuandoTipoClienteValidoYAlerta_RetornaOk`: Genera alerta si el usuario supera el límite de transacciones según su tipo.
- `getFastMultipleTransactionsAlert_CuandoTipoClienteNoValido_RetornaNotFound`: No genera alerta si el tipo de cliente es inválido.
- `getFastMultipleTransactionsAlert_CuandoNoHayAlerta_RetornaNotFound`: No genera alerta si el usuario no supera el límite.

### 🧪 Endpoints Probados
| Método HTTP | URL                                         | Escenario de Test                       | Resultado Esperado |
|-------------|---------------------------------------------|-----------------------------------------|--------------------|
| GET         | `/api/alerta-fast-multiple-transaction/{userId}` | Usuario supera límite                   | Alerta generada    |
| GET         | `/api/alerta-fast-multiple-transaction/{userId}` | Usuario no supera límite                | 404 Not Found      |
| GET         | `/api/alerta-fast-multiple-transaction/{userId}` | Tipo de cliente inválido                | 404 Not Found      |

---

## ✅ Estado
✔️ Completado

---

## 📦 Documentación de Integraciones Externas

_No aplica para este endpoint. No se utilizan servicios externos._

---

## 🧑‍💻 Historia de Usuario #220

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
| GET         | `/api/alerts-login/{userId}` | Path: userId | Alerta de login sospechoso o null |

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
| GET         | `/api/alerts-country/{userId}` | Usuario con logins sospechosos | Alerta generada |

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
  "description": "Alerta: Cliente individual de alto riesgo, con chargebacks: 5"
}
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testClientTypeNull`: Alerta si el clientType es null.
- `testChargebacksMissing`: Alerta si chargebacks es null.
- `testEmpresaHighRisk`: Alerta si el cliente empresa es de alto riesgo.
- `testEmpretestIndividuoHighRisksaHighRisk`: Alerta si el cliente individuo es de alto riesgo.
- `testClienteValidadoSinAlertas`: Alerta si el cliente es validado sin alertas de riesgo.
- `testJsonProcessingException`: Alerta si el json está mal procesado.

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
- `com.pei.domain.User.User`

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


## 👨‍💻 Historia de Usuario #232

### 📝 Título  
Integración y alerta de scoring externo BBVA

---

### 📌 Descripción Breve  
Se implementa la lógica para consultar el scoring de un cliente utilizando el servicio externo de BBVA (`scoringServiceExterno`). El sistema genera una alerta si el scoring recibido indica riesgo relevante. Esta integración permite evaluar el perfil crediticio y de fraude de los usuarios en tiempo real, facilitando la toma de decisiones automatizadas y la trazabilidad de dependencias externas.

---

### ⚙️ Detalles Técnicos  

#### 🧩 Clases/Métodos Afectados  
- `AlertController`
  - Método: `checkProccesTransaction(Long idCliente)`
- `ScoringController`
  - Métodos principales para consulta y gestión de scoring
- `ScoringServiceExterno` (ubicado en `service/bbva/scoringServiceExterno`)
  - Método: `consultarScoring(Long idCliente)`
- `Alert`
  - DTO para respuesta de alerta

#### 🌐 Endpoints Nuevos/Modificados  
| Método HTTP | URL                      | Parámetros         | Respuesta                |
|-------------|--------------------------|--------------------|--------------------------|
| POST        | `/api/alerta-scoring`    | `Long idCliente`   | `Alert` (JSON)           |
| POST        | `/api/scoring/consultar` | `Long idCliente`   | `ScoringResponse` (JSON) |

#### 🗃️ Cambios en Base de Datos  
- No se realizaron cambios estructurales en la base de datos.

---

### 🔍 Impacto en el Sistema  
- Módulo afectado: `com.pei.controller`, `com.pei.service.bbva`
- Dependencias relevantes:  
  - `ScoringServiceExterno` (servicio externo BBVA)
  - `Alert`
  - `ScoringController`

---

### 💻 Ejemplo de Uso  

**Request**  
```http
POST /api/alerta-scoring
Content-Type: application/json

12345
```

**Response**
```json
{
  "userId": 12345,
  "message": "Alerta: Scoring bajo detectado para el usuario 12345"
}
```
*Si el scoring es aceptable, retorna 404 (no hay alerta).*

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `checkProccesTransaction_CuandoScoringBajo_RetornaAlerta`: Cliente con scoring bajo → **alerta generada**.
- `checkProccesTransaction_CuandoScoringAlto_NoRetornaAlerta`: Cliente con scoring alto → **no genera alerta**.
- `checkProccesTransaction_CuandoServicioExternoFalla_RetornaError`: Fallo en servicio externo → **error interno**.

### 🧪 Endpoints Probados
| Método HTTP | URL                | Escenario de Test                  | Resultado Esperado         |
|-------------|--------------------|------------------------------------|---------------------------|
| POST        | `/api/alerta-scoring` | Scoring bajo                       | Alerta generada           |
| POST        | `/api/alerta-scoring` | Scoring alto                       | 404 Not Found             |
| POST        | `/api/alerta-scoring` | Servicio externo no disponible     | Error 500                 |

---

## ✅ Estado
✔️ Completado

---

## 🔗 Integraciones Externas

- **Servicio de Scoring BBVA**  
  - Ubicación: `service/bbva/scoringServiceExterno`
  - Descripción: Consulta el scoring crediticio y de fraude de los clientes.  
  - Dependencia registrada en README.md.


-----

## 👨‍💻 Historia de Usuario #218

### 📝 Título  
Alerta de fraude por dispositivo y geolocalización

---

### 📌 Descripción Breve  
Se implementa la lógica para detectar posibles fraudes relacionados con el uso de dispositivos y ubicaciones geográficas en los accesos de usuarios. El sistema genera una alerta si se detecta un acceso desde un dispositivo o país no habitual para el usuario, utilizando el servicio externo de geolocalización por IP.

---

### ⚙️ Detalles Técnicos  

#### 🧩 Clases/Métodos Afectados  
- `AlertController`
  - Método: `checkDeviceLocalization(Logins login)`
- `GeolocalizationService`
  - Método: `verifyFraudOfDeviceAndGeolocation(Logins login)`
- `GeoSimService` (servicio externo de geolocalización por IP)
  - Método: `getCountryFromIP(String ip)`
- `LoginsRepository`
  - Métodos para consulta y persistencia de accesos

#### 🌐 Endpoints Nuevos/Modificados  
| Método HTTP | URL                     | Parámetros         | Respuesta                |
|-------------|-------------------------|--------------------|--------------------------|
| POST        | `/api/alerta-dispositivo` | `Logins` (JSON)    | `Alert` (JSON)           |

#### 🗃️ Cambios en Base de Datos  
- Se registra cada acceso en la tabla de logins, incluyendo país y dispositivo.

---

### 🔍 Impacto en el Sistema  
- Módulo afectado: `com.pei.controller`, `com.pei.service`
- Dependencias relevantes:  
  - `GeoSimService` (servicio externo de geolocalización por IP)
  - `LoginsRepository`
  - `Alert`

---

### 💻 Ejemplo de Uso  

**Request**  
```http
POST /api/alerta-dispositivo
Content-Type: application/json

{
  "userId": 12345,
  "deviceID": "A1B2C3D4",
  "country": "AR",
  "timestamp": "2025-08-14T10:00:00"
}
```

**Response**
```json
{
  "userId": 12345,
  "message": "Device and geolocalization problem detected for 12345"
}
```
*Si no hay problema, retorna un mensaje alternativo o 404.*

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `checkDeviceLocalization_CuandoDispositivoYPaisNoCoinciden_RetornaAlerta`: Acceso desde dispositivo y país no habitual → **alerta generada**.
- `checkDeviceLocalization_CuandoDispositivoYPaisCoinciden_NoRetornaAlerta`: Acceso habitual → **no genera alerta**.
- `checkDeviceLocalization_CuandoLoginNull_RetornaBadRequest`: Login nulo → **400 Bad Request**.
- `checkDeviceLocalization_CuandoServicioExternoFalla_RetornaError`: Fallo en servicio externo → **error 500**.

### 🧪 Endpoints Probados
| Método HTTP | URL                     | Escenario de Test                  | Resultado Esperado         |
|-------------|-------------------------|------------------------------------|---------------------------|
| POST        | `/api/alerta-dispositivo` | Dispositivo/pais no habitual       | Alerta generada           |
| POST        | `/api/alerta-dispositivo` | Dispositivo/pais habitual          | Mensaje alternativo/404   |
| POST        | `/api/alerta-dispositivo` | Login nulo                         | 400 Bad Request           |
| POST        | `/api/alerta-dispositivo` | Servicio externo no disponible     | Error 500                 |

---

## ✅ Estado
✔️ Completado

---

## 🔗 Integraciones Externas

- **Servicio de Geolocalización por IP**  
  - Ubicación: `GeoSimService`
  - Descripción: Obtiene el país asociado a una dirección IP para validar accesos. GeoSimService accede a una una url que simula geolocalización para obtener esta información.
  - Dependencia registrada en

----

## 👨‍💻 Historia de Usuario #224

### 📝 Título  
Alerta por red de transacciones entre cuentas no relacionadas

---

### 📌 Descripción Breve  
Se implementa la lógica para detectar posibles fraudes mediante la identificación de transacciones entre múltiples cuentas que no tienen relación directa. El sistema genera una alerta si se detecta un patrón sospechoso, permitiendo la trazabilidad y prevención de actividades ilícitas como lavado de dinero o movimientos no autorizados.

---

### ⚙️ Detalles Técnicos  

#### 🧩 Clases/Métodos Afectados  
- `AlertController`
  - Método: `checkMultipleAccountsCashNotRelated(List<Transaction> transactions)`
- `AlertService`
  - Método: `verifyMultipleAccountsCashNotRelated(List<Transaction> transactions)`
- `Account`
  - Entidad para cuentas involucradas
- `Transaction`
  - Entidad para transacciones analizadas
- `Alert`
  - DTO para respuesta de alerta

#### 🌐 Endpoints Nuevos/Modificados  
| Método HTTP | URL                           | Parámetros                | Respuesta                |
|-------------|-------------------------------|---------------------------|--------------------------|
| POST        | `/api/alerta-red-transacciones` | `List<Transaction>` (JSON) | `Alert` (JSON)           |

#### 🗃️ Cambios en Base de Datos  
- No se realizaron cambios estructurales en la base de datos.

---

### 🔍 Impacto en el Sistema  
- Módulo afectado: `com.pei.controller`, `com.pei.service`
- Dependencias relevantes:  
  - `AlertService`
  - `Account`
  - `Transaction`
  - `Alert`

---

### 💻 Ejemplo de Uso  

**Request**  
```http
POST /api/alerta-red-transacciones
Content-Type: application/json

[
  {
    "id": 1,
    "amount": 5000,
    "account": { "id": 101, "owner": { "id": 123 } },
    "user": { "id": 123 }
  },
  {
    "id": 2,
    "amount": 7000,
    "account": { "id": 102, "owner": { "id": 124 } },
    "user": { "id": 124 }
  }
]
```

**Response**
```json
{
  "userId": 123,
  "message": "Alert: Multiples transactions not related to the account of 123 detected"
}
```
*Si no se detecta fraude, retorna 404 Not Found.*

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `checkMultipleAccountsCashNotRelated_CuandoTransaccionesNoRelacionadas_RetornaAlerta`: Transacciones entre cuentas no relacionadas → **alerta generada**.
- `checkMultipleAccountsCashNotRelated_CuandoTransaccionesRelacionadas_NoRetornaAlerta`: Transacciones legítimas → **no genera alerta**.
- `checkMultipleAccountsCashNotRelated_CuandoListaVacia_RetornaBadRequest`: Lista vacía → **400 Bad Request**.
- `checkMultipleAccountsCashNotRelated_CuandoTransaccionNula_RetornaBadRequest`: Transacción nula en la lista → **400 Bad Request**.
- `checkMultipleAccountsCashNotRelated_CuandoServicioFalla_RetornaError`: Error interno → **500 Internal Server Error**.

### 🧪 Endpoints Probados
| Método HTTP | URL                           | Escenario de Test                  | Resultado Esperado         |
|-------------|-------------------------------|------------------------------------|---------------------------|
| POST        | `/api/alerta-red-transacciones` | Transacciones no relacionadas      | Alerta generada           |
| POST        | `/api/alerta-red-transacciones` | Transacciones legítimas            | 404 Not Found             |
| POST        | `/api/alerta-red-transacciones` | Lista vacía                        | 400 Bad Request           |
| POST        | `/api/alerta-red-transacciones` | Transacción nula                   | 400 Bad Request           |
| POST        | `/api/alerta-red-transacciones` | Servicio falla                     | 500 Internal Server Error |

---

## ✅ Estado
✔️ Completado

---

## 🔗 Integraciones Externas

- **Servicio de Alertas**  
  - Ubicación: `AlertService`
  - Descripción: Lógica de negocio para detección de patrones sospechosos en transacciones entre cuentas no relacionadas.  


## 🧑‍💻 Historia de Usuario #234

### 📝 Título
Alerta de Transacción Internacional y Países de Riesgo

---

### 📌 Descripción Breve
Se implementa la lógica para generar alertas en transacciones internacionales, considerando países de riesgo y límites configurables de monto. El objetivo es identificar operaciones sospechosas y notificar al usuario cuando se detecta un país de riesgo o se supera el monto permitido. Se agregan servicios para parametrización y validación de países, así como la configuración de límites en archivos de propiedades.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `TransactionService`
  - Método: `processTransactionCountryInternational(Transaction transaction)`
- `TransactionParamsService`
  - Método: `getMontoAlertaInternacional()`
- `RiskCountryService`
  - Método: `isRiskCountry(String country)`
- Configuración: Parámetros de transferencia internacional (`application.yml` / `application.properties`)
- `AlertController`
  - Método: `postMethodName(Transaction transaction)` (endpoint `/api/alerta-transaccion-internacional`)

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                                 | Parámetros (Body)      | Respuesta                      |
|-------------|-------------------------------------|------------------------|-------------------------------|
| POST        | `/api/alerta-transaccion-internacional` | `Transaction` (JSON)   | `Alert` con mensaje de alerta |

#### 🗃️ Cambios en Base de Datos
- No se realizaron cambios estructurales en la base de datos.
- Se utilizan datos existentes de transacciones y cuentas.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: `com.pei.controller`, `com.pei.service`
- Dependencias relevantes: `TransactionService`, `TransactionParamsService`, `RiskCountryService`, configuración de límites internacionales

---

### 💻 Ejemplo de Uso

**Request**
```http
POST /api/alerta-transaccion-internacional
Content-Type: application/json
{
  "id": 123,
  "user": { "id": 1 },
  "amount": 100000,
  "sourceAccount": { "country": "Argentina" },
  "destinationAccount": { "country": "Chile" }
}
```

**Response (caso país de riesgo)**
```json
{
  "userId": 1,
  "description": "Alerta: Transacción internacional hacia un país de riesgo"
}
```

**Response (caso monto mayor al límite)**
```json
{
  "userId": 1,
  "description": "Alerta: Transacción internacional con monto mayor a: 50000"
}
```

**Response (caso internacional normal)**
```json
{
  "userId": 1,
  "description": "Alerta: Transacción internacional aprobada"
}
```

**Response (caso no internacional)**
```json
{
  "userId": 1,
  "description": "Alerta: Transacción aprobada"
}
```

**Response (caso negativo)**
```http
404 Not Found
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `testCargaConfigTransferenciaInternacional`: Verifica que los parámetros de transferencia internacional se cargan correctamente desde la configuración.
- `procesoTransaccion_PaisDeRiesgo_AlertaGenerada`: Transacción internacional hacia país de riesgo → **alerta generada**.
- `procesoTransaccion_MontoMayorAlLimite_AlertaGenerada`: Transacción internacional con monto mayor al límite → **alerta generada**.
- `procesoTransaccion_InternacionalNormal_AlertaAprobada`: Transacción internacional válida → **alerta de aprobación**.
- `procesoTransaccion_NoInternacional_AlertaAprobada`: Transacción nacional → **alerta de aprobación**.
- `riskCountryService_PaisRiesgo_True`: Verifica que el servicio identifica correctamente un país de riesgo.
- `riskCountryService_PaisSeguro_False`: Verifica que el servicio identifica correctamente un país seguro.

### 🧪 Endpoints Probados
| Método HTTP | URL                                 | Escenario de Test                                 | Resultado Esperado                |
|-------------|-------------------------------------|---------------------------------------------------|-----------------------------------|
| POST        | `/api/alerta-transaccion-internacional` | País de riesgo                                    | Alerta generada                   |
| POST        | `/api/alerta-transaccion-internacional` | Monto mayor al límite                             | Alerta generada                   |
| POST        | `/api/alerta-transaccion-internacional` | Transacción internacional válida                  | Alerta de aprobación              |
| POST        | `/api/alerta-transaccion-internacional` | Transacción nacional                              | Alerta de aprobación              |

---

## ✅ Estado
✔️ Completado

---

## 🧑‍💻 Historia de Usuario #250

### 📝 Título
Modificación de Escalado de Alertas por Canal

---

### 📌 Descripción Breve
Se modificó la lógica de escalado de alertas para enviar notificaciones a diferentes canales (Email, SMS, Slack) según la criticidad de la transacción. El envío real se realiza solo por Email utilizando JavaMail; los métodos de SMS y Slack simulan la acción. Se agregaron nuevos campos requeridos en la entidad de transacción y su DTO, según lo solicitado por la entidad regulatoria y el feedback de Adrian.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `Transaction`
    - Nuevos campos: `dateTime` (fecha y hora), `codCoelsa` (código regulatorio)
- `TransactionDTO`
    - Incluye: `id`, `amount`, `currency`, `accountDestinationId`, `dateTime`, `codCoelsa`
- `AlertNotificatorService`
    - Método: `executeNotificator(Long userId, TransactionDTO transactionDTO)`
- `AlertNotificatorStrategy`
    - Métodos:
        - `sendCriticalAlertEmail(...)` (envío real por email)
        - `sendCriticalAlertSms(...)` (simulado)
        - `sendCriticalAlertSlack(...)` (simulado)

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                  | Parámetros (Body)         | Respuesta                                      |
|-------------|----------------------|---------------------------|------------------------------------------------|
| POST        | `/api/alerta-canales` | `ChannelAlertRequest`     | `Alert` con mensaje y canal utilizado          |

#### 🗃️ Cambios en Base de Datos
- Se agregaron los campos `dateTime` y `codCoelsa` a la entidad `Transaction`.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: `AlertNotificatorService`, `Transaction`
- Dependencias relevantes: `JavaMail` para envío real de emails, simulaciones para SMS y Slack.

---

### 💻 Ejemplo de Uso

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

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `executeNotificator_CuandoEjecucionExitosa_VerificaEnvio`: Verifica que se llama al método de envío del canal correcto (email real, SMS/Slack simulado).
- `executeNotificator_CuandoEstrategiaLanzaExcepcion_LanzaAlertNotificatorException`: Simula error en el envío y verifica que se lanza la excepción personalizada.

### 🧪 Endpoints Probados
| Método HTTP | URL                  | Escenario de Test                       | Resultado Esperado |
|-------------|----------------------|-----------------------------------------|--------------------|
| POST        | `/api/alerta-canales` | Transacción y usuario válidos           | Alerta y notificación enviada |

---

## ✅ Estado
✔️ Completado

---

## 📦 Documentación de Integraciones Externas

- **Servicio de Email (JavaMail)**: Envío real de alertas por correo electrónico.
- **SMS y Slack**: Métodos simulados para pruebas y demostración.

---

## 🗃️ Cambios en Entidades

### Transaction
- Se agregaron los campos:
    - `dateTime`: Fecha y hora de la transacción.
    - `codCoelsa`: Código regulatorio alfanumérico de 22 caracteres.

### TransactionDTO
- Incluye los campos:
    - `id`, `codCoelsa`, `amount`, `currency`, `accountDestinationId`, `dateTime`.

---

## 🧪 Pruebas Implementadas

- Se implementaron tests unitarios en `AlertNotificatorServiceTest` usando JUnit 5 y Mockito, cubriendo casos de éxito y error en el envío de alertas por canal.

---

---

## 📦 Documentación de Integraciones Externas

- **Servicio de Email (JavaMail)**: Envío real de alertas por correo electrónico.
- **SMS y Slack**: Métodos simulados para pruebas y demostración.

---

## 🗃️ Cambios en Entidades

### Transaction
- Se agregaron los campos:
    - `dateTime`: Fecha y hora de la transacción.
    - `codCoelsa`: Código regulatorio alfanumérico de 22 caracteres.

### TransactionDTO
- Incluye los campos:
    - `id`, `codCoelsa`, `amount`, `currency`, `accountDestinationId`, `dateTime`.

---

## 🧪 Pruebas Implementadas

- Se implementaron tests unitarios en `AlertNotificatorServiceTest` usando JUnit 5 y Mockito, cubriendo casos de éxito y error en el envío de alertas por canal.

---


---------------------

  ## 🧑‍💻 Historia de Usuario #251

### 📝 Título
Modificacion umbral de velocidades de transacciones

---

### 📌 Descripción Breve
Se implementa la configuración de umbrales de monto mínimo y máximo para la alerta de transacciones rápidas, permitiendo que el sistema detecte actividad sospechosa solo si las transacciones se encuentran dentro de un rango de monto configurable según el tipo de cliente ("individuo" o "empresa"). Esto mejora la flexibilidad y precisión de la lógica antifraude.

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `TransactionService`
  - Método: `getFastMultipleTransactionAlert(Long userId, String clientType)`
    - Ahora utiliza los valores configurables de monto mínimo y máximo.
    - Se factorizo el metodo para que sea más claro y haga una consulta por tipo de cliente.
- `TransactionVelocityDetectorService`
  - Métodos: `getIndividuoUmbralMonto()`, `getEmpresaUmbralMonto()`
    - Devuelven `Map<String, BigDecimal>` con claves `minMonto` y `maxMonto`.
- `TransactionRepository`
  - Método: `countTransactionsByUserAfterDateBetweenMontos(Long userId, LocalDateTime fromDate, BigDecimal minMonto, BigDecimal maxMonto)`
    - Se asegura que la consulta considere los nuevos parámetros de monto.
    - Se modifico el nombre del metodo.
- `AlertController`
  - Método: `getFastMultipleTransactionsAlert(Long userId, String clientType)`
    - Expone la funcionalidad vía API.

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL                                         | Parámetros (Query)         | Respuesta                                      |
|-------------|---------------------------------------------|----------------------------|------------------------------------------------|
| GET         | `/api/alerta-fast-multiple-transaction`     | `userId`, `clientType`     | `Alert` con mensaje si se detecta actividad sospechosa |

#### 🗃️ Cambios en Base de Datos
- No se realizaron cambios estructurales en la base de datos.
- Se ajustó la consulta en el repositorio para considerar los nuevos parámetros de monto.

---

### 🔍 Impacto en el Sistema
- Módulo afectado: `com.pei.service`
- Dependencias relevantes: `VelocityTransactionsProperties`, `TransactionVelocityDetectorService`, `TransactionRepository`, configuración de límites en `application.yml`

---

### 💻 Ejemplo de Uso

**Request**
```http
GET /api/alerta-fast-multiple-transaction?userId=123
```

**Response (caso positivo)**
```json
{
  "userId": 123,
  "description": "Fast multiple transactions detected for user 123"
}
```

**Response (caso negativo)**
```http
204 No Content
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `getFastMultipleTransactionAlert_CuandoSuperaMaximoTransacciones_RetornaAlerta`: Retorna alerta si el usuario supera el máximo de transacciones en el rango de monto configurado.
- `getFastMultipleTransactionAlert_CuandoNoSuperaMaximoTransacciones_RetornaNull`: No retorna alerta si el usuario no supera el máximo.
- `getFastMultipleTransactionAlert_CuandoMontoFueraDeRango_NoCuentaTransaccion`: Solo se consideran transacciones dentro del rango de monto.
- `getFastMultipleTransactionAlert_CuandoTipoClienteEmpresa_UsaConfiguracionEmpresa`: Usa los umbrales correctos según el tipo de cliente.
- El resto de la logica esta tal cual el metodo original. Respeta la cantidad maxima de transacciones y el limite de tiempo.

### 🧪 Endpoints Probados
| Método HTTP | URL                                         | Escenario de Test                                   | Resultado Esperado                  |
|-------------|---------------------------------------------|-----------------------------------------------------|-------------------------------------|
| GET         | `/api/alerta-fast-multiple-transaction`     | Usuario supera máximo de transacciones              | Retorna alerta                      |
| GET         | `/api/alerta-fast-multiple-transaction`     | Usuario no supera máximo de transacciones           | 204 No Content                      |
| GET         | `/api/alerta-fast-multiple-transaction`     | Transacciones fuera de rango de monto               | No se consideran en el conteo       |

---

## 🧪 Pruebas Implementadas

- Se modificaron los tests unitarios en `TransactionService` usando JUnit 5 y Mockito, cubriendo casos de éxito y error.

--

## ✅ Estado
