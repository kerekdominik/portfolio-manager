package com.portfolio.controller;

import com.portfolio.dto.GroupRequestDto;
import com.portfolio.entity.Group;
import com.portfolio.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestBody GroupRequestDto group) {
        Group newGroup = new Group();
        newGroup.setName(group.getName());
        groupRepository.save(newGroup);
        return ResponseEntity.ok("Group created successfully");
    }
}
