package com.implementations.implementations.services;

import com.implementations.implementations.dao.IUserRepository;
import com.implementations.implementations.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    IUserRepository userRepository;
    public Optional<User> findById(Long ID){
        return userRepository.findById(ID);
    };
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    };
    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    };

}
