package com.implementations.implementations.dao;

import com.implementations.implementations.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IUserRepository  extends JpaRepository <User,Long>{
    Optional<User> findById(Long ID);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

}
