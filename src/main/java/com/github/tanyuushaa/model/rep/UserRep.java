package com.github.tanyuushaa.model.rep;

import com.github.tanyuushaa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRep extends JpaRepository<User, Long> {

    User findUserById(long id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);

}
