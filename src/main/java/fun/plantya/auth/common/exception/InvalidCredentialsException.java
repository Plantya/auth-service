package fun.plantya.auth.common.exception;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException(ApiError apiError) {
        super(apiError);
    }
}