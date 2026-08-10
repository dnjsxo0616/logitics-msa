package com.iftrue.hub.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsByIdAndDeletedAtIsNull(UUID id);

    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
               SELECT h FROM Hub h
               WHERE h.deletedAt IS NULL
               AND h.name LIKE CONCAT('%', :keyword, '%')
            """)
    Page<Hub> search(@Param("keyword") String keyword, Pageable pageable);
}
