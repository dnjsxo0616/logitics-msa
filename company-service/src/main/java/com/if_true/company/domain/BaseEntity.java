package com.if_true.company.domain;

import static jakarta.persistence.GenerationType.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = UUID)
	@Column(nullable = false, updatable = false)
	private UUID id;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	private Instant createdAt;

	@Column(name = "created_by", nullable = false, updatable = false)
	private UUID createdBy;

	@Column(name = "updated_at", nullable = false)
	@LastModifiedDate
	private Instant updatedAt;

	@Column(name = "updated_by", nullable = false)
	private UUID updatedBy;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Column(name = "deleted_by")
	private UUID deletedBy;

	@Version
	@Column(nullable = false) //초기값null될수 있으니 필수
	private Long version = 0L;

	protected void initializeAudit(UUID actorId) {
		this.createdBy = actorId;
		this.updatedBy = actorId;
	}

	protected void markUpdated(UUID actorId) {
		this.updatedBy = actorId;
	}

	protected void markDeleted(UUID actorId) {
		this.deletedAt = Instant.now();
		this.deletedBy = actorId;
		this.updatedBy = actorId;
	}

	public UUID getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public UUID getCreatedBy() {
		return createdBy;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public UUID getUpdatedBy() {
		return updatedBy;
	}

	public Instant getDeletedAt() {
		return deletedAt;
	}

	public UUID getDeletedBy() {
		return deletedBy;
	}

	public Long getVersion() {
		return version;
	}
}
