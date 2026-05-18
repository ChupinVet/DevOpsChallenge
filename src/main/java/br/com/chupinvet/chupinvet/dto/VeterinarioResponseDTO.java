package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados de um veterinário")
public record VeterinarioResponseDTO(

        @Schema(example = "1")
        Long idUsuario,

        @Schema(example = "Carlos Mendes")
        String nomeUsuario,

        @Schema(example = "carlos@vet.com")
        String email,

        @Schema(example = "12345678901")
        String cpf,

        @Schema(example = "SP")
        String estado,

        @Schema(example = "Campinas")
        String cidade,

        @Schema(example = "11999999999")
        String telefone,

        @Schema(example = "12345")
        String crmv,

        @Schema(example = "Cardiologia")
        String especialidade,

        @Schema(example = "10")
        Integer anosExperiencia,

        @Schema(example = "Segunda a Sexta")
        String disponibilidade,

        @Schema(example = "Consulta")
        String tipoServico

) {
}