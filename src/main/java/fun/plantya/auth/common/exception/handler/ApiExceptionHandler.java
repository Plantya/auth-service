package fun.plantya.auth.common.exception.handler;

import fun.plantya.auth.common.dto.response.ErrorResponse;
import fun.plantya.auth.common.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleApiException(
            ApiException e,
            HttpServletRequest request
    ) {
        HttpStatus status = getStatus(e);

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(
                    Markers.append("event", "system_exception")
                            .and(Markers.append("status", 500))
                            .and(Markers.append("path", request.getRequestURI())),
                    "System exception"
            );
        } else {
            log.warn(
                    Markers.append("event", "business_exception")
                            .and(Markers.append("status", status.value()))
                            .and(Markers.append("code", e.getError().getCode()))
                            .and(Markers.append("path", request.getRequestURI())),
                    "Business exception"
            );

        }

        ErrorResponse response = new ErrorResponse(
                status.getReasonPhrase(),
                status.value(),
                e.getDetail(),
                request.getRequestURI(),
                e.getError().getCode(),
                Instant.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    private HttpStatus getStatus(ApiException e) {
        return switch (e) {
            case BadRequestException ignored -> HttpStatus.BAD_REQUEST;
            case NotFoundException ignored -> HttpStatus.NOT_FOUND;
            case ConflictException ignored -> HttpStatus.CONFLICT;
            case InvalidCredentialsException ignored -> HttpStatus.UNAUTHORIZED;
            case UnauthorizedException ignored -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
