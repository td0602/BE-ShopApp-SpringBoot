package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserService{
    User createUser(UserDTO userDTO) throws DataNotFoundException;
//    tra ve token
    String login(String phoneNumber, String password);
}
