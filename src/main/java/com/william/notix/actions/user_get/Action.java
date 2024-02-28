package com.william.notix.actions.user_get;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.http.UnauthorizedHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("getUserAction")
@RequiredArgsConstructor
public class Action {

    private final UserService userService;

    @GetMapping("/api/user/{userId}")
    @Authenticated
    public Response<UserDto> action(
        @Caller User caller,
        @PathVariable("userId") Long userId
    ) {
        try {
            User user = userService
                .findById(userId)
                .orElseThrow(ResourceNotFoundException::new);

            return new Response<UserDto>()
                .setData(
                    new UserDto()
                        .setId(user.getId().toString())
                        .setFullName(user.getFullName())
                        .setEmail(user.getEmail())
                        .setImageId(
                            user.getImage() != null
                                ? user.getImage().getId().toString()
                                : null
                        )
                );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/user/{}", userId);
            e.printStackTrace();
            throw new UnauthorizedHttpException();
        }
    }
}
