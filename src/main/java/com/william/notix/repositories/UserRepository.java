package com.william.notix.repositories;

import com.william.notix.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * find user by email
     *
     * @param email {@link String} email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * find all users by email search string
     *
     * @param emailString {@link String} search query string ex: "%email%"
     * @return {@link List}<{@link Users}> search results
     */
    @Query(
        """
            SELECT u
            FROM users u
            WHERE LOWER(u.email) LIKE LOWER(:emailQuery)
        """
    )
    List<User> searchByEmail(@Param("emailQuery") String emailString);

    /**
     * search all user by email excluding a certain project members and owner
     *
     * @param emailString {@link String} search query string ex: "%email%"
     * @param projectId {@link Long} project id
     * @return {@link List}<{@link User}> search results
     */
    @Query(
        """
            SELECT u FROM users u
            WHERE LOWER(u.email) LIKE LOWER(:emailQuery)
            AND u.id NOT IN (
                SELECT a.user.id
                FROM authorities a WHERE a.project.id = :projectId
            )
            AND u.id NOT IN (
                SELECT p.owner.id
                FROM projects p WHERE p.id = :projectId
            )
        """
    )
    List<User> searchByEmailExcludingProject(
        @Param("emailQuery") String emailString,
        @Param("projectId") Long projectId
    );

    /**
     * find all project member
     *
     * @param projectId {@link Long} project id
     * @return {@link List}<{@link User}> search results
     */
    @Query(
        """
           SELECT u FROM users u
           WHERE u.id IN (
                SELECT a.user.id
                FROM authorities a WHERE a.project.id = : projectId
           )
           OR u.id (
                SELECT p.owner.id
                FROM projects p where p.id = :projectId
           )
        """
    )
    List<User> findAllByProject(@Param("projectId") Long projectId);
}
