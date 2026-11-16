package com.portfolio.controller;

import com.portfolio.dto.GroupRequestDto;
import com.portfolio.dto.GroupResponseDto;
import com.portfolio.entity.Group;
import com.portfolio.entity.User;
import com.portfolio.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupController controller;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
    }

    @Test
    void createGroup_savesGroupOwnedByUser() {
        ResponseEntity<Map<String, String>> response = controller.createGroup(new GroupRequestDto("Tech"), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Group created successfully", response.getBody().get("message"));
        ArgumentCaptor<Group> saved = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(saved.capture());
        assertEquals("Tech", saved.getValue().getName());
        assertEquals(user, saved.getValue().getUser());
    }

    @Test
    void getAllGroups_mapsToDtos() {
        when(groupRepository.findByUser(user)).thenReturn(List.of(
                new Group(1L, "Tech", user, new ArrayList<>()),
                new Group(2L, "Crypto", user, new ArrayList<>())));

        ResponseEntity<List<GroupResponseDto>> response = controller.getAllGroups(user);

        assertEquals(List.of(new GroupResponseDto(1L, "Tech"), new GroupResponseDto(2L, "Crypto")), response.getBody());
    }

    @Test
    void updateGroup_found_renamesGroup() {
        Group group = new Group(1L, "Old", user, new ArrayList<>());
        when(groupRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(group));

        ResponseEntity<Map<String, String>> response = controller.updateGroup(1L, new GroupRequestDto("New"), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Group updated successfully", response.getBody().get("message"));
        assertEquals("New", group.getName());
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_notFound_returns404() {
        when(groupRepository.findByIdAndUser(9L, user)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.updateGroup(9L, new GroupRequestDto("New"), user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void deleteGroup_found_deletesGroup() {
        Group group = new Group(1L, "Tech", user, new ArrayList<>());
        when(groupRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(group));

        ResponseEntity<Map<String, String>> response = controller.deleteGroup(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Group deleted successfully", response.getBody().get("message"));
        verify(groupRepository).delete(group);
    }

    @Test
    void deleteGroup_notFound_returns404() {
        when(groupRepository.findByIdAndUser(9L, user)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.deleteGroup(9L, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(groupRepository, never()).delete(any());
    }
}
