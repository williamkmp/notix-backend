package com.william.notix.services;

import com.william.notix.entities.User;
import com.william.notix.repositories.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EncoderService encoderService;

    /**
     * register a new user
     *
     * @param newUser {@link User} new user, id is empty
     * @return {@link Optional}<{@link User}> registered user data, else empty if failed
     */
    public Optional<User> registerUser(@NonNull User newUser) {
        try {
            String rawPassword = newUser.getPassword();
            String encodedPassword = encoderService.encode(rawPassword);
            newUser.setPassword(encodedPassword);
            User savedUser = userRepository.save(newUser);
            return Optional.of(savedUser);
        } catch (Exception e) {
            log.error("Failed registering user");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * find user by id
     *
     * @param userId {@link Long} user id
     * @return {@link Optional}<{@link User}> registered user information, else empty if not found
     */
    public Optional<User> findById(Long userId) {
        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            log.error("Error finding user id: {}", userId);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * search users by email address containing string
     *
     * @param searchEmail {@link String} email search query
     * @return {@link List}<{@link User}> search result
     */
    public List<User> searchByEmail(@NonNull String email) {
        return userRepository.searchByEmail("%" + email + "%");
    }

    /**
     * search users by email address, excluding all member and project owner with a certain project id
     *
     * @param searchEmail {@link String} email search query
     * @param projectId {@link Long} excluded project id
     * @return {@link List}<{@link User}> search result
     */
    public List<User> searchByEmailEcludingProject(
        @NonNull String email,
        @NonNull Long projectId
    ) {
        try {
            return userRepository.searchByEmailExcludingProject(
                "%" + email + "%",
                projectId
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * save new user, or update existing user
     *
     * @param user {@link User} user data
     * @return {@link Optional}<{@link User}> user data, else empty of operation failed
     */
    public Optional<User> save(@NonNull User user) {
        try {
            User savedUser = userRepository.saveAndFlush(user);
            return Optional.of(savedUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * check if a given email is available
     *
     * @param email {@link String} email
     * @return {@link Boolean} if email is available
     */
    public Boolean isEmailAvailable(@NonNull String email) {
        User registeredUser = userRepository.findByEmail(email).orElse(null);
        return Objects.isNull(registeredUser);
    }

    /**
     * check if a given email available, excluding a given user id
     *
     * @param email {@link String} email
     * @param userId {@link Long} excluded user id
     * @return {@link Boolean} is email available
     */
    public Boolean isEmailAvailable(
        @NonNull String email,
        @NonNull Long userId
    ) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (Objects.isNull(user)) {
            return Boolean.TRUE;
        }
        return Objects.equals(user.getId(), userId);
    }
}
