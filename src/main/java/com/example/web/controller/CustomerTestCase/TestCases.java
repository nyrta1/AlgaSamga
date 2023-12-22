package com.example.web.controller.CustomerTestCase;

import com.example.web.repository.FollowToSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCases {
    private static FollowToSubjectRepository followToSubjectRepository;

    @Autowired
    public TestCases(FollowToSubjectRepository followToSubjectRepository) {
        this.followToSubjectRepository = followToSubjectRepository;
    }

    public static boolean checkTheUserFollowedToTheSubject(Long userID, Long subjectID) {
        return followToSubjectRepository.findFollowed(userID, subjectID).isEmpty();
    }
}
