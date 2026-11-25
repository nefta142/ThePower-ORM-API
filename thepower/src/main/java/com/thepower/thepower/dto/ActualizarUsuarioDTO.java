package com.thepower.thepower.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioDTO(

        @NotBlank
        @Size(max = 25)
        String nombreUsuario,

        @NotBlank
        @Email
        @Size(max = 50)
        String email,

        @NotBlank
        @Size(max = 50)
        String nombre,

        @NotBlank
        @Size(max = 50)
        String apellido1,

        @Size(max = 50)
        String apellido2

) {}
