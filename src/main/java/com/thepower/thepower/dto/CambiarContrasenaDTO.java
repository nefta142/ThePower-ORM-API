package com.thepower.thepower.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarContrasenaDTO(

        @NotBlank
        String contrasenaActual,

        @NotBlank
        @Size(min = 8, max = 72)
        String contrasenaNueva

) {}

