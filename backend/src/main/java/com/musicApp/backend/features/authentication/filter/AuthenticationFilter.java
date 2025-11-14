/**
 * Date: November 10, 2025
 * @author Jose Bastidas
 *

 * Data Structures:
 * - List<String> unsecuredEndpoints:
 *     Stores endpoint paths that do not require authentication.
 *     Implemented as a fixed-size List created from Arrays.asList() for efficient lookup.
 * 
 * Algorithms:
 * - Token Validation Algorithm:
 *     Steps:
 *       1. Extract the "Authorization" header.
 *       2. Verify the presence of a Bearer token.
 *       3. Use JsonWebToken utility to check token expiration.
 *       4. Decode the email embedded in the token.
 *       5. Retrieve the corresponding user record via AuthenticationService.
 *     - The algorithm is linear in time (O(1) per request) because it involves
 *       only direct lookups and decoding operations.
 *     - Chosen over alternatives like session-based authentication for scalability,
 *       since JWT-based validation is stateless and ideal for distributed REST APIs.
 */

package com.musicApp.backend.features.authentication.filter;


import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The AuthenticationFilter is a servlet filter that intercepts all incoming HTTP
 * requests to validate authentication tokens (JWTs) before allowing access to
 * protected endpoints. It ensures that only authorized users can access secured
 * parts of the API. <br>
 *
 * The filter integrates with the AuthenticationService and JsonWebToken utility
 * classes to validate tokens and retrieve authenticated user details.
 * Certain endpoints are explicitly excluded from authentication checks, such as
 * login, registration, and password reset routes.
 */
@Component
public class AuthenticationFilter extends HttpFilter {
    private final List<String> unsecuredEndpoints = Arrays.asList(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password",
            // Spotify login
            "/login",
            "/logout",        
            "/callback",
            "/me/top",
            "/mood/by",
            "/playlist",
            "/playlist/add",
            "/playlist/from-mood",
            "/"

    );

    private final JsonWebToken jsonWebTokenService;
    private final AuthenticationService authenticationService;


    /**
     * Constructs an {@code AuthenticationFilter} with the required authentication and token services.
     *
     * @param jsonWebTokenService the {@link JsonWebToken} utility used for validating and decoding JWTs
     * @param authenticationService the {@link AuthenticationService} used to retrieve user details based on token data
     */
    public AuthenticationFilter(JsonWebToken jsonWebTokenService, AuthenticationService authenticationService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationService = authenticationService;

    }

    /**
     *doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain):
     *     The core filtering method. It checks whether the request targets a secured
     *     endpoint. If so, it verifies the Authorization header and validates the
     *     JWT. Upon success, it attaches the authenticated user to the request
     *     context for downstream access by controllers.<br>
     * 
     * @param request  the incoming {@link HttpServletRequest} to be filtered
     * @param response the {@link HttpServletResponse} used to send back the result
     * @param chain    the {@link FilterChain} allowing the request to proceed if authentication passes
     *
     * @throws IOException if an input or output error occurs while processing the request
     * @throws ServletException if the request could not be handled, typically due to an invalid or missing token
     */    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
       
        // Using for testing
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        System.out.println("Unsecured endpoints: " + unsecuredEndpoints);
        /////////////////////
       
       
       
       
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();

        if (unsecuredEndpoints.contains(path) 
        || path.startsWith("/static/")  // Optional: skip all static resources
        || path.endsWith(".ico")        // Favicon
        || path.endsWith(".js")         // JS files
        || path.endsWith(".css")) {     // CSS files
        chain.doFilter(request, response);
        return;
    }


        try{
            System.out.println("Request URI: " + path);
            String authorization = request.getHeader("Authorization");

            if(authorization == null || !authorization.startsWith("Bearer ")){
                throw new ServletException("Token missing.");
            }

            String token = authorization.substring(7);

            if(jsonWebTokenService.isTokenExpired(token)){
                throw new ServletException("Invalid token");
            }

            String email = jsonWebTokenService.getEmailFromToken(token);
            AuthenticationUser user = authenticationService.getUser(email);
            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);

        } catch(Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid authentication token, or token missing.\"}");
        }
    }
}
