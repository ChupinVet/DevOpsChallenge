package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.DadosUsuario;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Regras da identidade base (Usuario), compartilhadas entre o cadastro de
 * Responsável e o de Veterinário.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public Usuario criar(DadosUsuario dados) {
        validarDuplicidade(dados.email(), dados.cpf(), null);
        Usuario usuario = new Usuario();
        aplicarDados(usuario, dados);
        return usuarioRepository.save(usuario);
    }

    public void atualizar(Usuario usuario, DadosUsuario dados) {
        validarDuplicidade(dados.email(), dados.cpf(), usuario.getIdUsuario());
        aplicarDados(usuario, dados);
    }

    public void deletar(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    private void aplicarDados(Usuario usuario, DadosUsuario dados) {
        usuario.setNomeUsuario(dados.nomeUsuario());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuario.setCpf(dados.cpf());
        usuario.setEstado(dados.estado());
        usuario.setCidade(dados.cidade());
        usuario.setTelefone(dados.telefone());
    }

    private void validarDuplicidade(String email, String cpf, Long idUsuarioAtual) {
        usuarioRepository.findByEmailIgnoreCase(email).ifPresent(usuarioExistente -> {
            if (!usuarioExistente.getIdUsuario().equals(idUsuarioAtual)) {
                throw new DadoDuplicadoException("E-mail já cadastrado");
            }
        });
        usuarioRepository.findByCpf(cpf).ifPresent(usuarioExistente -> {
            if (!usuarioExistente.getIdUsuario().equals(idUsuarioAtual)) {
                throw new DadoDuplicadoException("CPF já cadastrado");
            }
        });
    }
}