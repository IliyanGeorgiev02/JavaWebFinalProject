package webproject.example.webproject.softuni.controllers;

import webproject.example.webproject.softuni.dtos.MovieSearchDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("movieSearchDto")
    public void globalAttribute(Model model) {
        model.addAttribute("movieSearchDto", new MovieSearchDto());
    }
}
