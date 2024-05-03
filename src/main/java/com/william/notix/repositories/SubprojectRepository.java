package com.william.notix.repositories;

import com.william.notix.entities.Subproject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubprojectRepository extends JpaRepository<Subproject, Long> {
    @Query(
        """
            SELECT sp
            FROM subprojects sp
            WHERE sp.project.id = :projectId
            ORDER BY sp.createdAt DESC
        """
    )
    public List<Subproject> findAllByProject(
        @Param("projectId") Long projectId
    );
}
