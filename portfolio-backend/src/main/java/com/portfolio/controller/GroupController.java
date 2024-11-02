package com.portfolio.controller;

import com.portfolio.dto.GroupRequestDto;
import com.portfolio.dto.GroupResponseDto;
import com.portfolio.entity.Group;
import com.portfolio.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;

    // Create a new group
    @PostMapping
    public ResponseEntity<Map<String, String>> createGroup(@RequestBody GroupRequestDto group) {
        Group newGroup = new Group();
        newGroup.setName(group.getName());
        groupRepository.save(newGroup);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Group created successfully");
        return ResponseEntity.ok(response);
    }

    // Retrieve all groups
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        List<GroupResponseDto> groupDtos = groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDtos);
    }

    // Update an existing group by ID
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateGroup(@PathVariable Long id, @RequestBody GroupRequestDto groupDto) {
        return groupRepository.findById(id)
                .map(existingGroup -> {
                    existingGroup.setName(groupDto.getName());
                    groupRepository.save(existingGroup);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Group updated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a group by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Group deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
