package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.LoginUserDto;
import WebProject.example.WebProject.softUni.dtos.RegisterUserDto;
import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.services.UserHelperService;
import WebProject.example.WebProject.softUni.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Controller()
public class UserController {
    private final UserService userService;
    private final UserHelperService userHelperService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, UserHelperService userHelperService, ModelMapper modelMapper) {
        this.userService = userService;
        this.userHelperService = userHelperService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/login")
    public String getLogin(Model model, LoginUserDto loginUserDto) {
        model.addAttribute("loginUserDto", loginUserDto);
        return "Login";
    }

    @GetMapping("/register")
    public String getRegister(Model model) {
        if (!model.containsAttribute("registerUserDto")) {
            model.addAttribute("registerUserDto", new RegisterUserDto());
        }
        return "Register";
    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @PostMapping("/register")
    public String doRegister(@Valid RegisterUserDto registerUserDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            this.userService.registerUser(registerUserDto);
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("registerUserDto", registerUserDto);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerUserDto", bindingResult);
        return "redirect:/register";
    }

    @GetMapping("/changePassword")
    public String getChangePassword() {
        return "ChangePassword";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model) {
        model.addAttribute("profileData", new UserProfileDto());
        return "EditProfile";
    }

    @GetMapping("/User")
    public String getUserProfile(Model model) {
        model.addAttribute("userData", userHelperService.getUser());
        return "User";
    }

    @PostMapping("/editProfile")
    public String changeUserInfo(UserProfileDto userProfileDto, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profileData", userProfileDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileData", userProfileDto);
            return "redirect:/editProfile";
        }
        userHelperService.updateUser(userProfileDto);
        return "redirect:/User";
    }

    @GetMapping("/User/{id}")
    public String getUserProfile(Model model, @PathVariable("id") long id) {
        Optional<User> userById = userService.findUserById(id);
        if (userById.isPresent()) {
            User user = userById.get();
            model.addAttribute("userData", user);
            return "User";
        }
        model.addAttribute("userData", null);
        return "User";
    }

}
