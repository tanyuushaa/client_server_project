package com.github.tanyuushaa.server.impl;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.rep.GroupRep;
import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.server.GroupService;
import com.github.tanyuushaa.server.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRep groupRepository;
    private final ProductService productService;

    @Autowired
    public GroupServiceImpl(GroupRep groupRepository, ProductService productService) {
        this.groupRepository = groupRepository;
        this.productService = productService;
    }

    @Override
    public Response<Group> save(Group group) {
        if (group == null) {
            return new Response<>(null, new LinkedList<>(Collections.singleton("Group can't be null")));
        }
        List<String> errors = validateGroup(group);
        if (!errors.isEmpty()) {
            return new Response<>(group, errors);
        }
        if (groupRepository.findGroupByName(group.getName()) != null) {
            errors.add("Group with name " + group.getName() + " already exists");
            return new Response<>(group, errors);
        }

        return new Response<>(groupRepository.save(group), errors);
    }

    @Override
    public List<String> validateGroup(Group group) {
        List<String> errors = new LinkedList<>();
        if (StringUtils.isAllBlank(group.getName())) {
            errors.add("Group name can't be empty");
        }
        if (StringUtils.isAllBlank(group.getDescription())) {
            errors.add("Description can't be empty");
        }

        return errors;
    }

    @Override
    public void delete(Group group) {
        productService.delete(productService.findAllByGroup(group));
        groupRepository.delete(group);
    }

    @Override
    public Group findGroupById(long id) {
        return groupRepository.findGroupById(id);
    }

    @Override
    public Response<Group> update(Group group) {

        List<String> errors = validateGroup(group);
        if (!errors.isEmpty()) {
            return new Response<>(group, errors);
        }

        Group groupWithGivenName = groupRepository.findGroupByName(group.getName());
        if (groupWithGivenName != null && groupWithGivenName.getId() != group.getId()) {
            errors.add("Group with name " + group.getName() + " already exists");
            return new Response<>(group, errors);
        }

        Group groupDB = groupRepository.findGroupById(group.getId());
        groupDB.setName(group.getName());
        groupDB.setDescription(group.getDescription());
        return new Response<>(groupRepository.save(groupDB), errors);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public double findOverallCost(Group group) {

        List<Product> products = productService.findAllByGroup(group);
        double cost = 0;
        for (Product product : products) {
            cost += productService.findOverallCost(product);
        }

        return cost;
    }

    @Override
    public List<Group> searchByKeyword(String keyword) {
        return groupRepository.searchGroups(keyword);
    }

}
