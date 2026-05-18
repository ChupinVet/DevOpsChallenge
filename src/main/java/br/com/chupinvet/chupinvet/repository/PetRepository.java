package br.com.chupinvet.chupinvet.repository;

import br.com.chupinvet.chupinvet.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByNomePetContainingIgnoreCase(String nomePet, Pageable pageable);

    Page<Pet> findByEspecieContainingIgnoreCase(String especie, Pageable pageable);

    Page<Pet> findByRacaContainingIgnoreCase(String raca, Pageable pageable);
}