package it.polito.gruppo2.mamk.lab3.models;

public class RegisteringUser {
    private String username;
    private String password;

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

    public RegisteringUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RegisteringUser() {
    }
}
