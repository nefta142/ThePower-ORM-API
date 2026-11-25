package com.thepower.thepower.controller;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.service.CancionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final CancionService cancionService;

    public CancionController(
            CancionService cancionService) {
        this.cancionService = cancionService;
    }

    @GetMapping
    public List<CancionDTO> buscar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Integer genero) {

        return cancionService.buscar(texto, genero);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CancionDTO> obtenerPorId(
            @PathVariable int id) {

        CancionDTO cancion =
                cancionService.obtenerPorId(id);

        if (cancion == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cancion);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CancionDTO> subir(
            @RequestParam String titulo,
            @RequestParam Integer idGenero,
            @RequestParam MultipartFile audio,
            @RequestParam MultipartFile imagen,
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión");
        }

        CancionDTO cancion = cancionService.subir(
                titulo,
                idGenero,
                audio,
                imagen,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cancion);
    }
}

