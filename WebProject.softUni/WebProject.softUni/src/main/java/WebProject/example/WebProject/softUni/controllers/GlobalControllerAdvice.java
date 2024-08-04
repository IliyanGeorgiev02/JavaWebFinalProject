package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddMovieToListDto;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.MovieSearchDto;
import WebProject.example.WebProject.softUni.dtos.OMDBSearchResponseDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("movieSearchDto")
    public void globalAttribute(Model model) {
        model.addAttribute("movieSearchDto", new MovieSearchDto());
    }
}
