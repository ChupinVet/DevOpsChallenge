package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "diario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idDiario")
@Schema(
        name = "Diario",
        description = "Registro de diário de um pet, feito pelo responsável"
)
public class Diario {

    @Id
    @SequenceGenerator(name = "diario_seq", sequenceName = "seq_diario", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diario_seq")
    @Column(name = "id_diario")
    @Schema(
            description = "ID único do registro de diário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idDiario;

    @NotNull
    @Column(name = "dt_registro", nullable = false)
    @Schema(description = "Data do registro", example = "2026-06-15")
    private LocalDate dataRegistro;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tp_humor", nullable = false, length = 50)
    @Schema(description = "Humor observado do pet", example = "Feliz")
    private String humor;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tp_alimentacao", nullable = false, length = 50)
    @Schema(description = "Alimentação do dia", example = "Normal")
    private String alimentacao;

    @NotBlank
    @Size(max = 50)
    @Column(name = "tp_agua", nullable = false, length = 50)
    @Schema(description = "Consumo de água do dia", example = "Bebeu bem")
    private String consumoAgua;

    @Size(max = 500)
    @Column(name = "ds_comportamento", length = 500)
    @Schema(description = "Observações sobre o comportamento")
    private String comportamento;

    @Size(max = 500)
    @Column(name = "ds_sintomas", length = 500)
    @Schema(description = "Sintomas observados, se houver")
    private String sintomas;

    @Size(max = 500)
    @Column(name = "ds_observacoes", length = 500)
    @Schema(description = "Observações gerais do responsável")
    private String observacoes;

    @Positive
    @Column(name = "vl_peso_registrado", precision = 5, scale = 2)
    @Schema(description = "Peso do pet no momento do registro (kg)", example = "28.5")
    private BigDecimal pesoRegistrado;

    @Size(max = 500)
    @Column(name = "ds_insight_ia", length = 500)
    @Schema(
            description = "Insight gerado automaticamente pela regra de negócio (ex.: alerta de variação de peso)",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String insightIA;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Pet_id_pet", nullable = false)
    @Schema(hidden = true)
    private Pet pet;
}