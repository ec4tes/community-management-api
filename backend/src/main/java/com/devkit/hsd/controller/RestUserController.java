package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.AddSkillRequest;
import com.devkit.hsd.dto.request.AddSocialLinkRequest;
import com.devkit.hsd.dto.request.ChangePasswordRequest;
import com.devkit.hsd.dto.request.UpdateUserRequest;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.dto.response.UserResponse;
import com.devkit.hsd.service.abs.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Kullanıcı", description = "Kullanıcı bilgilerini görüntüleme, listeleme ve şifre değiştirme işlemleri")
public class RestUserController {

    private final IUserService userService;

    public RestUserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Kullanıcıyı ID ile getirir",
            description = "Belirtilen ID değerine sahip kullanıcının bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu kullanıcı bilgisine erişim yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<UserResponse>> getFindById(
            @PathVariable Long id
    ) {
        UserResponse userByIdResponse = userService.getUserById(id);

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı başarıyla çekildi", userByIdResponse)
        );
    }

    @Operation(
            summary = "Mevcut kullanıcı bilgilerini getirir",
            description = "Kimliği doğrulanmış mevcut kullanıcının profil bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mevcut kullanıcı bilgileri başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/me")
    public ResponseEntity<RestResponse<UserResponse>> getMe() {
        UserResponse userResponse = userService.getMe();

        return ResponseEntity.ok(
                RestResponse.success("İşlem başarıyla gerçekleşti", userResponse)
        );
    }

    @Operation(
            summary = "Tüm kullanıcıları listeler",
            description = "Sistemde kayıtlı olan tüm kullanıcıları liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı listesi başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Kullanıcı listesini görüntüleme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/all")
    public ResponseEntity<RestResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userResponseList = userService.getAllUsers();

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı listesi başarıyla çekildi", userResponseList)
        );
    }

    @Operation(
            summary = "Kullanıcı şifresini değiştirir",
            description = "Mevcut kullanıcının eski şifresini doğrulayarak yeni şifre ile güncelleme işlemi yapar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Şifre başarıyla değiştirildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli veya mevcut şifre hatalı"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/change-password")
    public ResponseEntity<RestResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        userService.changePassword(changePasswordRequest);

        return ResponseEntity.ok(
                RestResponse.success("Şifre başarıyla değiştirildi")
        );
    }

    @Operation(
            summary = "Kullanıcı profilini günceller",
            description = "Mevcut kullanıcının profil bilgilerini kısmi olarak günceller. Sadece request içinde gönderilen alanlar güncellenir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Username veya email zaten kullanılıyor"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/update")
    public ResponseEntity<RestResponse<UserResponse>> updateUserProfile(UpdateUserRequest updateUserRequest) {
        UserResponse updated = userService.updateUserProfile(updateUserRequest);

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı bilgileri başarıyla güncellendi",updated)
        );


    }

    @Operation(
            summary = "Kullanıcı hesabını devre dışı bırakır",
            description = "Mevcut kullanıcının hesabını devre dışı bırakır. Bu işlem geri alınamaz ve kullanıcı hesabına erişimi engeller."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hesap başarıyla devre dışı bırakıldı"),
            @ApiResponse(responseCode = "400", description = "Hesap zaten devre dışı bırakılmış"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/me/deactivate")
    public ResponseEntity<RestResponse<Void>> deactivateMyAccount() {
        userService.deactivateMyAccount();

        return ResponseEntity.ok(
                RestResponse.success("Hesabınız başarıyla devre dışı bırakıldı")
        );
    }

    @Operation(
            summary = "Belirtilen kullanıcıyı devre dışı bırakır",
            description = "Yönetici yetkisi olan bir kullanıcı tarafından, belirtilen userId'ye sahip kullanıcının hesabını devre dışı bırakır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı başarıyla devre dışı bırakıldı"),
            @ApiResponse(responseCode = "400", description = "Kullanıcı zaten devre dışı bırakılmış"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi gerçekleştirmek için yeterli yetkiye sahip değilsiniz"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<RestResponse<Void>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı başarıyla devre dışı bırakıldı")
        );
    }

    @Operation(
            summary = "Belirtilen kullanıcıyı etkinleştirir",
            description = "Yönetici yetkisi olan bir kullanıcı tarafından, belirtilen userId'ye sahip kullanıcının hesabını etkinleştirir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı başarıyla etkinleştirildi"),
            @ApiResponse(responseCode = "400", description = "Kullanıcı zaten etkin"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi gerçekleştirmek için yeterli yetkiye sahip değilsiniz"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<RestResponse<Void>> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı başarıyla etkinleştirildi")
        );
    }

    @Operation(
            summary = "Kullanıcıya yeni bir yetenek ekler",
            description = "Mevcut kullanıcıya, gönderilen AddSkillRequest içeriğine göre yeni bir yetenek ekler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Yetenek başarıyla eklendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/me/skill")
    public ResponseEntity<RestResponse<Void>> addSkill(@Valid @RequestBody AddSkillRequest addSkillRequest) {
        userService.addSkill(addSkillRequest);

        return ResponseEntity.ok(
                RestResponse.success("Yetenek başarıyla eklendi")
        );
    }

    @Operation(
            summary = "Kullanıcıdan bir yeteneği siler",
            description = "Mevcut kullanıcıdan, belirtilen skillId'ye sahip yeteneği siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Yetenek başarıyla silindi"),
            @ApiResponse(responseCode = "400", description = "Yetenek zaten silinmiş veya geçersiz skillId"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı veya yetenek bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @DeleteMapping("/me/skill-delete/{skillId}")
    public ResponseEntity<RestResponse<Void>> deleteSkill(@PathVariable Long skillId) {
        userService.deleteSkill(skillId);

        return ResponseEntity.ok(
                RestResponse.success("Yetenek başarıyla silindi")
        );
    }
    @Operation(
            summary = "Kullanıcıya yeni bir sosyal bağlantı ekler",
            description = "Mevcut kullanıcıya, gönderilen AddSocialLinkRequest içeriğine göre yeni bir sosyal bağlantı ekler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sosyal bağlantı başarıyla eklendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/me/social-link")
    public ResponseEntity<RestResponse<Void>> addSocialLink(@Valid @RequestBody AddSocialLinkRequest addSocialLinkRequest) {
        userService.addSocialLink(addSocialLinkRequest);

        return ResponseEntity.ok(
                RestResponse.success("Sosyal bağlantı başarıyla eklendi")
        );
    }
    @Operation(
            summary = "Kullanıcıdan bir sosyal bağlantıyı siler",
            description = "Mevcut kullanıcıdan, belirtilen socialLinkId'ye sahip sosyal bağlantıyı siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sosyal bağlantı başarıyla silindi"),
            @ApiResponse(responseCode = "400", description = "Sosyal bağlantı zaten silinmiş veya geçersiz socialLinkId"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı veya sosyal bağlantı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @DeleteMapping("/me/social-link-delete/{socialLinkId}")     
    public ResponseEntity<RestResponse<Void>> deleteSocialLink(@PathVariable Long socialLinkId) {
        userService.deleteSocialLink(socialLinkId);

        return ResponseEntity.ok(
                RestResponse.success("Sosyal bağlantı başarıyla silindi")
        );
    }
    


}

