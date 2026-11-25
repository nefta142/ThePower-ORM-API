package com.thepower.thepower.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgregarColaboradorDTO(
        @NotNull
        @Positive
        Integer idArtista
) {}
