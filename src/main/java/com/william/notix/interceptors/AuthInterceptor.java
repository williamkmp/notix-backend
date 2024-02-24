package com.william.notix.interceptors;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.dto.JwtPayloadDto;
import com.william.notix.exceptions.http.UnauthorizedHttpException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.services.AuthService;
import com.william.notix.utils.values.KEY;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        try {
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            if (!isHandlerProtected((HandlerMethod) handler)) {
                return true;
            }
            String accessToken = extractAccessToken(request)
                .orElseThrow(ForbiddenException::new);
            JwtPayloadDto callerInformation = authService
                .verifyAccessToken(accessToken)
                .orElseThrow(ForbiddenException::new);
            request.setAttribute(
                KEY.REQUEST_ATTRIBUTE_CALLER_INFO,
                callerInformation
            );
            return true;
        } catch (Exception e) {
            throw new UnauthorizedHttpException();
        }
    }

    /**
     * extract access token from an incomig request
     *
     * @param request {@link HttpsServletRequest} the incoming request object with Authorization
     *     header containing bearer token
     * @return {@link Optional}<{@link String}> containing the access token, else Optional.empty() if
     *     no beare token detected
     */
    private Optional<String> extractAccessToken(HttpServletRequest request) {
        final String BEARER = "Bearer ";
        String authorizationHeader = request.getHeader("Authorization");
        if (
            authorizationHeader == null ||
            !authorizationHeader.startsWith(BEARER)
        ) {
            return Optional.empty();
        }
        String accessToken = authorizationHeader.substring(BEARER.length());
        return Optional.of(accessToken);
    }

    /**
     * check for the presence of Authenticated annotation for a certaion handler. Returns the value of
     * the Authenticated value.
     *
     * @param handler {@link MethodHandler} the route handler
     * @return true if the handler need authentication, else false
     */
    private boolean isHandlerProtected(HandlerMethod handler) {
        boolean isNeedAuth = false;
        boolean classIsAnnotated = handler
            .getBeanType()
            .isAnnotationPresent(Authenticated.class);
        boolean methodIsAnnotated = handler
            .getMethod()
            .isAnnotationPresent(Authenticated.class);

        if (classIsAnnotated) {
            isNeedAuth =
                handler
                    .getBeanType()
                    .getAnnotation(Authenticated.class)
                    .value();
        }

        if (methodIsAnnotated) {
            isNeedAuth =
                handler.getMethod().getAnnotation(Authenticated.class).value();
        }

        return isNeedAuth;
    }
}
