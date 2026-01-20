package fun.plantya.auth.service;

import fun.plantya.auth.common.exception.ConflictException;
import fun.plantya.auth.common.exception.InvalidCredentialsException;
import fun.plantya.auth.common.exception.NotFoundException;
import fun.plantya.auth.common.exception.message.ErrorMessage;
import fun.plantya.auth.common.mapper.ResponseMapper;
import fun.plantya.auth.dto.request.LoginRequest;
import fun.plantya.auth.dto.request.RegisterRequest;
import fun.plantya.auth.dto.response.BaseResponse;
import fun.plantya.auth.dto.response.LoginResponse;
import fun.plantya.auth.dto.response.RegisterResponse;
import fun.plantya.auth.model.User;
import fun.plantya.auth.model.UserRole;
import fun.plantya.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

import static fun.plantya.auth.common.exception.message.ErrorMessage.EMAIL_ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== LOGIN ===== //
    public BaseResponse<LoginResponse> login(LoginRequest request) {
        boolean isByEmail = isLoggedInByEmail(request.userIdOrEmail());

        log.info(
                "event=login_attempt identifier_type={} identifier={}",
                isByEmail ? "email" : "userId",
                maskIdentifier(request.userIdOrEmail())
        );

        User user = isByEmail
                ? authRepository.findByEmail(request.userIdOrEmail()).orElse(null)
                : authRepository.findByUserId(request.userIdOrEmail()).orElse(null);

        if (user == null) {
            log.warn("event=login_failed reason=user_not_found identifier={}", maskIdentifier(request.userIdOrEmail()));
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("event=login_failed reason=invalid_password userId={}", user.getUserId());
            throw new InvalidCredentialsException(ErrorMessage.INVALID_CREDENTIALS);
        }

        log.info("event=login_success userId={} email={}", user.getUserId(), user.getEmail());
        return ResponseMapper.toLoginResponse(user);
    }

    // ===== REGISTER ===== //
    @Transactional
    public BaseResponse<RegisterResponse> register(RegisterRequest request) {
        log.info("event=register_attempt email={}", maskEmail(request.email()));

        if (authRepository.findByEmail(request.email()).isPresent()) {
            log.warn("event=register_failed reason=email_exists email={}", maskEmail(request.email()));
            throw new ConflictException(EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        User savedUser = authRepository.save(user);

        log.info("event=register_success userId={} email={}", savedUser.getUserId(), maskEmail(savedUser.getEmail()));
        return ResponseMapper.toRegisterResponse(savedUser);
    }

    // ===== HELPERS ===== //
    private boolean isLoggedInByEmail(String input) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(input).matches();
    }

    private String maskIdentifier(String value) {
        if (value.contains("@")) {
            return value.replaceAll("(^.).*(@.*$)", "$1***$2");
        }

        return value.length() <= 3 ? "***" : value.substring(0, 2) + "***";
    }

    private String maskEmail(String email) {
        return email.replaceAll("(^.).*(@.*$)", "$1***$2");
    }
}
