package it.unipi.tonightscall.security;

import it.unipi.tonightscall.jwt.JWTAuthenticationFilter;
import it.unipi.tonightscall.utilies.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Security Configuration for the application.
 * <p>
 * This class configures how HTTP security is handled, including:
 * <ul>
 *      <li>Disabling CSRF (stateless API).</li>
 *      <li>Defining public vs protected endpoints.</li>
 *      <li>Registering the custom JWT authentication filter.</li>
 *      <li>Setting up password encoding.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Defines the password encoding mechanism.
     * <p>
     * Uses BCrypt, a strong hashing function, to ensure passwords are strictly stored securely.
     * </p>
     *
     * @return A new instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * <p>
     * This method specifies which URL paths are public and which require authentication.
     * It also integrates the JWT filter into the Spring Security pipeline.
     * </p>
     *
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Allow public access to authentication endpoints (Register/Login)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()   //Allow public access to Swagger/OpenAPI documentation endpoints
                        .requestMatchers("/organizer/registerOrganization").hasAuthority(Roles.ORGANIZER_ROLE)
                        .requestMatchers("/organizer/**").hasAnyAuthority(Roles.ORGANIZER_ROLE, Roles.ORGANIZATION_ROLE)
                        .requestMatchers("/user/**").hasAuthority(Roles.USER_ROLE)
                        .anyRequest().authenticated() // // 3. All other requests require a valid JWT token
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}