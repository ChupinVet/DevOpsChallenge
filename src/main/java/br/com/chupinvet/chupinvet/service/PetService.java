package br.com.chupinvet.chupinvet.service;

import br.com.chupinvet.chupinvet.dto.PetRequestDTO;
import br.com.chupinvet.chupinvet.dto.PetResponseDTO;
import br.com.chupinvet.chupinvet.exception.RecursoNaoEncontradoException;
import br.com.chupinvet.chupinvet.model.Pet;
import br.com.chupinvet.chupinvet.model.Responsavel;
import br.com.chupinvet.chupinvet.repository.PetRepository;
import br.com.chupinvet.chupinvet.repository.ResponsavelRepository;
import br.com.chupinvet.chupinvet.security.SecurityUtils;
import br.com.chupinvet.chupinvet.security.UserDetailsImpl;
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
        // O dono do pet é sempre o Responsável autenticado — nunca um
        // valor vindo do corpo da requisição.
        Long idResponsavelLogado = SecurityUtils.getUsuarioLogado().getIdResponsavel();
        Responsavel responsavel = buscarResponsavelOuFalhar(idResponsavelLogado);

        Pet pet = new Pet();
        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());
        pet.setResponsavel(responsavel);

        Pet petSalvo = petRepository.save(pet);
        return toResponseDTO(petSalvo);
    }

    /**
     * Responsável só lista os próprios pets; Veterinário continua vendo
     * todos (pode precisar consultar qualquer pet no atendimento).
     */
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listar(Pageable pageable) {
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            return petRepository.findByResponsavel_IdResponsavel(usuarioLogado.getIdResponsavel(), pageable)
                    .map(this::toResponseDTO);
        }
        return petRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id) {
        Pet pet = buscarOuFalhar(id);
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());
        }
        return toResponseDTO(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nomePet, Pageable pageable) {
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            return petRepository.findByResponsavel_IdResponsavelAndNomePetContainingIgnoreCase(
                            usuarioLogado.getIdResponsavel(), nomePet, pageable)
                    .map(this::toResponseDTO);
        }
        return petRepository.findByNomePetContainingIgnoreCase(nomePet, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorEspecie(String especie, Pageable pageable) {
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            return petRepository.findByResponsavel_IdResponsavelAndEspecieContainingIgnoreCase(
                            usuarioLogado.getIdResponsavel(), especie, pageable)
                    .map(this::toResponseDTO);
        }
        return petRepository.findByEspecieContainingIgnoreCase(especie, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorRaca(String raca, Pageable pageable) {
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado.isResponsavel()) {
            return petRepository.findByResponsavel_IdResponsavelAndRacaContainingIgnoreCase(
                            usuarioLogado.getIdResponsavel(), raca, pageable)
                    .map(this::toResponseDTO);
        }
        return petRepository.findByRacaContainingIgnoreCase(raca, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarOuFalhar(id);
        // O responsável do pet não muda numa atualização — só o dono
        // atual pode editar, e a posse não é transferível por aqui.
        SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());

        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());

        Pet petAtualizado = petRepository.save(pet);
        return toResponseDTO(petAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Pet pet = buscarOuFalhar(id);
        SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());
        petRepository.delete(pet);
    }

    private Pet buscarOuFalhar(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
    }

    private Responsavel buscarResponsavelOuFalhar(Long idResponsavel) {
        return responsavelRepository.findById(idResponsavel)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
    }

    private PetResponseDTO toResponseDTO(Pet pet) {
        Responsavel responsavel = pet.getResponsavel();
        return new PetResponseDTO(
                pet.getIdPet(),
                pet.getNomePet(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getPeso(),
                responsavel.getIdResponsavel(),
                responsavel.getUsuario().getNomeUsuario()
        );
    }
}