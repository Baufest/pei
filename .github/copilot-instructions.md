# Copilot Instructions

Estas instrucciones guían a GitHub Copilot para generar código en **Java** con **Spring Boot**, siguiendo buenas prácticas, principios SOLID y tests unitarios con **JUnit 5** y **Mockito**.

---

## Lenguaje y Frameworks
- Lenguaje: **Java 17+**
- Framework principal: **Spring Boot 3+**
- Testing: **JUnit 5** y **Mockito**
- Librerías de apoyo: Spring Data JPA, Spring Web, Spring Validation, Lombok (opcional).

---

## Estilo de Código
- Usar convenciones oficiales de Java: nombres de clases en PascalCase, variables y métodos en camelCase.
- Clases con una sola responsabilidad (SRP de SOLID).
- Métodos cortos y descriptivos, máximo 20-30 líneas.
- Evitar lógica compleja dentro de controladores: delegar a servicios.
- Usar **interfaces** para servicios e inyectar dependencias a través del constructor.
- Documentar métodos públicos con JavaDoc.
- Usar `final` para variables inmutables y dependencias inyectadas.

---

## Principios SOLID
1. **Single Responsibility Principle (SRP)**  
   Cada clase debe tener un único propósito claro.
   
2. **Open/Closed Principle (OCP)**  
   El código debe estar abierto a extensión pero cerrado a modificación. Usar herencia o composición.

3. **Liskov Substitution Principle (LSP)**  
   Subclases deben poder reemplazar a su superclase sin alterar el comportamiento esperado.

4. **Interface Segregation Principle (ISP)**  
   Preferir interfaces pequeñas y específicas, no interfaces enormes y genéricas.

5. **Dependency Inversion Principle (DIP)**  
   Depender de abstracciones (interfaces), no de implementaciones concretas.

---

## Arquitectura Recomendada
- **Controller**: solo maneja HTTP y delega a servicios.
- **Service**: lógica de negocio.
- **Repository**: acceso a datos (extender `JpaRepository` u otra).
- **DTOs**: para entrada y salida de datos (no exponer entidades directamente).
- **Mapper**: para convertir entre entidades y DTOs.
- **Exceptions personalizadas**: manejar errores con `@ControllerAdvice` y `@ExceptionHandler`.

---

## Pruebas con JUnit 5 y Mockito
- Nombrar métodos de test en formato: `metodo_CuandoCondicion_EsperaResultado`.
- Usar `@ExtendWith(MockitoExtension.class)` para habilitar Mockito.
- Usar `@Mock` para dependencias y `@InjectMocks` para la clase bajo prueba.
- Verificar interacciones con `verify(...)`.
- Usar `@BeforeEach` para inicializar datos comunes.
- Testear casos positivos, negativos y excepciones.
- Ejemplo:
  ```java
  @ExtendWith(MockitoExtension.class)
  class AccountServiceTest {

      @Mock
      private AccountRepository accountRepository;

      @InjectMocks
      private AccountService accountService;

      @Test
      void findById_CuandoExisteCuenta_RetornaCuenta() {
          Account cuenta = new Account(1L, "Juan");
          when(accountRepository.findById(1L)).thenReturn(Optional.of(cuenta));

          Account resultado = accountService.findById(1L);

          assertNotNull(resultado);
          assertEquals("Juan", resultado.getNombre());
          verify(accountRepository).findById(1L);
      }
  }
