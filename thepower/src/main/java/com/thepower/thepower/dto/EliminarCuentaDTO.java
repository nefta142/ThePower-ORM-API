package com.thepower.thepower.dto;

import jakarta.validation.constraints.NotBlank;

public record EliminarCuentaDTO(
        @NotBlank String contrasena
) {}
