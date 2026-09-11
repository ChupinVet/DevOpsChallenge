package br.com.chupinvet.chupinvet.repository;

import br.com.chupinvet.chupinvet.model.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Page<Veterinario> findByEspecialidadeContainingIgnoreCase(String especialidade, Pageable pageable);

    Page<Veterinario> findByTipoServicoContainingIgnoreCase(String tipoServico, Pageable pageable);

    Optional<Veterinario> findByUsuario_Email(String email);

    Optional<Veterinario> findByUsuario_Cpf(String cpf);

    Optional<Veterinario> findByUsuario_IdUsuario(Long idUsuario);
}