package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.MovieSearchDto;
import WebProject.example.WebProject.softUni.dtos.OMDBSearchResponseDto;
import WebProject.example.WebProject.softUni.services.OmdbService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Controller
public class OmdbController {

    private final OmdbService omdbService;

    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }


    @PostMapping("/search")
    public String search(MovieSearchDto movieSearchDto, Model model) {
        model.addAttribute("movieSearchDto",movieSearchDto);
        String title=movieSearchDto.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        OMDBSearchResponseDto result = omdbService.searchByTitle(title);
        model.addAttribute("result", result);
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
