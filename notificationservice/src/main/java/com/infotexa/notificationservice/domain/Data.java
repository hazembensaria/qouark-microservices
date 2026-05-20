package com.infotexa.notificationservice.domain;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Data {

    private String email;
    private String name;
    private String token;
    private String ticketTitle;
    private String ticketNumber;
    private String priority;
    private String comment;
    private String date;
    private String files;
}
