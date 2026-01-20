package fun.plantya.auth.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String userId;

    @Email
    private String email;

    private String password;
    private String name;

    private UserRole role;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
