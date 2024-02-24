package com.william.notix.actions.login;

import com.william.notix.dto.LoginDto;
import com.william.notix.dto.TokenDto;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.UnauthorizedHttpException;
import com.william.notix.exceptions.runtime.UnauthorizedException;
import com.william.notix.services.AuthService;
import com.william.notix.services.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("loginAction")
@RequiredArgsConstructor
public class Action {

    private final AuthService authService;
    private final ImageService imageService;

    @PostMapping("/api/auth/login")
    public Response<LoginDto> action(@RequestBody @Valid Request request) {
        try {
            User user = authService
                .loginUser(request.getEmail(), request.getPassword())
                .orElseThrow(UnauthorizedException::new);

            Long userId = user.getId();
            TokenDto userToken = authService
                .generateTokens(userId)
                .orElseThrow(UnauthorizedException::new);

            String imageUrl = imageService.getUserImageUrl(userId).orElse(null);

            return new Response<LoginDto>()
                .setData(
                    new LoginDto()
                        .setUser(
                            new UserDto()
                                .setId(user.getId().toString())
                                .setEmail(user.getEmail())
                                .setFullName(user.getFullName())
                                .setImageUrl(imageUrl)
                        )
                        .setToken(userToken)
                );
        } catch (Exception e) {
            throw new UnauthorizedHttpException();
        }
    }
}
