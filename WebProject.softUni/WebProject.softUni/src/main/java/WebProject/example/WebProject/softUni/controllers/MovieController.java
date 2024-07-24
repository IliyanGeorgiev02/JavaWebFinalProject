package WebProject.example.WebProject.softUni.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovieController {
    @GetMapping("/Movie")
    public String getMovie(){
        return "Movie";
    }
}
