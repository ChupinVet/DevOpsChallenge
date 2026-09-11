package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.VeterinarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.VeterinarioResponseDTO;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.model.Veterinario;
import br.com.chupinvet.chupinvet.repository.UsuarioRepository;
import br.com.chupinvet.chupinvet.repository.VeterinarioRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public VeterinarioResponseDTO cadastrar(VeterinarioRequestDTO dto) {
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

        Veterinario veterinario = new Veterinario();
        veterinario.setUsuario(usuario);
        veterinario.setCrmv(dto.crmv());
        veterinario.setEspecialidade(dto.especialidade());
        veterinario.setAnosExperiencia(dto.anosExperiencia());
        veterinario.setDisponibilidade(dto.disponibilidade());
        veterinario.setTipoServico(dto.tipoServico());
        veterinario.setNomeClinica(dto.nomeClinica());
        veterinario.setBio(dto.bio());

        Veterinario veterinarioSalvo = veterinarioRepository.save(veterinario);
        return toResponseDTO(veterinarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> listar(Pageable pageable) {
        return veterinarioRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarOuFalhar(id));
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> buscarPorEspecialidade(String especialidade, Pageable pageable) {
        return veterinarioRepository.findByEspecialidadeContainingIgnoreCase(especialidade, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> buscarPorTipoServico(String tipoServico, Pageable pageable) {
        return veterinarioRepository.findByTipoServicoContainingIgnoreCase(tipoServico, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO dto) {
        SecurityUtils.validarPosseVeterinario(id);
        Veterinario veterinario = buscarOuFalhar(id);
        validarDuplicidade(dto.email(), dto.cpf(), veterinario.getUsuario().getIdUsuario());

        Usuario usuario = veterinario.getUsuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setCpf(dto.cpf());
        usuario.setEstado(dto.estado());
        usuario.setCidade(dto.cidade());
        usuario.setTelefone(dto.telefone());

        veterinario.setCrmv(dto.crmv());
        veterinario.setEspecialidade(dto.especialidade());
        veterinario.setAnosExperiencia(dto.anosExperiencia());
        veterinario.setDisponibilidade(dto.disponibilidade());
        veterinario.setTipoServico(dto.tipoServico());
        veterinario.setNomeClinica(dto.nomeClinica());
        veterinario.setBio(dto.bio());

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinario);
        return toResponseDTO(veterinarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtils.validarPosseVeterinario(id);
        Veterinario veterinario = buscarOuFalhar(id);
        Usuario usuario = veterinario.getUsuario();
        veterinarioRepository.delete(veterinario);
        usuarioRepository.delete(usuario);
    }

    private Veterinario buscarOuFalhar(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));
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

    private VeterinarioResponseDTO toResponseDTO(Veterinario veterinario) {
        Usuario usuario = veterinario.getUsuario();
        return new VeterinarioResponseDTO(
                veterinario.getIdVeterinario(),
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getEstado(),
                usuario.getCidade(),
                usuario.getTelefone(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade(),
                veterinario.getAnosExperiencia(),
                veterinario.getDisponibilidade(),
                veterinario.getTipoServico(),
                veterinario.getNomeClinica(),
                veterinario.getBio()
        );
    }
}