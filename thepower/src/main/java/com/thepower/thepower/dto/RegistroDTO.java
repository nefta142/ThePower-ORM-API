package com.thepower.thepower.dto;

import jakarta.validation.constraints.*;

public record RegistroDTO(
        @NotBlank @Size(max = 25) String nombreUsuario,
        @NotBlank @Email @Size(max = 50) String email,
        @NotBlank @Size(min = 8, max = 72) String contrasena,
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Size(max = 50) String apellido1,
        @Size(max = 50) String apellido2
) {}
