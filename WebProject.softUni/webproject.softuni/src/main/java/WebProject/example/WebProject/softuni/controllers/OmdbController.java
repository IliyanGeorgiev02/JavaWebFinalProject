package webproject.example.webproject.softuni.controllers;

import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.services.ListService;
import webproject.example.webproject.softuni.services.OmdbService;
import webproject.example.webproject.softuni.services.UserHelperService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OmdbController {

    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final UserHelperService userHelperService;
    private final ListService listService;


    public OmdbController(OmdbService omdbService, UserHelperService userHelperService, ListService listService) {
        this.omdbService = omdbService;
        this.userHelperService = userHelperService;
        this.listService = listService;
    }

    @PostMapping("/search")
    public String search(MovieSearchDto movieSearchDto, HttpSession session) {
        String title = movieSearchDto.getTitle();
        OMDBSearchResponseDto result = omdbService.searchByTitle(title.trim());
        List<CustomList> listByUsername = this.listService.findListByUsername(this.userHelperService.getUser().getUsername());
        session.setAttribute("movieSearchDto", movieSearchDto);
        session.setAttribute("result", result);
        session.setAttribute("lists", listByUsername);
        return "redirect:/ListOfMovies";
    }


    @GetMapping("/ListOfMovies")
    public String loadMoviesPage(HttpSession session, Model model) {
        MovieSearchDto movieSearchDto = (MovieSearchDto) session.getAttribute("movieSearchDto");
        OMDBSearchResponseDto result = (OMDBSearchResponseDto) session.getAttribute("result");
        List<CustomList> lists = (List<CustomList>) session.getAttribute("lists");
        model.addAttribute("movieSearchDto", movieSearchDto);
        model.addAttribute("result", result);
        model.addAttribute("lists", lists);
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
