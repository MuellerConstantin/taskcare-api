package de.mueller_constantin.taskcare.api.presentation.rest.v1.error;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.user.application.IllegalDefaultAdminAlterationException;
import de.mueller_constantin.taskcare.api.core.user.application.UsernameAlreadyInUseException;
import de.mueller_constantin.taskcare.api.core.user.application.IllegalImportedUserAlterationException;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.InvalidTokenException;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestErrorHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    @Autowired
    public RestErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exc,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("MaxUploadSizeExceededError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exc,
                                                                    HttpHeaders headers,
                                                                    HttpStatusCode status,
                                                                    WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("NotFoundError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException exc,
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode status,
                                                                   WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("NotFoundError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<Object> details = exc.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDto.ValidationErrorDetails(error.getField(), error.getCode(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorDto dto = ErrorDto.builder()
                .error("ValidationError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .details(details)
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("MissingQueryParameterError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ErrorDto.InvalidParameterErrorDetails(exc.getParameterName(),
                        getMessage("javax.validation.constraints.NotNull.message", null)))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException exc,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("MissingPathVariableError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ErrorDto.InvalidParameterErrorDetails(exc.getVariableName(),
                        getMessage("javax.validation.constraints.NotNull.message", null)))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("HttpRequestMethodNotSupportedError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAllow(Objects.requireNonNull(exc.getSupportedHttpMethods()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("HttpMediaTypeNotSupportedError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAccept(Objects.requireNonNull(exc.getSupportedMediaTypes()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException exc,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode status,
                                                                      WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("HttpMediaTypeNotSupportedError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAccept(Objects.requireNonNull(exc.getSupportedMediaTypes()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exc,
                                                        HttpHeaders headers,
                                                        HttpStatusCode status,
                                                        WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("TypeMismatchError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ErrorDto.InvalidParameterErrorDetails(exc.getPropertyName(),
                        getMessage("de.mueller_constantin.taskcare.api.infrastructure.validation.Type.message",
                                new Object[]{exc.getRequiredType().getName()})))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("InvalidPayloadFormatError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exc,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<Object> handleEntityNotFound(NoSuchEntityException exc,
                                                       WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("NotFoundError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<Object> handleUsernameAlreadyInUse(UsernameAlreadyInUseException exc,
                                                       WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("ValidationError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ErrorDto.ValidationErrorDetails("username", "UniqueUsername",
                        getMessage("de.mueller_constantin.taskcare.api.infrastructure.validation.UniqueUsername.message", null)))
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(IllegalDefaultAdminAlterationException.class)
    public ResponseEntity<Object> handleIllegalDefaultAdminAlteration(IllegalDefaultAdminAlterationException exc,
                                                       WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("IllegalDefaultAdminAlterationError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(IllegalImportedUserAlterationException.class)
    public ResponseEntity<Object> handleIllegalImportedUserAlteration(IllegalImportedUserAlterationException exc,
                                                                      WebRequest request) {
        ErrorDto dto = ErrorDto.builder()
                .error("IllegalImportedUserAlterationError")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidToken(InvalidTokenException exc,
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
