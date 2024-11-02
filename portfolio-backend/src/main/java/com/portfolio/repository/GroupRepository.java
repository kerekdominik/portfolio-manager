package com.portfolio.repository;

import com.portfolio.entity.Group;
import com.portfolio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByUser(User user);
    Optional<Group> findByIdAndUser(Long id, User user);
}
