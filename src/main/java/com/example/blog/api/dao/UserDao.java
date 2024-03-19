package com.example.blog.api.dao;

import com.example.blog.api.model.User;
import com.example.blog.api.model.UserProfile;

import java.util.Optional;

public interface UserDao {

    User saveUser (User user);
    UserProfile saveUser (UserProfile userProfile);
    Optional<User> findUserByEmail(String email);
}
