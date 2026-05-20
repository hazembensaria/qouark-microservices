package com.infotexa.ticketservice.repository;




import com.infotexa.ticketservice.model.*;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface TicketRepository {


    List<Ticket> getTickets(String userUuid , int page , int size , String status , String type , String filter) ;
    List<Ticket> getUserTickets(String userUuid , int page , int size , String status , String type , String filter) ;
    int getPages(String userUuid , int page , int size , String status , String type , String filter) ;
    int getUserPages(String userUuid, int page, int size, String status, String type, String filter);
    Ticket createTicket(String userUuid , String projectUuid , String title , String description , String type , String priority);
    Ticket getUserTicket(String userUuid, String ticketUuid);
    Ticket getTicket(String userUuid, String ticketUuid);
    List<Comment> getTicketComments(String ticketUuid);
    List<Attachment> getTicketFiles(String ticketUuid);
    List<Task> getTicketTasks(String ticketUuid);
    Comment createComment(String userUuid , String ticketUuid , String comment);
    void deleteFile(String userUuid , String fileUuid);
    Comment updateComment(String userUuid , String commentUuid , String comment);
    void deleteComment(String userUuid , String commentUuid);
    Ticket updateTicket(String userUuid , String ticketUuid , String title , String description , int progress , String type , String priority , String status , String dueDate);
    Task createTask(String userUuid , String ticketUuid , String name , String description , String status);
    List<Ticket> report(String userUuid , String filter , String fromDate , String toDate , List<String> statuses , List<String> types , List<String> priorities);
    List<Ticket> report(String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities);
    Attachment saveTicketFile(Long ticketId, String filename, long size, String formatedSize, String extension, String uri);
    Attachment getTicketFile(String userUuid, String fileUuid);
    User updateAssignee(String userUuid, String assigneeUuid, String ticketUuid);
    User getTicketUser(String ticketUuid);
    Project createProject(String userUuid, String organizationUuid , String name, String description, String status);
    List<Project> getProjectsByStartup(String startupUuid);
}
