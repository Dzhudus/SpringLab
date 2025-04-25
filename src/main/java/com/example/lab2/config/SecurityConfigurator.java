package com.example.lab2.config;

import com.example.lab2.lib.TokenFilter;
import com.example.lab2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {
    private TokenFilter tokenFilter;
    private UserService userService;
    public SecurityConfigurator() {}


    @Autowired
    public void setTokenFilter(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(request ->
                                new CorsConfiguration().applyPermitDefaultValues()
                        )
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()

                        //авторизованный
                        .requestMatchers(HttpMethod.GET, "/students/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/courses/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").authenticated()
//                        .requestMatchers(HttpMethod.POST, "/enrollments/**").authenticated()
                        //user
                        .requestMatchers(HttpMethod.GET, "/students/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/courses/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").hasRole("USER")
                        // TEACHER
                        .requestMatchers(HttpMethod.GET, "/students/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/students").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/enrollments").hasRole("TEACHER")

                        // STUDENT
                        .requestMatchers(HttpMethod.GET, "/enrollments/student/{studentId}").hasRole("STUDENT")

                        // Только админ
                        .requestMatchers(HttpMethod.GET, "/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/enrollments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/students").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/courses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/enrollments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/enrollments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")
                        // Остальные маршруты
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/secured/user").fullyAuthenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




}
