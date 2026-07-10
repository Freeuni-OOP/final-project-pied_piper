package com.lecturboxd.exception;

import com.lecturboxd.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleValidationIncludesFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("obj", "email", "must not be blank")
        ));

        ResponseEntity<ApiErrorResponse> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals("must not be blank", response.getBody().getFieldErrors().get("email"));
    }

    @Test
    void handleInvalidUniversityEmail() {
        assertStatus(handler.handleInvalidUniversityEmail(
                new InvalidUniversityEmailException("bad email"), request), 400);
    }

    @Test
    void handleUserAlreadyExists() {
        assertStatus(handler.handleUserAlreadyExists(
                new UserAlreadyExistsException("exists"), request), 409);
    }

    @Test
    void handleConflict() {
        assertStatus(handler.handleConflict(new ConflictException("conflict"), request), 409);
    }

    @Test
    void handleInvalidOtp() {
        assertStatus(handler.handleInvalidOtp(new InvalidOtpException("otp"), request), 400);
    }

    @Test
    void handleUnauthorizedVariants() {
        assertStatus(handler.handleUnauthorized(new UnauthorizedException("no"), request), 401);
        assertStatus(handler.handleUnauthorized(new BadCredentialsException("bad"), request), 401);
    }

    @Test
    void handleForbiddenVariants() {
        assertStatus(handler.handleForbidden(new ForbiddenException("forbid"), request), 403);
        assertStatus(handler.handleForbidden(new AccessDeniedException("denied"), request), 403);
    }

    @Test
    void handleNotFound() {
        assertStatus(handler.handleNotFound(new ResourceNotFoundException("missing"), request), 404);
    }

    @Test
    void handleBadRequest() {
        assertStatus(handler.handleBadRequest(new BadRequestException("bad"), request), 400);
    }

    @Test
    void handleMail() {
        ResponseEntity<ApiErrorResponse> response = handler.handleMail(
                new MailSendException("smtp"), request);
        assertEquals(503, response.getStatusCode().value());
        assertEquals("Failed to send verification email. Check Gmail app password.",
                response.getBody().getMessage());
    }

    @Test
    void handleGeneral() {
        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(
                new RuntimeException("boom"), request);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    private static void assertStatus(ResponseEntity<ApiErrorResponse> response, int status) {
        assertEquals(status, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("/api/test", response.getBody().getPath());
    }
}
