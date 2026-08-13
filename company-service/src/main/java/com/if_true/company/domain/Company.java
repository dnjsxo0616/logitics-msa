package com.if_true.company.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "p_company")
public class Company extends BaseEntity {

	@Column(name = "company_name", nullable = false, length = 100)
	private String companyName;

	@Enumerated(EnumType.STRING)
	@Column(name = "company_type", nullable = false, length = 50)
	private CompanyType companyType;

	@Column(name = "hub_id", nullable = false)
	private UUID hubId;

	@Column(name = "company_address", nullable = false, length = 255)
	private String companyAddress;

	protected Company() {
	}

	private Company(String companyName, CompanyType companyType, UUID hubId, String companyAddress, UUID actorId) {
		this.companyName = companyName;
		this.companyType = companyType;
		this.hubId = hubId;
		this.companyAddress = companyAddress;
		initializeAudit(actorId);
	}

	public static Company create(String companyName, CompanyType companyType, UUID hubId, String companyAddress, UUID actorId) {
		return new Company(companyName, companyType, hubId, companyAddress, actorId);
	}

	public void update(String companyName, CompanyType companyType, UUID hubId, String companyAddress, UUID actorId) {
		if (companyName != null) {
			this.companyName = companyName;
		}
		if (companyType != null) {
			this.companyType = companyType;
		}
		if (hubId != null) {
			this.hubId = hubId;
		}
		if (companyAddress != null) {
			this.companyAddress = companyAddress;
		}
		markUpdated(actorId);
	}

	public void delete(UUID actorId) {
		markDeleted(actorId);
	}

	public String getCompanyName() {
		return companyName;
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public UUID getHubId() {
		return hubId;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}
}
