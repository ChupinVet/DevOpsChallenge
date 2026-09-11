package br.com.chupinvet.chupinvet.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Ponto único para obter o usuário autenticado e validar posse de
 * recursos nos services. Lançar AccessDeniedException aqui é
 * intencional: é a mesma exceção que o @PreAuthorize lança quando nega
 * acesso, então o ExceptionTranslationFilter do Spring Security já sabe
 * traduzir isso para 403 sem precisar de nada extra no
 * GlobalExceptionHandler.
 */
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
     * Garante que o Veterinário logado só mexe nos próprios dados.
     */
    public static void validarPosseVeterinario(Long idVeterinarioDoRecurso) {
        UserDetailsImpl usuarioLogado = getUsuarioLogado();
        if (!usuarioLogado.isVeterinario() || !usuarioLogado.getIdVeterinario().equals(idVeterinarioDoRecurso)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }
    }
}