package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Representa um veterinário.
 *
 * Novos campos em relação à Sprint 1: nm_clinica e ds_bio (ambos opcionais).
 *
 */
@Entity
@Table(name = "veterinario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idVeterinario")
@Schema(
        name = "Veterinario",
        description = "Representa um veterinário, associado a um Usuario"
)
public class Veterinario {

    @Id
    @SequenceGenerator(name = "veterinario_seq", sequenceName = "seq_veterinario", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "veterinario_seq")
    @Column(name = "id_veterinario")
    @Schema(
            description = "ID único do veterinário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idVeterinario;

    @NotBlank
    @Size(max = 10)
    @Column(name = "num_crmv", nullable = false, length = 10)
    @Schema(description = "Número do CRMV", example = "12345")
    private String crmv;

    @NotBlank
    @Size(max = 80)
    @Column(name = "ds_especialidade", nullable = false, length = 80)
    @Schema(description = "Especialidade do veterinário", example = "Cardiologia")
    private String especialidade;

    @PositiveOrZero
    @Column(name = "qtd_anos_experiencia")
    @Schema(description = "Quantidade de anos de experiência", example = "10")
    private Integer anosExperiencia;

    @Size(max = 100)
    @Column(name = "ds_disponibilidade", length = 100)
    @Schema(description = "Disponibilidade de atendimento", example = "Segunda a Sexta")
    private String disponibilidade;

    @Size(max = 100)
    @Column(name = "tp_servico", length = 100)
    @Schema(description = "Tipo de serviço prestado", example = "Consulta")
    private String tipoServico;

    @Size(max = 100)
    @Column(name = "nm_clinica", length = 100)
    @Schema(description = "Nome da clínica onde atende", example = "Clínica Pet Amigo")
    private String nomeClinica;

    @Size(max = 500)
    @Column(name = "ds_bio", length = 500)
    @Schema(description = "Biografia/descrição profissional do veterinário")
    private String bio;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Usuario_id_usuario", nullable = false, unique = true)
    @Schema(hidden = true)
    private Usuario usuario;
}