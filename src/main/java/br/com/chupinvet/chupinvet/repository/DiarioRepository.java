package br.com.chupinvet.chupinvet.repository;

import br.com.chupinvet.chupinvet.model.Diario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiarioRepository extends JpaRepository<Diario, Long> {

    Page<Diario> findByPet_IdPetOrderByDataRegistroDesc(Long idPet, Pageable pageable);
}