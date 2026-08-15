# CS2031 - Week 02 Lab 01: Inyección de Dependencias y Arquitectura por Capas

## ¿Para qué sirve este repositorio?

Este repositorio es una API REST construida con **Spring Boot** que gestiona una lista de películas usando una **base de datos en memoria (H2)** inicializada con datos de ejemplo mediante un archivo SQL de seed.

El objetivo principal de esta sesión de laboratorio es que los estudiantes comprendan dos conceptos fundamentales del desarrollo backend con Spring Boot:

1. **Inyección de dependencias (DI)**: cómo Spring gestiona y conecta automáticamente los componentes de la aplicación sin que el desarrollador tenga que instanciarlos manualmente.
2. **División por capas del repositorio**: cómo se organiza el código en capas bien definidas (Controller → Service → Repository) con responsabilidades separadas.

Adicionalmente, la API se integra con la [Star Wars API (SWAPI)](https://swapi.info) para enriquecer los datos de algunas películas con información del director obtenida externamente.

---

## Cómo probar los endpoints

### Colección Bruno

En la carpeta `bruno-collection/` encontrarás una colección lista para usar con [Bruno](https://www.usebruno.com/). Importa esa carpeta directamente en Bruno para tener todos los endpoints preconfigurados.

### Colección Postman

En la raíz del proyecto encontrarás el archivo `postman-collection.json`. Impórtalo en [Postman](https://www.postman.com/) usando **File → Import** para tener la misma colección de endpoints disponible.

### Endpoints disponibles

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/movies` | Crea una nueva película |
| `GET` | `/movies` | Retorna todas las películas |
| `GET` | `/movies/{id}` | Retorna una película por ID |
| `DELETE` | `/movies/{id}` | Elimina una película por ID |
| `PATCH` | `/movies/{id}/release-date?date=YYYY-MM-DD` | Actualiza la fecha de estreno |

> Las películas que tienen un `externalId` (como *Star Wars: A New Hope* con `externalId=1`) serán enriquecidas automáticamente con el nombre del director obtenido desde SWAPI al ser consultadas.

---

## Cómo ejecutar el proyecto

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

La consola de H2 está habilitada y accesible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:week02-lab01`).

> La base de datos es **en memoria pura**: los datos se reinician cada vez que la aplicación se reinicia (ya no persiste en un archivo en disco).

---

## Arquitectura y estructura del proyecto

El proyecto sigue una **arquitectura en capas** donde cada capa tiene una responsabilidad específica y se comunica únicamente con la capa inmediatamente inferior.

```
src/main/java/org/lab/week02lab01/
├── Week02Lab01Application.java   # Punto de entrada de Spring Boot
├── controller/
│   └── MovieController.java      # Capa de presentación: maneja las peticiones HTTP
├── service/
│   └── MovieService.java         # Capa de negocio: lógica de la aplicación
├── repository/
│   └── MovieRepository.java      # Capa de acceso a datos: consultas SQL con JdbcTemplate
├── model/
│   └── Movie.java                # Entidad JPA que representa una película
└── external/
    ├── SWAPI.java                 # Cliente HTTP para Star Wars API
    └── SWMovie.java              # DTO para mapear la respuesta de SWAPI
```

### Descripción de cada capa

#### `model/` — Entidad de dominio

`Movie.java` es la entidad JPA que mapea la tabla `movies` en la base de datos. Sus campos son:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Clave primaria autogenerada |
| `title` | `String` | Título de la película |
| `releaseDate` | `Date` | Fecha de estreno |
| `externalId` | `Long` | ID en SWAPI (opcional) |
| `directorName` | `String` | Director (no persistido, viene de SWAPI) |

#### `repository/` — Acceso a datos

`MovieRepository.java` usa `JdbcTemplate` directamente para ejecutar consultas SQL. **No usa JPA Repository**; esto es intencional para ilustrar el concepto de repositorio como abstracción de acceso a datos.

```java
@Repository  // Spring lo detecta y lo registra como bean
public class MovieRepository {
    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) { // Inyección por constructor
        this.jdbcTemplate = jdbcTemplate;
    }
    // findAll(), findById(), save(), updateReleaseDate(), deleteById()
}
```

#### `service/` — Lógica de negocio

`MovieService.java` orquesta las operaciones de negocio. Valida datos antes de delegar al repositorio.

```java
@Service  // Spring lo detecta y lo registra como bean
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) { // Inyección por constructor
        this.movieRepository = movieRepository;
    }
    // createMovie(), getAllMovies(), getMovieById(), updateReleaseDate(), deleteMovie()
}
```

#### `controller/` — Capa HTTP

`MovieController.java` recibe las peticiones HTTP, llama al servicio y devuelve las respuestas. También enriquece las películas con datos de SWAPI cuando corresponde.

```java
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;
    private final SWAPI swapi;

    public MovieController(MovieService movieService, SWAPI swapi) { // Inyección por constructor
        this.movieService = movieService;
        this.swapi = swapi;
    }
}
```

#### `external/` — Integración con API externa

`SWAPI.java` encapsula las llamadas HTTP a `https://swapi.info/api/films/{id}` usando `RestTemplate`. `SWMovie.java` es el DTO que mapea la respuesta JSON (campos `title`, `episodeId`, `director`).

### Inyección de Dependencias en acción

Spring Boot gestiona todos los beans automáticamente gracias a las anotaciones:

```
@SpringBootApplication
       │
       ├── @RestController  → MovieController
       │       ├── @Service     → MovieService
       │       │       └── @Repository → MovieRepository
       │       │                               └── JdbcTemplate (auto-configurado)
       │       └── SWAPI (bean definido en Application con @Bean)
       └── RestTemplate (@Bean en Week02Lab01Application)
```

Cada clase declara sus dependencias como parámetros del constructor y Spring las provee automáticamente — esto es **inyección de dependencias por constructor**, la forma recomendada en Spring Boot moderno.

### Base de datos

Se usa **H2** como base de datos en memoria (`jdbc:h2:mem:week02-lab01`), sin persistencia en disco. Al iniciar la aplicación, Hibernate recrea el esquema y Spring ejecuta `import.sql` para insertar los datos de ejemplo:

| ID | Título | Fecha | External ID |
|----|--------|-------|-------------|
| 1 | The Avengers | 2012-04-11 | — |
| 2 | Avengers: Infinity War | 2018-04-23 | — |
| 3 | Dune: Part Two | 2024-02-06 | — |
| 4 | Deadpool & Wolverine | 2024-07-22 | — |
| 5 | Vaguito | 2023-04-18 | — |
| 6 | Star Wars: A New Hope | 2023-04-18 | 1 |

### Stack tecnológico

- **Java 26**
- **Spring Boot 4.1.0** (Web, Data JPA, RestClient)
- **H2** (base de datos en memoria)
- **JdbcTemplate** (acceso a datos manual)
- **RestTemplate** (cliente HTTP para SWAPI)
