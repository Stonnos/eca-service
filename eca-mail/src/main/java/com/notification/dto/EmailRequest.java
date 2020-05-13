package com.notification.dto;

import lombok.Data;

@Data
public class EmailRequest {
    
    private String sender;
    
    private String receiver;
    private String subject;
   
    private String message;
    private boolean html;
}
