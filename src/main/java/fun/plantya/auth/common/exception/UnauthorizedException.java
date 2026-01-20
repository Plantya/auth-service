package fun.plantya.auth.common.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(ApiError apiError) {
        super(apiError);
    }
}
