package com.github.tanyuushaa.server;

import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.User;

import java.util.List;

public interface UserService {

    Response<User> addUser(User user);

    List<String> validateUserInputForm(User user);

    List<String> validateUsernameAndPassword(User user);

    Response<User> letUserLogIn(User user);

    User findUserById(long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    List<User> getAllUsers();
}
