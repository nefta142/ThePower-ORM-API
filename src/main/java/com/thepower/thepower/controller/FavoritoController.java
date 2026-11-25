package com.thepower.thepower.controller;

import com.thepower.thepower.dto.CancionDTO;
import com.thepower.thepower.service.FavoritoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping("/api/canciones/{id}/favorito")
    public ResponseEntity<Void> agregar(
            @PathVariable int id,
            Authentication authentication) {

        favoritoService.agregar(
                id,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/api/canciones/{id}/favorito")
    public ResponseEntity<Void> eliminar(
            @PathVariable int id,
            Authentication authentication) {

        favoritoService.eliminar(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/usuarios/me/favoritos")
    public List<CancionDTO> listar(
            Authentication authentication) {

        return favoritoService.listar(
                authentication.getName()
        );
    }
}

