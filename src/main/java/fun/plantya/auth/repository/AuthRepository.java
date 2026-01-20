package fun.plantya.auth.repository;

import fun.plantya.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final JdbcClient jdbcClient;

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcClient.sql(sql)
                .param(email)
                .query(User.class)
                .optional();
    }

    public Optional<User> findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcClient.sql(sql)
                .param(userId)
                .query(User.class)
                .optional();
    }

    public User save(User user) {
        String sql = """
                INSERT INTO users (email, password, name, role)
                VALUES (:email, :password, :name, :role)
                RETURNING id, user_id, email, password, name, role, created_at, updated_at, deleted_at
                """;

        return jdbcClient.sql(sql)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .param("role", user.getRole().name())
                .query(User.class)
                .single();
    }
}
