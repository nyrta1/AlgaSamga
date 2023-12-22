// SubjectMapper.java
package com.example.web.mapper;

import com.example.web.dto.SubjectsDto;
import com.example.web.models.Subjects;

import java.util.stream.Collectors;

import static com.example.web.mapper.LessonMapper.mapToLessonDto;

public class SubjectMapper {
    public static Subjects mapToSubject(SubjectsDto subjectsDto) {
        Subjects subjects = Subjects.builder()
                .id(subjectsDto.getId())
                .photoUrl(subjectsDto.getPhotoUrl())
                .title(subjectsDto.getTitle())
                .description(subjectsDto.getDescription())
                .type(subjectsDto.getType())
                .createdOn(subjectsDto.getCreatedOn())
                .createdBy(subjectsDto.getCreatedBy())
                .allLongitude(subjectsDto.getAllLongitude())
                .rate(subjectsDto.getRate())
                .cost(subjectsDto.getCost())
                .build();
        return subjects;
    }

    public static SubjectsDto mapToSubjectDto(Subjects subjects) {
        SubjectsDto subjectsDto = SubjectsDto.builder()
                .id(subjects.getId())
                .photoUrl(subjects.getPhotoUrl())
                .title(subjects.getTitle())
                .description(subjects.getDescription())
                .type(subjects.getType())
                .createdOn(subjects.getCreatedOn())
                .createdBy(subjects.getCreatedBy())
                .allLongitude(subjects.getAllLongitude())
                .lessons(subjects.getLessons().stream().map(lesson -> mapToLessonDto(lesson)).collect(Collectors.toList()))
                .rate(subjects.getRate())
                .cost(subjects.getCost())
                .build();
        return subjectsDto;
    }
}
