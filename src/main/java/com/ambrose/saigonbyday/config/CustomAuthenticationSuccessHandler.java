//package com.ambrose.saigonbyday.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.savedrequest.SavedRequest;
//
//public class CustomAuthenticationSuccessHandler extends
//    SavedRequestAwareAuthenticationSuccessHandler {
//
//
//  @Override
//  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//      throws IOException {
//    // Access the saved request directly from the request object
//    SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
//
//    response.setContentType("text/plain");
//    response.getWriter().write("Sign In Google Successfully");
//  }
//}
