# The Power — Proyecto interdisciplinar

## 1. Descripción del sistema

The Power es una aplicación de música desarrollada con React y Vite, Spring Boot y MariaDB. Permite consultar canciones, buscar por título o artista, filtrar por género, registrar usuarios, gestionar perfiles, publicar canciones y asociar varios artistas a una canción. Los archivos MP3 y las imágenes se almacenan en el servidor y sus rutas se registran en la base de datos.

## 2. Modelo de datos

### 2.1. Entidad–Relación

![Diagrama E/R](diagramas/modelo_er.png)

El sistema relaciona usuarios, artistas, canciones y géneros. Un género agrupa múltiples canciones; un artista puede colaborar en varias canciones y una canción puede tener varios artistas.

### 2.2. UML

![Diagrama UML](diagramas/modelo_uml.png)



### 2.3. Modelo relacional

![Modelo relacional](diagramas/modelo_relacional.png)



## 3. ORM y acceso a datos

Las clases JPA están mapeadas con `@Entity`; la herencia `JOINED` relaciona Usuario y Artista. Cancion utiliza `@ManyToOne` para el género, mientras que `Colaboracion` utiliza una clave compuesta con `@IdClass`. Los repositorios utilizan `JpaRepository`; determinadas operaciones de gestión utilizan además `JdbcTemplate`.

**Evidencia ORM:** [CAPTURA T1: entidades JPA en IntelliJ]

**Evidencia de relaciones:** [CAPTURA T2: `SHOW CREATE TABLE colaboracion`]

## 5. API y pruebas

Con PostMan podemos realizar las siguentes pruebas sobre la API 

| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/generos` | Catálogo de géneros |
| GET | `/api/canciones?texto=...&genero=...` | Listado, búsqueda y filtros |
| POST | `/api/usuarios/registro` | Registro |
| POST | `/api/usuarios/login` | Inicio de sesión |
| GET / PUT / DELETE | `/api/usuarios/me` | Gestión de la cuenta |
| POST | `/api/usuarios/me/avatar` | Avatar |
| PUT | `/api/usuarios/me/contrasena` | Contraseña |
| POST | `/api/canciones` | Publicar canción |
| PUT / DELETE | `/api/canciones/{id}` | Gestionar canción |
| GET | `/api/artistas/{id}/canciones` | Canciones de artista |
| GET / POST | `/api/canciones/{id}/colaboradores` | Consultar y añadir colaboradores |
| DELETE | `/api/canciones/{id}/colaboradores/{idArtista}` | Retirar colaborador |

## 6. Instalación y ejecución

Haremos in git clone importaremos la base datos adjuantada en este repositorio y ejecutaremos la app desde intelIJ 


## 7. Documentos de uso

- [Manual de instalación](../../Downloads/Manual_de_instalacion_The_Power.pdf)
- [Manual de usuario](../../Downloads/Manual_de_usuario_The_Power.pdf)