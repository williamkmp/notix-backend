package com.william.notix.repositories;

import com.william.notix.entities.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
        """
            SELECT p
            FROM projects p
            JOIN p.memberAuthorities a
            WHERE a.user.id = :userId OR p.owner.id = :userId
        """
    )
    public List<Project> findAllByUser(@Param("userId") Long userId);
}
