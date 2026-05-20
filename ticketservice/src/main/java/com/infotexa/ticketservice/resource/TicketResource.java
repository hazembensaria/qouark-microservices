package com.infotexa.ticketservice.resource;


import com.infotexa.ticketservice.domain.Response;
import com.infotexa.ticketservice.dtoRequest.ProjectRequest;
import com.infotexa.ticketservice.dtoRequest.ReportRequest;
import com.infotexa.ticketservice.dtoRequest.TicketRequest;
import com.infotexa.ticketservice.model.Ticket;
import com.infotexa.ticketservice.service.TicketService;


import com.infotexa.ticketservice.service.UserService;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.infotexa.ticketservice.consatant.Constant.FILE_NAME_HEADER;
import static com.infotexa.ticketservice.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/ticket")
public class TicketResource {
        private TicketService ticketService;
        private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<Response>getAllTickets(@NotNull Authentication authentication , HttpServletRequest request, @RequestParam(value = "page" , defaultValue = "0") int page , @RequestParam(value = "size" , defaultValue = "12") int size , @RequestParam(value = "status" , defaultValue = "") String status ,@RequestParam(value = "type" , defaultValue = "") String type, @RequestParam(value = "filter" , defaultValue = "") String filter ){
        var tickets = ticketService.getTickets(authentication.getName() , page , size , status , type , filter);
        return created(getUri()).body(getResponse(request , Map.of("tickets" , tickets) , "tickets retrieved successfully" , OK));
    }

        @GetMapping("/list")
        public ResponseEntity<Response>getTickets(@NotNull Authentication authentication , HttpServletRequest request, @RequestParam(value = "projectUuid" , defaultValue = "") String projectUuid, @RequestParam(value = "page" , defaultValue = "0") int page , @RequestParam(value = "size" , defaultValue = "12") int size , @RequestParam(value = "status" , defaultValue = "") String status ,@RequestParam(value = "type" , defaultValue = "") String type, @RequestParam(value = "filter" , defaultValue = "") String filter ){
            var tickets = ticketService.getTickets(projectUuid , page , size , status , type , filter);
            var pages = ticketService.getPages(authentication.getName() , page , size , status , type , filter);
            return created(getUri()).body(getResponse(request , Map.of("tickets" , tickets , "pages" , pages) , "tickets retrieved successfully" , OK));
        }

