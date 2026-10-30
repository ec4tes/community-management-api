package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.CommitteeRequest;
import com.devkit.hsd.dto.request.UpdateCommitteeRequest;
import com.devkit.hsd.dto.response.CommitteeMemberResponse;
import com.devkit.hsd.dto.response.CommitteeResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.ICommitteeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/committees")
@Tag(name = "Komite", description = "Komite oluşturma, listeleme ve komite üyelerini görüntüleme işlemleri")
public class RestCommitteeController {

    private final ICommitteeService committeeService;

    public RestCommitteeController(ICommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    @Operation(
            summary = "Yeni komite oluşturur",
            description = "Sisteme yeni bir komite kaydı oluşturur ve oluşturulan komite bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Komite başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "409", description = "Komite zaten mevcut veya çakışan veri var"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/create")
    public ResponseEntity<RestResponse<CommitteeResponse>> createCommittee(
            @Valid @RequestBody CommitteeRequest committeeRequest
    ) {
        CommitteeResponse committeeResponse = committeeService.createCommittee(committeeRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Komite başarıyla oluşturuldu!", committeeResponse));
    }

    @Operation(
            summary = "Tüm komiteleri listeler",
            description = "Sistemde kayıtlı olan tüm komiteleri liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Komite listesi başarıyla getirildi"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/committees")
    public ResponseEntity<RestResponse<List<CommitteeResponse>>> getAllCommittees() {
        List<CommitteeResponse> committeeResponses = committeeService.getAllCommittees();

        return ResponseEntity.ok(
                RestResponse.success("Komite listeleri başarıyla çekildi.", committeeResponses)
        );
    }

    @Operation(
            summary = "Komite üyelerini listeler",
            description = "Verilen komite ID değerine göre ilgili komiteye ait üyeleri liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Komite üyeleri başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Komite bulunamadı"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/{id}/members")
    public ResponseEntity<RestResponse<List<CommitteeMemberResponse>>> getCommitteeMembers(
            @PathVariable Long id
    ) {
        List<CommitteeMemberResponse> committeeMemberResponses = committeeService.getCommitteeMembers(id);

        return ResponseEntity.ok(
                RestResponse.success("Üyeler başarıyla çekildi.", committeeMemberResponses)
        );
    }

    @Operation(
        summary = "komite bilgilerini getirir",
        description = "verilen komite id değerine göre ilgili komite bilgilerini döner"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Komite bilgileri başarıyla getirildi"),
        @ApiResponse(responseCode = "404", description = "Komite bulunamadı"),
        @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/committee/{id}")
    public ResponseEntity<RestResponse<CommitteeResponse>> getCommitteeById(
        @PathVariable Long id
    ) {
    CommitteeResponse committeeResponse = committeeService.getCommitteeById(id);

    return ResponseEntity.ok(
            RestResponse.success("Komite bilgileri başarıyla getirildi.", committeeResponse)
        );
    }

    @Operation(
        summary = "komite bilgilerini günceller",
        description = "verilen komite id değerine göre ilgili komite bilgilerini günceller"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Komite bilgileri başarıyla güncellendi"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
        @ApiResponse(responseCode = "404", description = "Komite bulunamadı"),
        @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/update/{id}")
    public ResponseEntity<RestResponse<CommitteeResponse>> updateCommittee(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCommitteeRequest updateCommitteeRequest
    ) {

        CommitteeResponse committeeResponse = committeeService.updateCommittee(id, updateCommitteeRequest);
        return ResponseEntity.ok(
            RestResponse.success("Komite bilgileri başarıyla güncellendi.", committeeResponse)
        );

    }

    @Operation(
        summary = "komite siler",
        description = "verilen idli komite silinir"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Komite başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Komite bulunamadı"),
        @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    }) 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RestResponse<Void>> deleteCommittee(
        @PathVariable Long id
    ) {
        committeeService.deleteCommittee(id);

        return ResponseEntity.ok(
            RestResponse.success("Komite başarıyla silindi.", null)
        );
    }




}

