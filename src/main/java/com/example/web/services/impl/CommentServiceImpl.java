package com.example.web.services.impl;

import com.example.web.models.Comment;
import com.example.web.repository.CommentRepository;
import com.example.web.repository.LessonsRepository;
import com.example.web.repository.UserRepository;
import com.example.web.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private LessonsRepository lessonsRepository;
    private UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, LessonsRepository lessonsRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.lessonsRepository = lessonsRepository;
        this.userRepository = userRepository;
    }

    public CommentServiceImpl(CommentRepository commentRepository, LessonsRepository lessonsRepository) {
        this.commentRepository = commentRepository;
        this.lessonsRepository = lessonsRepository;
    }

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Comment findCommentById(Long id) {
        return commentRepository.findCommentById(id);
    }

    @Override
    public List<Comment> findAllCommentsForLesson(Long lesson_id) {
        List<Comment> comments = commentRepository.findAllCommentsForLesson(lesson_id);
        return comments;
    }

    @Override
    public void deleteCommentsByLessonId(Long lessonId) {
        commentRepository.deleteCommentsByLessonId(lessonId);
    }
}
