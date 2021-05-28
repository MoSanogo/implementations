package com.implementations.implementations.services;

import com.implementations.implementations.dao.IUserRepository;
import com.implementations.implementations.entities.User;
import com.implementations.implementations.securities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/*
This class is responsible for finding user details from the database when the user
enter his credentials.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
           User user=userRepository.findByEmail(email)
                   .orElseThrow(()->new UsernameNotFoundException("No such user with "+ email + " is found."));

        return UserPrincipal.create(user);
    }
    /*The implementation of the following method is not required when implementing the UserDetailsService
    * its implementation right here is to provide us with another way fetch a user with a given id;
    */
    @Transactional
    public UserDetails loadUserById(Long userId){
        User user=userRepository.findById(userId)
                   .orElseThrow(()->new UsernameNotFoundException("No such user with "+ userId + " is found."));
        return UserPrincipal.create(user);

    }
}
