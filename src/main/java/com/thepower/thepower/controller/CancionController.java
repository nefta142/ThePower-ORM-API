package com.thepower.thepower.controller;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.service.CancionService;
import com.thepower.thepower.service.GestionCancionService;

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
    private final GestionCancionService gestionCancionService;

    public CancionController(
            CancionService cancionService,
            GestionCancionService gestionCancionService) {

        this.cancionService = cancionService;
        this.gestionCancionService = gestionCancionService;
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

        CancionDTO cancion = cancionService.obtenerPorId(id);

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

        String email = obtenerEmail(authentication);

        CancionDTO cancion = cancionService.subir(
                titulo,
                idGenero,
                audio,
                imagen,
                email
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cancion);
    }

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<CancionDTO> modificar(
            @PathVariable int id,
            @RequestParam String titulo,
            @RequestParam Integer idGenero,
            @RequestParam(required = false) MultipartFile audio,
            @RequestParam(required = false) MultipartFile imagen,
            Authentication authentication) {

        String email = obtenerEmail(authentication);

        CancionDTO cancion = gestionCancionService.modificar(
                id,
                email,
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

        String email = obtenerEmail(authentication);

        gestionCancionService.eliminar(id, email);

        return ResponseEntity.noContent().build();
    }

    private String obtenerEmail(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal() instanceof String
                        && authentication.getPrincipal().equals("anonymousUser")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        return authentication.getName();
    }
}
