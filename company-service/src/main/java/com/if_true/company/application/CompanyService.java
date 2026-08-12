package com.if_true.company.application;

import com.if_true.company.domain.Company;
import com.if_true.company.domain.CompanyType;
import com.if_true.company.infrastructure.CompanyRepository;
import com.if_true.company.infrastructure.client.HubClient;
import com.if_true.company.infrastructure.client.ProductClient;
import com.if_true.company.presentation.dto.CompanyRequest;
import com.if_true.company.presentation.dto.CompanyResponse;
import com.if_true.company.presentation.dto.CompanyUpdateRequest;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class CompanyService {

	private final CompanyRepository companyRepository;
	private final HubClient hubClient;
	private final ProductClient productClient;
	private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
	private final boolean hubValidationEnabled;
	private final boolean productDeletePolicyEnabled;

	public CompanyService(
		CompanyRepository companyRepository,
		HubClient hubClient,
		ProductClient productClient,
		CircuitBreakerFactory<?, ?> circuitBreakerFactory,
		@Value("${msa.validation.hub.enabled:false}") boolean hubValidationEnabled,
		@Value("${msa.validation.product-delete-policy.enabled:false}") boolean productDeletePolicyEnabled
	) {
		this.companyRepository = companyRepository;
		this.hubClient = hubClient;
		this.productClient = productClient;
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.hubValidationEnabled = hubValidationEnabled;
		this.productDeletePolicyEnabled = productDeletePolicyEnabled;
	}

	@Transactional
	public CompanyResponse create(CompanyRequest request, UUID actorId) {
		validateHubExists(request.hubId());
		Company company = Company.create(
			request.companyName(),
			request.companyType(),
			request.hubId(),
			request.companyAddress(),
			actorId
		);
		return CompanyResponse.from(companyRepository.save(company));
	}

	public CompanyResponse get(UUID id) {
		return CompanyResponse.from(findActiveCompany(id));
	}

	public Page<CompanyResponse> search(String companyName, CompanyType companyType, UUID hubId, Pageable pageable) {
		Specification<Company> spec = active()
			.and(companyNameContains(companyName))
			.and(companyTypeEquals(companyType))
			.and(hubIdEquals(hubId));
		return companyRepository.findAll(spec, pageable).map(CompanyResponse::from);
	}

	@Transactional
	public CompanyResponse update(UUID id, CompanyUpdateRequest request, UUID actorId) {
		Company company = findActiveCompany(id);
		if (request.hubId() != null) {
			validateHubExists(request.hubId());
		}
		company.update(request.companyName(), request.companyType(), request.hubId(), request.companyAddress(), actorId);
		return CompanyResponse.from(company);
	}

	@Transactional
	public void delete(UUID id, UUID actorId) {
		Company company = findActiveCompany(id);
		validateNoActiveProducts(id);
		company.delete(actorId);
	}

	private Company findActiveCompany(UUID id) {
		return companyRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new EntityNotFoundException("Company not found: " + id));
	}

	private Specification<Company> active() {
		return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
	}

	private Specification<Company> companyNameContains(String companyName) {
		return (root, query, criteriaBuilder) -> {
			if (!StringUtils.hasText(companyName)) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), "%" + companyName.toLowerCase() + "%");
		};
	}

	private Specification<Company> companyTypeEquals(CompanyType companyType) {
		return (root, query, criteriaBuilder) ->
			companyType == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("companyType"), companyType);
	}

	private Specification<Company> hubIdEquals(UUID hubId) {
		return (root, query, criteriaBuilder) ->
			hubId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("hubId"), hubId);
	}

	private void validateHubExists(UUID hubId) {
		if (!hubValidationEnabled) {
			return;
		}
		circuitBreakerFactory.create("hub-service").run(
			() -> hubClient.getHub(hubId),
			throwable -> {
				if (throwable instanceof FeignException.NotFound) {
					throw new EntityNotFoundException("Hub not found: " + hubId);
				}
				throw new IllegalStateException("Failed to validate hub.");
			}
		);
	}

	private void validateNoActiveProducts(UUID companyId) {
		if (!productDeletePolicyEnabled) {
			return;
		}
		long activeProductCount = circuitBreakerFactory.create("product-service").run(
			() -> productClient.countProducts(companyId),
			throwable -> {
				throw new IllegalStateException("Failed to validate company product policy.");
			}
		);
		if (activeProductCount > 0) {
			throw new IllegalStateException("Company has active products.");
		}
	}
}
