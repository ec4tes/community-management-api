package com.devkit.hsd.service.abs;

import com.devkit.hsd.dto.request.AuthRequest;
import com.devkit.hsd.dto.request.LoginRequest;
import com.devkit.hsd.dto.request.RefreshTokenRequest;
import com.devkit.hsd.dto.request.RegisterRequest;
import com.devkit.hsd.dto.response.AuthResponse;
import com.devkit.hsd.entity.User;

public interface IAuthService {

    public AuthResponse register(RegisterRequest registerRequest);

    public AuthResponse login(LoginRequest loginRequest);

    public User getAuthenticatedUser();

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

}
