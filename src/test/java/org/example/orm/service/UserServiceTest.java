package org.example.orm.service;

import org.example.orm.entity.User;
import org.example.orm.entity.enums.UserRole;
import org.example.orm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        var user = userService.createUser("John Doe", "john@test.com", UserRole.STUDENT);
        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@test.com", user.getEmail());
        assertEquals(UserRole.STUDENT, user.getRole());
    }

    @Test
    void testCreateTeacher() {
        var user = userService.createTeacher("Dr. Smith", "smith@test.com");
        assertNotNull(user.getId());
        assertEquals(UserRole.TEACHER, user.getRole());
    }

    @Test
    void testCreateStudent() {
        var user = userService.createStudent("Jane Doe", "jane@test.com");
        assertNotNull(user.getId());
        assertEquals(UserRole.STUDENT, user.getRole());
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setName("Find User");
        user.setEmail("find@test.com");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);

        var found = userService.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Find User", found.get().getName());
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setName("Email User");
        user.setEmail("email@test.com");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);

        var found = userService.findByEmail("email@test.com");
        assertTrue(found.isPresent());
        assertEquals("Email User", found.get().getName());
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("u1@test.com");
        user1.setRole(UserRole.STUDENT);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("u2@test.com");
        user2.setRole(UserRole.TEACHER);
        userRepository.save(user2);

        List<User> users = userService.findAll();
        assertTrue(users.size() >= 2);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("update@test.com");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);

        var updated = userService.updateUser(user.getId(), "New Name", "newupdate@test.com");
        assertEquals("New Name", updated.getName());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setName("Delete User");
        user.setEmail("del@test.com");
        user.setRole(UserRole.STUDENT);
        user = userRepository.save(user);

        Long id = user.getId();
        userService.deleteUser(id);

        assertFalse(userService.findById(id).isPresent());
    }
}
