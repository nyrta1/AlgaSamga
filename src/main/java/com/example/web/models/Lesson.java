package com.example.web.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photoUrl;
    private String title;
    private String description;
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdOn;
    private int longitude;
    private String url;

    @ManyToOne
    @JoinColumn(name = "subjects_id", nullable = false)
    private Subjects subjects;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
    private final List<Comment> comments = new ArrayList<>();
}
