package br.com.chupinvet.chupinvet.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserDetailsImpl getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    /**
     * Garante que o Responsável logado só mexe nos próprios dados.
     */
    public static void validarPosseResponsavel(Long idResponsavelDoRecurso) {
        UserDetailsImpl usuarioLogado = getUsuarioLogado();
        if (!usuarioLogado.isResponsavel() || !usuarioLogado.getIdResponsavel().equals(idResponsavelDoRecurso)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }
    }

    /**
     * Regra de leitura: Veterinário pode ler o recurso de qualquer
     * Responsável; o Responsável só lê o que é dele.
     */
    public static void validarAcessoLeitura(Long idResponsavelDoRecurso) {
        if (getUsuarioLogado().isResponsavel()) {
            validarPosseResponsavel(idResponsavelDoRecurso);
        }
    }

    /**
     * Garante que o Veterinário logado só mexe nos próprios dados.
     */
    public static void validarPosseVeterinario(Long idVeterinarioDoRecurso) {
        UserDetailsImpl usuarioLogado = getUsuarioLogado();
        if (!usuarioLogado.isVeterinario() || !usuarioLogado.getIdVeterinario().equals(idVeterinarioDoRecurso)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }
    }
}