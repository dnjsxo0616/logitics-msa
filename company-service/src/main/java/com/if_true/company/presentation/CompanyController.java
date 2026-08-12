package com.if_true.company.presentation;

import com.if_true.company.application.CompanyService;
import com.if_true.company.domain.CompanyType;
import com.if_true.company.presentation.dto.CompanyRequest;
import com.if_true.company.presentation.dto.CompanyResponse;
import com.if_true.company.presentation.dto.CompanyUpdateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

	private final CompanyService companyService;

	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public ResponseEntity<CompanyResponse> create(
		@RequestHeader("X-User-Id") UUID actorId,
		@Valid @RequestBody CompanyRequest request
	) {
		CompanyResponse response = companyService.create(request, actorId);
		return ResponseEntity.created(URI.create("/api/v1/companies/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
	public CompanyResponse get(@PathVariable UUID id) {
		return companyService.get(id);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
	public Page<CompanyResponse> search(
		@RequestParam(required = false) String companyName,
		@RequestParam(required = false) CompanyType companyType,
		@RequestParam(required = false) UUID hubId,
		Pageable pageable
	) {
		return companyService.search(companyName, companyType, hubId, pageable);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public CompanyResponse update(
		@PathVariable UUID id,
		@RequestHeader("X-User-Id") UUID actorId,
		@Valid @RequestBody CompanyUpdateRequest request
	) {
		return companyService.update(id, request, actorId);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
	public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID actorId) {
		companyService.delete(id, actorId);
		return ResponseEntity.noContent().build();
	}
}
