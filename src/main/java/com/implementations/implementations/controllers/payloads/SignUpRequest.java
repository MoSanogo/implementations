package com.implementations.implementations.controllers.payloads;
/* This is for email and password login.And not for oAuth2.
 */

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
@Getter @Setter
public class SignUpRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;

}
