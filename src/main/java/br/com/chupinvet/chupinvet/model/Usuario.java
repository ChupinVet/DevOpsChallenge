package br.com.chupinvet.chupinvet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tp_usuario", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(of = "idUsuario")
@Schema(
        name = "Usuario",
        description = "Classe base para usuários do sistema"
)
public abstract class Usuario {

    @Id
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @Column(name = "id_usuario")
    @Schema(
            description = "ID único do usuário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idUsuario;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nm_usuario", nullable = false, length = 100)
    @Schema(
            description = "Nome completo do usuário",
            example = "João Silva",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    private String nomeUsuario;

    @NotBlank
    @Size(max = 150)
    @Column(name = "ds_email", nullable = false, unique = true, length = 150)
    @Schema(
            description = "E-mail do usuário",
            example = "joao@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 150
    )
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ds_senha", nullable = false, length = 100)
    @Schema(
            description = "Senha de acesso",
            example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    private String senha;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(name = "num_cpf", nullable = false, unique = true, length = 11)
    @Schema(
            description = "CPF do usuário",
            example = "12345678901",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 11,
            maxLength = 11
    )
    private String cpf;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nm_estado", nullable = false, length = 50)
    @Schema(
            description = "Estado do usuário",
            example = "SP"
    )
    private String estado;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nm_cidade", nullable = false, length = 80)
    @Schema(
            description = "Cidade do usuário",
            example = "São Paulo"
    )
    private String cidade;

    @NotBlank
    @Size(max = 15)
    @Column(name = "num_telefone", nullable = false, length = 15)
    @Schema(
            description = "Telefone principal",
            example = "11999999999"
    )
    private String telefone;

}