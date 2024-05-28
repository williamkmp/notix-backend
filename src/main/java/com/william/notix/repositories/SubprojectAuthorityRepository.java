package com.william.notix.repositories;

import com.william.notix.entities.SubprojectAuthority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubprojectAuthorityRepository
    extends JpaRepository<SubprojectAuthority, Long> {
    /**
     * get authority information from a given user and project
     *
     * @param userId {@link Long} user id
     * @param projectId {@link Long} project id
     * @return {@link Optional}<{@link ProjectAuthority}> user-project authority
     */
    @Query(
        """
            SELECT sa
            FROM subproject_authorities sa
                JOIN sa.projectAuthority.project p
                JOIN sa.projectAuthority.user u
            WHERE u.id = :userId
                AND p.id = :projectId
        """
    )
    public Optional<SubprojectAuthority> findByUserAndProject(
        @Param("userId") Long userId,
        @Param("projectId") Long projectId
    );
}
