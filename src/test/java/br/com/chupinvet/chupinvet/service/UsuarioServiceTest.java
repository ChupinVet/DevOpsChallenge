package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import br.com.chupinvet.chupinvet.support.DadosDeTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private ResponsavelRequestDTO dados;

    @BeforeEach
    void setUp() {
        dados = new ResponsavelRequestDTO("Ana Teste", "ana@teste.com", "senha123", "12345678901",
                "SP", "São Paulo", "11999999999", LocalDate.of(2000, 1, 1), null, null, null);
    }

    @Test
    @DisplayName("Deve criar o usuário com a senha criptografada quando e-mail e CPF estão livres")
    void criar_QuandoEmailECpfLivres_DeveSalvarComSenhaCriptografada() {
        // Arrange
        when(usuarioRepository.findByEmailIgnoreCase("ana@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        // Act
        Usuario resultado = usuarioService.criar(dados);

        // Assert
        assertThat(resultado.getEmail()).isEqualTo("ana@teste.com");
        assertThat(resultado.getSenha()).isEqualTo("hash-bcrypt");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve recusar o cadastro quando o e-mail já pertence a outro usuário")
    void criar_QuandoEmailJaExiste_DeveLancarDadoDuplicado() {
        when(usuarioRepository.findByEmailIgnoreCase("ana@teste.com"))
                .thenReturn(Optional.of(DadosDeTeste.usuario(5L, "Outra Pessoa")));

        assertThatThrownBy(() -> usuarioService.criar(dados))
                .isInstanceOf(DadoDuplicadoException.class)
                .hasMessage("E-mail já cadastrado");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve recusar o cadastro quando o CPF já pertence a outro usuário")
    void criar_QuandoCpfJaExiste_DeveLancarDadoDuplicado() {
        when(usuarioRepository.findByEmailIgnoreCase("ana@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf("12345678901"))
                .thenReturn(Optional.of(DadosDeTeste.usuario(5L, "Outra Pessoa")));

        assertThatThrownBy(() -> usuarioService.criar(dados))
                .isInstanceOf(DadoDuplicadoException.class)
                .hasMessage("CPF já cadastrado");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve permitir que o próprio usuário mantenha o mesmo e-mail e CPF ao atualizar")
    void atualizar_QuandoEmailECpfSaoDoProprioUsuario_DeveAtualizar() {
        Usuario usuarioAtual = DadosDeTeste.usuario(7L, "Ana Antiga");
        when(usuarioRepository.findByEmailIgnoreCase("ana@teste.com")).thenReturn(Optional.of(usuarioAtual));
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuarioAtual));
        when(passwordEncoder.encode("senha123")).thenReturn("hash-bcrypt");

        usuarioService.atualizar(usuarioAtual, dados);

        assertThat(usuarioAtual.getNomeUsuario()).isEqualTo("Ana Teste");
        assertThat(usuarioAtual.getCidade()).isEqualTo("São Paulo");
    }
}