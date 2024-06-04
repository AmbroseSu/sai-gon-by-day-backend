package com.ambrose.saigonbyday.config;


import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.services.JWTService;
import com.ambrose.saigonbyday.services.UserService;
import com.ambrose.saigonbyday.services.impl.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final UserService userService;

//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//    final String authHeader = request.getHeader("Authorization");
//    final String jwt;
//    final String userEmail;
//
//    if(StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader,"Bearer")){
//      filterChain.doFilter(request,response);
//      return;
//    }
//    jwt = authHeader.substring(7);
//    userEmail = jwtService.extractUserName(jwt);
//
//    if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null){
//      UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
//
//      if(jwtService.isTokenValid(jwt, userDetails)){
//        //String roleString = jwtService.extractRoles(jwt);
//       // Role role = Role.valueOf(roleString); // Chuyển đổi string sang enum
//        //List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//            userDetails, null, userDetails.getAuthorities()/*authorities*/
//        );
//        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//        securityContext.setAuthentication(token);;
//        SecurityContextHolder.setContext(securityContext);
//      }
//    }
//
//    filterChain.doFilter(request,response);
//
//  }
//}

  private final TokenBlacklistService tokenBlacklistService;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;


    if(StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader,"Bearer")){
      filterChain.doFilter(request,response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUserName(jwt);

    if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null){
      UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

      if(jwtService.isTokenValid(jwt, userDetails)){
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        securityContext.setAuthentication(token);;
        SecurityContextHolder.setContext(securityContext);
      }
    }

    filterChain.doFilter(request,response);

  }
}
