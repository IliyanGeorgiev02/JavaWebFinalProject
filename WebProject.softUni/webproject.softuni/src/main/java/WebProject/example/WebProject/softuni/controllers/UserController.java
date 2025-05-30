package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.services.ListService;
import webproject.example.webproject.softuni.services.UserHelperService;
import webproject.example.webproject.softuni.services.UserService;
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

import java.util.*;


@Controller()
public class UserController {
    private final UserService userService;
    private final UserHelperService userHelperService;
    private final ModelMapper modelMapper;
    private final ListService listService;
    private final ReviewClient reviewClient;

    public UserController(UserService userService, UserHelperService userHelperService, ModelMapper modelMapper, ListService listService, ReviewClient reviewClient) {
        this.userService = userService;
        this.userHelperService = userHelperService;
        this.modelMapper = modelMapper;
        this.listService = listService;
        this.reviewClient = reviewClient;
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

    @GetMapping("/editProfile")
    public String editProfile(Model model) {
        model.addAttribute("profileData", new UserProfileDto());
        return "EditProfile";
    }

    @GetMapping("/User")
    public String getUserProfile(Model model) {
        User user = userHelperService.getUser();
        return "redirect:/User/" + user.getId();
    }

    @PostMapping("/editProfile")
    public String changeUserInfo(UserProfileDto userProfileDto, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        MultipartFile file = userProfileDto.getProfilePicture();
        if (file.getSize() > 2 * 1024 * 1024) {
            redirectAttributes.addFlashAttribute("profileData", userProfileDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileData", userProfileDto);
            return "redirect:/editProfile";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profileData", userProfileDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileData", userProfileDto);
            return "redirect:/editProfile";
        }
        userHelperService.updateUser(userProfileDto);
        return "redirect:/User/" + userProfileDto.getId();
    }

    @Transactional
    @GetMapping("/User/{id}")
    public String getUserProfile(Model model, @PathVariable("id") long id) {
        Optional<User> userById = userService.findUserById(id);
        if (userById.isPresent()) {
            User user = userById.get();
            DisplayUserDto mappedUser = this.modelMapper.map(user, DisplayUserDto.class);
            List<CustomList> listsByUser = this.listService.findListsByUser(user.getId());
            List<ReviewFullInfoDto> reviews = this.reviewClient.getReviewsByUserId(user.getId());
            DisplayReviewDto mappedReviews = this.reviewClient.mapReviewsToDisplayReviewDto(reviews);
            ListDto mappedLists = this.listService.mapCustomListsToListDto(listsByUser);
            model.addAttribute("userData", mappedUser);
            model.addAttribute("reviewsData", mappedReviews);
            model.addAttribute("listsData", mappedLists);
            return "User";
        }
        model.addAttribute("userData", null);
        return "User";
    }
}
