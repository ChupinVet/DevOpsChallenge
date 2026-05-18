package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados retornados de um responsável")
public record ResponsavelResponseDTO(

        @Schema(example = "1")
        Long idUsuario,

        @Schema(example = "João Silva")
        String nomeUsuario,

        @Schema(example = "joao@email.com")
        String email,

        @Schema(example = "12345678901")
        String cpf,

        @Schema(example = "SP")
        String estado,

        @Schema(example = "São Paulo")
        String cidade,

        @Schema(example = "11999999999")
        String telefone,

        @Schema(example = "2000-05-10")
        LocalDate dataNascimento,

        @Schema(example = "Masculino")
        String genero,

        @Schema(example = "Casa")
        String tipoResidencia,

        @Schema(example = "11888888888")
        String telefoneSecundario

) {
}