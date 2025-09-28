package com.pr.analisys.demo.repository;

import com.pr.analisys.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all active users
     * @return list of active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find users by first name (case insensitive)
     * @param firstName the first name to search for
     * @return list of users with matching first name
     */
    List<User> findByFirstNameIgnoreCase(String firstName);
    
    /**
     * Find users by last name (case insensitive)
     * @param lastName the last name to search for
     * @return list of users with matching last name
     */
    List<User> findByLastNameIgnoreCase(String lastName);
    
    /**
     * Find users by first name and last name (case insensitive)
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return list of users with matching first and last name
     */
    List<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users with orders count using custom query
     * @return list of users with their order count
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.isActive = true")
    List<User> findActiveUsersWithOrders();
    
    /**
     * Count active users
     * @return number of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    /**
     * Find users by email containing text (case insensitive)
     * @param emailPart part of email to search for
     * @return list of users with matching email part
     */
    List<User> findByEmailContainingIgnoreCase(String emailPart);
}