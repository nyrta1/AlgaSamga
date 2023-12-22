package com.example.web.security.CustomHandler;

import com.example.web.models.UserEntity;
import com.example.web.repository.UserRepository;
import com.example.web.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public CustomLogoutHandler(UserRepository userRepository, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        UserEntity user = userRepository.findByUsername(authentication.getName());
        user.setActiveStatus(false);
        userRepository.save(user);
        customUserDetailsService.deleteUserFromActiveSession(user.getId());
        httpServletResponse.sendRedirect("/login");
    }
}
