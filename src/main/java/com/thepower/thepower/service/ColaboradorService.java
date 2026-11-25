package com.thepower.thepower.service;

import com.thepower.thepower.dto.ColaboradorRespuestaDTO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ColaboradorService {

    private final JdbcTemplate jdbcTemplate;

    public ColaboradorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void bloquearCancion(int idCancion) {
        try {
            jdbcTemplate.queryForObject(
                    """
                    SELECT id_cancion
                    FROM cancion
                    WHERE id_cancion = ?
                    FOR UPDATE
                    """,
                    Integer.class,
                    idCancion
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Canción no encontrada"
            );
        }
    }

    private int obtenerUsuario(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    """
                    SELECT id_usuario
                    FROM usuario
                    WHERE email = ?
                    """,
                    Integer.class,
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sesión no válida"
            );
        }
    }

    private boolean esColaborador(
            int idCancion,
            int idArtista) {

        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM colaboracion
                WHERE id_cancion = ?
                AND id_usuario = ?
                """,
                Integer.class,
                idCancion,
                idArtista
        );

        return total != null && total > 0;
    }

    private void comprobarPermisos(
            int idCancion,
            String email) {

        int idUsuario = obtenerUsuario(email);

        if (!esColaborador(idCancion, idUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos sobre esta canción"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ColaboradorRespuestaDTO> listar(int idCancion) {

        Integer existe = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM cancion
                WHERE id_cancion = ?
                """,
                Integer.class,
                idCancion
        );

        if (existe == null || existe == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Canción no encontrada"
            );
        }

        return jdbcTemplate.query(
                """
                SELECT u.id_usuario,
                       u.nombre_user,
                       u.avatar
                FROM colaboracion col
                JOIN artista a
                    ON col.id_usuario = a.id_usuario
                JOIN usuario u
                    ON a.id_usuario = u.id_usuario
                WHERE col.id_cancion = ?
                ORDER BY u.nombre_user
                """,
                (rs, fila) -> new ColaboradorRespuestaDTO(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_user"),
                        rs.getString("avatar")
                ),
                idCancion
        );
    }

    @Transactional
    public void agregar(
            int idCancion,
            int idArtista,
            String email) {

        bloquearCancion(idCancion);
        comprobarPermisos(idCancion, email);

        Integer existeArtista = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM artista
                WHERE id_usuario = ?
                """,
                Integer.class,
                idArtista
        );

        if (existeArtista == null || existeArtista == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El artista no existe"
            );
        }

        if (esColaborador(idCancion, idArtista)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El artista ya participa en esta canción"
            );
        }

        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO colaboracion
                        (id_usuario, id_cancion)
                    VALUES (?, ?)
                    """,
                    idArtista,
                    idCancion
            );
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El artista ya participa en esta canción",
                    e
            );
        }
    }

    @Transactional
    public void eliminar(
            int idCancion,
            int idArtista,
            String email) {

        bloquearCancion(idCancion);
        comprobarPermisos(idCancion, email);

        if (!esColaborador(idCancion, idArtista)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El colaborador no existe en esta canción"
            );
        }

        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM colaboracion
                WHERE id_cancion = ?
                """,
                Integer.class,
                idCancion
        );

        if (total == null || total <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La canción debe tener al menos un artista"
            );
        }

        jdbcTemplate.update(
                """
                DELETE FROM colaboracion
                WHERE id_cancion = ?
                AND id_usuario = ?
                """,
                idCancion,
                idArtista
        );
    }
}

