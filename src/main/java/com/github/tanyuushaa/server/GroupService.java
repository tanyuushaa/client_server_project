package com.github.tanyuushaa.server;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Response;

import java.util.List;

public interface GroupService {

    Response<Group> save(Group group);

    List<String> validateGroup(Group group);

    void delete(Group group);

    Group findGroupById(long id);

    Response<Group> update(Group group);

    double findOverallCost(Group group);

    List<Group> findAll();

    List<Group> searchByKeyword(String keyword);


}