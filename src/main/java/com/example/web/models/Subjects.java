// Subjects.java
package com.example.web.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "subjects")
public class Subjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photoUrl;
    private String title;
    private String description;
    private String type;
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdOn;
    @ColumnDefault("0")
    private Long allLongitude;
    @ColumnDefault("0")
    private Float rate;
    @ColumnDefault("0")
    private Long cost;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @OneToMany(mappedBy = "subjects", cascade = CascadeType.REMOVE)
    private final List<FollowToSubject> followToSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "subjects", cascade = CascadeType.REMOVE)
    private final List<Lesson> lessons = new ArrayList<>();
}
