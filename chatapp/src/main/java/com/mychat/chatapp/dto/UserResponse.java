package com.mychat.chatapp.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String nickname;
    private int age;
    private String profilePictureUrl;
}

