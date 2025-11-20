/**
 * Date: September 25, 2025
 * @author Jose Bastidas
*/ 

package com.musicApp.backend.features.authentication.repository;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is a Spring Data JPA repository for managing AuthenticationUser entities.
 * It provides CRUD operations, as well as custom query methods for retrieving users
 * based on email, username, and login status.
 */
public interface AuthenticationUserRepository extends JpaRepository<AuthenticationUser, Long> {
    /**
     * Finds a user by their email address. Returns Optional
     * to safely handle the case where no user exists with the given email.
     *
     * @param email the email address of the user to search for
     * @return an {@link Optional} containing the {@link AuthenticationUser} if found,
     *         or an empty {@link Optional} if no user exists with the given email
     */
    Optional<AuthenticationUser> findByEmail(String email);

    /**
     * Finds a user by their unique username. Also
     * returns an Optional for safe null handling.
     * @param username the username of the user to search for
     * @return an {@link Optional} containing the {@link AuthenticationUser} if found,
     *         or an empty {@link Optional} if no user exists with the given username
     */
    Optional<AuthenticationUser> findByUsername(String username);

 
    /**
     * Retrieves a list of all users who are currently marked
     * as logged in (loginStatus == true). Useful for displaying online users.
     * @return a {@link List} of {@link AuthenticationUser} objects representing
     *         all users currently logged in
     */
    // Find all currently logged-in users
    List<AuthenticationUser> findByLoginStatusTrue();
}

