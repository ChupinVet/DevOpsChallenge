package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.dto.ResponsavelResponseDTO;
import br.com.chupinvet.chupinvet.exception.DadoDuplicadoException;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    public ResponsavelResponseDTO cadastrar(ResponsavelRequestDTO dto) {
        if (responsavelRepository.findByEmail(dto.email()).isPresent()) {
            throw new DadoDuplicadoException("E-mail já cadastrado");
        }

        if (responsavelRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new DadoDuplicadoException("CPF já cadastrado");
        }
        Responsavel responsavel = new Responsavel();

        responsavel.setNomeUsuario(dto.nomeUsuario());
        responsavel.setEmail(dto.email());
        responsavel.setSenha(dto.senha());
        responsavel.setCpf(dto.cpf());
        responsavel.setEstado(dto.estado());
        responsavel.setCidade(dto.cidade());
        responsavel.setTelefone(dto.telefone());
        responsavel.setDataNascimento(dto.dataNascimento());
        responsavel.setGenero(dto.genero());
        responsavel.setTipoResidencia(dto.tipoResidencia());
        responsavel.setTelefoneSecundario(dto.telefoneSecundario());

        Responsavel responsavelSalvo = responsavelRepository.save(responsavel);

        return toResponseDTO(responsavelSalvo);
    }

    public Page<ResponsavelResponseDTO> listar(Pageable pageable) {
        return responsavelRepository
                .findAll(pageable)
                .map(this::toResponseDTO);
    }

    public ResponsavelResponseDTO buscarPorId(Long id) {
        Responsavel responsavel = responsavelRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));

        return toResponseDTO(responsavel);
    }

    private ResponsavelResponseDTO toResponseDTO(Responsavel responsavel) {
        return new ResponsavelResponseDTO(
                responsavel.getIdUsuario(),
                responsavel.getNomeUsuario(),
                responsavel.getEmail(),
                responsavel.getCpf(),
                responsavel.getEstado(),
                responsavel.getCidade(),
                responsavel.getTelefone(),
                responsavel.getDataNascimento(),
                responsavel.getGenero(),
                responsavel.getTipoResidencia(),
                responsavel.getTelefoneSecundario()
        );
    }
    public ResponsavelResponseDTO atualizar(Long id, ResponsavelRequestDTO dto) {
        responsavelRepository.findByEmail(dto.email())
                .ifPresent(responsavelExistente -> {
                    if (!responsavelExistente.getIdUsuario().equals(id)) {
                        throw new DadoDuplicadoException("E-mail já cadastrado");
                    }
                });

        responsavelRepository.findByCpf(dto.cpf())
                .ifPresent(responsavelExistente -> {
                    if (!responsavelExistente.getIdUsuario().equals(id)) {
                        throw new DadoDuplicadoException("CPF já cadastrado");
                    }
                });
        Responsavel responsavel = responsavelRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));

        responsavel.setNomeUsuario(dto.nomeUsuario());
        responsavel.setEmail(dto.email());
        responsavel.setSenha(dto.senha());
        responsavel.setCpf(dto.cpf());
        responsavel.setEstado(dto.estado());
        responsavel.setCidade(dto.cidade());
        responsavel.setTelefone(dto.telefone());
        responsavel.setDataNascimento(dto.dataNascimento());
        responsavel.setGenero(dto.genero());
        responsavel.setTipoResidencia(dto.tipoResidencia());
        responsavel.setTelefoneSecundario(dto.telefoneSecundario());

        Responsavel responsavelAtualizado = responsavelRepository.save(responsavel);
        return toResponseDTO(responsavelAtualizado);
    }
    public void deletar(Long id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
        responsavelRepository.delete(responsavel);
    }
}