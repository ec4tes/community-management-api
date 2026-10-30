package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.CreateCommunityRequest;
import com.devkit.hsd.dto.request.UpdateCommunityRequest;
import com.devkit.hsd.dto.response.AdminCommunitySummaryResponse;
import com.devkit.hsd.dto.response.CommunityResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.ICommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/community")
@RestController
@Tag(name = "Topluluk (Community) Yönetimi", description = "Topluluklar için CRUD, listeleme ve admin istatistik uçları")
public class RestCommunityController {

    private final ICommunityService communityService;

    public RestCommunityController(ICommunityService communityService) {
        this.communityService = communityService;
    }

    @Operation(summary = "Yeni Topluluk Oluştur", description = "Sisteme yeni bir topluluk kaydeder. Yalnızca SUPER_ADMIN yetkisine sahip kullanıcılar tarafından kullanılabilir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Topluluk başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Validasyon (doğrulama) hatası"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim (Sadece Super Admin)"),
            @ApiResponse(responseCode = "409", description = "Belirtilen slug değerine sahip topluluk zaten mevcut")
    })
    @PostMapping({"", "/save", "/create"})
    public ResponseEntity<RestResponse<CommunityResponse>> createCommunity(@Valid @RequestBody CreateCommunityRequest request) {
        CommunityResponse communityResponse = communityService.createCommunity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Topluluk başarıyla oluşturuldu", communityResponse));
    }

    @Operation(summary = "Tüm Toplulukları Listele", description = "Sistemdeki tüm toplulukların listesini getirir. Yalnızca SUPER_ADMIN yetkisine sahip kullanıcılar görebilir.")
    @ApiResponse(responseCode = "200", description = "Topluluk listesi başarıyla getirildi")
    @GetMapping({"", "/all"})
    public ResponseEntity<RestResponse<List<CommunityResponse>>> getAllCommunities() {
        return ResponseEntity.ok(RestResponse.success("Topluluklar başarıyla getirildi", communityService.getAllCommunities()));
    }

    @Operation(summary = "Topluluk Detayı Getir (ID ile)", description = "Belirtilen benzersiz ID'ye (kimlik numarasına) sahip topluluğun detaylı bilgilerini döner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk başarıyla bulundu ve getirildi"),
            @ApiResponse(responseCode = "404", description = "Belirtilen ID'ye ait topluluk bulunamadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<CommunityResponse>> getCommunityById(@PathVariable Long id) {
        return ResponseEntity.ok(RestResponse.success("Topluluk başarıyla getirildi", communityService.getCommunityById(id)));
    }

    @Operation(summary = "Topluluk Detayı Getir (Slug ile)", description = "Belirtilen benzersiz URL slug (kısa ad) değerine sahip topluluğun detaylı bilgilerini döner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk başarıyla bulundu ve getirildi"),
            @ApiResponse(responseCode = "404", description = "Belirtilen slug değerine ait topluluk bulunamadı")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<RestResponse<CommunityResponse>> getCommunityBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(RestResponse.success("Topluluk başarıyla getirildi", communityService.getCommunityBySlug(slug)));
    }

    @Operation(summary = "Topluluk Bilgilerini Güncelle", description = "Mevcut bir topluluğun bilgilerini günceller. Yalnızca SUPER_ADMIN yetkisine sahip kullanıcılar kullanabilir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Validasyon (doğrulama) hatası"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim (Sadece Super Admin)"),
            @ApiResponse(responseCode = "404", description = "Güncellenmek istenen topluluk bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Güncellenmek istenen slug değeri başka bir topluluk tarafından kullanılıyor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<CommunityResponse>> updateCommunity(@PathVariable Long id, @Valid @RequestBody UpdateCommunityRequest request) {
        return ResponseEntity.ok(RestResponse.success("Topluluk başarıyla güncellendi", communityService.updateCommunity(id, request)));
    }

    @Operation(summary = "Topluluğu Sil", description = "Belirtilen ID'ye sahip topluluğu sistemden tamamen kaldırır. Yalnızca SUPER_ADMIN yetkisine sahip kullanıcılar kullanabilir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim (Sadece Super Admin)"),
            @ApiResponse(responseCode = "404", description = "Silinmek istenen topluluk bulunamadı")
    })
    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<RestResponse<Void>> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success("Topluluk başarıyla silindi", null));
    }

    @Operation(summary = "Topluluk Durumunu Güncelle (Aktif/Pasif)", description = "Belirtilen topluluğun aktif veya pasif durumunu günceller. Yalnızca SUPER_ADMIN yetkisine sahip kullanıcılar kullanabilir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk durumu başarıyla güncellendi"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim (Sadece Super Admin)"),
            @ApiResponse(responseCode = "404", description = "İlgili topluluk bulunamadı")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestResponse<CommunityResponse>> changeCommunityStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        return ResponseEntity.ok(RestResponse.success("Topluluk durumu başarıyla değiştirildi", communityService.changeCommunityStatus(id, isActive)));
    }

    @Operation(summary = "Admin Paneli İçin Topluluk Özeti", description = "Belirtilen topluluğa ait kullanıcı, proje, etkinlik vb. istatistikleri sayısal (count) olarak döner. Yalnızca SUPER_ADMIN kullanabilir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topluluk özeti başarıyla getirildi"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim (Sadece Super Admin)"),
            @ApiResponse(responseCode = "404", description = "İlgili topluluk bulunamadı")
    })
    @GetMapping("/{id}/summary")
    public ResponseEntity<RestResponse<AdminCommunitySummaryResponse>> getCommunitySummaryForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(RestResponse.success("Topluluk admin özeti başarıyla getirildi", communityService.getCommunitySummaryForAdmin(id)));
    }
}

