package fun.plantya.auth.common.mapper;

import fun.plantya.auth.dto.response.BaseResponse;
import fun.plantya.auth.dto.response.LoginResponse;
import fun.plantya.auth.dto.response.RegisterResponse;
import fun.plantya.auth.model.User;

import java.time.Instant;

public class ResponseMapper {

    public static BaseResponse<LoginResponse> toLoginResponse(User user) {
        LoginResponse loginResponse = new LoginResponse(
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new BaseResponse<>("User " + user.getName() + " logged in successfully", Instant.now(), loginResponse);
    }

    public static BaseResponse<RegisterResponse> toRegisterResponse(User user) {
        RegisterResponse response = new RegisterResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                Instant.now()
        );

        return new BaseResponse<>("User registered successfully", Instant.now(), response);
    }
}
