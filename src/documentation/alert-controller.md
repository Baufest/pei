# 📄 Documentación de Cambio

## 📝 Título del Cambio
Implementación de endpoints de validación de alertas para cuentas nuevas, clientes de alto riesgo y perfil de usuario

---

## 📌 Resumen Breve
Se agregaron tres endpoints al controlador `AlertController` para validar distintos tipos de alertas relacionadas con comportamiento financiero sospechoso. Estas validaciones permiten detectar cuentas recién creadas que reciben transferencias, clientes considerados de alto riesgo y transacciones que no se alinean con el perfil del usuario.

---

## ⚙️ Detalles Técnicos
### Clases/Métodos Afectados
- `com.pei.controller.AlertController`
    - `validateNewAccountTransfers(TransferRequest transferReq)`
    - `validateHighRiskClient(Long userId)`
    - `validateUserProfileTransaction(UserTransaction userTransaction)`
- `com.pei.dto.TransferRequest`
- `com.pei.dto.UserTransaction`
- `com.pei.domain.Transaction`
- `com.pei.domain.Account`
- `com.pei.domain.User`
- `com.pei.dto.Alert`
- `com.pei.service.AccountService`

### Endpoints Nuevos/Modificados
| Método HTTP | URL                                      | Parámetros                                | Respuesta                                      |
|-------------|-------------------------------------------|-------------------------------------------|------------------------------------------------|
| POST        | /api/alerta-cuenta-nueva                 | `TransferRequest` en el body JSON         | `Alert` con mensaje si se detecta actividad sospechosa |
| GET         | /api/alerta-cliente-alto-riesgo/{userId} | `userId` como path variable               | `Alert` si el cliente es considerado de alto riesgo |
| POST        | /api/alerta-perfil                       | `UserTransaction` en el body JSON         | `Alert` con mensaje si la transacción no coincide con el perfil |

### Cambios en Base de Datos
- No aplica. Las validaciones se realizan sobre datos existentes sin modificar la estructura de la base.

---

## 🔍 Impacto en el Sistema
- Módulo afectado: `AlertController`
- Dependencias relevantes: `AccountService`, `Transaction`, `User`, `Account`

---

## 💻 Ejemplo de Uso

**Request - Validación de cuenta nueva**
```http
POST /api/alerta-cuenta-nueva
Content-Type: application/json

{
  "destinationAccount": {
    "id": 1,
    "user": {
      "id": 123
    }
  },
  "currentTransaction": {
    "amount": 500.0,
    "date": "2025-08-12T10:00:00",
    "sourceAccount": { "id": 2 },
    "destinationAccount": { "id": 1 }
  }
}
```
### Response

```json
{
  "userId": null,
  "description": "Alerta de prueba"
}
```

### Request - Validación de cliente de alto riesgo

```http
GET /api/alerta-cliente-alto-riesgo/123
```

---

### Response

```json
{
    "userId": 123,
    "description": "Alerta: El cliente es de alto riesgo."
}
```

### Request - Validación de perfil de usuario

```http
GET /api/alerta-cliente-alto-riesgo/123
```

---

### Response

```json
{
    "userId": 123,
    "description": "Alerta: El cliente es de alto riesgo."
}
```

### Request - Validación de perfil de usuario

```json
POST /api/alerta-perfil
Content-Type: application/json

{
  "user": {
    "id": 123
  },
  "transaction": {
    "amount": 1000.0,
    "date": "2025-08-12T15:00:00",
    "sourceAccount": { "id": 2 },
    "destinationAccount": { "id": 3 }
  }
}
```

---

# ⚠️ Notas y Advertencias

Los endpoints no implementan autenticación; se asume que los datos del usuario están presentes en el payload.
En caso de error interno, se retorna HTTP 500 con un mensaje genérico.
Las validaciones dependen de la lógica implementada en AccountService.
📅 Fecha y Autor
Fecha: 13/08/2025
Autor: Fernando Elian Benitez
