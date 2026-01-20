package fun.plantya.auth.common.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final ApiError error;
    private final String detail;

    protected ApiException(ApiError error) {
        super(error.getDefaultDetail());
        this.error = error;
        this.detail = error.getDefaultDetail();
    }
}
