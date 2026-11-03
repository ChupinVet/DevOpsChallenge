package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.DiarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.DiarioResponseDTO;
import br.com.chupinvet.chupinvet.model.Diario;
import br.com.chupinvet.chupinvet.repository.DiarioRepository;
import br.com.chupinvet.chupinvet.repository.PetRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiarioServiceTest {

    @Mock
    private DiarioRepository diarioRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private DiarioService diarioService;

    private DiarioRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DiarioRequestDTO(LocalDate.of(2026, 10, 30), "Feliz", "Normal", "Bebeu bem",
                null, null, null, new BigDecimal("12.50"), 1L);
    }

    @AfterEach
    void tearDown() {
        DadosDeTeste.deslogar();
    }

    @Test
    @DisplayName("Dono do pet consegue registrar um diário")
    void cadastrar_QuandoResponsavelEDono_DeveSalvar() {
        DadosDeTeste.logarComoResponsavel(10L);
        when(petRepository.findById(1L)).thenReturn(Optional.of(DadosDeTeste.pet(1L, DadosDeTeste.responsavel(10L))));
        when(diarioRepository.save(any(Diario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        DiarioResponseDTO resultado = diarioService.cadastrar(dto);

        assertThat(resultado.humor()).isEqualTo("Feliz");
        assertThat(resultado.idPet()).isEqualTo(1L);
        verify(diarioRepository).save(any(Diario.class));
    }

    @Test
    @DisplayName("Responsável não pode registrar diário para o pet de outro responsável")
    void cadastrar_QuandoPetEDeOutroResponsavel_DeveNegarENaoSalvar() {
        DadosDeTeste.logarComoResponsavel(10L);
        when(petRepository.findById(1L)).thenReturn(Optional.of(DadosDeTeste.pet(1L, DadosDeTeste.responsavel(20L))));

        assertThatThrownBy(() -> diarioService.cadastrar(dto))
                .isInstanceOf(AccessDeniedException.class);
        verify(diarioRepository, never()).save(any(Diario.class));
    }
}