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
