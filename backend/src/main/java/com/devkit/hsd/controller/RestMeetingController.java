package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.CreateMeetingRequest;
import com.devkit.hsd.dto.request.MeetingAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateMeetingRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.MeetingAttendanceResponse;
import com.devkit.hsd.dto.response.MeetingResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/meetings")
@Tag(name = "Toplantı", description = "Toplantı oluşturma ve QR ile toplantı katılım işlemleri")
public class RestMeetingController {

    private final IMeetingService meetingService;

    public RestMeetingController(IMeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Operation(
            summary = "Yeni toplantı oluşturur",
            description = "Sisteme yeni bir toplantı kaydı oluşturur ve toplantıya ait QR katılım bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Toplantı başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "403", description = "Bu işlem için yetki bulunmuyor"),
            @ApiResponse(responseCode = "409", description = "Aynı bilgilerle oluşturulmuş toplantı zaten mevcut"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/create-meeting")
    public ResponseEntity<RestResponse<MeetingResponse>> createMeeting(
            @Valid @RequestBody CreateMeetingRequest request
    ) {
        MeetingResponse meetingResponse = meetingService.createMeeting(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Toplantı başarıyla oluşturuldu.", meetingResponse));
    }

    @Operation(
            summary = "Toplantı katılımını QR ile doğrular",
            description = "QR kod verisini doğrular, kullanıcının toplantıya katılımını kaydeder ve katılım puanını işler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toplantı katılımı başarıyla kaydedildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi, hatalı QR kod veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Toplantı bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Kullanıcı bu toplantıya daha önce katılmış"),
            @ApiResponse(responseCode = "410", description = "QR kodun süresi dolmuş"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/scan")
    public ResponseEntity<RestResponse<MeetingAttendanceResponse>> scanMeetingAttendance(
            @Valid @RequestBody MeetingAttendanceScanRequest request
    ) {
        MeetingAttendanceResponse meetingAttendanceResponse = meetingService.scanMeetingAttendance(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Toplantıya başarıyla katılım sağlandı.", meetingAttendanceResponse));
    }

    @Operation(
            summary = "Toplantı bilgilerini getirir",
            description = "Verilen toplantı ID değerine göre ilgili toplantının detaylı bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toplantı bilgileri başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Toplantı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/meet/{id}")
    public ResponseEntity<RestResponse<MeetingResponse>> getMeetingById(@PathVariable("id") Long meetingId) {
        MeetingResponse meetingResponse = meetingService.getMeetingById(meetingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Toplantı bilgileri başarıyla getirildi.", meetingResponse));

    }

    @GetMapping("/{id}/attendances")
    public ResponseEntity<RestResponse<java.util.List<AttendanceListItemResponse>>> getMeetingAttendances(@PathVariable("id") Long meetingId) {
        java.util.List<AttendanceListItemResponse> attendances = meetingService.getMeetingAttendances(meetingId);
        return ResponseEntity.ok(RestResponse.success("Toplantı katılımcıları başarıyla getirildi.", attendances));
    }

    @Operation(
            summary = "Toplantıyı siler",
            description = "Verilen toplantı ID değerine göre ilgili toplantıyı siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toplantı başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Toplantı bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RestResponse<Void>> deleteMeetingById(@PathVariable("id") Long meetingId) {
        meetingService.deleteMeeting(meetingId);;
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Toplantı başarıyla silindi.", null));
    }

    @Operation(
            summary = "Toplantı bilgilerini günceller",
            description = "Verilen toplantı ID değerine göre ilgili toplantının bilgilerini günceller."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toplantı bilgileri başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "404", description = "Toplantı bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Aynı bilgilerle oluşturulmuş toplantı zaten mevcut"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/update/{id}")
    public ResponseEntity<RestResponse<MeetingResponse>> updateMeeting(@PathVariable("id") Long meetingId, @Valid @RequestBody UpdateMeetingRequest request) {
        MeetingResponse meetingResponse = meetingService.updateMeeting(meetingId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Toplantı bilgileri başarıyla güncellendi.", meetingResponse));
    }

    @Operation(
            summary = "Tüm toplantıları getirir",
            description = "Sistemde kayıtlı tüm toplantıların özet bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toplantılar başarıyla getirildi"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/all")
    public ResponseEntity<RestResponse<java.util.List<MeetingResponse>>> getAllMeetings() {
        java.util.List<MeetingResponse> meetingResponses = meetingService.getAllMeetings();
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Toplantılar başarıyla getirildi.", meetingResponses));
    }



    
    
}
