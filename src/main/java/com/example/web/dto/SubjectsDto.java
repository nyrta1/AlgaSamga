// SubjectsDto.java
package com.example.web.dto;

import com.example.web.models.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SubjectsDto {
    private Long id;
    @NotEmpty
    private String photoUrl;
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotEmpty
    private String type;
    private LocalDateTime createdOn;
    private UserEntity createdBy;
    private Long allLongitude;
    private List<LessonDto> lessons;
    private Boolean followed;
    private Float rate;
    private Long cost;
}
