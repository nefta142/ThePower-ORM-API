package com.thepower.thepower.service;

import com.thepower.thepower.entity.Usuario;
import com.thepower.thepower.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Iterator;
import java.util.UUID;

@Service
public class AvatarService {

    private final UsuarioRepository usuarioRepository;

    private final Path carpeta = Paths.get("uploads", "perfiles")
            .toAbsolutePath().normalize();

    public AvatarService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public String actualizar(String email, MultipartFile imagen) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Sesión no válida"
                ));

        if (imagen == null || imagen.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecciona una imagen"
            );
        }

        if (imagen.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La imagen no puede superar los 5 MB"
            );
        }

        String extension = validarImagen(imagen);
        String nombre = UUID.randomUUID() + extension;
        String ruta = "/api/usuarios/avatares/" + nombre;

        Path destino = carpeta.resolve(nombre).normalize();
        String avatarAnterior = usuario.getAvatar();

        try {
            Files.createDirectories(carpeta);

            try (InputStream entrada = imagen.getInputStream()) {
                Files.copy(entrada, destino);
            }
        } catch (IOException e) {
            eliminarArchivo(destino);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la imagen", e
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        eliminarAnterior(avatarAnterior);
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            eliminarArchivo(destino);
                        }
                    }
                }
        );

        usuario.setAvatar(ruta);
        usuarioRepository.saveAndFlush(usuario);

        return ruta;
    }

    private String validarImagen(MultipartFile imagen) {

        try (InputStream entrada = imagen.getInputStream();
             ImageInputStream flujo =
                     ImageIO.createImageInputStream(entrada)) {

            if (flujo == null) {
                throw new IOException("Imagen no válida");
            }

            Iterator<ImageReader> lectores =
                    ImageIO.getImageReaders(flujo);

            if (!lectores.hasNext()) {
                throw new IOException("Formato no reconocido");
            }

            ImageReader lector = lectores.next();

            try {
                lector.setInput(flujo);

                String formato = lector.getFormatName();
                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);

                if (ancho < 1 || alto < 1 ||
                        ancho > 4096 || alto > 4096) {
                    throw new IOException("Dimensiones no válidas");
                }

                if (formato.equalsIgnoreCase("PNG")) {
                    return ".png";
                }

                if (formato.equalsIgnoreCase("JPEG")) {
                    return ".jpg";
                }

                throw new IOException("Formato no permitido");

            } finally {
                lector.dispose();
            }

        } catch (IOException | RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La imagen debe ser JPG o PNG válido", e
            );
        }
    }

    private void eliminarAnterior(String ruta) {

        if (ruta == null ||
                !ruta.startsWith("/api/usuarios/avatares/")) {
            return;
        }

        String nombre = ruta.substring(
                "/api/usuarios/avatares/".length()
        );

        if (!nombre.matches(
                "[a-fA-F0-9-]{36}\\.(jpg|png)")) {
            return;
        }

        eliminarArchivo(carpeta.resolve(nombre));
    }

    private void eliminarArchivo(Path archivo) {
        try {
            Path normalizado = archivo.toAbsolutePath().normalize();

            if (normalizado.startsWith(carpeta)) {
                Files.deleteIfExists(normalizado);
            }
        } catch (IOException ignored) {
        }
    }

    public Path obtener(String nombre) {

        if (!nombre.matches(
                "[a-fA-F0-9-]{36}\\.(jpg|png)")) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

        Path archivo = carpeta.resolve(nombre).normalize();

        if (!archivo.startsWith(carpeta) ||
                !Files.isRegularFile(archivo)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

        return archivo;
    }
}

