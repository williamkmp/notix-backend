package com.william.notix.repositories;

import com.william.notix.entities.ProjectLog;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectLogRepository extends JpaRepository<ProjectLog, Long> {
    @Query(
        """
        SELECT log FROM  project_logs log WHERE log.updatee.id = :projectId ORDER BY createdAt DESC
        """
    )
    public List<ProjectLog> findAllByProject(
        @Param("projectId") Long projectId,
        Pageable page
    );
}
