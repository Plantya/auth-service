package fun.plantya.auth.common.exception.message;

import fun.plantya.auth.common.exception.ApiError;

public enum ErrorMessage implements ApiError {
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists"),
    PASSWORD_DOES_NOT_MATCH("PASSWORD_DOES_NOT_MATCH", "Password does not match"),

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),

    INVALID_USER_ID_OR_PASSWORD("INVALID_USER_ID_OR_PASSWORD", "Invalid user id or password"),
    INVALID_NAME("INVALID_NAME", "Invalid name"),
    INVALID_EMAIL_ADDRESS("INVALID_EMAIL_ADDRESS", "Invalid email address"),
    INVALID_PASSWORD("INVALID_PASSWORD", "Password must be 8–64 chars, with upper, lower, and number"),
    INVALID_REGISTRATION_DATA("INVALID_REGISTRATION_DATA", "Invalid registration data"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid credentials"),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error");

    private final String code;
    private final String defaultDetail;

    ErrorMessage(String code, String defaultDetail) {
        this.code = code;
        this.defaultDetail = defaultDetail;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultDetail() {
        return defaultDetail;
    }
}