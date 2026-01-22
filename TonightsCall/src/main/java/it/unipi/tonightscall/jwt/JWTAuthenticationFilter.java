package it.unipi.tonightscall.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Custom security filter that executes once per request.
 * <p>
 * It checks for the presence of a valid JWT in the "Authorization" header.
 * If valid, it authenticates the user in the Spring Security context.
 * </p>
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    public JWTAuthenticationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Intercepts the request to check for a valid JWT.
     *
     * @param request     The incoming HTTP request.
     * @param response    The outgoing HTTP response.
     * @param filterChain The chain of filters to proceed with.
     * @throws ServletException If a servlet error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Retrieve the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. Check if the header is valid (must start with "Bearer ")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extract the token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(7);

            // 4. Delegate validation to JWTService to extract the username
            final String username = jwtService.validateTokenAndGetUsername(jwt);

             // 5. If email is valid and the user is not yet authenticated in the context
             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                String role = jwtService.getRoleFromToken(jwt);
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                         username,
                         null,
                         authorities
                 );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                 // 6. Set the authentication in the Spring Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // If the token is invalid, we do nothing here.
            // The Security Context remains empty, and subsequent filters/security config
            // will handle the 403 Forbidden error if the endpoint requires auth.
            System.out.println("Validation Error JWT: " + e.getMessage());
        }

        // 7. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}