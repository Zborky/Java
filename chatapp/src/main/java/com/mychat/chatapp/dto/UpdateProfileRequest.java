package com.mychat.chatapp.dto;

public class UpdateProfileRequest {
    private String nickName;
    private String profilePictureUrl;
    private Integer age;

    public String getNickName(){
        return nickName;
    }

    public void setNickname(String nickName){
        this.nickName = nickName;
    }

    public String getProfilePictureUrl(){
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl){
        this.profilePictureUrl = profilePictureUrl;

    }

    public Integer getAge(){
        return age;
    }

    public void setAge(Integer age){
        this.age = age;
    }


}
