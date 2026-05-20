package com.infotexa.ticketservice.model;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private Long projectId;
    private String projectUuid;
    private String name;
    private String description;
    private String status;
    private Long ownerId;
    private String startDate;
    private String endDate;
    private String createdAt;
    private String updatedAt;
}
