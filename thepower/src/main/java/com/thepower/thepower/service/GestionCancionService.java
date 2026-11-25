package com.thepower.thepower.service;

import com.thepower.thepower.dto.CancionDTO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GestionCancionService {

    private final JdbcTemplate jdbcTemplate;
    private final ArchivoService archivoService;
    private final CancionService cancionService;

    public GestionCancionService(
            JdbcTemplate jdbcTemplate,
            ArchivoService archivoService,
            CancionService cancionService) {

        this.jdbcTemplate = jdbcTemplate;
        this.archivoService = archivoService;
        this.cancionService = cancionService;
    }

    private void comprobarPermisos(int idCancion, String email) {

        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM colaboracion col
                JOIN usuario u
                    ON col.id_usuario = u.id_usuario
                WHERE col.id_cancion = ?
                AND u.email = ?
                """,
                Integer.class,
                idCancion,
                email
        );

        if (total == null || total == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permisos sobre esta canción"
            );
        }
    }

    private Map<String, Object> obtenerCancion(int id) {

        try {
            return jdbcTemplate.queryForMap(
                    """
                    SELECT id_cancion, titulo, id_genero,
                           audio, imagen
                    FROM cancion
                    WHERE id_cancion = ?
                    FOR UPDATE
                    """,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Canción no encontrada"
            );
        }
    }

    @Transactional
    public CancionDTO modificar(
            int id,
            String email,
            String titulo,
            Integer idGenero,
            MultipartFile audio,
            MultipartFile imagen) {

        Map<String, Object> datos = obtenerCancion(id);

        comprobarPermisos(id, email);

        if (titulo == null ||
                titulo.isBlank() ||
                titulo.trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Título incorrecto"
            );
        }

        if (idGenero == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El género es obligatorio"
            );
        }

        Integer existeGenero = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM genero WHERE id_genero = ?",
                Integer.class,
                idGenero
        );

        if (existeGenero == null || existeGenero == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El género no existe"
            );
        }

        String audioAnterior = (String) datos.get("audio");
        String imagenAnterior = (String) datos.get("imagen");

        String nuevoAudio = null;
        String nuevaImagen = null;

        try {
            if (audio != null && !audio.isEmpty()) {
                nuevoAudio = archivoService.guardar(
                        audio, "audio");
            }

            if (imagen != null && !imagen.isEmpty()) {
                nuevaImagen = archivoService.guardar(
                        imagen, "imagenes");
            }

            String rutaAudio = nuevoAudio != null
                    ? nuevoAudio : audioAnterior;

            String rutaImagen = nuevaImagen != null
                    ? nuevaImagen : imagenAnterior;

            String audioCreado = nuevoAudio;
            String imagenCreada = nuevaImagen;

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {
                                    if (audioCreado != null) {
                                        archivoService.eliminar(
                                                audioAnterior);
                                    }

                                    if (imagenCreada != null) {
                                        archivoService.eliminar(
                                                imagenAnterior);
                                    }
                                }

                                @Override
                                public void afterCompletion(int status) {
                                    if (status != STATUS_COMMITTED) {
                                        archivoService.eliminar(
                                                audioCreado);
                                        archivoService.eliminar(
                                                imagenCreada);
                                    }
                                }
                            }
                    );

            jdbcTemplate.update(
                    """
                    UPDATE cancion
                    SET titulo = ?,
                        id_genero = ?,
                        audio = ?,
                        imagen = ?
                    WHERE id_cancion = ?
                    """,
                    titulo.trim(),
                    idGenero,
                    rutaAudio,
                    rutaImagen,
                    id
            );

            return cancionService.obtenerPorId(id);

        } catch (RuntimeException e) {
            archivoService.eliminar(nuevoAudio);
            archivoService.eliminar(nuevaImagen);
            throw e;
        }
    }

    @Transactional
    public void eliminar(int id, String email) {

        Map<String, Object> datos = obtenerCancion(id);

        comprobarPermisos(id, email);

        Integer colaboradores = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM colaboracion
                WHERE id_cancion = ?
                """,
                Integer.class,
                id
        );

        if (colaboradores != null && colaboradores > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La canción tiene otros colaboradores"
            );
        }

        String audio = (String) datos.get("audio");
        String imagen = (String) datos.get("imagen");

        jdbcTemplate.update(
                "DELETE FROM favoritos WHERE id_cancion = ?", id);

        jdbcTemplate.update(
                "DELETE FROM reproducciones WHERE id_cancion = ?", id);

        jdbcTemplate.update(
                "DELETE FROM colaboracion WHERE id_cancion = ?", id);

        jdbcTemplate.update(
                "DELETE FROM cancion WHERE id_cancion = ?", id);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        archivoService.eliminar(audio);
                        archivoService.eliminar(imagen);
                    }
                }
        );
    }

    @Transactional(readOnly = true)
    public List<CancionDTO> cancionesDeArtista(int idArtista) {

        Integer existe = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM artista WHERE id_usuario = ?",
                Integer.class,
                idArtista
        );

        if (existe == null || existe == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Artista no encontrado"
            );
        }

        List<Integer> ids = jdbcTemplate.queryForList(
                """
                SELECT c.id_cancion
                FROM cancion c
                JOIN colaboracion col
                    ON c.id_cancion = col.id_cancion
                WHERE col.id_usuario = ?
                ORDER BY c.titulo
                """,
                Integer.class,
                idArtista
        );

        List<CancionDTO> resultado = new ArrayList<>();

        for (Integer id : ids) {
            CancionDTO cancion = cancionService.obtenerPorId(id);

            if (cancion != null) {
                resultado.add(cancion);
            }
        }

        return resultado;
    }
}
