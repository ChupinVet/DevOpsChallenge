package br.com.chupinvet.chupinvet.repository;

import br.com.chupinvet.chupinvet.model.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    Optional<Responsavel> findByUsuario_Email(String email);

    Optional<Responsavel> findByUsuario_Cpf(String cpf);

    Optional<Responsavel> findByUsuario_IdUsuario(Long idUsuario);
}