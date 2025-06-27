package com.github.tanyuushaa.controll;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.server.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupRestController {

    private final GroupService groupService;

    @Autowired
    public GroupRestController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.findAll();
    }

    @PostMapping
    public ResponseEntity<Response<Group>> createGroup(@RequestBody Group group) {
        Response<Group> response = groupService.save(group);
        if (!response.isOkay()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Group>> updateGroup(@PathVariable long id, @RequestBody Group group) {
        group.setId(id);
        Response<Group> response = groupService.update(group);
        if (!response.isOkay()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable long id) {
        Group group = groupService.findGroupById(id);
        groupService.delete(group);
    }
}

