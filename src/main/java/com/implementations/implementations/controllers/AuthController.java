package com.implementations.implementations.controllers;

import com.implementations.implementations.controllers.payloads.ApiResponse;
import com.implementations.implementations.controllers.payloads.AuthResponse;
import com.implementations.implementations.controllers.payloads.LoginRequest;
import com.implementations.implementations.controllers.payloads.SignUpRequest;
import com.implementations.implementations.dao.IUserRepository;
import com.implementations.implementations.entities.User;
import com.implementations.implementations.jwt.TokenProvider;
import com.implementations.implementations.oauth2usersinfo.AuthProvider;
import com.implementations.implementations.securities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenProvider tokenProvider;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity("Bad credentials",HttpStatus.BAD_REQUEST);

        }
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                       loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(authentication.isAuthenticated());
        UserPrincipal user=(UserPrincipal )authentication.getPrincipal();
        System.out.println(user.getId());
        System.out.println(user.getId());
        String jwt=tokenProvider.createToken(authentication);
        return new ResponseEntity<>(new AuthResponse(jwt),HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest,Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity("Bad credentials",HttpStatus.BAD_REQUEST);
        }
        //Creating user's account
        User user=new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setName("Modibo");
        try {
            user=userRepository.save(user);
        }catch(Exception e){
            throw new RuntimeException("Oops!! Something bad happened.:-(");
        }

        return new ResponseEntity<>(new ApiResponse(true,user.getEmail(),"1222222"),HttpStatus.CREATED);
    }
}
