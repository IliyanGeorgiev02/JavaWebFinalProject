package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.*;
import WebProject.example.WebProject.softUni.services.OmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class OmdbController {

    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);


    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @GetMapping("/listOfMovies")
    public String listOfMovies(Model model) {
        model.addAttribute("result", new OMDBSearchResponseDto());
        model.addAttribute("movieResponseDto", new MovieFullInfoDto());
        return "ListOfMovies";
    }

    @PostMapping("/search")
    public String search(MovieSearchDto movieSearchDto, Model model) {
        model.addAttribute("movieSearchDto", movieSearchDto);
        String title = movieSearchDto.getTitle();
        OMDBSearchResponseDto result = omdbService.searchByTitle(title);
        logger.info(result.getSearch().toString());
        model.addAttribute("result", result);
        model.addAttribute("movieResponseDto", new MovieFullInfoDto());
        return "ListOfMovies";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException e, Model model) {
        model.addAttribute("error", "Invalid input");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e, Model model) {
        model.addAttribute("error", "An error occurred: " + e.getMessage());
        return "error";
    }


}
