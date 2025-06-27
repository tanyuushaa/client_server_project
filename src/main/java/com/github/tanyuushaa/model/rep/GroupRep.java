package com.github.tanyuushaa.model.rep;

import com.github.tanyuushaa.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRep extends JpaRepository<Group, Long> {

    Group findGroupById(long id);

    Group findGroupByName(String name);

    @Query("SELECT g FROM groups g WHERE " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Group> searchGroups(@Param("keyword") String keyword);
}
