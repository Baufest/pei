# pei

## Descripción

Aplicación base PEI desarrollada en **Java 17** con **Spring Boot 3**. Alerta transferencias a cuentas recientemente creadas y expone endpoints REST para la gestión de alertas.

## Estructura del Proyecto

- **Controller:** Maneja las peticiones HTTP y delega la lógica a los servicios.
- **Service:** Contiene la lógica de negocio.
- **DTOs:** Objetos de transferencia de datos para entrada/salida.
- **Domain:** Entidades JPA.
- **Tests:** Pruebas unitarias con JUnit 5 y Mockito.

## Endpoints Principales

### POST `/api/alerta-cuenta-nueva`

Valida si una transferencia se realiza a una cuenta creada hace menos de 48 horas.

**Request Body:**
```json
{
  "cuentaDestino": {
    "id": 123,
    "creationDate": "2025-08-12T10:00:00",
    "type": "Ahorros"
  },
  "transaccionActual": {
    "id": 456,
    "date": "2025-08-12T12:00:00"
  }
}

```

**Response:**
```json
{
  "message": "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas."
}
```

## Ejecución

Para compilar y ejecutar la aplicación:

```sh
./mvnw spring-boot:run
```

## Ejecución de Tests

Para correr los tests unitarios:

```sh
./mvnw test
```

## Pruebas Unitarias

Las pruebas están implementadas en:

- [src/test/java/com/pei/service/AccountServiceTest.java](src/test/java/com/pei/service/AccountServiceTest.java)
- [src/test/java/com/pei/controller/AlertControllerTest.java](src/test/java/com/pei/controller/AlertControllerTest.java)

### Ejemplo de test de servicio

Se valida que el servicio [`AccountService`](src/main/java/com/pei/service/AccountService.java) retorna la alerta correcta según la fecha de creación de la cuenta destino.

```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Test
    void testCuentaCreadaHaceMenosDe48Horas() {
        // ...setup...
        Alert alert = accountService.validarTransferenciasCuentasRecienCreadas(cuentaDestino, transaccionActual);
        assertEquals("Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.", alert.getMessage());
    }
}
```

### Ejemplo de test de controlador

Se valida que el endpoint `/api/alerta-cuenta-nueva` responde correctamente ante una solicitud válida.

```java
@ExtendWith(MockitoExtension.class)
class AlertControllerTest {
    @Test
    void validarTransferenciasCuentasRecienCreadas_CuandoOk_RetornaAlerta() throws Exception {
        // ...setup...
        mockMvc.perform(post("/api/alerta-cuenta-nueva")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());
    }
}
```

---
