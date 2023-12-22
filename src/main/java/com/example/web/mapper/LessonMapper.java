// LessonMapper.java
package com.example.web.mapper;

import com.example.web.dto.LessonDto;
import com.example.web.models.Lesson;

public class LessonMapper {
    public static Lesson mapToLesson(LessonDto lessonDto) {
        Lesson lesson = Lesson.builder()
                .id(lessonDto.getId())
                .photoUrl(lessonDto.getPhotoUrl())
                .title(lessonDto.getTitle())
                .description(lessonDto.getDescription())
                .createdOn(lessonDto.getCreatedOn())
                .longitude(lessonDto.getLongitude())
                .url(lessonDto.getUrl())
                .subjects(lessonDto.getSubjects())
                .build();
        return lesson;
    }

    public static LessonDto mapToLessonDto(Lesson lesson) {
        LessonDto lessonDto = LessonDto.builder()
                .id(lesson.getId())
                .photoUrl(lesson.getPhotoUrl())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .createdOn(lesson.getCreatedOn())
                .longitude(lesson.getLongitude())
                .url(lesson.getUrl())
                .subjects(lesson.getSubjects())
                .build();
        return lessonDto;
    }
}
