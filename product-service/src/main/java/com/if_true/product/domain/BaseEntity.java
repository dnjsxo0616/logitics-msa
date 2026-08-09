package com.if_true.product.domain;

import static jakarta.persistence.GenerationType.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
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
	@CreatedBy
	private UUID createdBy;

	@Column(name = "updated_at", nullable = false)
	@LastModifiedDate
	private Instant updatedAt;

	@Column(name = "updated_by", nullable = false)
	@LastModifiedBy
	private UUID updatedBy;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Column(name = "deleted_by")
	private UUID deletedBy;

	@Version
	@Column(nullable = false)
	private Long version = 0L;

	protected void markDeleted(UUID actorId) {
		this.deletedAt = Instant.now();
		this.deletedBy = actorId;
	}
}
