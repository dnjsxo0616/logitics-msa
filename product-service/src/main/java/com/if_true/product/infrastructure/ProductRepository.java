package com.if_true.product.infrastructure;

import com.if_true.product.domain.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

	Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

	long countByCompanyIdAndDeletedAtIsNull(UUID companyId);
}
