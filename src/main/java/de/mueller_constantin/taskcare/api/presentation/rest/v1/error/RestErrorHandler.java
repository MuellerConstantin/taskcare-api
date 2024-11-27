package de.mueller_constantin.taskcare.api.presentation.rest.v1.error;

import de.mueller_constantin.taskcare.api.infrastructure.security.token.InvalidTokenException;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class RestErrorHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Object> handleAuthentication(InvalidTokenException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidTokenError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException exc,
                                                         WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidCredentialsError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabled(DisabledException exc,
                                                 WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AccountDisabledError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLocked(LockedException exc,
                                               WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AccountLockedError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidCredentialsError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AuthenticationError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException exc,
                                                     WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InsufficientPermissionsError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception exc, WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        log.error("Unexpected error occurred", exc);

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }
}
