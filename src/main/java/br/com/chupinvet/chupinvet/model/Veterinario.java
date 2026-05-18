package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "veterinario")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("VETERINARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(
        name = "Veterinario",
        description = "Representa um veterinário do sistema"
)
public class Veterinario extends Usuario {

    @NotBlank
    @Size(max = 10)
    @Column(name = "num_crmv", nullable = false, unique = true, length = 10)
    @Schema(
            description = "Número do CRMV",
            example = "12345"
    )
    private String crmv;

    @NotBlank
    @Size(max = 80)
    @Column(name = "ds_especialidade", nullable = false, length = 80)
    @Schema(
            description = "Especialidade do veterinário",
            example = "Cardiologia"
    )
    private String especialidade;

    @PositiveOrZero
    @Column(name = "qtd_anos_experiencia")
    @Schema(
            description = "Quantidade de anos de experiência",
            example = "10"
    )
    private int anosExperiencia;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ds_disponibilidade", nullable = false, length = 100)
    @Schema(
            description = "Disponibilidade de atendimento",
            example = "Segunda a Sexta"
    )
    private String disponibilidade;

    @NotBlank
    @Size(max = 100)
    @Column(name = "tp_servico", nullable = false, length = 100)
    @Schema(
            description = "Tipo de serviço prestado",
            example = "Consulta"
    )
    private String tipoServico;
}