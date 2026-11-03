package br.com.chupinvet.chupinvet.support;

import br.com.chupinvet.chupinvet.model.Pet;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.model.Veterinario;
import br.com.chupinvet.chupinvet.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Objetos e usuário logado usados pelos testes unitários dos services.
 * Fica num lugar só para os testes não repetirem a mesma montagem.
 */
public final class DadosDeTeste {

    private DadosDeTeste() {
    }

    public static void logarComoResponsavel(Long idResponsavel) {
        logar(new UserDetailsImpl(100L, "Responsável Teste", "resp@teste.com", "hash",
                idResponsavel, null, List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))));
    }

    public static void logarComoVeterinario(Long idVeterinario) {
        logar(new UserDetailsImpl(200L, "Veterinário Teste", "vet@teste.com", "hash",
                null, idVeterinario, List.of(new SimpleGrantedAuthority("ROLE_VETERINARIO"))));
    }

    public static void deslogar() {
        SecurityContextHolder.clearContext();
    }

    public static Usuario usuario(Long idUsuario, String nome) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNomeUsuario(nome);
        usuario.setEmail(nome.toLowerCase().replace(" ", ".") + "@teste.com");
        return usuario;
    }

    public static Responsavel responsavel(Long idResponsavel) {
        Responsavel responsavel = new Responsavel();
        responsavel.setIdResponsavel(idResponsavel);
        responsavel.setUsuario(usuario(idResponsavel, "Responsavel " + idResponsavel));
        return responsavel;
    }

    public static Veterinario veterinario(Long idVeterinario) {
        Veterinario veterinario = new Veterinario();
        veterinario.setIdVeterinario(idVeterinario);
        veterinario.setUsuario(usuario(idVeterinario, "Veterinario " + idVeterinario));
        return veterinario;
    }

    public static Pet pet(Long idPet, Responsavel dono) {
        Pet pet = new Pet();
        pet.setIdPet(idPet);
        pet.setNomePet("Thor");
        pet.setEspecie("Cachorro");
        pet.setRaca("Vira-lata");
        pet.setIdade(3);
        pet.setPeso(new BigDecimal("12.40"));
        pet.setResponsavel(dono);
        return pet;
    }

    private static void logar(UserDetailsImpl usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }
}