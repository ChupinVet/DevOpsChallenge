package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.dto.ResponsavelResponseDTO;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.support.DadosDeTeste;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponsavelServiceTest {

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ResponsavelService responsavelService;

    private ResponsavelRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ResponsavelRequestDTO("Ana Teste", "ana@teste.com", "senha123", "12345678901",
                "SP", "São Paulo", "11999999999", LocalDate.of(2000, 1, 1), "Feminino", "Casa", null);
    }

    @AfterEach
    void tearDown() {
        DadosDeTeste.deslogar();
    }

    @Test
    @DisplayName("Cadastro cria o usuário e o responsável ligado a ele")
    void cadastrar_DeveCriarUsuarioEResponsavel() {
        when(usuarioService.criar(dto)).thenReturn(DadosDeTeste.usuario(1L, "Ana Teste"));
        when(responsavelRepository.save(any(Responsavel.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ResponsavelResponseDTO resultado = responsavelService.cadastrar(dto);

        assertThat(resultado.idUsuario()).isEqualTo(1L);
        assertThat(resultado.genero()).isEqualTo("Feminino");
        assertThat(resultado.tipoResidencia()).isEqualTo("Casa");
    }

    @Test
    @DisplayName("Responsável não pode alterar o cadastro de outro responsável")
    void atualizar_QuandoOutroResponsavel_DeveNegarENaoAlterar() {
        DadosDeTeste.logarComoResponsavel(10L);

        assertThatThrownBy(() -> responsavelService.atualizar(20L, dto))
                .isInstanceOf(AccessDeniedException.class);
        verify(responsavelRepository, never()).save(any(Responsavel.class));
        verify(usuarioService, never()).atualizar(any(), any());
    }
}