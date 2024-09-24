package org.example.backedkickunity.controller.dto;

import lombok.Data;

@Data
public class JoinRequest {

    private String memberId;
    private String password;
    private String email;
    private String name;

}
