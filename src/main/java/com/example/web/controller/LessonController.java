package com.example.web.controller;

import com.example.web.controller.CustomerTestCase.TestCases;
import com.example.web.convertOrGenerator.CustomMultipartFile;
import com.example.web.dto.LessonDto;
import com.example.web.models.Comment;
import com.example.web.models.Lesson;
import com.example.web.models.UserEntity;
import com.example.web.security.SecurityUtil;
import com.example.web.services.*;
import com.example.web.services.impl.properties.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.web.mapper.LessonMapper.*;

@Controller
public class LessonController {
    private final LessonService lessonService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final CommentService commentService;
    private final StorageService storageService;
    private UserEntity currentUser;

    @Autowired
    public LessonController(LessonService lessonService, SubjectService subjectService,UserService userService, CommentService commentService, StorageService storageService) {
        this.lessonService = lessonService;
        this.subjectService = subjectService;
        this.userService = userService;
        this.commentService = commentService;
        this.storageService = storageService;
    }

    @ModelAttribute("currentUser")
    public UserEntity getCurrentUser() {
        if (currentUser == null) {
            currentUser = userService.findByUsername(SecurityUtil.getSessionUser());
        }
        return currentUser;
    }

    @GetMapping("/courses/{subjectId}/new")
    public String createLesson( @PathVariable("subjectId") Long subjectId,
                                Model model
                                ) {
        // START SECURITY CONFIG
        Long currentSubject = subjectService.findSubjectById(subjectId).getCreatedBy().getId();
        if (!currentSubject.equals(currentUser.getId())) {
            return "redirect:/courses/" + subjectId;
        }
        // END SECURITY CONFIG


        Lesson lesson = new Lesson();
        model.addAttribute("subject", subjectId);
        model.addAttribute("lesson", lesson);
        return "lesson-create";
    }

    @PostMapping("/lessons/{subjectId}")
    public String saveLesson(@PathVariable("subjectId") Long subjectId,
                             @RequestParam("photo") MultipartFile photo,
                             @RequestParam("video") MultipartFile video,
                             @ModelAttribute("lesson") LessonDto lessonDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()){
            model.addAttribute("lesson", lessonDto);
            return "lesson-create";
        }
        if (photo.isEmpty() || video.isEmpty()) {
            return "lesson-create";
        }
        if (!photo.getContentType().startsWith("image/") || !video.getContentType().startsWith("video/")) {
            return "lesson-create";
        }

        String originalFilename = photo.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = lessonDto.getTitle().replaceAll("[^a-zA-Z]", "") + fileExtension;

        StringBuilder path = new StringBuilder((
                new StorageProperties()
                        .getLocation()
        )).append(newFileName);

        lessonDto.setPhotoUrl(
                path.substring(
                        path.lastIndexOf("\\img")
                )
        );

        String videoPath = subjectId + "_" + lessonDto.getTitle().replaceAll("[^a-zA-Z]", "");

        lessonDto.setUrl(videoPath);

        try {
            Path tempFilePath = Paths.get(path.toString());
            Files.write(tempFilePath, photo.getBytes());
            MultipartFile newFile = new CustomMultipartFile(Files.readAllBytes(tempFilePath), newFileName);

            Path videoFilePath = Paths.get(videoPath);
            Files.write(videoFilePath, video.getBytes());
            MultipartFile newVideo = new CustomMultipartFile(Files.readAllBytes(videoFilePath), videoPath + "mp4");

            storageService.store(newFile);
            storageService.storeVideo(newVideo);
            lessonService.saveLesson(subjectId, lessonDto);
        } catch (IOException e) {
            return "lesson-create";
        }
        return "redirect:/courses/" + subjectId;
    }

    @GetMapping("/courses/{subjectId}/{lessonId}/lesson-watch")
    public String watchLesson(@PathVariable("lessonId") Long lessonId,
                              @PathVariable("subjectId") Long subjectId,
                              Model model) {
        if (TestCases.checkTheUserFollowedToTheSubject(currentUser.getId(), subjectId)) {
            return "redirect:/courses/" + subjectId;
        }

        List<Comment> comments = commentService.findAllCommentsForLesson(lessonId);
        LessonDto lesson = lessonService.findByLessonId(lessonId);
        boolean mark = lessonService.knowTheLessonMarkedOrNot(subjectId, lessonId, currentUser.getId());

        model.addAttribute("mark", mark);
        model.addAttribute("comments", comments);
        model.addAttribute("lesson", lesson);
        return "lesson-watch";
    }
    @GetMapping("/courses/{subjectId}/{lessonId}/delete")
    public String deleteLesson(@PathVariable("subjectId") Long subjectId,
                               @PathVariable("lessonId") Long lessonId) {
        // START SECURITY CONFIG
        Long currentSubject = subjectService.findSubjectById(subjectId).getCreatedBy().getId();
        if (!currentSubject.equals(currentUser.getId())) {
            return "redirect:/courses/" + subjectId;
        }
        // END SECURITY CONFIG

        commentService.deleteCommentsByLessonId(lessonId);
        lessonService.deleteLesson(lessonId);
        return "redirect:/courses/" + subjectId;
    }

    // -------------- COMMENT SIDE -------------- //

    @PostMapping("/courses/{subjectId}/{lessonId}/lesson-watch/send-comment")
    public String sendComment(
            @RequestParam(value = "query") String content,
            @PathVariable("lessonId") Long lessonId
    ) {
        LessonDto lesson = lessonService.findByLessonId(lessonId);
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setLesson(mapToLesson(lesson));
        comment.setContent(content);
        commentService.saveComment(comment);
        return "redirect:/courses/" + lesson.getSubjects().getId() + "/" + lessonId + "/lesson-watch";
    }

    @PostMapping("/courses/{subjectId}/{lessonId}/mark")
    public String markTheLesson(@PathVariable("subjectId") Long subjectId,
                                @PathVariable("lessonId") Long lessonId
                                ) {
        UserEntity user = userService.findByUsername(SecurityUtil.getSessionUser());
        lessonService.markAsWatched(subjectId, lessonId, user.getId());
        return "redirect:/courses/" + subjectId + "/" + lessonId + "/lesson-watch";
    }

    @PostMapping("/courses/{subjectId}/{lessonId}/unmark")
    public String unmarkTheLesson(@PathVariable("subjectId") Long subjectId,
                                @PathVariable("lessonId") Long lessonId
    ) {
        UserEntity user = userService.findByUsername(SecurityUtil.getSessionUser());
        lessonService.unmarkAsWatched(subjectId, lessonId, user.getId());
        return "redirect:/courses/" + subjectId + "/" + lessonId + "/lesson-watch";
    }
}
