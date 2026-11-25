package com.thepower.thepower.service;

import com.thepower.thepower.entity.Usuario;
import com.thepower.thepower.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class EliminarCuentaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JdbcTemplate jdbcTemplate;

    public EliminarCuentaService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder encoder,
            JdbcTemplate jdbcTemplate) {

        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void eliminar(String email, String contrasena) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Sesión no válida"
                ));

        if (!encoder.matches(
                contrasena,
                usuario.getContrasena())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contraseña incorrecta"
            );
        }

        int idUsuario = usuario.getIdUsuario();

        jdbcTemplate.queryForObject(
                "SELECT id_usuario FROM usuario WHERE id_usuario = ? FOR UPDATE",
                Integer.class,
                idUsuario
        );

        List<String> archivos = new ArrayList<>();

        if (usuario.getAvatar() != null) {
            archivos.add(usuario.getAvatar());
        }

        List<Integer> canciones = jdbcTemplate.queryForList(
                "SELECT id_cancion FROM colaboracion WHERE id_usuario = ?",
                Integer.class,
                idUsuario
        );

        List<Integer> cancionesEliminar = new ArrayList<>();

        for (Integer idCancion : canciones) {

            Integer otrosArtistas = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM colaboracion
                    WHERE id_cancion = ?
                    AND id_usuario <> ?
                    """,
                    Integer.class,
                    idCancion,
                    idUsuario
            );

            if (otrosArtistas != null && otrosArtistas == 0) {
                cancionesEliminar.add(idCancion);
            }
        }

        jdbcTemplate.update(
                "DELETE FROM favoritos WHERE id_usuario = ?",
                idUsuario
        );

        jdbcTemplate.update(
                "DELETE FROM reproducciones WHERE id_usuario = ?",
                idUsuario
        );

        jdbcTemplate.update(
                "DELETE FROM colaboracion WHERE id_usuario = ?",
                idUsuario
        );

        for (Integer idCancion : cancionesEliminar) {

            List<String[]> rutas = jdbcTemplate.query(
                    """
                    SELECT audio, imagen
                    FROM cancion
                    WHERE id_cancion = ?
                    """,
                    (rs, rowNum) -> new String[]{
                            rs.getString("audio"),
                            rs.getString("imagen")
                    },
                    idCancion
            );

            for (String[] ruta : rutas) {
                archivos.add(ruta[0]);
                archivos.add(ruta[1]);
            }

            jdbcTemplate.update(
                    "DELETE FROM favoritos WHERE id_cancion = ?",
                    idCancion
            );

            jdbcTemplate.update(
                    "DELETE FROM reproducciones WHERE id_cancion = ?",
                    idCancion
            );

            jdbcTemplate.update(
                    "DELETE FROM cancion WHERE id_cancion = ?",
                    idCancion
            );
        }

        jdbcTemplate.update(
                "DELETE FROM artista WHERE id_usuario = ?",
                idUsuario
        );

        jdbcTemplate.update(
                "DELETE FROM usuario WHERE id_usuario = ?",
                idUsuario
        );

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        for (String archivo : archivos) {
                            eliminarArchivo(archivo);
                        }
                    }
                }
        );
    }

    private void eliminarArchivo(String ruta) {

        if (ruta == null) {
            return;
        }

        String relativa;

        if (ruta.matches(
                "/api/archivos/(audio|imagenes)/[a-fA-F0-9-]{36}\\.(mp3|jpg|png)")) {

            relativa = ruta.substring(
                    "/api/archivos/".length()
            );

        } else if (ruta.matches(
                "/api/usuarios/avatares/[a-fA-F0-9-]{36}\\.(jpg|png)")) {

            relativa = "perfiles/" + ruta.substring(
                    "/api/usuarios/avatares/".length()
            );

        } else {
            return;
        }

        Path raiz = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();

        Path destino = raiz.resolve(relativa).normalize();

        if (!destino.startsWith(raiz)) {
            return;
        }

        try {
            Files.deleteIfExists(destino);
        } catch (IOException ignored) {
        }
    }
}

