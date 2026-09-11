package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// Representa um responsável por pets.

@Entity
@Table(name = "responsavel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idResponsavel")
@Schema(
        name = "Responsavel",
        description = "Representa um responsável pelos pets, associado a um Usuario"
)
public class Responsavel {

    @Id
    @SequenceGenerator(name = "responsavel_seq", sequenceName = "seq_responsavel", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "responsavel_seq")
    @Column(name = "id_responsavel")
    @Schema(
            description = "ID único do responsável",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idResponsavel;

    @Column(name = "dt_nascimento")
    @Schema(description = "Data de nascimento do responsável", example = "2000-05-10")
    private LocalDate dataNascimento;

    @Size(max = 20)
    @Column(name = "tp_genero", length = 20)
    @Schema(description = "Gênero do responsável", example = "Masculino")
    private String genero;

    @Size(max = 20)
    @Column(name = "tp_residencia", length = 20)
    @Schema(description = "Tipo de residência", example = "Casa")
    private String tipoResidencia;

    @Size(max = 15)
    @Column(name = "num_telefone_secundario", length = 15)
    @Schema(description = "Telefone secundário", example = "11888888888")
    private String telefoneSecundario;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Usuario_id_usuario", nullable = false, unique = true)
    @Schema(hidden = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Schema(hidden = true)
    private List<Pet> pets = new ArrayList<>();
}