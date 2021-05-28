package com.implementations.implementations.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.implementations.implementations.oauth2usersinfo.AuthProvider;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@ToString
@Table(name="users",uniqueConstraints={
        @UniqueConstraint(columnNames="email")
})
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable=false)
    private String email;

    private String imageUrl;

    @Column(nullable=true)
    private Boolean emailVerified;

    @JsonIgnore
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    private String providerId;

}
