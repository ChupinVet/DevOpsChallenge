package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.math.BigDecimal;

@Schema(description = "Dados para cadastro ou atualização de um registro de diário")
public record DiarioRequestDTO(

        @Schema(description = "Data do registro", example = "2026-06-15")
        @NotNull
        LocalDate dataRegistro,

        @Schema(description = "Humor observado do pet", example = "Feliz")
        @NotBlank
        @Size(max = 50)
        String humor,

        @Schema(description = "Alimentação do dia", example = "Normal")
        @NotBlank
        @Size(max = 50)
        String alimentacao,

        @Schema(description = "Consumo de água do dia", example = "Bebeu bem")
        @NotBlank
        @Size(max = 50)
        String consumoAgua,

        @Schema(description = "Observações sobre o comportamento")
        @Size(max = 500)
        String comportamento,

        @Schema(description = "Sintomas observados, se houver")
        @Size(max = 500)
        String sintomas,

        @Schema(description = "Observações gerais do responsável")
        @Size(max = 500)
        String observacoes,

        @Schema(description = "Peso do pet no momento do registro (kg)", example = "28.5")
        @Positive
        BigDecimal pesoRegistrado,

        @Schema(description = "ID do pet", example = "1")
        @NotNull
        Long idPet

) {
}