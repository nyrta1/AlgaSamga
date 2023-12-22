package com.example.web.security;

import com.example.web.models.UserEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityUtil {
    public static String getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            return currentUsername;
        }
        return null;
    }

    public static void updateSessionUser(UserEntity updatedUserEntity) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuthentication != null && currentAuthentication.isAuthenticated()) {
            User updatedUser = new User(updatedUserEntity.getUsername(), updatedUserEntity.getPassword(), currentAuthentication.getAuthorities());
            UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(updatedUser, currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }
    }
}
