package it.polito.gruppo2.mamk.lab3.persistence.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Entity
// PostSQL does not allow User tables
@Table(name = "user_entity")
public class User {
    @Id
    private String username;

    @JsonIgnore
    private String password;

    private String role;

    private Long balance = 2000L;

    private HashSet<String> boughtArchives; //Id of the bought archives

    @JsonCreator
    public User(String username,
                String password) {

        this.username = username;
        this.password = password;
        this.boughtArchives = new HashSet<String>();
    }

    public User() {
        boughtArchives= new HashSet<String>();
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public HashSet<String> getBoughtArchives() {
        if(boughtArchives == null){
            boughtArchives = new HashSet<String>();
        }
        return boughtArchives;
    }

    public void setBoughtArchives(HashSet<String> boughtArchives) {
        this.boughtArchives = boughtArchives;
    }

    @Override
    public boolean equals(Object o) {
        // This equal must include the username, as it is not autogenerated.
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getRole(), user.getRole()) &&
                Objects.equals(getBalance(), user.getBalance()) &&
                Objects.equals(getBoughtArchives(), user.getBoughtArchives());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getRole(), getBalance(), getBoughtArchives());
    }
}