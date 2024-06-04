package com.ambrose.saigonbyday.config;


import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.services.UserService;
import com.ambrose.saigonbyday.services.impl.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserService userService;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request.requestMatchers("api/v1/auth/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "swagger-resources/**",
                "/v3/api-docs/**",
                "webjars/**",
                "/login/oauth2/**",
                "oauth2/**")
            .permitAll()
            //.requestMatchers("/api/v1/auth/signingoogle").authenticated()
            .requestMatchers("/api/v1/admin/**").hasAnyAuthority(Role.ADMIN.name())
            .requestMatchers("/api/v1/user/**").hasAnyAuthority(Role.CUSTOMER.name())
            .anyRequest().authenticated())

        .oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/api/v1/auth/signingoogle", true))

  //      .oauth2Login(Customizer.withDefaults())
  //      .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .authenticationProvider(authenticationProvider()).addFilterBefore(
            jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
        );
    http.exceptionHandling(exception -> exception
        .authenticationEntryPoint(authenticationEntryPoint())
        .accessDeniedHandler(accessDeniedHandler()));
    return http.build();
  }

  private AuthenticationEntryPoint authenticationEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
  }

  private AccessDeniedHandler accessDeniedHandler() {
    AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
    accessDeniedHandler.setErrorPage("/403");
    return accessDeniedHandler;
  }
  @Bean
  public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userService.userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
    return config.getAuthenticationManager();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET,POST,PATCH,PUT,DELETE,OPTIONS,HEAD")
        .allowedHeaders("*")
        .exposedHeaders("X-Get-Header");
  }

}
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfiguration implements WebMvcConfigurer {
//
//  private final JwtAuthenticationFilter jwtAuthenticationFilter;
//  private final UserService userService;
//  private final TokenBlacklistService tokenBlacklistService;
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http.csrf(AbstractHttpConfigurer::disable)
//        .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auth/**",
//                "/swagger-ui/**",
//                "/swagger-ui.html",
//                "/swagger-resources/**",
//                "/v3/api-docs/**",
//                "/webjars/**",
//                "/login/oauth2/**",
//                "/oauth2/**")
//            .permitAll()
//            .requestMatchers("/api/v1/admin").hasAuthority((Role.ADMIN.name())
//            .requestMatchers("/api/v1/user/**").hasAuthority(Role.CUSTOMER.name())
//            .anyRequest().authenticated())
//        .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/api/v1/auth/signingoogle", true))
//        .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        .authenticationProvider(authenticationProvider())
//        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//
//    http.exceptionHandling(exception -> exception
//        .authenticationEntryPoint(authenticationEntryPoint())
//        .accessDeniedHandler(accessDeniedHandler()));
//
//    return http.build();
//  }
//
//  private AuthenticationEntryPoint authenticationEntryPoint() {
//    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
//  }
//
//  private AccessDeniedHandler accessDeniedHandler() {
//    AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
//    accessDeniedHandler.setErrorPage("/403");
//    return accessDeniedHandler;
//  }
//
//  @Bean
//  public AuthenticationProvider authenticationProvider() {
//    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//    authenticationProvider.setUserDetailsService(userService.userDetailsService());
//    authenticationProvider.setPasswordEncoder(passwordEncoder());
//    return authenticationProvider;
//  }
//
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }
//
//  @Bean
//  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//    return config.getAuthenticationManager();
//  }
//
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOrigins("*")
//        .allowedMethods("GET,POST,PATCH,PUT,DELETE,OPTIONS,HEAD")
//        .allowedHeaders("*")
//        .exposedHeaders("X-Get-Header");
//  }
//}
