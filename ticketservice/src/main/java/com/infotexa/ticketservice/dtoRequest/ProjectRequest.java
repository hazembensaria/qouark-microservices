package com.infotexa.ticketservice.dtoRequest;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {
    private String name;
    private String description;
    private String status;
}
