package com.if_true.company.infrastructure;

import com.if_true.company.domain.Company;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

	Optional<Company> findByIdAndDeletedAtIsNull(UUID id);
}
