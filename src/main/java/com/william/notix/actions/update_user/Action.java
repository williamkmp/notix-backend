package com.william.notix.actions.update_user;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.services.FileService;
import com.william.notix.services.UserService;
import com.william.notix.utils.values.MESSAGES;
import com.william.notix.utils.values.VALIDATION;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("updateUserAction")
@RequiredArgsConstructor
public class Action {

    private final UserService userService;
    private final FileService fileService;

    @Transactional
    @Authenticated(true)
    @PutMapping("/api/user")
    public Response<UserDto> action(
        @Caller User user,
        @RequestBody @Valid Request request
    ) {
        try {
            Boolean isEmailAvailable = userService.isEmailAvailable(
                request.getEmail(),
                user.getId()
            );
            if (Boolean.FALSE.equals(isEmailAvailable)) {
                return new Response<UserDto>(HttpStatus.BAD_REQUEST)
                    .addError("email", VALIDATION.UNIQUE)
                    .setMessage(MESSAGES.UPDATE_FAIL);
            }

            Optional<Long> requestImageId = parseLong(request.getImageId());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());

            if (requestImageId.isPresent()) {
                fileService.updateUserImage(user.getId(), requestImageId.get());
            } else {
                fileService.deleteUserImage(user.getId());
            }

            userService.save(user);

            return new Response<UserDto>()
                .setData(
                    new UserDto()
                        .setId(user.getId().toString())
                        .setEmail(user.getEmail())
                        .setFullName(user.getFullName())
                        .setImageId(
                            requestImageId.isPresent()
                                ? requestImageId.get().toString()
                                : null
                        )
                )
                .setMessage(MESSAGES.UPDATE_SUCCESS);
        } catch (ResourceNotFoundException e) {
            log.error(
                "ResourceNotFoundException [PUT] /api/user, request: {}",
                request.toString()
            );
            log.error("Image not found, imageId: {}", request.getImageId());
            e.printStackTrace();
            throw new ResourceNotFoundHttpException();
        } catch (Exception e) {
            log.error("Error [PUT] /api/user, request: {}", request.toString());
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }

    private Optional<Long> parseLong(String longStr) {
        try {
            return Optional.of(Long.valueOf(longStr));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
