package com.infotexa.storageservice.resource;

import com.infotexa.storageservice.domain.Response;
import com.infotexa.storageservice.dtoRequest.ShareRequest;
import com.infotexa.storageservice.service.StorageService;
import com.infotexa.storageservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


import static com.infotexa.storageservice.consatant.Constant.FILE_NAME_HEADER;
import static com.infotexa.storageservice.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/storage")
public class StorageResource {

    private final StorageService storageService;

    private final UserService userService;

    @PostMapping("/folder")
    public ResponseEntity<Response> createFolder(@NotNull Authentication authentication, HttpServletRequest request, @RequestParam(required = false) String parentFolderUuid, @RequestParam String name) {
        var folder = storageService.createFolder(authentication.getName(), parentFolderUuid, name);
        return created(getUri()).body(getResponse(request, Map.of("folder", folder), "folder created successfully", OK));
    }
    @GetMapping("/folder/{folderUuid}")
    public ResponseEntity<Response> getFolder(@PathVariable String folderUuid, HttpServletRequest request) {
        var folder = storageService.getFolder(folderUuid);
        return ok(getResponse(request, Map.of("folder", folder) ,"folder retrieved successfully", OK));
    }

    @GetMapping("/folders/{folderUuid}")
    public ResponseEntity<Response> getFolders(@NotNull Authentication authentication, HttpServletRequest request, @PathVariable String folderUuid) {
        var folders = storageService.getFolders(folderUuid);
        return ok(getResponse(request, Map.of("folders", folders), "folders retrieved successfully", OK));
    }

    // ================= FILES =================

    @PostMapping("/file/upload")
    public ResponseEntity<Response> uploadFiles(
            @NotNull Authentication authentication,
            HttpServletRequest request,
            @RequestParam String folderUuid,
            @RequestPart List<MultipartFile> files
    ) {

        var uploadedFiles = storageService.uploadFile(
                authentication.getName(),
                folderUuid,
                files
        );

        return ok()
                .body(getResponse(
                        request,
                        Map.of("files", uploadedFiles),
                        "files uploaded successfully",
                        OK
                ));
    }

    @GetMapping("/files/{folderUuid}")
    public ResponseEntity<Response> getFiles(
            @NotNull Authentication authentication,
            HttpServletRequest request,
            @PathVariable String folderUuid
    ) {

        var files = storageService.getFiles(folderUuid);

        return ok()
                .body(getResponse(
                        request,
                        Map.of("files", files),
                        "files retrieved successfully",
                        OK
                ));
    }

    @DeleteMapping("/file")
    public ResponseEntity<Response> deleteFile(
            @NotNull Authentication authentication,
            HttpServletRequest request,
            @RequestParam String fileUuid
    ) {

        var result = storageService.deleteFile(fileUuid);

        return ok()
                .body(getResponse(
                        request,
                        Map.of("deleted", result),
                        "file deleted successfully",
                        OK
                ));
    }

    @GetMapping("/root")
    public ResponseEntity<Response> getRootFolder(
            @NotNull Authentication authentication,
            HttpServletRequest request
    ) {
        var folder = storageService.getRootFolder(authentication.getName());
        return ok(getResponse(request, Map.of("folder", folder), "root folder retrieved successfully",OK));
    }

    @GetMapping( value = "/file/download/{fileUuid}" , produces = {APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource>downloadFile(@NotNull Authentication authentication , HttpServletRequest request, @PathVariable("fileUuid") String fileUuid) throws IOException {
        var file = storageService.downloadFile(authentication.getName() , fileUuid);
        Resource resource=new UrlResource(file.filePath().toUri());
        HttpHeaders headers=new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""+file.fileName()+"\""
        );
        headers.add("Access-Control-Expose-Headers","Content-Disposition");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.filePath())))
                .headers(headers)
                .body(resource);

    }

    @PostMapping("/folder/share")
    public ResponseEntity<Response> shareFolder(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody ShareRequest shareRequest
    ) {
        storageService.shareFolder(authentication.getName(), shareRequest);
        return ok(getResponse(request, Map.of(), "Folder shared successfully",OK));
    }
    @PostMapping("/file/share")
    public ResponseEntity<Response> shareFile(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody ShareRequest shareRequest
    ) {
        storageService.shareFile(authentication.getName(), shareRequest);
        return ok(getResponse(request, Map.of(), "File shared successfully",OK));
    }

    private URI getUri(){
        return URI.create("/storage/ticketUuid");
    }

}