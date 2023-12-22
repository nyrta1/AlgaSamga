package com.example.web.component;

import com.example.web.models.UserEntity;
import com.example.web.repository.UserRepository;
import com.example.web.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserStatusTask {
    private Logger logger = LoggerFactory.getLogger(UserStatusTask.class);
    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserStatusTask(UserRepository userRepository, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Scheduled(fixedDelay = 600000)
    public void checkUserStatusEveryOneHour() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<UserEntity> inactiveUsers = userRepository.findByLastActivityBefore(oneHourAgo);

        for (UserEntity user : inactiveUsers) {
            if (user.getActiveStatus()){
                user.setActiveStatus(false);
                userRepository.save(user);
                customUserDetailsService.deleteUserFromActiveSession(user.getId());
                logger.info(user.getUsername() + " is set to offline status");
            }
        }
    }
}
