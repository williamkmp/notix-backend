package com.william.notix.exceptions;

import com.william.notix.dto.response.Response;
import com.william.notix.entities.ExceptionDto;
import com.william.notix.exceptions.socket.StandardProjectSocketException;
import com.william.notix.utils.values.KEY;
import com.william.notix.utils.values.MESSAGES;
import com.william.notix.utils.values.TOPIC;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionCatcher {

    private final SimpMessagingTemplate socket;

    @ExceptionHandler(Exception.class)
    public Response<Object> internalServerException(Exception exception) {
        log.error("Unhandled exception occured", exception);
        return new Response<Object>(HttpStatus.INTERNAL_SERVER_ERROR)
            .setMessage(MESSAGES.ERROR_INTERNAL_SERVER);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Object> missingRequestParameterException(
        MissingServletRequestParameterException exception
    ) {
        return new Response<>(HttpStatus.BAD_REQUEST)
            .setMessage(exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Response<Object> maxUploadSizeException(
        MaxUploadSizeExceededException exception
    ) {
        return new Response<Object>(HttpStatus.PAYLOAD_TOO_LARGE)
            .setMessage(MESSAGES.ERROR_PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public Response<Object> httpStatusCodeException(
        HttpStatusCodeException exception
    ) {
        return new Response<Object>(exception.getStatusCode())
            .setMessage(exception.getStatusText());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> validationException(
        MethodArgumentNotValidException exception
    ) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        Map<String, String> failedValidations = new HashMap<>();
        for (FieldError error : errors) {
            String message = error.getDefaultMessage();
            String key = error.getField();
            if (failedValidations.containsKey(key)) continue;
            failedValidations.put(key, message);
        }

        return new Response<>(HttpStatus.BAD_REQUEST)
            .setError(failedValidations);
    }

    @MessageExceptionHandler(StandardProjectSocketException.class)
    public void projectMessagingException(StandardProjectSocketException e) {
        Long userId = e.getUserId();
        Long projectId = e.getProjectId();
        String sessionUuid = e.getSessionUuid();
        final String USER_ID = KEY.STOMP_CALLER_USER_ID;
        final String SESSION_UUID = KEY.CALLER_SESSION_UUID;

        socket.convertAndSend(
            TOPIC.userProjectErrors(userId, projectId),
            new ExceptionDto()
                .setStatus(e.getCode().value())
                .setMessage(e.getMessage()),
            Map.ofEntries(
                Map.entry(USER_ID, userId.toString()),
                Map.entry(SESSION_UUID, sessionUuid)
            )
        );
    }
}
