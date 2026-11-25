
package com.thepower.thepower.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ArchivoService {

    private final Path raiz = Paths.get("uploads")
            .toAbsolutePath().normalize();

    public String guardar(
            MultipartFile archivo,
            String tipo) {

        if (!tipo.equals("audio") &&
                !tipo.equals("imagenes")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de archivo incorrecto");
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo es obligatorio");
        }

        long limite = tipo.equals("audio")
                ? 20L * 1024 * 1024
                : 5L * 1024 * 1024;

        if (archivo.getSize() > limite) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Archivo demasiado grande");
        }

        String extension;

        try (InputStream entrada = archivo.getInputStream()) {
            byte[] cabecera = entrada.readNBytes(12);

            if (tipo.equals("audio")) {
                boolean mp3 = cabecera.length >= 3 &&
                        cabecera[0] == 'I' &&
                        cabecera[1] == 'D' &&
                        cabecera[2] == '3';

                boolean frame = cabecera.length >= 2 &&
                        (cabecera[0] & 0xFF) == 0xFF &&
                        (cabecera[1] & 0xE0) == 0xE0;

                if (!mp3 && !frame) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "El audio debe ser MP3");
                }

                extension = ".mp3";
            } else {
                boolean png = cabecera.length >= 8 &&
                        (cabecera[0] & 0xFF) == 0x89 &&
                        cabecera[1] == 'P' &&
                        cabecera[2] == 'N' &&
                        cabecera[3] == 'G';

                boolean jpg = cabecera.length >= 3 &&
                        (cabecera[0] & 0xFF) == 0xFF &&
                        (cabecera[1] & 0xFF) == 0xD8 &&
                        (cabecera[2] & 0xFF) == 0xFF;

                if (!png && !jpg) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "La portada debe ser JPG o PNG");
                }

                extension = png ? ".png" : ".jpg";
            }

            Path carpeta = raiz.resolve(tipo);
            Files.createDirectories(carpeta);

            String nombre = UUID.randomUUID() + extension;
            Path destino = carpeta.resolve(nombre);

            try (InputStream contenido =
                         archivo.getInputStream()) {
                Files.copy(contenido, destino);
            }

            return "/api/archivos/" + tipo + "/" + nombre;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el archivo", e);
        }
    }

    public void eliminar(String ruta) {
        if (ruta == null ||
                !ruta.startsWith("/api/archivos/")) {
            return;
        }

        String relativa =
                ruta.substring("/api/archivos/".length());

        Path archivo = raiz.resolve(relativa).normalize();

        if (!archivo.startsWith(raiz)) {
            return;
        }

        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo eliminar el archivo", e);
        }
    }

    public Path obtener(String tipo, String nombre) {

        if (!tipo.equals("audio") &&
                !tipo.equals("imagenes")) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND);
        }

        if (!nombre.matches("[a-fA-F0-9-]{36}\\.(mp3|jpg|png)")) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND);
        }

        Path archivo = raiz.resolve(tipo)
                .resolve(nombre).normalize();

        if (!archivo.startsWith(raiz) ||
                !Files.isRegularFile(archivo)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND);
        }

        return archivo;
    }
}

