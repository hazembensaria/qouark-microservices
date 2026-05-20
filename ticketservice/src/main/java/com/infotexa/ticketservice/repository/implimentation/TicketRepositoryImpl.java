package com.infotexa.ticketservice.repository.implimentation;


import com.infotexa.ticketservice.exception.ApiException;
import com.infotexa.ticketservice.model.*;
import com.infotexa.ticketservice.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.infotexa.ticketservice.query.TicketQuery.*;
import static com.infotexa.ticketservice.utils.QueryUtils.*;
import static com.infotexa.ticketservice.utils.TicketUtils.randomUUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

    private final JdbcClient jdbc;


    @Override
    public List<Ticket> getTickets(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectTicketsQuery(status , type , filter);
            return jdbc.sql(query)
                    .params( Map.of("size" , size , "status" , status , "type" , type , "filter" , filter  ,"offset" ,  offSet.apply(size , page) ))
                    .query(Ticket.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public List<Ticket> getUserTickets(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            log.info("htis is my uuid from front user" + userUuid);

            var query = createSelectUserTicketsQuery(status , type , filter);
            return jdbc.sql(query)
                    .params( Map.of("projectUuid" , userUuid , "size" , size , "status" , status , "type" , type , "filter" , filter  ,"offset" ,  offSet.apply(size , page) ))
                    .query(Ticket.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }


    @Override
    public int getPages(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectPagesQuery(status , type , filter);
            return jdbc.sql(query)
                    .params( Map.of("size" , size , "status" , status , "type" , type , "filter" , filter ))
                    .query(Integer.class).single();
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public int getUserPages(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectUserPagesQuery(status , type , filter);
            return jdbc.sql(query)
                    .params( Map.of("userUuid" , userUuid, "size" , size , "status" , status , "type" , type , "filter" , filter ))
                    .query(Integer.class).single();
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Ticket createTicket(String userUuid, String projectUuid , String title, String description, String type, String priority) {
        try {
            log.info("userUuid={}", userUuid);
            log.info("projectUuid={}", projectUuid);
                return jdbc.sql(CREATE_TICKET_FUNCTION)
                    .params( Map.of("ticketUuid" , randomUUID.get() , "projectUuid" ,projectUuid , "userUuid" , userUuid , "title" , title , "description" , description  , "type" , type , "priority" , priority))
                    .query(Ticket.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Ticket getUserTicket(String userUuid, String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_BY_USER_UUID_QUERY)
                    .params( Map.of("userUuid" , userUuid , "ticketUuid" , ticketUuid))
                    .query(Ticket.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Ticket getTicket(String userUuid, String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_QUERY)
                    .params( Map.of("userUuid" , userUuid , "ticketUuid" , ticketUuid))
                    .query(Ticket.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public List<Comment> getTicketComments(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_COMMENTS_QUERY)
                    .params( Map.of("ticketUuid" , ticketUuid))
                    .query(Comment.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Ticket with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public List<Task> getTicketTasks(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_TASKS_QUERY)
                    .params( Map.of("ticketUuid" , ticketUuid))
                    .query(Task.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Ticket with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Comment createComment(String userUuid, String ticketUuid, String comment) {
        try {
            return jdbc.sql(CREATE_COMMENT_FUNCTION)
                    .params( Map.of("commentUuid" , randomUUID.get() ,"userUuid" , userUuid , "ticketUuid" , ticketUuid , "comment" , comment))
                    .query(Comment.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Comment with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public List<Attachment> getTicketFiles(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_FILES_QUERY)
                    .params( Map.of( "ticketUuid" , ticketUuid ))
                    .query(Attachment.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Files with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }


    @Override
    public void deleteFile(String userUuid, String fileUuid) {
        try {
             jdbc.sql(DELETE_FILES_QUERY)
                    .params( Map.of("userUuid" , userUuid , "fileUuid" , fileUuid))
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Files with fileUuid %s not found", fileUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Comment updateComment(String userUuid, String commentUuid, String comment) {
        try {
            return jdbc.sql(UPDATE_COMMENT_FUNCTION)
                    .params( Map.of("commentUuid" , commentUuid , "userUuid" , userUuid , "comment" , comment))
                    .query(Comment.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Comments with commentUuid %s not found", commentUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public void deleteComment(String userUuid, String commentUuid) {
        try {
             jdbc.sql(DELETE_COMMENT_QUERY)
                    .params( Map.of("commentUuid" , commentUuid ))
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("Comments with commentUuid %s not found", commentUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Ticket updateTicket(String userUuid, String ticketUuid, String title, String description, int progress, String type, String priority, String status, String dueDate) {
        try {
            log.info("{} this is progress", progress);
            return jdbc.sql(UPDATE_TICKET_FUNCTION)
                    .params( Map.of("ticketUuid" , ticketUuid , "userUuid" , userUuid , "title" , title , "description" , description  , "type" , type , "priority" , priority , "progress" , progress , "status" , status , "dueDate" , dueDate))
                    .query(Ticket.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("ticket with commentUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Task createTask(String userUuid, String ticketUuid, String name, String description, String status) {
        try {
            return jdbc.sql(CREATE_TASK_FUNCTION)
                    .params( Map.of("userUuid" , userUuid , "ticketUuid" , ticketUuid , "taskUuid" , randomUUID.get() , "name" , name , "description" , description  , "status" , status ))
                    .query(Task.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("ticket with commentUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public List<Ticket> report(String userUuid, String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities) {
        try {
            var query = createTicketReportQuery(userUuid , filter, fromDate , toDate , statuses , types , priorities);
            return jdbc.sql(query)
                    .query(Ticket.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("user with user %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public List<Ticket> report(String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities) {
        try {
            var query = createTicketReportQuery(filter, fromDate , toDate , statuses , types , priorities);
            return jdbc.sql(query)
                    .query(Ticket.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(" ticket not found");
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Attachment saveTicketFile(Long ticketId, String filename, long size, String formatedSize, String extension, String uri) {
        try {
            return jdbc.sql(SAVE_TICKET_FILE_FUNCTION)
                    .params( Map.of("fileUuid" , randomUUID.get() , "ticketId" , ticketId , "filename" , filename , "size" , size , "formatedSize" , formatedSize , "extension" , extension  , "uri" , uri))
                    .query(Attachment.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("file with name %s not found", filename));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Attachment getTicketFile(String userUuid, String fileUuid) {
        try {
            return jdbc.sql(SELECT_FILES_BY_USER_UUID_QUERY)
                    .params( Map.of("userUuid" , userUuid , "fileUuid" , fileUuid))
                    .query(Attachment.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("file with userUuid %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public User updateAssignee(String userUuid, String assigneeUuid, String ticketUuid) {
        log.info(assigneeUuid + " this is the assignee uuid");
        try {
            return jdbc.sql(UPDATE_ASSIGNEE_FUNCTION)
                    .params( Map.of("userUuid" , assigneeUuid , "ticketUuid" , ticketUuid))
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("ticket with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public User getTicketUser(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_USER_QUERY)
                    .params( Map.of("ticketUuid" , ticketUuid))
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("ticket with ticketUuid %s not found", ticketUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public Project createProject(String userUuid,String organizationUuid , String name, String description, String status) {

        try {
            return jdbc.sql(CREATE_PROJECT_FUNCTION)
                    .params(Map.of(
                            "userUuid", userUuid,
                            "organizationUuid" , organizationUuid,
                            "name", name,
                            "description", description,
                            "status", status
                    ))
                    .query(Project.class)
                    .single();

        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException("Project not found or creation failed");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException("An error occurred while creating project");
        }
    }

    public List<Project> getProjectsByStartup(String startupUuid) {
        try {
            return jdbc.sql(GET_PROJECTS_BY_STARTUP)
                    .param("startupUuid", startupUuid)
                    .query(Project.class)
                    .list();

        } catch (Exception e) {
            log.error("Error fetching projects for startup {}", startupUuid, e);
            throw new ApiException("Unable to fetch projects");
        }
    }

    private final BiFunction<Integer , Integer , Integer> offSet = (size , page) -> page==0 ?0 : page == 1 ? size : (page - 1) * size;

}
