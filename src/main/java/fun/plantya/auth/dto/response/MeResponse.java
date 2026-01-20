package fun.plantya.auth.dto.response;

import java.time.Instant;
import java.util.List;

public record MeResponse(
        String username,
        List<String> role,
        Instant expiresAt
) {}
