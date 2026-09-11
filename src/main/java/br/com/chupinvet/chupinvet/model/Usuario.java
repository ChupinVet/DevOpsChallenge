package br.com.chupinvet.chupinvet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 *
 * IMPORTANTE: diferente da Sprint 1, aqui NÃO existe herança JPA (@Inheritance).
 * O Oracle foi remodelado, agora trata Responsavel e Veterinario como tabelas
 * independentes, cada uma com seu próprio PK, associadas a Usuario por uma
 * FK 1:1 (Usuario_id_usuario), ou seja, a relação é de COMPOSIÇÃO
 * (@OneToOne em Responsavel/Veterinario apontando para Usuario), não de
 * herança.
 *
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
@Schema(
        name = "Usuario",
        description = "Identidade base de um usuário do sistema (responsável ou veterinário)"
)
public class Usuario {

    @Id
    @SequenceGenerator(name = "usuario_seq", sequenceName = "seq_usuario", allocationSize = 1)
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
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nomeUsuario;

    @NotBlank
    @Size(max = 150)
    @Column(name = "ds_email", nullable = false, length = 150)
    @Schema(description = "E-mail do usuário, usado como login", example = "joao@email.com")
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ds_senha", nullable = false, length = 100)
    @Schema(description = "Hash da senha de acesso", accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonIgnore
    private String senha;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(name = "num_cpf", nullable = false, length = 11)
    @Schema(description = "CPF com 11 dígitos", example = "12345678901")
    private String cpf;

    @Size(max = 50)
    @Column(name = "nm_estado", length = 50)
    @Schema(description = "Estado do usuário", example = "SP")
    private String estado;

    @Size(max = 80)
    @Column(name = "nm_cidade", length = 80)
    @Schema(description = "Cidade do usuário", example = "São Paulo")
    private String cidade;

    @NotBlank
    @Size(max = 15)
    @Column(name = "num_telefone", nullable = false, length = 15)
    @Schema(description = "Telefone principal", example = "11999999999")
    private String telefone;
}