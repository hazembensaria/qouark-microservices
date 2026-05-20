package com.infotexa.ticketservice.service;


import com.infotexa.ticketservice.model.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface TicketService {

    List<Ticket> getTickets(String projectUuid , int page , int size , String status , String type , String filter) ;
    int getPages(String userUuid , int page , int size , String status , String type , String filter) ;
    Ticket createTicket(String userUuid , String projectUuid ,  String title , String description , String type , String priority , List<MultipartFile> files);
    Ticket getUserTicket(String userUuid, String ticketUuid);
    List<Comment> getTicketComments(String ticketUuid);
    List<Attachment> getTicketFiles(String ticketUuid);
    List<Attachment> uploadFiles(String userUuid , String ticketUuid , List<MultipartFile> files);
    List<Task> getTicketTasks(String ticketUuid);
    Comment createComment(String userUuid , String ticketUuid , String comment);
    void deleteFile(String userUuid , String fileUuid);
    Path downloadFile(String userUuid , String fileUuid);
    Comment updateComment(String userUuid , String commentUuid , String comment);
    void deleteComment(String userUuid , String commentUuid);
    Ticket updateTicket(String userUuid , String ticketUuid , String title , String description , int progress , String type , String priority , String status , String dueDate);
    Task createTask(String userUuid , String ticketUuid , String name , String description , String status);
    Attachment getTicketFile(String userUuid ,String fileUuid);
    List<Ticket> report(String userUuid , String filter , String fromDate , String toDate , List<String> statues , List<String> types , List<String> priorities);
    void exportPdf(HttpServletResponse response , String userUuid , String filter , String fromDate , String toDate , List<String> statuses , List<String> types , List<String> priorities);
    User updateAssignee(String userUuid , String assigneeUuid ,String ticketUuid );
    User getTicketUser(String ticketUuid);
    Project createProject(String userUuid, String organizationUuid, String name, String description, String status);
    List<Project> getProjectsByStartup(String startupUuid);
}
