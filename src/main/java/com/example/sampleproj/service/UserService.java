package com.example.sampleproj.service;

import com.example.sampleproj.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserService() {
        // Initialize with some sample data
        createUser(new User("John Doe", "john.doe@example.com"));
        createUser(new User("Jane Smith", "jane.smith@example.com"));
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    public Optional<User> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    
    public Optional<User> getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        users.add(user);
        return user;
    }
    
    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = getUserById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            return Optional.of(user);
        }
        return Optional.empty();
    }
    
    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
    
    public long getUserCount() {
        return users.size();
    }
}