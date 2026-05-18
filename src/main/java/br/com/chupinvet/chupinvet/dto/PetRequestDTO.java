package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para cadastro ou atualização de pet")
public record PetRequestDTO(

        @Schema(description = "Nome do pet", example = "Thor")
        @NotBlank
        @Size(max = 80)
        String nomePet,

        @Schema(description = "Espécie do pet", example = "Cachorro")
        @NotBlank
        @Size(max = 50)
        String especie,

        @Schema(description = "Raça do pet", example = "Golden Retriever")
        @Size(max = 50)
        String raca,

        @Schema(description = "Data de nascimento", example = "2022-01-10")
        LocalDate dataNascimento,

        @Schema(description = "Peso do pet", example = "25.5")
        @Positive
        double peso,

        @Schema(description = "ID do responsável", example = "1")
        @NotNull
        Long idResponsavel

) {
}