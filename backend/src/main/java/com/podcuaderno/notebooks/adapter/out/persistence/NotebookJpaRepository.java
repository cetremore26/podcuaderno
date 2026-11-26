package com.podcuaderno.notebooks.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface NotebookJpaRepository extends JpaRepository<NotebookJpaEntity, UUID> {

    List<NotebookSummaryView> findByOwnerIdOrderByUpdatedAtDesc(UUID ownerId);

    /** Bulk delete: the database cascades to topics and concepts without loading them. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from NotebookJpaEntity n where n.id = :id")
    void deleteNotebook(@Param("id") UUID id);
}
