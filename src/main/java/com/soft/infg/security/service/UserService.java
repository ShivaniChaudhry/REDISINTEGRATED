package com.soft.infg.security.service;

import java.util.List;

import com.soft.infg.dao.entity.User;
import com.soft.infg.security.model.UserDto;

public interface UserService {

    User save(UserDto user);
    List<User> findAll();
    void delete(long id);
    User findOne(String username);

    User findById(long id);
}
