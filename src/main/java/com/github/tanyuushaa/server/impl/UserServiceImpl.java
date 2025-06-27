package com.github.tanyuushaa.server.impl;

import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.Role;
import com.github.tanyuushaa.model.User;
import com.github.tanyuushaa.model.rep.UserRep;
import com.github.tanyuushaa.server.PassService;
import com.github.tanyuushaa.server.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRep userRepository;
    private final PassService passwordService;

    @Autowired
    public UserServiceImpl(UserRep userRepository, PassService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Response<User> addUser(User user) {

        if (user == null) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("User can't be null")));
        }

        List<String> errors = validateUserInputForm(user);
        if (!errors.isEmpty()) {
            return new Response<>(user, errors);
        }
        if (userRepository.findUserByUsername(user.getUsername()) != null) {
            errors.add("User with username " + user.getUsername() + " already exists");
            return new Response<>(user, errors);
        }
        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            errors.add("User with email + " + user.getEmail() + " already exists");
            return new Response<>(user, errors);
        }
        if (!passwordService.comparePasswordAndConfirmationPassword(user.getPassword(), user.getPasswordConfirm())) {
            errors.add("Passwords are different");
            return new Response<>(user, errors);
        }

        user.setRoles(Collections.singleton(new Role(1L, "ROLE_ADMIN")));
        user.setPassword(passwordService.encodePassword(user.getPassword()));
        return new Response<>(userRepository.save(user), new LinkedList<>());
    }

    @Override
    public List<String> validateUserInputForm(User user) {

        List<String> errors = validateUsernameAndPassword(user);
        if (StringUtils.isAllBlank(user.getEmail())) {
            errors.add("Email can't be empty");
        }

        return errors;
    }

    @Override
    public List<String> validateUsernameAndPassword(User user) {

        List<String> errors = new LinkedList<>();
        if (StringUtils.isAllBlank(user.getUsername())) {
            errors.add("Username can't be empty");
        }
        if (StringUtils.isAllBlank(user.getPassword())) {
            errors.add("Password can't be empty");
        }
        return errors;
    }

    @Override
    public Response<User> letUserLogIn(User user) {

        if (user == null) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("User can't be null")));
        }
        List<String> errors = validateUsernameAndPassword(user);
        if (!errors.isEmpty()) {
            return new Response<>(user, errors);
        }

        User userWithGivenUsername = findUserByUsername(user.getUsername());
        if (userWithGivenUsername == null) {
            errors.add("User with username + " + user.getUsername() + " doesn't exist");
            return new Response<>(user, errors);
        }
        if (!passwordService.compareRawAndEncodedPassword(user.getPassword(), userWithGivenUsername.getPassword())) {
            errors.add("Wrong password");
            return new Response<>(user, errors);
        }

        return new Response<>(user, errors);
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}

