package com.example.web.services.impl;

import com.example.web.dto.LessonDto;
import com.example.web.models.Lesson;
import com.example.web.models.Subjects;
import com.example.web.models.UsersLessonProgress;
import com.example.web.repository.*;
import com.example.web.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.web.mapper.LessonMapper.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonsRepository lessonsRepository;
    private final SubjectsRepository subjectsRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final FollowToSubjectRepository followToSubjectRepository;

    @Autowired
    public LessonServiceImpl(LessonsRepository lessonsRepository, SubjectsRepository subjectsRepository, ProgressRepository progressRepository, UserRepository userRepository, FollowToSubjectRepository followToSubjectRepository) {
        this.lessonsRepository = lessonsRepository;
        this.subjectsRepository = subjectsRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.followToSubjectRepository = followToSubjectRepository;
    }

    @Override
    public void createLesson(Long subjectId, LessonDto lessonDto) {
        Subjects subjects = subjectsRepository.findById(subjectId).get();
        Lesson lesson = mapToLesson(lessonDto);
        lesson.setSubjects(subjects);
        lessonsRepository.save(lesson);
    }

    @Override
    public List<LessonDto> findAllLessons() {
        List<Lesson> lessons = lessonsRepository.findAll();
        return lessons.stream().map(lesson -> mapToLessonDto(lesson)).collect(Collectors.toList());
    }

    @Override
    public LessonDto findByLessonId(Long lessonId) {
        Lesson lesson = lessonsRepository.findById(lessonId).orElse(null);
        return mapToLessonDto(lesson);
    }

    @Override
    public void updateLesson(LessonDto lessonDto) {
        Lesson lesson = mapToLesson(lessonDto);
        lessonsRepository.save(lesson);
    }

    @Override
    public void deleteLesson(Long lessonId) {
        lessonsRepository.deleteById(lessonId);
    }

    @Override
    public List<LessonDto> searchLesson(String query) {
        List<Lesson> lessons = lessonsRepository.searchLesson(query);
        return lessons.stream().map(lesson -> mapToLessonDto(lesson)).collect(Collectors.toList());
    }

    @Override
    public void saveLesson(Long subjectId, LessonDto lessonDto) {
        Subjects subjects = subjectsRepository.findById(subjectId).get();
        Lesson lesson = mapToLesson(lessonDto);
        lesson.setSubjects(subjects);
        lessonsRepository.save(lesson);
        subjectsRepository.addLongitude(subjectId, lessonDto.getLongitude());
    }

    @Override
    public void markAsWatched(Long subjectId, Long lessonId, Long userId) {
        UsersLessonProgress progress = new UsersLessonProgress();
        progress.setSubject(subjectsRepository.findById(subjectId).get());
        progress.setLesson(lessonsRepository.findById(lessonId).get());
        progress.setUser(userRepository.findById(userId).get());
        progressRepository.save(progress);
        userSubjectProgress(subjectId, lessonId, userId);
    }

    @Override
    public void unmarkAsWatched(Long subjectId, Long lessonId, Long userId) {
        progressRepository.deleteTheProgressSQL(subjectId, lessonId, userId);
        userSubjectProgress(subjectId, lessonId, userId);
    }

    @Override
    public boolean knowTheLessonMarkedOrNot(Long subjectId, Long lessonId, Long userId) {
        return progressRepository.knowTheLessonMarkedOrNotSQL(subjectId, lessonId, userId);
    }

    @Override
    public void userSubjectProgress(Long subjectId, Long lessonId, Long userId) {
        Long totalLessons = lessonsRepository.countOfLessonByID(subjectId);
        Long watchedLessons = progressRepository.countOfWatchedLessons(subjectId, userId);

        Long percent = (watchedLessons * 100) / totalLessons;

        followToSubjectRepository.updateTheProgressPercent(subjectId, userId, percent);
    }

}
