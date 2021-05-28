package com.implementations.implementations.controllers.payloads;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
/* This is for email and password login.And not for oAuth2.
 */
@Getter
@Setter
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Email
    private String password;


}
