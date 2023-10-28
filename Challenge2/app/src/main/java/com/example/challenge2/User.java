package com.example.challenge2;

import java.io.Serializable;

public class User implements Serializable {
    private String username,password;

    public User(String username,String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String owner) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String owner) {
        this.password = password;
    }


}
