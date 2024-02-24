package com.william.notix.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.william.notix.dto.JwtPayloadDto;
import com.william.notix.dto.TokenDto;
import com.william.notix.entities.User;
import com.william.notix.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.security.jwt.access_token.duration:300000}")
    Long ACCESS_TOKEN_DURATION_MS;

    @Value("${app.security.jwt.refresh_token.duration:86400000}")
    Long REFRESH_TOKEN_DURATION_MS;

    @Value("${app.security.jwt.access_token.secret:SECRET_123}")
    String ACCESS_TOKEN_SECRET;

    @Value("${app.security.jwt.refresh_token.secret:SECRET_456}")
    String REFRESH_TOKEN_SECRET;

    private final Gson gson;
    private final UserRepository userRepository;
    private final EncoderService encoderService;

    public Optional<User> loginUser(
        @NonNull String email,
        @NonNull String password
    ) {
        try {
            User registeredUser = userRepository
                .findByEmail(email)
                .orElseThrow(Exception::new);
            String encodedPassword = registeredUser.getPassword();
            Boolean isPasswordMatch = encoderService.matches(
                password,
                encodedPassword
            );
            if (Boolean.FALSE.equals(isPasswordMatch)) {
                throw new Exception();
            }
            return Optional.of(registeredUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<TokenDto> generateTokens(Long userid) {
        Optional<String> maybeAccessToken = generateAccessToken(userid);
        Optional<String> maybeRefreshtoken = generateRefreshToken(userid);

        if (maybeAccessToken.isEmpty() || maybeRefreshtoken.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
            new TokenDto()
                .setAccessToken(maybeAccessToken.get())
                .setRefreshToken(maybeRefreshtoken.get())
        );
    }

    public Optional<JwtPayloadDto> verifyAccessToken(String accessToken) {
        return verifyToken(ACCESS_TOKEN_SECRET, accessToken);
    }

    public Optional<JwtPayloadDto> verifyRefreshToken(String refreshToken) {
        Optional<JwtPayloadDto> maybePayload = verifyToken(
            REFRESH_TOKEN_SECRET,
            refreshToken
        );

        if (maybePayload.isEmpty()) return Optional.empty();
        JwtPayloadDto payload = maybePayload.get();

        Optional<User> maybeUser = userRepository.findById(payload.getId());

        if (maybeUser.isEmpty()) return Optional.empty();
        User user = maybeUser.get();

        if (
            user.getRefreshToken() == null ||
            !user.getRefreshToken().equals(refreshToken)
        ) return Optional.empty();

        return Optional.of(payload);
    }

    /**
     * generate access token froma given user
     *
     * @param userId {@link Long} user id
     * @return {@link Optional}<{@link String}> containing access token, else Optional.empty() if
     *     generation failed
     */
    @Transactional
    private Optional<String> generateAccessToken(Long userId) {
        Optional<User> maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty()) return Optional.empty();

        User user = maybeUser.get();
        JwtPayloadDto tokenPayload = new JwtPayloadDto()
            .setId(user.getId())
            .setEmail(user.getEmail());

        return signPayload(
            ACCESS_TOKEN_SECRET,
            ACCESS_TOKEN_DURATION_MS,
            tokenPayload
        );
    }

    /**
     * generate refresh token and update the user record inside the database.
     *
     * @param userId {@link Long} user id
     * @return {@link Optional}<{@link String}> containing refresh token, else Optional.empty() if
     *     generation failed
     */
    @Transactional
    private Optional<String> generateRefreshToken(Long userId) {
        Optional<User> maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty()) return Optional.empty();

        User user = maybeUser.get();
        JwtPayloadDto tokenPayload = new JwtPayloadDto()
            .setId(user.getId())
            .setEmail(user.getEmail());

        Optional<String> maybeToken = signPayload(
            REFRESH_TOKEN_SECRET,
            REFRESH_TOKEN_DURATION_MS,
            tokenPayload
        );

        if (maybeToken.isEmpty()) return Optional.empty();

        user.setRefreshToken(maybeToken.get());
        userRepository.save(user);

        return maybeToken;
    }

    /**
     * sign payload to create the json webtoken
     *
     * @param secret {@link String} secret string to verify and sign the json web token
     * @param duration {@link Long} token validity duration is miliseconds
     * @param payload {@link JwtPayloadDto} the payload for the token
     * @return {@link Optional}<{@link String}> conatining the generated token signed using the
     *     secret, else Optional.empty() if generation process failed
     */
    public Optional<String> signPayload(
        String secret,
        Long duration,
        JwtPayloadDto payload
    ) {
        Optional<String> token;
        try {
            String payloadJsonString = gson.toJson(payload);

            Long systemTime = System.currentTimeMillis();
            Date now = new Date(systemTime);
            Date expiredAt = new Date(systemTime + duration);

            String tokenString = JWT
                .create()
                .withPayload(payloadJsonString)
                .withIssuedAt(now)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC256(secret));

            token = Optional.of(tokenString);
        } catch (Exception e) {
            token = Optional.empty();
        }
        return token;
    }

    /**
     * verify a JSON Webtoken using a scret string
     *
     * @param secret {@link String} secret string used to verify the given token
     * @param token {@link String} token validity duration is miliseconds
     * @return {@link Optional}<{@link JwtPayloadDto}> containing the payload of the token , else
     *     Optional.empty() if failed
     */
    public Optional<JwtPayloadDto> verifyToken(String secret, String token) {
        Optional<JwtPayloadDto> maybePayload;
        try {
            JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secret))
                .build();
            String encodedPayload = verifier.verify(token).getPayload();
            String payloadJsonString = new String(
                Base64.getDecoder().decode(encodedPayload)
            );
            JwtPayloadDto payloadData = gson.fromJson(
                payloadJsonString,
                JwtPayloadDto.class
            );
            maybePayload = Optional.of(payloadData);
        } catch (Exception e) {
            maybePayload = Optional.empty();
        }
        return maybePayload;
    }
}
