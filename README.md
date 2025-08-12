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

### GET `/alerta-cliente-alto-riesgo/{userId}`

Alerta si un cliente(usuario) es de alto riesgo. Busca en la base de datos el ID y comprueba su etiqueta de riesgo

**Response:**
```json
{
  "message": "Alerta: El cliente es de alto riesgo."
}
```


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

Tests unitarios aplicados en AlertController
En los tests de controlador se mockea el servicio (AccountService) para aislar la capa REST y validar que los endpoints responden correctamente, sin depender de la lógica interna o base de datos.

Tests unitarios aplicados en AccountService
Validar cliente alto riesgo
testUsuarioAltoRiesgo: verifica que se genera la alerta correcta cuando el usuario es de alto riesgo.

testUsuarioBajoRiesgo: verifica que se genera la alerta correcta cuando el usuario es de bajo riesgo.

testUsuarioNoEncontrado: verifica que se genera la alerta correcta cuando el usuario no existe.

Validar transferencias a cuentas recién creadas
testCuentaCreadaHaceMenosDe48Horas: alerta si la cuenta fue creada hace menos de 48 horas.

testCuentaCreadaHaceMasDe48Horas: permite transferencia si la cuenta fue creada hace más de 48 horas.

testCuentaCreadaExactamente48Horas: permite transferencia si la cuenta fue creada hace exactamente 48 horas.

testCuentaCreadaDespuesDeTransaccion: permite transferencia si la cuenta fue creada después de la fecha de la transacción.

---
