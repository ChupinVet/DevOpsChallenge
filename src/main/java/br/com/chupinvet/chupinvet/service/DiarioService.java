package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.DiarioRequestDTO;
import br.com.chupinvet.chupinvet.dto.DiarioResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Diario;
import br.com.chupinvet.chupinvet.model.Pet;
import br.com.chupinvet.chupinvet.repository.DiarioRepository;
import br.com.chupinvet.chupinvet.repository.PetRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiarioService {

    private final DiarioRepository diarioRepository;

    private final PetRepository petRepository;

    @Transactional
    public DiarioResponseDTO cadastrar(DiarioRequestDTO dto) {
        Pet pet = buscarPetOuFalhar(dto.idPet());
        // Só o Responsável dono do pet pode registrar um diário para ele.
        SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());

        Diario diario = new Diario();
        diario.setPet(pet);
        aplicarDados(diario, dto);

        Diario diarioSalvo = diarioRepository.save(diario);
        return toResponseDTO(diarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<DiarioResponseDTO> listarPorPet(Long idPet, Pageable pageable) {
        Pet pet = buscarPetOuFalhar(idPet);
        SecurityUtils.validarAcessoLeitura(pet.getResponsavel().getIdResponsavel());
        return diarioRepository.findByPet_IdPetOrderByDataRegistroDesc(idPet, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public DiarioResponseDTO buscarPorId(Long id) {
        Diario diario = buscarOuFalhar(id);
        SecurityUtils.validarAcessoLeitura(diario.getPet().getResponsavel().getIdResponsavel());
        return toResponseDTO(diario);
    }

    @Transactional
    public DiarioResponseDTO atualizar(Long id, DiarioRequestDTO dto) {
        Diario diario = buscarOuFalhar(id);
        // O pet de um registro de diário não muda numa atualização
        // (dto.idPet() é ignorado aqui de propósito).
        SecurityUtils.validarPosseResponsavel(diario.getPet().getResponsavel().getIdResponsavel());

        aplicarDados(diario, dto);

        Diario diarioAtualizado = diarioRepository.save(diario);
        return toResponseDTO(diarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Diario diario = buscarOuFalhar(id);
        SecurityUtils.validarPosseResponsavel(diario.getPet().getResponsavel().getIdResponsavel());
        diarioRepository.delete(diario);
    }

    private Diario buscarOuFalhar(Long id) {
        return diarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de diário não encontrado"));
    }

    private Pet buscarPetOuFalhar(Long idPet) {
        return petRepository.findById(idPet)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
    }

    private void aplicarDados(Diario diario, DiarioRequestDTO dto) {
        diario.setDataRegistro(dto.dataRegistro());
        diario.setHumor(dto.humor());
        diario.setAlimentacao(dto.alimentacao());
        diario.setConsumoAgua(dto.consumoAgua());
        diario.setComportamento(dto.comportamento());
        diario.setSintomas(dto.sintomas());
        diario.setObservacoes(dto.observacoes());
        diario.setPesoRegistrado(dto.pesoRegistrado());
    }

    private DiarioResponseDTO toResponseDTO(Diario diario) {
        return new DiarioResponseDTO(
                diario.getIdDiario(),
                diario.getDataRegistro(),
                diario.getHumor(),
                diario.getAlimentacao(),
                diario.getConsumoAgua(),
                diario.getComportamento(),
                diario.getSintomas(),
                diario.getObservacoes(),
                diario.getPesoRegistrado(),
                diario.getPet().getIdPet(),
                diario.getPet().getNomePet()
        );
    }
}