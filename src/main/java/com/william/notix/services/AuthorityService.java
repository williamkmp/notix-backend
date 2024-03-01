package com.william.notix.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.william.notix.entities.Authority;
import com.william.notix.entities.Project;
import com.william.notix.repositories.AuthorityRepository;
import com.william.notix.utils.values.ROLE;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@AllArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    /**
     * get the user ROLE in a given project, else return empty if user have no access
     * 
     * @param userId {@link Long} user id
     * @param projectId {@link Project} project id
     * @return {@link Optional}<{@link ROLE}> user's role, else empty
     */
    public Optional<ROLE> getUserProjectRole(
        @NonNull Long userId, 
        @NonNull Long projectId
    ) {
        try {
            Authority authority = authorityRepository
                .findByUserAndProject(userId, projectId)
                .orElseThrow(Exception::new);
            return Optional.ofNullable(authority.getRole());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
