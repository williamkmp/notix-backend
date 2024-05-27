package com.william.notix.repositories;

import com.william.notix.entities.ProjectLog;
import com.william.notix.entities.SubprojectLog;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubprojectlogRepository
    extends JpaRepository<SubprojectLog, Long> {
    @Query(
        """
        SELECT log
        FROM  subproject_logs log
        WHERE log.updatee.id = :subprojectId
        ORDER BY createdAt DESC
        """
    )
    public List<ProjectLog> findAllById(
        @Param("subprojectId") Long subprojectId,
        Pageable page
    );
}
