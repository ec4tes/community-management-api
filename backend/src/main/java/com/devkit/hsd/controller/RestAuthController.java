package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.LoginRequest;
import com.devkit.hsd.dto.request.RefreshTokenRequest;
import com.devkit.hsd.dto.request.RegisterRequest;
import com.devkit.hsd.dto.response.AuthResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
@Tag(name = "Auth",description = "Auth işlemleri ve token yönetim endpointleri")
public class RestAuthController {

    private final IAuthService authService;

    public RestAuthController(IAuthService authService) {
        this.authService = authService;
    }


    @Operation(
            summary = "Yeni kullanıcı kaydı",
            description = "Yeni kullanıcı kaydı oluşturma ve auth tokeni dönme"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Kullanıcı başarıyla kayıt oldu!"),
            @ApiResponse(responseCode = "400",description = "Yanlış istek ya validasyon hatası!"),
            @ApiResponse(responseCode = "401",description = "Kullanıcı zaten mevcut!"),
            @ApiResponse(responseCode = "500",description = "Bilinmeyen sunucu hatası!")
    })
    @PostMapping("/register")
    public ResponseEntity<RestResponse<AuthResponse>> register(@Valid @RequestBody  RegisterRequest registerRequest) {
        AuthResponse registerResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Kullanıcı başarıyla oluşturuldu!",registerResponse));
    }

    @Operation(
            summary = "Kullanıcı girişi yapar",
            description = "Kullanıcı bilgilerini doğrular ve başarılı giriş sonrası kimlik doğrulama bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş başarılı"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kullanıcı adı, e-posta veya şifre hatalı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/login")
    public ResponseEntity<RestResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(RestResponse.success("Giriş başarılı",loginResponse));
    }


    @Operation(
            summary = "Token yeniler",
            description = "Geçerli refresh token ile yeni access token üretir ve güncel kimlik doğrulama bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token başarıyla yenilendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Refresh token geçersiz veya süresi dolmuş"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")

    })
    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(RestResponse.success("Giriş başarılı",authResponse));
    }
}


