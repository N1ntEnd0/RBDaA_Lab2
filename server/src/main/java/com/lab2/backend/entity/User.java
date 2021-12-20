package com.lab2.backend.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rbdip_user")
public class User {
    @Id
    @Column(unique = true)
    private String login;
    private String password;

    @OneToMany
    private List<Task> taskList;

    public User() {

    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
