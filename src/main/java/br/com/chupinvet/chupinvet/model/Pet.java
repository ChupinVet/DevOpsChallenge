package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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
    @SequenceGenerator(name = "pet_seq", sequenceName = "seq_pet", allocationSize = 1)
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
    @Schema(description = "Nome do pet", example = "Nasus")
    private String nomePet;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tp_especie", nullable = false, length = 50)
    @Schema(description = "Espécie do pet", example = "Cachorro")
    private String especie;

    @Size(max = 50)
    @Column(name = "nm_raca", length = 50)
    @Schema(description = "Raça do pet", example = "Golden Retriever")
    private String raca;

    @PositiveOrZero
    @Column(name = "qtd_idade_pet")
    @Schema(description = "Idade do pet em anos", example = "6")
    private Integer idade;

    @Positive
    @Column(name = "vl_peso", precision = 5, scale = 2)
    @Schema(description = "Peso atual do pet (kg)", example = "30.5")
    private BigDecimal peso;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Responsavel_id_responsavel", nullable = false)
    @Schema(hidden = true)
    private Responsavel responsavel;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Schema(hidden = true)
    private List<Diario> diarios = new ArrayList<>();
}