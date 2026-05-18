package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPet")
@Schema(
        name = "Pet",
        description = "Representa um pet cadastrado no sistema"
)
public class Pet {

    @Id
    @SequenceGenerator(name = "pet_seq", sequenceName = "pet_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pet_seq")
    @Column(name = "id_pet")
    @Schema(
            description = "ID único do pet",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idPet;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nm_pet", nullable = false, length = 80)
    @Schema(
            description = "Nome do pet",
            example = "Thor"
    )
    private String nomePet;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tp_especie", nullable = false, length = 50)
    @Schema(
            description = "Espécie do pet",
            example = "Cachorro"
    )
    private String especie;

    @Size(max = 50)
    @Column(name = "nm_raca", length = 50)
    @Schema(
            description = "Raça do pet",
            example = "Golden Retriever"
    )
    private String raca;

    @Column(name = "dt_nascimento_pet")
    @Schema(
            description = "Data de nascimento do pet",
            example = "2022-01-10"
    )
    private LocalDate dataNascimento;

    @Positive
    @Column(name = "vl_peso", precision = 5, scale = 2)
    @Schema(
            description = "Peso do pet",
            example = "25.5"
    )
    private double peso;

    @Schema(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Responsavel responsavel;
}