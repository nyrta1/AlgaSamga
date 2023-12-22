// LessonController.java
package com.example.web.controller;

import com.example.web.convertOrGenerator.CustomMultipartFile;
import com.example.web.dto.LessonDto;
import com.example.web.dto.SubjectsDto;
import com.example.web.models.RateOfSubjects;
import com.example.web.models.Subjects;
import com.example.web.models.UserEntity;
import com.example.web.security.SecurityUtil;
import com.example.web.services.LessonService;
import com.example.web.services.StorageService;
import com.example.web.services.SubjectService;
import com.example.web.services.UserService;

import com.example.web.services.impl.properties.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.web.mapper.SubjectMapper.*;

@Controller
public class SubjectController {
    private final LessonService lessonService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final StorageService storageService;

    @Autowired
    public SubjectController(LessonService lessonService, SubjectService subjectService, UserService userService, StorageService storageService) {
        this.lessonService = lessonService;
        this.subjectService = subjectService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("")
    public String redirectToHomePage(Model model) {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        List<SubjectsDto> subjects = subjectService.findAllSubjects();
        List<SubjectsDto> limitedLessons = subjects.subList(0, Math.min(subjects.size(), 3));
        model.addAttribute("subjects", limitedLessons);
        return "home";
    }

    @GetMapping("/courses")
    public String subjectList(Model model){
        List<SubjectsDto> subjects = subjectService.findAllSubjects();
        model.addAttribute("subjects", subjects);
        return "course-list";
    }

    @GetMapping("/courses/new")
    public String createSubject(Model model){
        Subjects subjects = new Subjects();
        model.addAttribute("subject", subjects);
        return "course-create";
    }

    @PostMapping("/courses/save")
    public String saveSubject(@ModelAttribute("subject") SubjectsDto subjectsDto,
                              @RequestParam("file") MultipartFile file,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()){
            model.addAttribute("subjects", subjectsDto);
            return "course-create";
        }
        if (file.isEmpty()) {
            return "course-create";
        }

        if (!file.getContentType().startsWith("image/")) {
            return "course-create";
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = subjectsDto.getTitle().replaceAll("[^a-zA-Z]", "") + fileExtension;

        StringBuilder path = new StringBuilder((
                new StorageProperties()
                        .getLocation()
        )).append(newFileName);

        subjectsDto.setPhotoUrl(
                path.substring(
                        path.lastIndexOf("\\img")
                )
        );

        try {
            Path tempFilePath = Paths.get(path.toString());
            Files.write(tempFilePath, file.getBytes());
            MultipartFile newFile = new CustomMultipartFile(Files.readAllBytes(tempFilePath), newFileName);
            storageService.store(newFile);
            subjectService.saveSubject(subjectsDto);
        } catch (IOException e) {
            return "course-create";
        }
        return "redirect:/courses";
    }

    @GetMapping("/courses/{courseId}")
    public String subjectDetail(@PathVariable("courseId") Long id, Model model) {
        String username = SecurityUtil.getSessionUser();
        UserEntity user = new UserEntity();
        SubjectsDto subject = subjectService.findSubjectById(id);
        subject.setFollowed(subjectService.followed(userService.findByUsername(username).getId(), id));
        if (username != null) {
            user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        model.addAttribute("ratingValue", subjectService.averageRate(id) + 1);
        model.addAttribute("avgRate", subjectService.floatAverageRate(id));
        model.addAttribute("countOfRated", subjectService.countOfRated(id));

        model.addAttribute("user", user);
        model.addAttribute("subject", subject);
        return "course-detail";
    }

    @GetMapping("/courses/search")
    public String searchSubject(@RequestParam(value = "query") String query, Model model) {
        List<SubjectsDto> subjects = subjectService.searchSubjects(query);
        model.addAttribute("subjects", subjects);
        return "course-list";
    }

    @GetMapping("/course-detail/{subjectId}/search")
    public String searchLesson(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam(value = "query") String query,
            Model model) {
        SubjectsDto subject = subjectService.findSubjectById(subjectId);
        List<LessonDto> lessons = lessonService.searchLesson(query);
        subject.setLessons(lessons);
        model.addAttribute("subject", subject);
        return "course-detail";
    }

    @GetMapping("/courses/{subjectId}/payment")
    public String buyTheSubject(@PathVariable("subjectId") Long subjectId,
                                Model model) {
        SubjectsDto subject = subjectService.findSubjectById(subjectId);
        model.addAttribute("subject", subject);
        return "payment";
    }

    @GetMapping("/courses/{subjectId}/follow")
    public String followToTheSubject(@PathVariable("subjectId") Long subjectId) {
        subjectService.followToSubject(subjectId);
        return "redirect:/courses/" + subjectId;
    }

//    @GetMapping("/courses/{subjectId}/unfollow")
//    public String unfollowToTheSubject(@PathVariable("subjectId") Long subjectId) {
//        subjectService.unfollowToSubject(subjectId);
//        return "redirect:/courses/" + subjectId;
//    }

    @PostMapping("/courses/{subjectId}/submit-rating")
    public String rateTheSubject(@PathVariable("subjectId") Long subjectId,
                                 @RequestParam("rating") Integer ratingValue) {
        String currentUser = SecurityUtil.getSessionUser();
        RateOfSubjects rate = new RateOfSubjects();
        rate.setSubjectId(mapToSubject(subjectService.findSubjectById(subjectId)));
        rate.setUserId(userService.findByUsername(currentUser));
        rate.setRate(ratingValue);
        subjectService.rateTheSubject(rate);
        return "redirect:/courses/" + subjectId;
    }
}
