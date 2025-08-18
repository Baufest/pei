
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
