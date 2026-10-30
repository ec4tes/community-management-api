package com.devkit.hsd.controller;

import com.devkit.hsd.dto.request.CreateProjectRequest;
import com.devkit.hsd.dto.request.UpdateProjectRequest;
import com.devkit.hsd.dto.response.ProjectBoardResponse;
import com.devkit.hsd.dto.response.ProjectResponse;
import com.devkit.hsd.dto.response.RestResponse;
import com.devkit.hsd.service.abs.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/projects")
@RestController
@Tag(name = "Project", description = "Project CRUD and board endpoints")
public class RestProjectController {

    private final IProjectService projectService;

    public RestProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Create project", description = "Creates a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Project already exists")
    })
    @PostMapping({"", "/save", "/create"})
    public ResponseEntity<RestResponse<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        ProjectResponse projectResponse = projectService.createProject(createProjectRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.success("Proje başarıyla oluşturuldu", projectResponse));
    }

    @Operation(summary = "List projects", description = "Returns all projects")
    @ApiResponse(responseCode = "200", description = "Project list returned")
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "Get project detail", description = "Returns project detail by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @Operation(summary = "Update project", description = "Updates project fields by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "409", description = "Project already exists")
    })
    @PutMapping("{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @Operation(summary = "Delete project", description = "Deletes a project by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping({"{id}", "delete/{id}"})
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @Operation(summary = "Get project board", description = "Returns board columns and cards for project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board returned"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("{id}/board")
    public ResponseEntity<RestResponse<ProjectBoardResponse>> getProjectBoard(@PathVariable Long id) {
        ProjectBoardResponse boardResponse = projectService.getProjectBoard(id);
        return ResponseEntity.ok(RestResponse.success("Veriler başarıyla çekildi.", boardResponse));
    }
}


