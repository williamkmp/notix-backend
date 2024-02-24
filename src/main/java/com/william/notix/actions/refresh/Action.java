package com.william.notix.actions.refresh;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.william.notix.dto.JwtPayloadDto;
import com.william.notix.dto.LoginDto;
import com.william.notix.dto.TokenDto;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.UnauthorizedHttpException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.services.AuthService;
import com.william.notix.services.ImageService;
import com.william.notix.services.UserService;

import jakarta.validation.Valid;


@Controller("refreshToken")
@RequiredArgsConstructor
public class Action {
    
    private final AuthService authService;
    private final UserService userService;
    private final ImageService imageService;

    @PostMapping("/api/auth/refresh")
    public Response<LoginDto> action(
        @RequestBody @Valid Request request
    ) {
        try {
            String refreshToken = request.getRefreshToken();
            JwtPayloadDto callerPayload = authService
                .verifyRefreshToken(refreshToken)
                .orElseThrow(UserNotFoundException::new);
            TokenDto tokens = authService
                .generateTokens(callerPayload.getId())
                .orElseThrow(UserNotFoundException::new);
            User caller = userService
                .findById(callerPayload.getId())
                .orElseThrow(UserNotFoundException::new);
            String callerImageUrl = imageService.getUserImageUrl(caller.getId())
                .orElse(null);

            return new Response<LoginDto>(HttpStatus.OK)
                .setData(
                    new LoginDto()
                        .setUser(
                            new UserDto()
                                .setId(caller.getId().toString())
                                .setEmail(caller.getEmail())
                                .setFullName(caller.getFullName())
                                .setImageUrl(callerImageUrl)
                        )
                        .setToken(tokens)
                );
        } catch (UserNotFoundException e) {
            throw new UnauthorizedHttpException();
        } catch (Exception e) {
            throw new InternalServerErrorHttpException();
        }
    }
    
}
