package com.portfolio.controller;

import com.portfolio.dto.GroupRequestDto;
import com.portfolio.dto.GroupResponseDto;
import com.portfolio.entity.Group;
import com.portfolio.entity.User;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // Create a new group for the logged-in user
    @PostMapping
    public ResponseEntity<Map<String, String>> createGroup(@RequestBody GroupRequestDto groupDto, @AuthenticationPrincipal User user) {
        Group newGroup = new Group(groupDto.getName(), user); // Set the logged-in user as the owner of the group
        groupRepository.save(newGroup);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Group created successfully");
        return ResponseEntity.ok(response);
    }

    // Retrieve all groups for the logged-in user
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> getAllGroups(@AuthenticationPrincipal User user) {
        List<Group> groups = groupRepository.findByUser(user);
        List<GroupResponseDto> groupDtos = groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDtos);
    }

    // Update an existing group by ID for the logged-in user
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateGroup(@PathVariable Long id, @RequestBody GroupRequestDto groupDto, @AuthenticationPrincipal User user) {
        return groupRepository.findByIdAndUser(id, user)
                .map(existingGroup -> {
                    existingGroup.setName(groupDto.getName());
                    groupRepository.save(existingGroup);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Group updated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a group by ID for the logged-in user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return groupRepository.findByIdAndUser(id, user)
                .map(group -> {
                    groupRepository.delete(group);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Group deleted successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
