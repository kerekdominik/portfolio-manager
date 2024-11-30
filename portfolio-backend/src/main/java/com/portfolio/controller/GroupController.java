package com.portfolio.controller;

import com.portfolio.dto.GroupRequestDto;
import com.portfolio.dto.GroupResponseDto;
import com.portfolio.entity.Group;
import com.portfolio.entity.User;
import com.portfolio.repository.GroupRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "GroupController", description = "API for managing user-defined groups")
public class GroupController {

    private final GroupRepository groupRepository;

    @PostMapping
    @Operation(
            summary = "Create a new group",
            description = "Allows the logged-in user to create a new group. The group is associated with the user's account."
    )
    public ResponseEntity<Map<String, String>> createGroup(@RequestBody GroupRequestDto groupDto, @AuthenticationPrincipal User user) {
        Group newGroup = new Group(groupDto.getName(), user); // Set the logged-in user as the owner of the group
        groupRepository.save(newGroup);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Group created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all groups",
            description = "Fetches all groups associated with the logged-in user."
    )
    public ResponseEntity<List<GroupResponseDto>> getAllGroups(@AuthenticationPrincipal User user) {
        List<Group> groups = groupRepository.findByUser(user);
        List<GroupResponseDto> groupDtos = groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDtos);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a group",
            description = "Updates the name of an existing group for the logged-in user by the group ID."
    )
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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a group",
            description = "Deletes a group for the logged-in user by the group ID."
    )
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
