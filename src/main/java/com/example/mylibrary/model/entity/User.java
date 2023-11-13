package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();



    public User() {


    }

    public User(String name, String email, String password) {
        this.password = password;
        this.email = email;
        this.name = name;
    }

}
