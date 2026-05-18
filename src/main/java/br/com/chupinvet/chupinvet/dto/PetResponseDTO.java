package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados retornados de um pet")
public record PetResponseDTO(

        @Schema(example = "1")
        Long idPet,

        @Schema(example = "Thor")
        String nomePet,

        @Schema(example = "Cachorro")
        String especie,

        @Schema(example = "Golden Retriever")
        String raca,

        @Schema(example = "2022-01-10")
        LocalDate dataNascimento,

        @Schema(example = "25.5")
        double peso,

        @Schema(example = "1")
        Long idResponsavel,

        @Schema(example = "João Silva")
        String nomeResponsavel

) {
}