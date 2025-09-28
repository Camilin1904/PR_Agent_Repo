package com.pr.analisys.demo.service;

import com.pr.analisys.demo.entity.User;
import com.pr.analisys.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Create a new user
     * @param user the user to create
     * @return created user
     * @throws IllegalArgumentException if email already exists
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    
    /**
     * Get all users
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get all active users
     * @return list of active users
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    /**
     * Get user by ID
     * @param id user ID
     * @return Optional containing user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     * @param email user email
     * @return Optional containing user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Search users by first name
     * @param firstName first name to search
     * @return list of users with matching first name
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByFirstName(String firstName) {
        return userRepository.findByFirstNameIgnoreCase(firstName);
    }
    
    /**
     * Search users by last name
     * @param lastName last name to search
     * @return list of users with matching last name
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByLastName(String lastName) {
        return userRepository.findByLastNameIgnoreCase(lastName);
    }
    
    /**
     * Search users by email containing text
     * @param emailPart part of email to search
     * @return list of users with matching email part
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByEmail(String emailPart) {
        return userRepository.findByEmailContainingIgnoreCase(emailPart);
    }
    
    /**
     * Update an existing user
     * @param id user ID
     * @param userDetails updated user details
     * @return updated user
     * @throws RuntimeException if user not found
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDetails.getEmail());
        }
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setIsActive(userDetails.getIsActive());
        
        return userRepository.save(user);
    }
    
    /**
     * Deactivate a user (soft delete)
     * @param id user ID
     * @return deactivated user
     * @throws RuntimeException if user not found
     */
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(false);
        return userRepository.save(user);
    }
    
    /**
     * Activate a user
     * @param id user ID
     * @return activated user
     * @throws RuntimeException if user not found
     */
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(true);
        return userRepository.save(user);
    }
    
    /**
     * Delete a user permanently
     * @param id user ID
     * @throws RuntimeException if user not found
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        userRepository.delete(user);
    }
    
    /**
     * Get count of active users
     * @return number of active users
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    /**
     * Check if email exists
     * @param email email to check
     * @return true if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}