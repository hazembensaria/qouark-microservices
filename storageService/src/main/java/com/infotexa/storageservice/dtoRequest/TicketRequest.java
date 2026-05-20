package com.infotexa.storageservice.dtoRequest;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    private Long ticketId;
    private String ticketUuid;
    private String title;
    private String description;
    private String status;
    private String type;
    private String priority;
    private Integer fileCount;
    private Integer commentCount;
    private Integer progress;
    private String dueDate;
    private String createdAt;
    private String updatedAt;

}
