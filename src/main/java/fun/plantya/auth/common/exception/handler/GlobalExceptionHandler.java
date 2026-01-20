package fun.plantya.auth.common.exception.handler;

import fun.plantya.auth.common.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUnhandledException(
            Throwable exception,
            HttpServletRequest request
    ) {

        String errorCode = "ERR-500-" + UUID.randomUUID();
        String path = request != null ? request.getRequestURI() : "unknown";

        log.error(
                Markers.append("event", "unhandled_exception")
                        .and(Markers.append("status", 500))
                        .and(Markers.append("code", errorCode))
                        .and(Markers.append("path", path)),
                "Unhandled exception",
                exception
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "Internal Server Error",
                500,
                "An unexpected error occurred while processing the request.",
                path,
                errorCode,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
