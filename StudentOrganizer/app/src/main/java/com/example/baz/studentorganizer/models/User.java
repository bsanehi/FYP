package com.example.baz.studentorganizer.models;

/**
 * Created by Baz on 22/11/2017.
 */

public class User {

    private String username;
    private String email;
    private String password;
    private String created_at;
    private String newPassword;
    private String token;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
