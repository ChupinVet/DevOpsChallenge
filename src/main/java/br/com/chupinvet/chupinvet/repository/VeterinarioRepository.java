package br.com.chupinvet.chupinvet.repository;

import br.com.chupinvet.chupinvet.model.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Page<Veterinario> findByEspecialidadeContainingIgnoreCase(String especialidade, Pageable pageable);

    Page<Veterinario> findByTipoServicoContainingIgnoreCase(String tipoServico, Pageable pageable);

    Optional<Veterinario> findByEmail(String email);

    Optional<Veterinario> findByCpf(String cpf);
}