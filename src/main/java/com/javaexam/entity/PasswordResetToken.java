package com.javaexam.entity;

public class PasswordResetToken {
    private String id;
    private String userId;
    private String token;
    private String expiryDate;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}

    public String getExpiryDate() {return expiryDate;}
    public void setExpiryDate(String expiryDate) {this.expiryDate = expiryDate;}



}
