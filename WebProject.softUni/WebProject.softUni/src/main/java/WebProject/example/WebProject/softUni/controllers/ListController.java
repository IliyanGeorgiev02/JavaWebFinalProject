package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.*;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.services.*;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

@Controller
public class ListController {
    private final ListService listService;
    private final CommentsService commentsService;
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(ListController.class);
    private final UserHelperService userHelperService;
    private final OmdbService omdbService;
    private final MovieService movieService;

    public ListController(ListService listService, CommentsService commentsService, ModelMapper modelMapper, UserHelperService userHelperService, OmdbService omdbService, MovieService movieService) {
        this.listService = listService;
        this.commentsService = commentsService;
        this.modelMapper = modelMapper;
        this.userHelperService = userHelperService;
        this.omdbService = omdbService;
        this.movieService = movieService;
    }

    @GetMapping("/CreateList")
    public String getCreateList(Model model) {
        model.addAttribute("listDto", new CreateListDto());
        return "CreateList";
    }

    @GetMapping("/CustomList")
    public String getCustomList(Model model, ListDataDto listData) {
        model.addAttribute("listData", listData);
        model.addAttribute("addCommentDto", new AddCommentDto());
        return "CustomList";
    }

    @GetMapping("/CustomList/{id}")
    public String viewListById(@PathVariable("id") Long id, Model model) {
        Optional<CustomList> customList = listService.findListById(id);
        if (customList.isPresent()) {
            List<Comment> allCommentsInList = commentsService.findAllCommentsInList(customList.get().getId());
            model.addAttribute("commentsData", allCommentsInList);
            model.addAttribute("listData", customList.get());
            model.addAttribute("addCommentDto", new AddCommentDto());
            return "CustomList";
        } else {
            model.addAttribute("errorMessage", "The requested list does not exist.");
            return "CustomList";
        }
    }


    @PostMapping("/CreateList")
    public String createList(@Valid CreateListDto listDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (!bindingResult.hasErrors()) {
            this.listService.addList(listDto);
            ListDataDto mappedList = modelMapper.map(listDto, ListDataDto.class);
            mappedList.setUser(userHelperService.getUser().getUsername());
            Optional<List<Movie>> allInMovieList = this.listService.findAllInMovieList(mappedList.getTitle(), mappedList.getDescription());
            if (allInMovieList.isPresent()) {
                mappedList.setMovies(allInMovieList.get());
            } else {
                mappedList.setMovies(new ArrayList<>());
            }
            model.addAttribute("listData", mappedList);
            return "/CustomList";
        }
        redirectAttributes.addFlashAttribute("listDto", listDto);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.listDto", bindingResult);
        return "redirect:/CreateList";
    }

    @GetMapping("/Lists")
    public String getLists(Model model) {
        if (!model.containsAttribute("findListDto")) {
            model.addAttribute("findListDto", new FindListDto());
        }
        model.addAttribute("allLists", this.listService.findAllLists());
        return "Lists";
    }

    @PostMapping("/Lists")
    public String getSpecificList(Model model, @ModelAttribute("findListDto") FindListDto findListDto) {
        CustomList customList = listService.findListByTitleAndDescription(findListDto);
        model.addAttribute("listData", customList);
        logger.info("Received title: " + findListDto.getTitle());
        logger.info("Received description: " + findListDto.getDescription());
        logger.info("Received username: " + findListDto.getUsername());
        return "CustomList";
    }

    @DeleteMapping("/CustomList/{id}")
    public String deleteList(@PathVariable long id) {
        Optional<CustomList> listById = this.listService.findListById(id);
        listById.ifPresent(this.listService::deleteList);
        return "redirect:/Lists";
    }

    @PostMapping("/AddToList")
    private String addMovieToList(@ModelAttribute("movieAndListData") @Valid AddMovieToListDto addMovieToListDto, BindingResult bindingResult,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("movieAndListData", addMovieToListDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movieAndListData", bindingResult);
            return "redirect:/ListOfMovies";
        }

        long id = addMovieToListDto.getListId();
        Optional<CustomList> listById = this.listService.findListById(id);
        if (listById.isEmpty()) {
            redirectAttributes.addFlashAttribute("movieAndListData", addMovieToListDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movieAndListData", bindingResult);
            return "redirect:/ListOfMovies";
        }

        MovieFullInfoDto movieFullInfoDto = this.omdbService.searchByTitleAndYear(addMovieToListDto.getMovieTitle(), addMovieToListDto.getMovieYear());
        if (movieFullInfoDto == null) {
            redirectAttributes.addFlashAttribute("movieAndListData", addMovieToListDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movieAndListData", bindingResult);
            return "redirect:/ListOfMovies";
        }
        Movie mappedMovie = this.modelMapper.map(movieFullInfoDto, Movie.class);
        Optional<Movie> movie = this.movieService.findMovie(mappedMovie);
        if (movie.isEmpty()) {
            this.movieService.saveMovie(mappedMovie);
        }
        CustomList customList = listById.get();
        if (!customList.getMovies().contains(mappedMovie)) {
            customList.getMovies().add(mappedMovie);
        }
        return "redirect:/ListOfMovies";
    }
}
