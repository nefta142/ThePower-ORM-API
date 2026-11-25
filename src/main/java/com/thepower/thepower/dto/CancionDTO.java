package com.thepower.thepower.dto;

public record CancionDTO(
        int idCancion,
        String titulo,
        String imagen,
        String audio,
        int idGenero,
        String genero
) {
}

