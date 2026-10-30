package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.CreateEventRequest;
import com.devkit.hsd.dto.request.EventAttendanceScanRequest;
import com.devkit.hsd.dto.request.UpdateEventRequest;
import com.devkit.hsd.dto.response.AttendanceListItemResponse;
import com.devkit.hsd.dto.response.EventAttendanceResponse;
import com.devkit.hsd.dto.response.EventResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Etkinlik", description = "Etkinlik oluşturma ve QR ile etkinlik katılım işlemleri")
public class RestEventController {

    private final IEventService eventService;

    public RestEventController(IEventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "Yeni etkinlik oluşturur",
            description = "Sisteme yeni bir etkinlik kaydı oluşturur ve etkinliğe ait QR katılım bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Etkinlik başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "403", description = "Bu işlem için yetki bulunmuyor"),
            @ApiResponse(responseCode = "409", description = "Aynı bilgilerle oluşturulmuş etkinlik zaten mevcut"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/create-event")
    public ResponseEntity<RestResponse<EventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        EventResponse eventResponse = eventService.createEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Etkinlik oluşturuldu", eventResponse));
    }

    @Operation(
            summary = "Etkinlik katılımı QR ile doğrular",
            description = "QR kod verisini doğrular, kullanıcının etkinliğe katılımını kaydeder ve katılım puanını işler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinliğe katılım başarıyla kaydedildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi, hatalı QR kod veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Kullanıcı bu etkinliğe daha önce katılmış"),
            @ApiResponse(responseCode = "410", description = "QR kodun süresi dolmuş"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/scan")
    public ResponseEntity<RestResponse<EventAttendanceResponse>> scanEventAttendance(
            @Valid @RequestBody EventAttendanceScanRequest request
    ) {
        EventAttendanceResponse eventAttendanceResponse = eventService.scanEventAttendance(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Etkinliğe başarıyla katılım sağlandı.", eventAttendanceResponse));
    }

    @Operation(
            summary = "Etkinlik bilgilerini ID ile getirir",
            description = "Belirtilen ID'ye sahip etkinliğin detaylı bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik bilgileri başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Belirtilen ID'ye sahip etkinlik bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<RestResponse<EventResponse>> getEventById(@PathVariable Long eventId) {
        EventResponse eventResponse = eventService.getEventById(eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Etkinlik bilgileri getirildi", eventResponse));
    }

    @GetMapping("/{eventId}/attendances")
    public ResponseEntity<RestResponse<List<AttendanceListItemResponse>>> getEventAttendances(@PathVariable Long eventId) {
        List<AttendanceListItemResponse> attendances = eventService.getEventAttendances(eventId);
        return ResponseEntity.ok(RestResponse.success("Etkinlik katılımcıları getirildi", attendances));
    }


    @Operation(
            summary = "Etkinlik siler",
            description = "Belirtilen ID'ye sahip etkinliği siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Belirtilen ID'ye sahip etkinlik bulunamadı"),
            @ApiResponse(responseCode = "403", description = "Bu işlem için yetki bulunmuyor"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<RestResponse<Void>> deleteEventById(@PathVariable Long eventId) {
        eventService.deleteEventById(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Etkinlik başarıyla silindi", null));
    }

    @Operation(
            summary = "Etkinlik bilgilerini günceller",
            description = "Belirtilen ID'ye sahip etkinliğin bilgilerini günceller."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "404", description = "Belirtilen ID'ye sahip etkinlik bulunamadı"),
            @ApiResponse(responseCode = "403", description = "Bu işlem için yetki bulunmuyor"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/update/{eventId}")
    public ResponseEntity<RestResponse<EventResponse>> updateEvent( @PathVariable  Long eventId, @Valid @RequestBody UpdateEventRequest request) {
        EventResponse eventResponse = eventService.updateEvent(eventId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Etkinlik başarıyla güncellendi", eventResponse));
    }
    @Operation(
            summary = "Tüm etkinlikleri getirir",
            description = "Sistemde kayıtlı tüm etkinliklerin listesini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla getirildi"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/events")
    public ResponseEntity<RestResponse<List<EventResponse>>> getAllEvents(){
        List<EventResponse> eventResponses = eventService.getAllEvents();

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestResponse.success("Etkinlikler başarıyla getirildi", eventResponses));
    }
 
}
