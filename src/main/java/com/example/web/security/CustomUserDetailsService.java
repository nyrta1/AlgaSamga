package com.example.web.security;

import com.example.web.models.UserEntity;
import com.example.web.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    private static final Map<Long, HttpSession> activeSessions = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findFirstByUsername(username);
        if (user != null) {
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            User userDetails = new User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );

            updateUserStatus(user, true);
            user.setLastActivity(LocalDateTime.now());
            userRepository.save(user);

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession session = request.getSession();

            activeSessions.put(user.getId(), session);

            logger.info("The user logs in: {}", user.getUsername());

            return userDetails;
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    private void updateUserStatus(UserEntity user, boolean activeStatus) {
        user.setActiveStatus(activeStatus);
        userRepository.save(user);
    }

    public static Map<Long, HttpSession> getActiveSessions() {
        return activeSessions;
    }

    public void deleteUserFromActiveSession(Long userID) {activeSessions.remove(userID);}
}