    @PostMapping("/create")
    public ResponseEntity<Response>createTicket(@NotNull Authentication authentication , HttpServletRequest request, @RequestParam(value = "title" , defaultValue = "") String title ,@RequestParam(value = "projectUuid" , defaultValue = "") String projectUuid , @RequestParam(value = "description" , defaultValue = "") String description , @RequestParam(value = "status" , defaultValue = "") String status , @RequestParam(value = "type" , defaultValue = "0") String type, @RequestParam(value = "priority" , defaultValue = "") String priority , @RequestParam(value = "files" , required = false ) List<MultipartFile> files){
        var ticket = ticketService.createTicket(authentication.getName() , projectUuid , title , description , type , priority , files);
        return ok(getResponse(request , Map.of("ticket" , ticket ) , "ticket created successfully" , CREATED));
    }
    @PostMapping("/project/create")
    public ResponseEntity<Response> createProject(
            @NotNull Authentication authentication,
            HttpServletRequest request,
            @RequestParam(value = "organizationUuid", defaultValue = "") String organizationUuid,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "status", defaultValue = "ACTIVE") String status
    ) {
        var project = ticketService.createProject(authentication.getName(), organizationUuid, name,description,status);
        return ok(getResponse(request, Map.of("project", project), "project created successfully", CREATED));
    }


    @GetMapping("/projects/{startupUuid}")
    public ResponseEntity<Response> getProjectsByStartup(
            @PathVariable(value = "startupUuid") String startupUuid,
            HttpServletRequest request
    ) {
        var projects = ticketService.getProjectsByStartup(startupUuid);
        return ok(getResponse(request,Map.of("projects", projects),"projects fetched successfully",OK));
    }


    @GetMapping("/{ticketUuid}")
    public ResponseEntity<Response>getTicket(@NotNull Authentication authentication , HttpServletRequest request, @PathVariable(value = "ticketUuid") String ticketUuid ){
        var ticket = ticketService.getUserTicket(authentication.getName() , ticketUuid);
        var comments = ticketService.getTicketComments(ticketUuid);
        var files = ticketService.getTicketFiles(ticketUuid);
        var tasks = ticketService.getTicketTasks(ticketUuid);
        var assignee = userService.getAssignee(ticketUuid);
        var techSupports = userService.getTechSupports();
        var user = ticketService.getTicketUser(ticketUuid);
        return ok(getResponse(request , Map.of("ticket" , ticket , "comments" , comments , "files" , files , "tasks" , tasks , "assignee" , assignee , "techSupports" , techSupports , "user" , user) , "ticket retrieved successfully" , OK));

    }

    @PutMapping("/update")
    public ResponseEntity<Response>updateTicket(@NotNull Authentication authentication , HttpServletRequest request, @RequestBody TicketRequest ticketRequest){
        log.info("Received update ticket request: {}", ticketRequest);
        var ticket = ticketService.updateTicket(authentication.getName() , ticketRequest.getTicketUuid() , ticketRequest.getTitle() , ticketRequest.getDescription() , ticketRequest.getProgress() , ticketRequest.getType() , ticketRequest.getPriority() , ticketRequest.getStatus() , ticketRequest.getDueDate());
        return ok(getResponse(request , Map.of("ticket" , ticket) , "ticket updated successfully" , OK));
    }

    @PutMapping("/update/assignee")
    public ResponseEntity<Response>updateAssignee(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "assigneeUuid" , defaultValue = "") String assigneeUuid , @RequestParam(value = "ticketUuid" , defaultValue = "") String ticketUuid){
            var assignee = ticketService.updateAssignee(authentication.getName() , assigneeUuid , ticketUuid);
            return ok(getResponse(request , Map.of("assignee" , assignee) , "assignee updated successfully" , OK));
    }

    @PostMapping("/comment/create")
    public ResponseEntity<Response>createComment(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "ticketUuid" , defaultValue = "") String ticketUuid ,  @RequestParam(value = "comment" , defaultValue = "") String comment ){
        var createdComment = ticketService.createComment(authentication.getName() , ticketUuid , comment);
        return created(getUri()).body(getResponse(request , Map.of("comment" , createdComment) , "comment created successfully" , CREATED));
    }

    @PutMapping("/comment/update")
    public ResponseEntity<Response>updateComment(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "commentUuid" , defaultValue = "") String commentUuid ,  @RequestParam(value = "comment" , defaultValue = "") String comment ){
        var updatedComment = ticketService.updateComment(authentication.getName() , commentUuid , comment);
        return ok(getResponse(request , Map.of("comment" , updatedComment) , "comment updated successfully" , OK));
    }

    @DeleteMapping("/comment/delete")
    public ResponseEntity<Response>deleteComment(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "commentUuid" , defaultValue = "") String commentUuid ){
        ticketService.deleteComment(authentication.getName() , commentUuid);
        return ok(getResponse(request , emptyMap() , "comment deleted successfully" , OK));
    }

    @PutMapping("/task/create")
    public ResponseEntity<Response>createTask(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "ticketUuid" , defaultValue = "") String ticketUuid ,  @RequestParam(value = "name" , defaultValue = "") String name ,  @RequestParam(value = "description" , defaultValue = "") String description ,  @RequestParam(value = "status" , defaultValue = "") String status ){
        var createdTask = ticketService.createTask(authentication.getName() , ticketUuid , name , description , status);
        return ok(getResponse(request , Map.of("task" , createdTask) , "task created successfully" , OK));
    }

    @PostMapping("/file/upload")
    public ResponseEntity<Response>uploadFiles(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "ticketUuid" , defaultValue = "") String ticketUuid ,  @RequestParam(value = "files" , defaultValue = "") List<MultipartFile> files ){
        var newFiles = ticketService.uploadFiles(authentication.getName() , ticketUuid , files);
        return created(getUri()).body(getResponse(request , Map.of("files" , newFiles) , "files uploaded successfully" , CREATED));
    }

    @DeleteMapping("/file/delete")
    public ResponseEntity<Response>deleteFile(@NotNull Authentication authentication , HttpServletRequest request,  @RequestParam(value = "fileUuid" , defaultValue = "") String fileUuid){
        ticketService.deleteFile(authentication.getName() , fileUuid);
        return ok(getResponse(request , emptyMap() , "file deleted successfully" , OK));
    }

    @GetMapping("/file/download/{fileUuid}")
    public ResponseEntity<Resource>downloadFile(@NotNull Authentication authentication , HttpServletRequest request, @PathVariable("fileUuid") String fileUuid) throws IOException {
        var filePath = ticketService.downloadFile(authentication.getName() , fileUuid);
        var resource = new UrlResource(filePath.toUri());
        var headers = new HttpHeaders();
        headers.add(FILE_NAME_HEADER,  resource.getFilename());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;" + FILE_NAME_HEADER + "=" + resource.getFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(headers)
                .body(resource);

    }

    @PostMapping("/report")
    public ResponseEntity<Response>report(@NotNull Authentication authentication , HttpServletRequest request,  @RequestBody ReportRequest report){
        var tickets = ticketService.report(authentication.getName() , report.getFilter() , report.getFromDate() , report.getToDate() , report.getStatuses() , report.getTypes() , report.getPriorities());
        return ok(getResponse(request , Map.of("tickets" , tickets) , "report generated successfully" , OK));
    }

    @PostMapping( value = "/report/download" , produces = {APPLICATION_PDF_VALUE})
    public ResponseEntity<ResponseEntity.BodyBuilder>exportPdf(@NotNull Authentication authentication , HttpServletResponse response, @RequestBody ReportRequest report){
        ticketService.exportPdf(response , authentication.getName() , report.getFilter() , report.getFromDate() , report.getToDate() , report.getStatuses() , report.getTypes() , report.getPriorities());
        return ok().build();
    }







        private URI getUri(){
            return URI.create("/ticket/ticketUuid");
        }

}
