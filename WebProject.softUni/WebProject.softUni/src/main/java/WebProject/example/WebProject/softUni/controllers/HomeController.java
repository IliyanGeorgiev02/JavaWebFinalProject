package WebProject.example.WebProject.softUni.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String getHome() {
        return "home";
    }

    @GetMapping("/Reviews")
    public String getReviews() {
        return "Reviews";
    }

    @GetMapping("/Lists")
    public String getLists() {
        return "Lists";
    }
}
