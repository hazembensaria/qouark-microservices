package com.infotexa.ticketservice.service;

import com.infotexa.ticketservice.model.User;

import java.util.List;

public interface UserService {

    User getTicketUser(String ticketUuid);
    User getUserByUuid(String userUuid);
    User getAssignee(String ticketUuid);
    List<User> getTechSupports();


}
