package com.example.web.controller;

import com.example.web.convertOrGenerator.ColorGenerator;
import com.example.web.convertOrGenerator.CustomMultipartFile;
import com.example.web.dto.RegistrationDto;
import com.example.web.models.UserEntity;
import com.example.web.repository.UserRepository;
import com.example.web.security.CustomUserDetailsService;
import com.example.web.security.SecurityUtil;
import com.example.web.services.StorageService;
import com.example.web.services.SubjectService;
import com.example.web.services.UserService;
import com.example.web.services.impl.PhotoCopyServiceImpl;
import com.example.web.services.impl.properties.StorageProperties;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("admin")
public class AdminController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final SubjectService subjectService;
    private final StorageService storageService;
    private final PhotoCopyServiceImpl photoCopyService;

    @Autowired
    public AdminController(UserRepository userRepository, UserService userService, SubjectService subjectService, StorageService storageService, PhotoCopyServiceImpl photoCopyService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.subjectService = subjectService;
        this.storageService = storageService;
        this.photoCopyService = photoCopyService;
    }

    @ModelAttribute("currentUser")
    public UserEntity getCurrentUser() {
        return userService.findByUsername(SecurityUtil.getSessionUser());
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("countOfAllUser", userService.countOfAllUsers());

        model.addAttribute("countOfSubject", subjectService.countOfSubject());

        List<String> chartLabels = subjectService.getAllTypeOfSubjects(); // Replace with your labels
        List<Integer> chartData = subjectService.getCountOfAllSubjects();// Replace this with your data retrieval logic
        List<String> chartColors = ColorGenerator.generateRandomColors(chartData.size());
        List<Object[]> followedPeopleData = subjectService.countFollowedPeopleByDate();

        List<Map<String, Object>> formattedData = new ArrayList<>();
        for (Object[] data : followedPeopleData) {
            Date date = (Date) data[0];
            Long followedPeopleCount = (Long) data[1];

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

            Map<String, Object> entry = new HashMap<>();
            entry.put("date", formattedDate);
            entry.put("followedPeople", followedPeopleCount);

            formattedData.add(entry);
        }

        model.addAttribute("followedPeopleData", formattedData);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);
        model.addAttribute("chartColors", chartColors);

        return "admin-home";
    }

    @GetMapping("/all-users")
    public String getAllUsers(@RequestParam(value = "userId", required = false) Long userId, Model model) {
        model.addAttribute("userId", userId);

        List<UserEntity> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("users", users);

        if (userId != null) {
            UserEntity userCorrection = userRepository.findById(userId).orElse(null);
            if (userCorrection != null) {
                model.addAttribute("activeStatus", userCorrection.getActiveStatus());
                model.addAttribute("correctionUser", userCorrection);
            } else {
                model.addAttribute("activeStatus", false);
                model.addAttribute("correctionUser", new RegistrationDto());
            }
        } else {
            model.addAttribute("activeStatus", false);
            model.addAttribute("correctionUser", new RegistrationDto());
        }

        return "admin-all-users";
    }

    @GetMapping("/all-users/{userId}/delete")
    public String deleteUser(@PathVariable("userId") Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                userService.deleteUser(userId);

                Path path = Paths.get(photoUrl);
                String filename = path.getFileName().toString();
                storageService.deleteFile(filename);
            } else {
                userService.deleteUser(userId);
            }
            return "redirect:/admin/all-users";
        } else {
            return "redirect:/admin/all-users?error=UserNotFound";
        }
    }

    @PostMapping("/all-users")
    public String saveProfile(@Valid @ModelAttribute("correctionUser") RegistrationDto registrationDto,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              BindingResult result
    ) {
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
            photoCopyService.copyPhotoToTargetFolder(newFileName);
            userService.updateUser(registrationDto);
            Files.delete(tempFilePath);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error uploading or renaming the file: " + e.getMessage());
            return "user-edit-profile";
        }
        return "redirect:/admin/all-users";
    }

    @GetMapping("/all-users/{userId}/logout")
    public String logoutUserFromSystem(@PathVariable("userId") Long userId) {
        if (CustomUserDetailsService.getActiveSessions().containsKey(userId)) {
            HttpSession httpSession = CustomUserDetailsService.getActiveSessions().get(userId);
            if (httpSession != null) {
                httpSession.invalidate();
                CustomUserDetailsService.getActiveSessions().remove(userId);
                UserEntity user = userService.findById(userId);
                user.setActiveStatus(false);
                userRepository.save(user);
                return "redirect:/admin/all-users";
            }
        }
        return "redirect:/admin/all-users";
    }

}
