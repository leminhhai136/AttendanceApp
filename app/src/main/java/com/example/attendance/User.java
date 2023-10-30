package com.example.attendance;

public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String name;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValidLogin(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) &&
                this.password.equals(inputPassword);
    }

}
