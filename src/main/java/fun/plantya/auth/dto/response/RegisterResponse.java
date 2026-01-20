package fun.plantya.auth.dto.response;

import fun.plantya.auth.model.UserRole;

import java.time.Instant;

public record RegisterResponse(
        String userId,
        String name,
        String email,
        UserRole role,
        Instant createdAt
) {}
