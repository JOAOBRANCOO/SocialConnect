package br.com.socialconnect.api.beneficiarios.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    Optional<Beneficiario> findByCpf(String cpf);

    Page<Beneficiario> findByCpf(String cpf, Pageable pageable);

    Page<Beneficiario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdBeneficiarioNot(String cpf, Long idBeneficiario);
}