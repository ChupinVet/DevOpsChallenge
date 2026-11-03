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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    private final ResponsavelRepository responsavelRepository;

    @Transactional
    public PetResponseDTO cadastrar(PetRequestDTO dto) {
        // O dono do pet é sempre o Responsável autenticado — nunca um
        // valor vindo do corpo da requisição.
        Long idResponsavelLogado = SecurityUtils.getUsuarioLogado().getIdResponsavel();
        Responsavel responsavel = buscarResponsavelOuFalhar(idResponsavelLogado);

        Pet pet = new Pet();
        pet.setResponsavel(responsavel);
        aplicarDados(pet, dto);

        Pet petSalvo = petRepository.save(pet);
        return toResponseDTO(petSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listar(Pageable pageable) {
        return buscarConformePapel(
                idResponsavel -> petRepository.findByResponsavel_IdResponsavel(idResponsavel, pageable),
                () -> petRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id) {
        Pet pet = buscarOuFalhar(id);
        SecurityUtils.validarAcessoLeitura(pet.getResponsavel().getIdResponsavel());
        return toResponseDTO(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nomePet, Pageable pageable) {
        return buscarConformePapel(
                idResponsavel -> petRepository.findByResponsavel_IdResponsavelAndNomePetContainingIgnoreCase(
                        idResponsavel, nomePet, pageable),
                () -> petRepository.findByNomePetContainingIgnoreCase(nomePet, pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorEspecie(String especie, Pageable pageable) {
        return buscarConformePapel(
                idResponsavel -> petRepository.findByResponsavel_IdResponsavelAndEspecieContainingIgnoreCase(
                        idResponsavel, especie, pageable),
                () -> petRepository.findByEspecieContainingIgnoreCase(especie, pageable));
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorRaca(String raca, Pageable pageable) {
        return buscarConformePapel(
                idResponsavel -> petRepository.findByResponsavel_IdResponsavelAndRacaContainingIgnoreCase(
                        idResponsavel, raca, pageable),
                () -> petRepository.findByRacaContainingIgnoreCase(raca, pageable));
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarOuFalhar(id);
        // O responsável do pet não muda numa atualização — só o dono
        // atual pode editar, e a posse não é transferível por aqui.
        SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());

        aplicarDados(pet, dto);

        Pet petAtualizado = petRepository.save(pet);
        return toResponseDTO(petAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Pet pet = buscarOuFalhar(id);
        SecurityUtils.validarPosseResponsavel(pet.getResponsavel().getIdResponsavel());
        petRepository.delete(pet);
    }

    /**
     * Responsável só enxerga os próprios pets; Veterinário enxerga todos
     * (pode precisar consultar qualquer pet no atendimento).
     */
    private Page<PetResponseDTO> buscarConformePapel(
            Function<Long, Page<Pet>> consultaDoResponsavel,
            Supplier<Page<Pet>> consultaGeral
    ) {
        UserDetailsImpl usuarioLogado = SecurityUtils.getUsuarioLogado();
        Page<Pet> pets = usuarioLogado.isResponsavel()
                ? consultaDoResponsavel.apply(usuarioLogado.getIdResponsavel())
                : consultaGeral.get();
        return pets.map(this::toResponseDTO);
    }

    private Pet buscarOuFalhar(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado"));
    }

    private Responsavel buscarResponsavelOuFalhar(Long idResponsavel) {
        return responsavelRepository.findById(idResponsavel)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
    }

    private void aplicarDados(Pet pet, PetRequestDTO dto) {
        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());
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