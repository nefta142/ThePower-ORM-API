package com.thepower.thepower.controller;

import com.thepower.thepower.service.ArchivoService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @GetMapping("/{tipo}/{nombre}")
    public ResponseEntity<Resource> obtener(
            @PathVariable String tipo,
            @PathVariable String nombre) {

        Path archivo = archivoService.obtener(tipo, nombre);

        MediaType mediaType;

        if (nombre.endsWith(".mp3")) {
            mediaType = MediaType.parseMediaType("audio/mpeg");
        } else if (nombre.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline")
                .body(new FileSystemResource(archivo));
    }
}

