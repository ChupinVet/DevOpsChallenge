package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados retornados de um pet")
public record PetResponseDTO(

        @Schema(example = "1")
        Long idPet,

        @Schema(example = "Nasus")
        String nomePet,

        @Schema(example = "Cachorro")
        String especie,

        @Schema(example = "Golden Retriever")
        String raca,

        @Schema(example = "6")
        Integer idade,

        @Schema(example = "30.5")
        BigDecimal peso,

        @Schema(example = "1")
        Long idResponsavel,

        @Schema(example = "João Silva")
        String nomeResponsavel

) {
}