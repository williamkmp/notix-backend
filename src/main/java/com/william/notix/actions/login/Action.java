package com.william.notix.actions.login;

import com.william.notix.dto.FileDto;
import com.william.notix.dto.LoginDto;
import com.william.notix.dto.TokenDto;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.File;
import com.william.notix.entities.User;
import com.william.notix.services.AuthService;
import com.william.notix.services.FileService;
import com.william.notix.utils.values.VALIDATION;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("loginAction")
@RequiredArgsConstructor
public class Action {

    private final AuthService authService;
    private final FileService fileService;

    @PostMapping("/api/auth/login")
    public Response<LoginDto> action(@RequestBody @Valid Request request) {
        try {
            User user = authService
                .loginUser(request.getEmail(), request.getPassword())
                .orElseThrow(Exception::new);

            Long userId = user.getId();
            TokenDto userToken = authService
                .generateTokens(userId)
                .orElseThrow(Exception::new);

            Long imageId = Optional
                .ofNullable(user.getImage())
                .map(File::getId)
                .orElse(null);
            String imageUrl = fileService
                .getFileInfo(imageId)
                .map(FileDto::getUrl)
                .orElse(null);

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
            return new Response<LoginDto>(HttpStatus.BAD_REQUEST)
                .setRootError(VALIDATION.INVALID_CREDENTIAL);
        }
    }
}
