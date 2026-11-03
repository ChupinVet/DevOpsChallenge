package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.ResponsavelRequestDTO;
import br.com.chupinvet.chupinvet.dto.ResponsavelResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.model.Usuario;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    private final UsuarioService usuarioService;

    @Transactional
    public ResponsavelResponseDTO cadastrar(ResponsavelRequestDTO dto) {
        Usuario usuario = usuarioService.criar(dto);

        Responsavel responsavel = new Responsavel();
        responsavel.setUsuario(usuario);
        aplicarDados(responsavel, dto);

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
        SecurityUtils.validarAcessoLeitura(id);
        return toResponseDTO(responsavel);
    }

    @Transactional
    public ResponsavelResponseDTO atualizar(Long id, ResponsavelRequestDTO dto) {
        SecurityUtils.validarPosseResponsavel(id);
        Responsavel responsavel = buscarOuFalhar(id);

        usuarioService.atualizar(responsavel.getUsuario(), dto);
        aplicarDados(responsavel, dto);

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
        usuarioService.deletar(usuario);
    }

    private Responsavel buscarOuFalhar(Long id) {
        return responsavelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
    }

    private void aplicarDados(Responsavel responsavel, ResponsavelRequestDTO dto) {
        responsavel.setDataNascimento(dto.dataNascimento());
        responsavel.setGenero(dto.genero());
        responsavel.setTipoResidencia(dto.tipoResidencia());
        responsavel.setTelefoneSecundario(dto.telefoneSecundario());
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