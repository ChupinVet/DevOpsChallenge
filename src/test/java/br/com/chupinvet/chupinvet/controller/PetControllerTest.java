package br.com.chupinvet.chupinvet.controller;

import br.com.chupinvet.chupinvet.dto.PetRequestDTO;
import br.com.chupinvet.chupinvet.dto.PetResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.service.PetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa só a camada web do PetController: status HTTP, JSON e validação.
 * O service é simulado e os filtros de segurança ficam desligados aqui;
 * a segurança é testada de ponta a ponta no teste de integração.
 */
@WebMvcTest(PetController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;

    private final PetResponseDTO thor = new PetResponseDTO(
            1L, "Thor", "Cachorro", "Vira-lata", 3, new BigDecimal("12.40"), 10L, "Ana Teste");

    @Test
    @DisplayName("POST /pets - Deve cadastrar o pet e retornar 201")
    void cadastrar_QuandoDadosValidos_DeveRetornar201() throws Exception {
        when(petService.cadastrar(any(PetRequestDTO.class))).thenReturn(thor);

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomePet": "Thor", "especie": "Cachorro", "raca": "Vira-lata", "idade": 3, "peso": 12.4}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPet").value(1))
                .andExpect(jsonPath("$.nomePet").value("Thor"))
                .andExpect(jsonPath("$.idResponsavel").value(10));
    }

    @Test
    @DisplayName("POST /pets - Deve retornar 400 indicando o campo inválido")
    void cadastrar_QuandoNomeEmBranco_DeveRetornar400() throws Exception {
        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomePet": "", "especie": "Cachorro"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nomePet").exists());

        verify(petService, never()).cadastrar(any(PetRequestDTO.class));
    }

    @Test
    @DisplayName("GET /pets/{id} - Deve retornar o pet quando ele existe")
    void buscarPorId_QuandoExiste_DeveRetornar200() throws Exception {
        when(petService.buscarPorId(1L)).thenReturn(thor);

        mockMvc.perform(get("/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomePet").value("Thor"))
                .andExpect(jsonPath("$.nomeResponsavel").value("Ana Teste"));
    }

    @Test
    @DisplayName("GET /pets/{id} - Deve retornar 404 quando o pet não existe")
    void buscarPorId_QuandoNaoExiste_DeveRetornar404() throws Exception {
        when(petService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Pet não encontrado"));

        mockMvc.perform(get("/pets/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("encontrado")));
    }

    @Test
    @DisplayName("DELETE /pets/{id} - Deve apagar o pet e retornar 204")
    void deletar_DeveRetornar204() throws Exception {
        mockMvc.perform(delete("/pets/1"))
                .andExpect(status().isNoContent());

        verify(petService).deletar(1L);
    }
}