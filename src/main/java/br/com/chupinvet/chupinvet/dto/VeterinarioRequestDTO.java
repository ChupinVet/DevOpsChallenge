package br.com.chupinvet.chupinvet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou atualização de veterinário")
public record VeterinarioRequestDTO(

        @Schema(description = "Nome do veterinário", example = "Carlos Mendes")
        @NotBlank
        @Size(max = 100)
        String nomeUsuario,

        @Schema(description = "E-mail do veterinário", example = "carlos@vet.com")
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

        @Schema(description = "Estado do veterinário", example = "SP")
        @NotBlank
        @Size(max = 50)
        String estado,

        @Schema(description = "Cidade do veterinário", example = "Campinas")
        @NotBlank
        @Size(max = 80)
        String cidade,

        @Schema(description = "Telefone principal", example = "11999999999")
        @NotBlank
        @Size(max = 15)
        String telefone,

        @Schema(description = "Número do CRMV", example = "12345")
        @NotBlank
        @Size(max = 10)
        String crmv,

        @Schema(description = "Especialidade do veterinário", example = "Cardiologia")
        @NotBlank
        @Size(max = 80)
        String especialidade,

        @Schema(description = "Quantidade de anos de experiência", example = "10")
        @PositiveOrZero
        Integer anosExperiencia,

        @Schema(description = "Disponibilidade de atendimento", example = "Segunda a Sexta")
        @NotBlank
        @Size(max = 100)
        String disponibilidade,

        @Schema(description = "Tipo de serviço prestado", example = "Consulta")
        @NotBlank
        @Size(max = 100)
        String tipoServico

) {
}