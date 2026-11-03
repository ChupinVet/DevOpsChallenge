package br.com.chupinvet.chupinvet.integration;

import br.com.chupinvet.chupinvet.repository.DiarioRepository;
import br.com.chupinvet.chupinvet.repository.PetRepository;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import br.com.chupinvet.chupinvet.repository.VeterinarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa de ponta a ponta, com a segurança real e um banco H2 em memória,
 * os dois fluxos principais da API: autenticação (cadastro, login, JWT,
 * papel) e controle de acesso por posse.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacaoEPosseIntegrationTest {

    private static final String SENHA = "senha123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBanco() {
        diarioRepository.deleteAll();
        petRepository.deleteAll();
        responsavelRepository.deleteAll();
        veterinarioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Fluxo completo: cadastro, login e pet criado com o dono vindo do token")
    void cadastroLoginECriacaoDePet_DeveUsarDonoDoToken() throws Exception {
        long idResponsavel = cadastrarResponsavel("ana@teste.com", "11111111111");
        String token = login("ana@teste.com");

        mockMvc.perform(post("/pets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idResponsavel").value(idResponsavel));
    }

    @Test
    @DisplayName("Rota protegida sem token deve retornar 401")
    void rotaProtegida_SemToken_DeveRetornar401() throws Exception {
        mockMvc.perform(get("/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Cadastro com e-mail já usado deve retornar 409")
    void cadastro_ComEmailRepetido_DeveRetornar409() throws Exception {
        cadastrarResponsavel("ana@teste.com", "11111111111");

        mockMvc.perform(post("/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responsavelJson("ana@teste.com", "22222222222")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Responsável não acessa o pet de outro responsável (403)")
    void responsavel_AcessandoPetDeOutro_DeveRetornar403() throws Exception {
        cadastrarResponsavel("ana@teste.com", "11111111111");
        long idPet = criarPet(login("ana@teste.com"));
        cadastrarResponsavel("bruno@teste.com", "22222222222");
        String tokenBruno = login("bruno@teste.com");

        mockMvc.perform(get("/pets/" + idPet)
                        .header("Authorization", "Bearer " + tokenBruno))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Veterinário lê o pet de qualquer responsável, mas não pode cadastrar pet")
    void veterinario_LePetDeQualquerUmMasNaoCadastra() throws Exception {
        cadastrarResponsavel("ana@teste.com", "11111111111");
        long idPet = criarPet(login("ana@teste.com"));
        cadastrarVeterinario("carla.vet@teste.com", "33333333333");
        String tokenVet = login("carla.vet@teste.com");

        mockMvc.perform(get("/pets/" + idPet)
                        .header("Authorization", "Bearer " + tokenVet))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPet").value(idPet));

        mockMvc.perform(post("/pets")
                        .header("Authorization", "Bearer " + tokenVet)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON))
                .andExpect(status().isForbidden());
    }

    // ---- passos reaproveitados pelos testes ----

    private static final String PET_JSON = """
            {"nomePet": "Thor", "especie": "Cachorro", "raca": "Vira-lata", "idade": 3, "peso": 12.4}
            """;

    private static String responsavelJson(String email, String cpf) {
        return """
                {"nomeUsuario": "Responsavel Teste", "email": "%s", "senha": "%s",
                 "cpf": "%s", "telefone": "11999999999"}
                """.formatted(email, SENHA, cpf);
    }

    private long cadastrarResponsavel(String email, String cpf) throws Exception {
        String resposta = mockMvc.perform(post("/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responsavelJson(email, cpf)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return lerId(resposta, "$.idResponsavel");
    }

    private void cadastrarVeterinario(String email, String cpf) throws Exception {
        mockMvc.perform(post("/veterinarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeUsuario": "Vet Teste", "email": "%s", "senha": "%s", "cpf": "%s",
                                 "telefone": "11988888888", "crmv": "SP12345", "especialidade": "Clinica Geral"}
                                """.formatted(email, SENHA, cpf)))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String resposta = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, SENHA)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }

    private long criarPet(String token) throws Exception {
        String resposta = mockMvc.perform(post("/pets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PET_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return lerId(resposta, "$.idPet");
    }

    private static long lerId(String json, String caminho) {
        Number id = JsonPath.read(json, caminho);
        return id.longValue();
    }
}