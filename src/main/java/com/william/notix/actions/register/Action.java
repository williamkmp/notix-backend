package com.william.notix.actions.register;

import com.william.notix.dto.LoginDto;
import com.william.notix.dto.TokenDto;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.services.AuthService;
import com.william.notix.services.UserService;
import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("registerAction")
@RequiredArgsConstructor
public class Action {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/api/auth/register")
    public Response<LoginDto> action(@RequestBody @Valid Request request) {
        try {
            Boolean isEmailAvailable = userService.isEmailAvailable(
                request.getEmail()
            );
            if (Boolean.FALSE.equals(isEmailAvailable)) {
                return new Response<LoginDto>(HttpStatus.BAD_REQUEST)
                    .addError("email", VALIDATION.UNIQUE);
            }

            User registeredUser = userService
                .registerUser(
                    new User()
                        .setEmail(request.getEmail())
                        .setFullName(request.getFullName())
                        .setPassword(request.getPassword())
                )
                .orElseThrow(Exception::new);

            Long userId = registeredUser.getId();
            TokenDto userTokens = authService
                .generateTokens(userId)
                .orElseThrow(Exception::new);

            return new Response<LoginDto>()
                .setData(
                    new LoginDto()
                        .setUser(
                            new UserDto()
                                .setId(registeredUser.getId().toString())
                                .setEmail(registeredUser.getEmail())
                                .setFullName(registeredUser.getFullName())
                        )
                        .setToken(userTokens)
                );
        } catch (Exception e) {
            throw new InternalServerErrorHttpException();
        }
    }
}
