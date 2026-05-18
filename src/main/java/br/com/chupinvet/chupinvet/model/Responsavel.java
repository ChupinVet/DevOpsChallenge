package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "responsavel")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("RESPONSAVEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(
        name = "Responsavel",
        description = "Representa um responsável pelos pets"
)
public class Responsavel extends Usuario {

    @NotNull
    @Column(name = "dt_nascimento", nullable = false)
    @Schema(
            description = "Data de nascimento do responsável",
            example = "2000-05-10"
    )
    private LocalDate dataNascimento;

    @NotBlank
    @Size(max = 20)
    @Column(name = "tp_genero", nullable = false, length = 20)
    @Schema(
            description = "Gênero do responsável",
            example = "Masculino"
    )
    private String genero;

    @NotBlank
    @Size(max = 20)
    @Column(name = "tp_residencia", nullable = false, length = 20)
    @Schema(
            description = "Tipo de residência",
            example = "Casa"
    )
    private String tipoResidencia;

    @Size(max = 15)
    @Column(name = "num_telefone_secundario", length = 15)
    @Schema(
            description = "Telefone secundário",
            example = "11888888888"
    )
    private String telefoneSecundario;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();
}