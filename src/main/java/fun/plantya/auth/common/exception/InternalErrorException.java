package fun.plantya.auth.common.exception;

public class InternalErrorException extends ApiException {
    public InternalErrorException(ApiError error) {
        super(error);
    }
}