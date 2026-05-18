package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.PetRequestDTO;
import br.com.chupinvet.chupinvet.dto.PetResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Pet;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.repository.PetRepository;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Transactional
    public PetResponseDTO cadastrar(PetRequestDTO dto) {

        Responsavel responsavel = responsavelRepository.findById(dto.idResponsavel())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));

        Pet pet = new Pet();
        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setDataNascimento(dto.dataNascimento());
        pet.setPeso(dto.peso());
        pet.setResponsavel(responsavel);

        Pet petSalvo = petRepository.save(pet);

        return toResponseDTO(petSalvo);
    }
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listar(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }
    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));

        return toResponseDTO(pet);
    }
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nomePet, Pageable pageable) {
        return petRepository.findByNomePetContainingIgnoreCase(nomePet, pageable)
                .map(this::toResponseDTO);
    }
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorEspecie(String especie, Pageable pageable) {
        return petRepository.findByEspecieContainingIgnoreCase(especie, pageable)
                .map(this::toResponseDTO);
    }
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorRaca(String raca, Pageable pageable) {
        return petRepository.findByRacaContainingIgnoreCase(raca, pageable)
                .map(this::toResponseDTO);
    }

    private PetResponseDTO toResponseDTO(Pet pet) {
        return new PetResponseDTO(
                pet.getIdPet(),
                pet.getNomePet(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getPeso(),
                pet.getResponsavel().getIdUsuario(),
                pet.getResponsavel().getNomeUsuario()
        );
    }
    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));

        Responsavel responsavel = responsavelRepository.findById(dto.idResponsavel())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setDataNascimento(dto.dataNascimento());
        pet.setPeso(dto.peso());
        pet.setResponsavel(responsavel);

        Pet petAtualizado = petRepository.save(pet);
        return toResponseDTO(petAtualizado);
    }
    @Transactional
    public void deletar(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
        petRepository.delete(pet);
    }
}