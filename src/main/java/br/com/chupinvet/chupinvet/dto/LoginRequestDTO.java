package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de login")
public record LoginRequestDTO(

        @Schema(example = "joao@email.com")
        @NotBlank
        @Email
        String email,

        @Schema(example = "senhaForte123")
        @NotBlank
        String senha

) {
}