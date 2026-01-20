package fun.plantya.auth.controller;

import fun.plantya.auth.dto.request.LoginRequest;
import fun.plantya.auth.dto.request.RegisterRequest;
import fun.plantya.auth.dto.response.BaseResponse;
import fun.plantya.auth.dto.response.LoginResponse;
import fun.plantya.auth.dto.response.MeResponse;
import fun.plantya.auth.dto.response.RegisterResponse;
import fun.plantya.auth.security.service.JwtService;
import fun.plantya.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/login")
    public ResponseEntity<@NonNull BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        BaseResponse<LoginResponse> response = authService.login(request);

        String token = jwtService.generateToken(response.data().username());
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(3600)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<@NonNull BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        BaseResponse<RegisterResponse> response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<@NonNull Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // TODO
    @GetMapping("/me")
    public ResponseEntity<@NonNull MeResponse> me(Authentication authentication) {
        String username = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant expiresAt = Instant.now().plusSeconds(3600);

        return ResponseEntity.status(HttpStatus.OK).body(new MeResponse(username, roles, expiresAt));
    }
}
