package com.thepower.thepower.controller;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.service.GestionCancionService;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/canciones")
public class GestionCancionController {

    private final GestionCancionService gestionCancionService;

    public GestionCancionController(
            GestionCancionService gestionCancionService) {
        this.gestionCancionService = gestionCancionService;
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CancionDTO> modificar(
            @PathVariable int id,
            @RequestParam String titulo,
            @RequestParam Integer idGenero,
            @RequestParam(required = false)
            MultipartFile audio,
            @RequestParam(required = false)
            MultipartFile imagen,
            Authentication authentication) {

        CancionDTO cancion = gestionCancionService.modificar(
                id,
                authentication.getName(),
                titulo,
                idGenero,
                audio,
                imagen
        );

        return ResponseEntity.ok(cancion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable int id,
            Authentication authentication) {

        gestionCancionService.eliminar(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}

