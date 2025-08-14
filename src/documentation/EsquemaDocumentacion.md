
# 📘 Historias de Usuario - Proyecto PEI

Este documento tiene como objetivo registrar las historias de usuario desarrolladas por el equipo. Cada sección corresponde a una historia completada, incluyendo su descripción funcional, técnica y ejemplos de uso.

---

## 🧑‍💻 Historia de Usuario #[Número]

### 📝 Título
[Título descriptivo de la funcionalidad o cambio]

---

### 📌 Descripción Breve
[Resumen funcional de lo que se implementó, incluyendo el objetivo del cambio y su impacto esperado]

---

### ⚙️ Detalles Técnicos

#### 🧩 Clases/Métodos Afectados
- `[Nombre de clase o archivo]`
  - Método: `[nombre del método]`
- `[Otros elementos relevantes]`

#### 🌐 Endpoints Nuevos/Modificados
| Método HTTP | URL | Parámetros | Respuesta |
|-------------|-----|------------|-----------|
| POST        | `/api/...` | `[Body/Query Params]` | `[Respuesta esperada]` |

#### 🗃️ Cambios en Base de Datos
- [Indicar si hubo cambios en la estructura, migraciones, o si no aplica]

---

### 🔍 Impacto en el Sistema
- Módulo afectado: `[Nombre del módulo]`
- Dependencias relevantes: `[Servicios, entidades, etc.]`

---

### 💻 Ejemplo de Uso

**Request**
```http
[Ejemplo de request HTTP]
```

**Response**
```json
[Ejemplo de respuesta JSON]
```

---

## 🧪 Pruebas Unitarias

### 🧪 Escenarios Cubiertos
- `[Nombre del test]`: [Descripción del escenario]
- `[Otro test]`: [Descripción del escenario]

### 🧪 Endpoints Probados
| Método HTTP | URL | Escenario de Test | Resultado Esperado |
|-------------|-----|-------------------|---------------------|
| POST        | `/api/...` | [Descripción] | [Resultado] |

---

## ✅ Estado
- [✔️ Completado / 🕒 En progreso / ❌ Rechazado]

---

```

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

