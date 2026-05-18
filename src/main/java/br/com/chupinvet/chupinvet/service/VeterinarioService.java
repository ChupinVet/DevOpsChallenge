package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.VeterinarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.VeterinarioResponseDTO;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Veterinario;
import br.com.chupinvet.chupinvet.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    public VeterinarioResponseDTO cadastrar(VeterinarioRequestDTO dto) {
        if (veterinarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new DadoDuplicadoException("E-mail já cadastrado");
        }

        if (veterinarioRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new DadoDuplicadoException("CPF já cadastrado");
        }
        Veterinario veterinario = new Veterinario();

        veterinario.setNomeUsuario(dto.nomeUsuario());
        veterinario.setEmail(dto.email());
        veterinario.setSenha(dto.senha());
        veterinario.setCpf(dto.cpf());
        veterinario.setEstado(dto.estado());
        veterinario.setCidade(dto.cidade());
        veterinario.setTelefone(dto.telefone());
        veterinario.setCrmv(dto.crmv());
        veterinario.setEspecialidade(dto.especialidade());
        veterinario.setAnosExperiencia(dto.anosExperiencia());
        veterinario.setDisponibilidade(dto.disponibilidade());
        veterinario.setTipoServico(dto.tipoServico());

        Veterinario veterinarioSalvo = veterinarioRepository.save(veterinario);

        return toResponseDTO(veterinarioSalvo);
    }

    public Page<VeterinarioResponseDTO> listar(Pageable pageable) {

        return veterinarioRepository
                .findAll(pageable)
                .map(this::toResponseDTO);
    }

    public VeterinarioResponseDTO buscarPorId(Long id) {

        Veterinario veterinario = veterinarioRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));

        return toResponseDTO(veterinario);
    }

    public Page<VeterinarioResponseDTO> buscarPorEspecialidade(
            String especialidade,
            Pageable pageable
    ) {

        return veterinarioRepository
                .findByEspecialidadeContainingIgnoreCase(especialidade, pageable)
                .map(this::toResponseDTO);
    }

    public Page<VeterinarioResponseDTO> buscarPorTipoServico(
            String tipoServico,
            Pageable pageable
    ) {

        return veterinarioRepository
                .findByTipoServicoContainingIgnoreCase(tipoServico, pageable)
                .map(this::toResponseDTO);
    }

    private VeterinarioResponseDTO toResponseDTO(Veterinario veterinario) {

        return new VeterinarioResponseDTO(
                veterinario.getIdUsuario(),
                veterinario.getNomeUsuario(),
                veterinario.getEmail(),
                veterinario.getCpf(),
                veterinario.getEstado(),
                veterinario.getCidade(),
                veterinario.getTelefone(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade(),
                veterinario.getAnosExperiencia(),
                veterinario.getDisponibilidade(),
                veterinario.getTipoServico()
        );
    }
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO dto) {

        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));

        veterinarioRepository.findByEmail(dto.email())
                .ifPresent(veterinarioExistente -> {
                    if (!veterinarioExistente.getIdUsuario().equals(id)) {
                        throw new DadoDuplicadoException("E-mail já cadastrado");
                    }
                });

        veterinarioRepository.findByCpf(dto.cpf())
                .ifPresent(veterinarioExistente -> {
                    if (!veterinarioExistente.getIdUsuario().equals(id)) {
                        throw new DadoDuplicadoException("CPF já cadastrado");
                    }
                });
        veterinario.setNomeUsuario(dto.nomeUsuario());
        veterinario.setEmail(dto.email());
        veterinario.setSenha(dto.senha());
        veterinario.setCpf(dto.cpf());
        veterinario.setEstado(dto.estado());
        veterinario.setCidade(dto.cidade());
        veterinario.setTelefone(dto.telefone());

        veterinario.setCrmv(dto.crmv());
        veterinario.setEspecialidade(dto.especialidade());
        veterinario.setAnosExperiencia(dto.anosExperiencia());
        veterinario.setDisponibilidade(dto.disponibilidade());
        veterinario.setTipoServico(dto.tipoServico());

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinario);

        return toResponseDTO(veterinarioAtualizado);
    }
    public void deletar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado"));

        veterinarioRepository.delete(veterinario);
    }
}