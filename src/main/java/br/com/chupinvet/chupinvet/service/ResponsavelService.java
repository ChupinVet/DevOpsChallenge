package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.dto.ResponsavelResponseDTO;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import br.com.chupinvet.chupinvet.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ResponsavelResponseDTO cadastrar(ResponsavelRequestDTO dto) {
        validarDuplicidade(dto.email(), dto.cpf(), null);

        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setCpf(dto.cpf());
        usuario.setEstado(dto.estado());
        usuario.setCidade(dto.cidade());
        usuario.setTelefone(dto.telefone());
        usuario = usuarioRepository.save(usuario);

        Responsavel responsavel = new Responsavel();
        responsavel.setUsuario(usuario);
        responsavel.setDataNascimento(dto.dataNascimento());
        responsavel.setGenero(dto.genero());
        responsavel.setTipoResidencia(dto.tipoResidencia());
        responsavel.setTelefoneSecundario(dto.telefoneSecundario());

        Responsavel responsavelSalvo = responsavelRepository.save(responsavel);
        return toResponseDTO(responsavelSalvo);
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelResponseDTO> listar(Pageable pageable) {
        return responsavelRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ResponsavelResponseDTO buscarPorId(Long id) {
        Responsavel responsavel = buscarOuFalhar(id);

        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            SecurityUtils.validarPosseResponsavel(id);
        }
        return toResponseDTO(responsavel);
    }

    @Transactional
    public ResponsavelResponseDTO atualizar(Long id, ResponsavelRequestDTO dto) {
        SecurityUtils.validarPosseResponsavel(id);
        Responsavel responsavel = buscarOuFalhar(id);
        validarDuplicidade(dto.email(), dto.cpf(), responsavel.getUsuario().getIdUsuario());

        Usuario usuario = responsavel.getUsuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setCpf(dto.cpf());
        usuario.setEstado(dto.estado());
        usuario.setCidade(dto.cidade());
        usuario.setTelefone(dto.telefone());

        responsavel.setDataNascimento(dto.dataNascimento());
        responsavel.setGenero(dto.genero());
        responsavel.setTipoResidencia(dto.tipoResidencia());
        responsavel.setTelefoneSecundario(dto.telefoneSecundario());

        Responsavel responsavelAtualizado = responsavelRepository.save(responsavel);
        return toResponseDTO(responsavelAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtils.validarPosseResponsavel(id);
        Responsavel responsavel = buscarOuFalhar(id);
        Usuario usuario = responsavel.getUsuario();
        // Deleta o filho antes do pai por causa da FK (Responsavel referencia Usuario)
        responsavelRepository.delete(responsavel);
        usuarioRepository.delete(usuario);
    }

    private Responsavel buscarOuFalhar(Long id) {
        return responsavelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
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

    private ResponsavelResponseDTO toResponseDTO(Responsavel responsavel) {
        Usuario usuario = responsavel.getUsuario();
        return new ResponsavelResponseDTO(
                responsavel.getIdResponsavel(),
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getEstado(),
                usuario.getCidade(),
                usuario.getTelefone(),
                responsavel.getDataNascimento(),
                responsavel.getGenero(),
                responsavel.getTipoResidencia(),
                responsavel.getTelefoneSecundario()
        );
    }
}