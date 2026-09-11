package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de login: token JWT + dados básicos do usuário autenticado")
public record JwtResponseDTO(

        @Schema(description = "Token JWT a ser enviado no header Authorization: Bearer <token>")
        String token,

        @Schema(example = "Bearer")
        String tipo,

        @Schema(example = "1")
        Long idUsuario,

        @Schema(example = "João Silva")
        String nomeUsuario,

        @Schema(example = "joao@email.com")
        String email,

        @Schema(description = "Papel do usuário no sistema", example = "ROLE_RESPONSAVEL")
        String role,

        @Schema(description = "Preenchido apenas se o usuário for um Responsável", example = "1")
        Long idResponsavel,

        @Schema(description = "Preenchido apenas se o usuário for um Veterinário", example = "null")
        Long idVeterinario

) {
}