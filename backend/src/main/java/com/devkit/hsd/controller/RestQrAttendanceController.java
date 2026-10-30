package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.QrAttendanceScanRequest;
import com.devkit.hsd.dto.response.EventAttendanceResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IEventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/qr/attendances")
public class RestQrAttendanceController {

    private final IEventService eventService;

    public RestQrAttendanceController(IEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/scan")
    public ResponseEntity<RestResponse<EventAttendanceResponse>> scanAttendance(@Valid @RequestBody QrAttendanceScanRequest request) {
        EventAttendanceResponse response = eventService.scanEventAttendanceByQrCode(request.getCode());
        return ResponseEntity.ok(RestResponse.success("Etkinliğe başarıyla katılım sağlandı.", response));
    }
}
