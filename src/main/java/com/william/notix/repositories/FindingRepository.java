package com.william.notix.repositories;

import com.william.notix.entities.Finding;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FindingRepository extends JpaRepository<Finding, Long> {
    @Query(
        """
           SELECT f
           FROM findings f
           WHERE f.subproject.id = :subprojectId
        """
    )
    public List<Finding> findAllBySubproject(
        @Param("subprojectId") Long subprojectId
    );
}
