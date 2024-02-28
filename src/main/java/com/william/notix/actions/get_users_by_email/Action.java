package com.william.notix.actions.get_users_by_email;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller("getUsersAction")
@RequiredArgsConstructor
public class Action {

    private final UserService userService;

    @Authenticated(true)
    @GetMapping("/api/users")
    public Response<List<UserDto>> getMethodName(
        @RequestParam(required = true) String email,
        @RequestParam(required = false) Optional<Long> excludeProject
    ) {
        try {
            List<User> searchResults = excludeProject.isPresent()
                ? userService.searchByEmailEcludingProject(
                    email,
                    excludeProject.get()
                )
                : userService.searchByEmail(email);

            return new Response<List<UserDto>>()
                .setData(
                    searchResults
                        .stream()
                        .map(user ->
                            new UserDto()
                                .setId(user.getId().toString())
                                .setEmail(user.getEmail())
                                .setFullName(user.getFullName())
                                .setImageId(getImageId(user).orElse(null))
                        )
                        .toList()
                );
        } catch (Exception e) {
            if (excludeProject.isPresent()) {
                log.error(
                    "Error [GET] /api/users?email={}&excludeProject={}",
                    email,
                    excludeProject.get()
                );
            } else {
                log.error("Error [GET] /api/users?email={}", email);
            }
            throw new InternalServerErrorHttpException();
        }
    }

    private Optional<String> getImageId(User user) {
        try {
            Long imageId = Optional
                .ofNullable(user.getImage())
                .map(File::getId)
                .orElseThrow(Exception::new);
            return Optional.of(imageId.toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
