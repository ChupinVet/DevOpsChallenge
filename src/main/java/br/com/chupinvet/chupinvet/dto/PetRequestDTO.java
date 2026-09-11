package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;


@Schema(description = "Dados para cadastro ou atualização de pet")
public record PetRequestDTO(

        @Schema(description = "Nome do pet", example = "Nasus")
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

        @Schema(description = "Idade do pet em anos", example = "6")
        @PositiveOrZero
        Integer idade,

        @Schema(description = "Peso do pet (kg)", example = "30.5")
        @Positive
        BigDecimal peso

) {
}