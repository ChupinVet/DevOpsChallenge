package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import br.com.chupinvet.chupinvet.repository.VeterinarioRepository;
import br.com.chupinvet.chupinvet.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return responsavelRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(UserDetailsImpl::fromResponsavel)
                .map(UserDetails.class::cast)
                .or(() -> veterinarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                        .map(UserDetailsImpl::fromVeterinario))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário sem papel definido (nem Responsável nem Veterinário): " + email));
    }
}