package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.MonthlyHistoryRequest;
import com.devkit.hsd.dto.response.*;
import com.devkit.hsd.service.abs.IStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/stats")
@RestController
@Tag(name = "İstatistik", description = "Kullanıcı istatistikleri, log kayıtları, aylık geçmiş ve liderlik tablosu işlemleri")
public class RestStatsController {

    private final IStatsService statsService;

    public RestStatsController(IStatsService statsService) {
        this.statsService = statsService;
    }

    @Operation(
            summary = "Kullanıcı istatistiklerini getirir",
            description = "Mevcut kullanıcının toplam puan, tamamlanan görev ve benzeri kişisel istatistik bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı istatistikleri başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/my-stats")
    public ResponseEntity<RestResponse<MyStatsResponse>> getMyStats() {
        MyStatsResponse myStatsResponse = statsService.getMyStats();

        return ResponseEntity.ok(
                RestResponse.success("Kullanıcı istatistikleri başarıyla çekildi", myStatsResponse)
        );
    }

    @Operation(
            summary = "Kullanıcının log kayıtlarını getirir",
            description = "Mevcut kullanıcıya ait görev, proje veya sistem hareketlerinden oluşan log kayıtlarını liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log kayıtları başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/logs/my-logs")
    public ResponseEntity<RestResponse<List<MyLogResponse>>> getMyLogs() {
        List<MyLogResponse> myLogResponses = statsService.getMyLogs();

        return ResponseEntity.ok(
                RestResponse.success("Log kayıtları başarıyla çekildi", myLogResponses)
        );
    }

    @Operation(
            summary = "Kullanıcının aylık geçmişini getirir",
            description = "İstek gövdesinde gönderilen ay ve yıl bilgisine göre mevcut kullanıcının aylık istatistik geçmişini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aylık istatistikler başarıyla getirildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/my-monthly-history")
    public ResponseEntity<RestResponse<MonthlyHistoryResponse>> getMonthlyHistory(
            @Valid @RequestBody MonthlyHistoryRequest request
    ) {
        MonthlyHistoryResponse monthlyHistoryResponse = statsService.getMonthlyHistory(request);

        return ResponseEntity.ok(
                RestResponse.success("Aylık istatistikler başarıyla çekildi", monthlyHistoryResponse)
        );
    }

    @Operation(
            summary = "Aylık liderlik tablosunu getirir",
            description = "Mevcut ay içinde kullanıcıların puan durumuna göre sıralandığı liderlik tablosunu döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aylık liderlik tablosu başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/leader-board")
    public ResponseEntity<RestResponse<List<LeaderBoardResponse>>> getCurrentMonthLeaderboard(
            @RequestParam(value = "communityId", required = false) Long communityId
    ) {
        List<LeaderBoardResponse> leaderBoardResponses = statsService.getCurrentMonthLeaderboard(communityId);
        return ResponseEntity.ok(
                RestResponse.success("Aylık lider tablosu başarıyla çekildi!", leaderBoardResponses)
        );
    }
}

