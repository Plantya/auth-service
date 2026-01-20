package fun.plantya.auth.dto.response;

import fun.plantya.auth.model.UserRole;

public record LoginResponse(
        String username,
        String email,
        UserRole role
) {}
