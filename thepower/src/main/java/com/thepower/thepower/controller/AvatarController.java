package com.thepower.thepower.controller;

import com.thepower.thepower.service.AvatarService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> subir(
            @RequestParam("imagen") MultipartFile imagen,
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        String ruta = avatarService.actualizar(
                authentication.getName(), imagen
        );

        return ResponseEntity.ok(
                Map.of("avatar", ruta)
        );
    }

    @GetMapping("/avatares/{nombre}")
    public ResponseEntity<Resource> obtener(
            @PathVariable String nombre) {

        Path archivo = avatarService.obtener(nombre);

        MediaType tipo = nombre.endsWith(".png")
                ? MediaType.IMAGE_PNG
                : MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(
                        "X-Content-Type-Options",
                        "nosniff"
                )
                .body(new FileSystemResource(archivo));
    }
}

