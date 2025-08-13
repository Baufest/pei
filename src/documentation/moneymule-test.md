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
