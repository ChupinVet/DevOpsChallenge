package br.com.chupinvet.chupinvet.dto;

/**
 * Dados de identidade que todos os cadastros de usuário tem em comum.
 * ResponsavelRequestDTO e VeterinarioRequestDTO implementam esta interface,
 * o que permite ao UsuarioService tratar os dois do mesmo jeito.
 */
public interface DadosUsuario {

    String nomeUsuario();

    String email();

    String senha();

    String cpf();

    String estado();

    String cidade();

    String telefone();
}