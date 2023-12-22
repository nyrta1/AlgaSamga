package com.example.web.services;

import com.example.web.models.Comment;

import java.util.List;

public interface CommentService {
    void saveComment(Comment comment);
    Comment findCommentById(Long id);
    List<Comment> findAllCommentsForLesson(Long lesson_id);
    void deleteCommentsByLessonId(Long lessonId);
}
