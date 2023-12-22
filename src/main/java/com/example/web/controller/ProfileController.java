package com.example.web.controller;

import com.example.web.convertOrGenerator.CustomMultipartFile;
import com.example.web.dto.RegistrationDto;
import com.example.web.models.FollowToSubject;
import com.example.web.models.Subjects;
import com.example.web.models.UserEntity;
import com.example.web.security.SecurityUtil;
import com.example.web.services.StorageService;
import com.example.web.services.SubjectService;
import com.example.web.services.UserService;
import com.example.web.services.impl.PhotoCopyServiceImpl;
import com.example.web.services.impl.properties.StorageProperties;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Controller
public class ProfileController {
    private final UserService userService;
    private final StorageService storageService;
    private final SubjectService subjectService;
    private final PhotoCopyServiceImpl photoCopyService;

    @Autowired
    public ProfileController(UserService userService, StorageService storageService, SubjectService subjectService, PhotoCopyServiceImpl photoCopyService) {
        this.userService = userService;
        this.storageService = storageService;
        this.subjectService = subjectService;
        this.photoCopyService = photoCopyService;
    }

    @GetMapping("/profile")
    public String profileList(Model model) {
        String sessionUsername = SecurityUtil.getSessionUser();
        UserEntity sessionUser = userService.findByUsername(sessionUsername);
        model.addAttribute("sessionUser", sessionUser);

        UserEntity user = userService.findByUsername(sessionUsername);
        List<FollowToSubject> followed = subjectService.allSubjectsUserFollowed(user.getId());
        List<Subjects> followedSubjects = new ArrayList<>();

        for (FollowToSubject followToSubject : followed) {
            followedSubjects.add(followToSubject.getSubjects());
        }

        List<List<Subjects>> dividedLists = partition(followedSubjects, 3);
        model.addAttribute("AllFollowed", followed);
        model.addAttribute("followed", dividedLists);
        model.addAttribute("user", user);
        return "user-profile";
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable("username") String username,
                              Model model) {
        String sessionUsername = SecurityUtil.getSessionUser();
        UserEntity sessionUser = userService.findByUsername(sessionUsername);
        model.addAttribute("sessionUser", sessionUser);

        UserEntity user = userService.findByUsername(username);
        System.err.println(user.getActiveStatus());
        List<FollowToSubject> followed = subjectService.allSubjectsUserFollowed(user.getId());
        List<Subjects> followedSubjects = new ArrayList<>();

        for (FollowToSubject followToSubject : followed) {
            followedSubjects.add(followToSubject.getSubjects());
        }

        List<List<Subjects>> dividedLists = partition(followedSubjects, 3);
        model.addAttribute("AllFollowed", followed);
        model.addAttribute("followed", dividedLists);
        model.addAttribute("user", user);
        return "user-profile";
    }

    public static <T> List<List<T>> partition(List<T> list, int chunkSize) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            result.add(list.subList(i, end));
        }
        return result;
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        String sessionUsername = SecurityUtil.getSessionUser();
        UserEntity userEntity = userService.findByUsername(sessionUsername);
        model.addAttribute("userEntity", userEntity);

        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setId(userEntity.getId());
        registrationDto.setSurname(userEntity.getSurname());
        registrationDto.setName(userEntity.getName());
        registrationDto.setPhoneNumber(userEntity.getPhoneNumber());
        registrationDto.setPhotoUrl(userEntity.getPhotoUrl());
        registrationDto.setEmail(userEntity.getEmail());
        registrationDto.setUsername(userEntity.getUsername());
        registrationDto.setPassword(userEntity.getPassword());
        model.addAttribute("registrationDto", registrationDto);

        return "user-edit-profile";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              BindingResult result,
                              Model model
    ) {
        String sessionUsername = SecurityUtil.getSessionUser();
        UserEntity userEntity = userService.findByUsername(sessionUsername);
        model.addAttribute("userEntity", userEntity);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "user-edit-profile";
        }

        if (!file.getContentType().startsWith("image/")) {
            redirectAttributes.addFlashAttribute("message", "Please upload an image file.");
            return "user-edit-profile";
        }
        if (result.hasErrors()) {
            return "user-edit-profile";
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = registrationDto.getUsername() + fileExtension;

        StringBuilder path = new StringBuilder((
                new StorageProperties()
                        .getLocation()
        )).append(newFileName);

        registrationDto.setPhotoUrl(
                path.substring(
                        path.lastIndexOf("\\img")
                )
        );

        try {
            String tempFileName = "temp_" + file.getOriginalFilename();
            Path tempFilePath = Paths.get(tempFileName);
            Files.write(tempFilePath, file.getBytes());

            MultipartFile newFile = new CustomMultipartFile(Files.readAllBytes(tempFilePath), newFileName);

            storageService.store(newFile);
            userService.updateUser(registrationDto);
            SecurityUtil.updateSessionUser(userService.findByUsername(registrationDto.getUsername()));
            Files.delete(tempFilePath);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error uploading or renaming the file: " + e.getMessage());
            return "user-edit-profile";
        }
        try {
            photoCopyService.copyPhotoToTargetFolder(newFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/profile";
    }
}
