package br.com.chupinvet.chupinvet.security;

import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.model.Veterinario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long idUsuario;
    private final String nomeUsuario;
    private final String email;

    @JsonIgnore
    private final String senha;

    private final Long idResponsavel;
    private final Long idVeterinario;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long idUsuario, String nomeUsuario, String email, String senha,
                           Long idResponsavel, Long idVeterinario,
                           Collection<? extends GrantedAuthority> authorities) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.idResponsavel = idResponsavel;
        this.idVeterinario = idVeterinario;
        this.authorities = authorities;
    }

    public static UserDetailsImpl fromResponsavel(Responsavel responsavel) {
        Usuario usuario = responsavel.getUsuario();
        return new UserDetailsImpl(
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getSenha(),
                responsavel.getIdResponsavel(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))
        );
    }

    public static UserDetailsImpl fromVeterinario(Veterinario veterinario) {
        Usuario usuario = veterinario.getUsuario();
        return new UserDetailsImpl(
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getSenha(),
                null,
                veterinario.getIdVeterinario(),
                List.of(new SimpleGrantedAuthority("ROLE_VETERINARIO"))
        );
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Long getIdResponsavel() {
        return idResponsavel;
    }

    public Long getIdVeterinario() {
        return idVeterinario;
    }

    public boolean isResponsavel() {
        return idResponsavel != null;
    }

    public boolean isVeterinario() {
        return idVeterinario != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * O "username" do Spring Security aqui é o e-mail — é o que usamos
     * para login (não existe campo "username" separado no nosso domínio).
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}