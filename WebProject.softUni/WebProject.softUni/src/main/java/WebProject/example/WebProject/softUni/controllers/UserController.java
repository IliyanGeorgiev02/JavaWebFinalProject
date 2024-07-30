package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.LoginUserDto;
import WebProject.example.WebProject.softUni.dtos.RegisterUserDto;
import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.services.UserHelperService;
import WebProject.example.WebProject.softUni.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
        model.addAttribute("profileData", userService.getProfileData());
        model.addAttribute("listsData", this.userService.getAllLists(this.userHelperService.getUser()));
        return "User";
    }

    @PostMapping("/editProfile")
    public String changeUserInfo(UserProfileDto userProfileDto) {
        userHelperService.updateUser(userProfileDto);
        return "redirect:/User";
    }
}
