package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro ou atualização de responsável")
public record ResponsavelRequestDTO(

        @Schema(description = "Nome completo do responsável", example = "João Silva")
        @NotBlank
        @Size(max = 100)
        String nomeUsuario,

        @Schema(description = "E-mail do responsável", example = "joao@email.com")
        @NotBlank
        @Size(max = 150)
        String email,

        @Schema(description = "Senha de acesso", example = "123456")
        @NotBlank
        @Size(max = 100)
        String senha,

        @Schema(description = "CPF com 11 dígitos", example = "12345678901")
        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,

        @Schema(description = "Estado onde mora", example = "SP")
        @NotBlank
        @Size(max = 50)
        String estado,

        @Schema(description = "Cidade onde mora", example = "São Paulo")
        @NotBlank
        @Size(max = 80)
        String cidade,

        @Schema(description = "Telefone principal", example = "11999999999")
        @NotBlank
        @Size(max = 15)
        String telefone,

        @Schema(description = "Data de nascimento", example = "2000-05-10")
        @NotNull
        LocalDate dataNascimento,

        @Schema(description = "Gênero do responsável", example = "Masculino")
        @NotBlank
        @Size(max = 20)
        String genero,

        @Schema(description = "Tipo de residência", example = "Casa")
        @NotBlank
        @Size(max = 20)
        String tipoResidencia,

        @Schema(description = "Telefone secundário", example = "11888888888")
        @Size(max = 15)
        String telefoneSecundario

) {
}