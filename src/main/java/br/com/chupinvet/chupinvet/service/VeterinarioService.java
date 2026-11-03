package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.VeterinarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.VeterinarioResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.model.Veterinario;
import br.com.chupinvet.chupinvet.repository.VeterinarioRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    private final UsuarioService usuarioService;

    @Transactional
    public VeterinarioResponseDTO cadastrar(VeterinarioRequestDTO dto) {
        Usuario usuario = usuarioService.criar(dto);

        Veterinario veterinario = new Veterinario();
        veterinario.setUsuario(usuario);
        aplicarDados(veterinario, dto);

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

        usuarioService.atualizar(veterinario.getUsuario(), dto);
        aplicarDados(veterinario, dto);

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinario);
        return toResponseDTO(veterinarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtils.validarPosseVeterinario(id);
        Veterinario veterinario = buscarOuFalhar(id);
        Usuario usuario = veterinario.getUsuario();
        // Deleta o filho antes do pai por causa da FK (Veterinario referencia Usuario)
        veterinarioRepository.delete(veterinario);
        usuarioService.deletar(usuario);
    }

    private Veterinario buscarOuFalhar(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));
    }

    private void aplicarDados(Veterinario veterinario, VeterinarioRequestDTO dto) {
        veterinario.setCrmv(dto.crmv());
        veterinario.setEspecialidade(dto.especialidade());
        veterinario.setAnosExperiencia(dto.anosExperiencia());
        veterinario.setDisponibilidade(dto.disponibilidade());
        veterinario.setTipoServico(dto.tipoServico());
        veterinario.setNomeClinica(dto.nomeClinica());
        veterinario.setBio(dto.bio());
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