package fun.plantya.auth.dto.response;

import java.time.Instant;

public record BaseResponse<T>(
        String message,
        Instant timestamp,
        T data
) {}
