package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.PetRequestDTO;
import br.com.chupinvet.chupinvet.dto.PetResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Pet;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.repository.PetRepository;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.support.DadosDeTeste;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private ResponsavelRepository responsavelRepository;

    @InjectMocks
    private PetService petService;

    private final Pageable pageable = PageRequest.of(0, 10);

    @AfterEach
    void tearDown() {
        DadosDeTeste.deslogar();
    }

    @Test
    @DisplayName("Deve cadastrar o pet com o dono vindo do token, nunca do corpo da requisição")
    void cadastrar_DeveUsarResponsavelLogadoComoDono() {
        // Arrange
        DadosDeTeste.logarComoResponsavel(10L);
        Responsavel dono = DadosDeTeste.responsavel(10L);
        when(responsavelRepository.findById(10L)).thenReturn(Optional.of(dono));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        PetRequestDTO dto = new PetRequestDTO("Thor", "Cachorro", "Vira-lata", 3, new BigDecimal("12.40"));

        // Act
        PetResponseDTO resultado = petService.cadastrar(dto);

        // Assert
        ArgumentCaptor<Pet> petSalvo = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(petSalvo.capture());
        assertThat(petSalvo.getValue().getResponsavel().getIdResponsavel()).isEqualTo(10L);
        assertThat(resultado.idResponsavel()).isEqualTo(10L);
        assertThat(resultado.nomePet()).isEqualTo("Thor");
    }

    @Test
    @DisplayName("Deve lançar 'não encontrado' quando o pet não existe")
    void buscarPorId_QuandoPetNaoExiste_DeveLancarRecursoNaoEncontrado() {
        DadosDeTeste.logarComoResponsavel(10L);
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Pet não encontrado");
    }

    @Test
    @DisplayName("Responsável não pode ver o pet de outro responsável")
    void buscarPorId_QuandoPetEDeOutroResponsavel_DeveNegarAcesso() {
        DadosDeTeste.logarComoResponsavel(10L);
        when(petRepository.findById(1L)).thenReturn(Optional.of(DadosDeTeste.pet(1L, DadosDeTeste.responsavel(20L))));

        assertThatThrownBy(() -> petService.buscarPorId(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Veterinário pode ver o pet de qualquer responsável")
    void buscarPorId_QuandoVeterinario_DevePermitir() {
        DadosDeTeste.logarComoVeterinario(30L);
        when(petRepository.findById(1L)).thenReturn(Optional.of(DadosDeTeste.pet(1L, DadosDeTeste.responsavel(20L))));

        PetResponseDTO resultado = petService.buscarPorId(1L);

        assertThat(resultado.idPet()).isEqualTo(1L);
        assertThat(resultado.idResponsavel()).isEqualTo(20L);
    }

    @Test
    @DisplayName("Responsável lista somente os próprios pets")
    void listar_QuandoResponsavel_DeveConsultarSomenteOsProprios() {
        DadosDeTeste.logarComoResponsavel(10L);
        Page<Pet> pagina = new PageImpl<>(List.of(DadosDeTeste.pet(1L, DadosDeTeste.responsavel(10L))));
        when(petRepository.findByResponsavel_IdResponsavel(10L, pageable)).thenReturn(pagina);

        Page<PetResponseDTO> resultado = petService.listar(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(petRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Veterinário lista os pets de todos os responsáveis")
    void listar_QuandoVeterinario_DeveConsultarTodos() {
        DadosDeTeste.logarComoVeterinario(30L);
        Page<Pet> pagina = new PageImpl<>(List.of(
                DadosDeTeste.pet(1L, DadosDeTeste.responsavel(10L)),
                DadosDeTeste.pet(2L, DadosDeTeste.responsavel(20L))));
        when(petRepository.findAll(pageable)).thenReturn(pagina);

        Page<PetResponseDTO> resultado = petService.listar(pageable);

        assertThat(resultado.getContent()).hasSize(2);
        verify(petRepository, never()).findByResponsavel_IdResponsavel(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Responsável não pode apagar o pet de outro responsável")
    void deletar_QuandoPetEDeOutroResponsavel_DeveNegarENaoApagar() {
        DadosDeTeste.logarComoResponsavel(10L);
        Pet petDeOutro = DadosDeTeste.pet(1L, DadosDeTeste.responsavel(20L));
        when(petRepository.findById(1L)).thenReturn(Optional.of(petDeOutro));

        assertThatThrownBy(() -> petService.deletar(1L))
                .isInstanceOf(AccessDeniedException.class);
        verify(petRepository, never()).delete(any(Pet.class));
    }
}