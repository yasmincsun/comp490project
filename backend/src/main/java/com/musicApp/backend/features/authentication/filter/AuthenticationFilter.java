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

@Component
public class AuthenticationFilter extends HttpFilter {
    private final List<String> unsecuredEndpoints = Arrays.asList(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password",
            // Spotify login
            "/login",        
            "/callback",
            "/me/top"
    );

    private final JsonWebToken jsonWebTokenService;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(JsonWebToken jsonWebTokenService, AuthenticationService authenticationService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationService = authenticationService;

    }

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

        // if(unsecuredEndpoints.contains(path)){
        //     chain.doFilter(request, response);
        //     return;
        // }

//         boolean isUnsecured = unsecuredEndpoints.stream().anyMatch(path::startsWith);
//         if (isUnsecured) {
//             chain.doFilter(request, response);
//             return;
// }

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
