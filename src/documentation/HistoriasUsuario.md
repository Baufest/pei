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

