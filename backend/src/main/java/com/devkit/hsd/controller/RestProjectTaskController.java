package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.ChangeProjectTaskStatusRequest;
import com.devkit.hsd.dto.request.CreateProjectTaskRequest;
import com.devkit.hsd.dto.request.TransferProjectTaskRequest;
import com.devkit.hsd.dto.request.UpdateProjectTaskRequest;
import com.devkit.hsd.dto.response.ProjectTaskResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IProjectTaskService;
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
@RequestMapping("/api/v1/project-task")
@Tag(name = "Proje Görevi", description = "Proje görevlerini oluşturma, listeleme, alma, tamamlama, iptal etme, devretme ve durum değiştirme işlemleri")
public class RestProjectTaskController {

    private final IProjectTaskService projectTaskService;

    public RestProjectTaskController(IProjectTaskService projectTaskService) {
        this.projectTaskService = projectTaskService;
    }

    @Operation(
            summary = "Yeni proje görevi oluşturur",
            description = "Belirtilen proje için yeni bir görev oluşturur ve oluşturulan görev bilgilerini döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proje görevi başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "403", description = "Bu işlem için yetki bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "İlgili proje veya kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Aynı bilgilerle oluşturulmuş görev zaten mevcut"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/create")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> createProjectTask(
            @Valid @RequestBody CreateProjectTaskRequest createProjectTaskRequest
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.createProjectTask(createProjectTaskRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Proje görevi başarıyla oluşturuldu!", projectTaskResponse));
    }

    @Operation(
            summary = "Müsait görevleri listeler",
            description = "Henüz bir kullanıcı tarafından alınmamış ve müsait durumda olan proje görevlerini liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Müsait görevler başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/available-tasks")
    public ResponseEntity<RestResponse<List<ProjectTaskResponse>>> getAvailableTasks() {
        List<ProjectTaskResponse> availableTasksResponse = projectTaskService.getAvailableTasks();

        return ResponseEntity.ok(
                RestResponse.success("Müsait durumundaki görevler çekildi", availableTasksResponse)
        );
    }

    @Operation(
            summary = "Görevi üzerine alır",
            description = "Belirtilen ID değerine sahip müsait görevi mevcut kullanıcı üzerine alır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla alındı"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev alınabilir durumda değil"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/claim/{id}")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> claimTask(
            @PathVariable Long id
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.claimTask(id);

        return ResponseEntity.ok(
                RestResponse.success("Görev başarıyla alındı.", projectTaskResponse)
        );
    }

    @Operation(
            summary = "Kullanıcının görevlerini listeler",
            description = "Mevcut kullanıcıya atanmış veya kullanıcı tarafından alınmış görevleri liste halinde döner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcının görevleri başarıyla getirildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @GetMapping("/my-tasks")
    public ResponseEntity<RestResponse<List<ProjectTaskResponse>>> myTasks() {
        List<ProjectTaskResponse> projectTaskResponses = projectTaskService.myTasks();

        return ResponseEntity.ok(
                RestResponse.success("Görevler başarıyla çekildi", projectTaskResponses)
        );
    }

    @Operation(
            summary = "Görevi tamamlar",
            description = "Belirtilen ID değerine sahip görevi tamamlandı durumuna alır. Tamamlanan görev için ilgili log ve puan kayıtları oluşturulabilir."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla tamamlandı"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevi tamamlama yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev tamamlanabilir durumda değil veya zaten tamamlanmış"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/complete-task/{id}")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> completeTask(
            @PathVariable Long id
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.completeTask(id);

        return ResponseEntity.ok(
                RestResponse.success("Görev başarıyla tamamlandı", projectTaskResponse)
        );
    }

    @Operation(
            summary = "Görevi iptal eder",
            description = "Belirtilen ID değerine sahip görevi iptal edildi durumuna alır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla iptal edildi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevi iptal etme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev iptal edilebilir durumda değil"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/cancel-task/{id}")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> cancelTask(
            @PathVariable Long id
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.cancelTask(id);

        return ResponseEntity.ok(
                RestResponse.success("Görev başarıyla iptal edildi.", projectTaskResponse)
        );
    }

    @Operation(
            summary = "Görevi başka kullanıcıya devreder",
            description = "Belirtilen ID değerine sahip görevi istek gövdesinde gönderilen kullanıcıya devreder."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev devri başarıyla gerçekleşti"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevi devretme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev veya hedef kullanıcı bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev devredilebilir durumda değil"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/transfer-task/{id}")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> transferTask(
            @PathVariable Long id,
            @Valid @RequestBody TransferProjectTaskRequest transferProjectTaskRequest
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.transferTask(id, transferProjectTaskRequest);

        return ResponseEntity.ok(
                RestResponse.success("Görev değişimi başarıyla gerçekleşti", projectTaskResponse)
        );
    }

    @Operation(
            summary = "Görev durumunu değiştirir",
            description = "Belirtilen ID değerine sahip görevin durumunu istek gövdesinde gönderilen yeni durum bilgisine göre günceller."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev durumu başarıyla değiştirildi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevin durumunu değiştirme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev zaten bu durumda veya durum geçişi uygun değil"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PostMapping("/{id}/change-status")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> changeTaskStatus(
            @Valid @RequestBody ChangeProjectTaskStatusRequest request,
            @PathVariable Long id
    ) {
        ProjectTaskResponse projectTaskResponse = projectTaskService.changeTaskStatus(request, id);

        return ResponseEntity.ok(
                RestResponse.success("Görev durumu başarıyla değiştirildi", projectTaskResponse)
        );
    }

    @Operation (
            summary = "Görevi siler",
            description = "Belirtilen ID değerine sahip görevi sistemden siler."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla silindi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevi silme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev mevcut durumu nedeniyle silinemez"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RestResponse<Void>> deleteTask(@Valid
                                                          @PathVariable Long id) {
        projectTaskService.deleteTask(id);
        return ResponseEntity.ok(
                RestResponse.success("Görev başarıyla silindi!")
        );
    }

    @Operation(

            summary = "Görevi günceller",

            description = "Belirtilen ID değerine sahip görevin başlık, açıklama veya puan bilgilerini kısmi olarak günceller."

    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Görev başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi veya validasyon hatası"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "403", description = "Bu görevi güncelleme yetkisi bulunmuyor"),
            @ApiResponse(responseCode = "404", description = "Görev bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Görev mevcut durumu nedeniyle güncellenemez"),
            @ApiResponse(responseCode = "500", description = "Sunucu tarafında beklenmeyen hata")
    })
    @PatchMapping("/update/{id}")
    public ResponseEntity<RestResponse<ProjectTaskResponse>> updateProjectTask(@Valid @PathVariable Long id,
                                                                              @RequestBody UpdateProjectTaskRequest projectTaskRequest) {

        ProjectTaskResponse projectTaskResponse = projectTaskService.updateProjectTask(id, projectTaskRequest);
        return ResponseEntity.ok(
                RestResponse.success("Görev durumu başarıyla değiştirildi", projectTaskResponse)
        );
    }
}
