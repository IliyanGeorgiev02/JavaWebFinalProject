package webproject.example.webproject.softuni.controllers;

import jakarta.persistence.Temporal;
import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.Comment;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.model.enums.UserRoleEnum;
import webproject.example.webproject.softuni.services.*;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.modelmapper.ModelMapper;

import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

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
        if (!model.containsAttribute("listDto")) {
            model.addAttribute("listDto", new CreateListDto());
        }
        return "CreateList";
    }

    @GetMapping("/CustomList")
    public String getCustomList(Model model, ListDataDto listData) {
        model.addAttribute("listData", listData);
        model.addAttribute("addCommentDto", new AddCommentDto());
        return "CustomList";
    }

    @Transactional
    @GetMapping("/CustomList/{id}")
    public String viewListById(@PathVariable("id") Long id, Model model) {
        Optional<CustomList> customList = listService.findListById(id);
        User user = this.userHelperService.getUser();
        boolean isAdmin = user.getRole().equals(UserRoleEnum.ADMIN);
        if (customList.isPresent()) {
            CustomList customList1 = customList.get();
            List<Comment> commentByListId = this.commentsService.findCommentByListId(customList1.getId());
            ListOfCommentsDto listOfCommentsDto = this.commentsService.mapCommentsToListOfCommentsDto(commentByListId);
            DisplayListDto displayListDto = this.listService.convertToDisplayListDto(customList1);
            displayListDto.setUsername(customList1.getUser().getUsername());
            displayListDto.setUserId(customList1.getUser().getId());
            model.addAttribute("listData", displayListDto);
            model.addAttribute("commentsData", listOfCommentsDto);
            model.addAttribute("addCommentDto", new AddCommentDto());
            model.addAttribute("userHasRoleAdmin", isAdmin);
            model.addAttribute("currentUsername", userHelperService.getUserDetails().getUsername());
            return "CustomList";
        } else {
            model.addAttribute("errorMessage", "The requested list does not exist.");
            return "CustomList";
        }
    }

    @PostMapping("/CreateList")
    public String createList(@Valid CreateListDto listDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (!bindingResult.hasErrors()) {
            CustomList mappedList = this.listService.addList(listDto);
            return "redirect:/CustomList/" + mappedList.getId();
        }
        redirectAttributes.addFlashAttribute("listDto", listDto);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.listDto", bindingResult);
        return "redirect:/CreateList";
    }

    @Transactional
    @GetMapping("/Lists")
    public String getLists(Model model) {
        if (!model.containsAttribute("findListDto")) {
            model.addAttribute("findListDto", new FindListDto());
        }
        List<CustomList> allLists = this.listService.findAllLists();
        ListDto listDto = this.listService.mapCustomListsToListDto(allLists);
        model.addAttribute("listData", listDto);
        return "Lists";
    }

    @PostMapping("/GetList")
    public String getSpecificList(Model model, @ModelAttribute("findListDto") FindListDto findListDto) {
        CustomList customList = listService.findListByTitleAndDescription(findListDto);
        model.addAttribute("listData", customList);
        return "CustomList";
    }

    @DeleteMapping("/CustomList/{id}")
    public String deleteList(@PathVariable long id) {
        Optional<CustomList> listById = this.listService.findListById(id);
        listById.ifPresent(this.listService::deleteList);
        return "redirect:/Lists";
    }

    @PostMapping("/AddToList")
    public String addMovieToList(@RequestParam("title") String title,
                                 @RequestParam("year") String year,
                                 @RequestParam("listId") String listId,
                                 RedirectAttributes redirectAttributes) {

        long id = Long.parseLong(listId);
        Optional<CustomList> listById = this.listService.findListById(id);

        if (listById.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Custom list not found");
            return "redirect:/ListOfMovies";
        }

        MovieFullInfoDto movieFullInfoDto = this.omdbService.searchByTitleAndYear(title, year);
        if (movieFullInfoDto == null) {
            redirectAttributes.addFlashAttribute("error", "Movie not found");
            return "redirect:/ListOfMovies";
        }

        Movie movie = this.movieService.mapMovie(movieFullInfoDto);
        Optional<Movie> existingMovie = this.movieService.findMovie(movie);

        if (existingMovie.isEmpty()) {
            this.movieService.saveMovie(movie);
            existingMovie = Optional.of(movie);
        }

        Movie movieToUse = existingMovie.get();
        CustomList customList = listById.get();

        if (!customList.getMovies().contains(movieToUse)) {
            customList.getMovies().add(movieToUse);
            this.listService.saveList(customList);
            redirectAttributes.addFlashAttribute("message", "Movie added to the list");
        } else {
            redirectAttributes.addFlashAttribute("message", "The list already contains this movie");
        }

        return "redirect:/ListOfMovies";
    }

    @Transactional
    @PostMapping("/CustomList/{listId}/like")
    public String likeList(@PathVariable("listId") Long listId, Model model) {
        this.listService.likeList(listId, userHelperService.getUser());
        return "redirect:/CustomList/" + listId;
    }

    @Transactional
    @PostMapping("/CustomList/{listId}/dislike")
    public String dislikeList(@PathVariable("listId") Long listId, Model model) {
        this.listService.dislikeList(listId, userHelperService.getUser());
        return "redirect:/CustomList/" + listId;
    }

    @Transactional
    @PostMapping("/CustomList/Movie/Remove/{id}/{movieIndex}")
    public String removeMovieInList(@PathVariable("id") Long listId, @PathVariable("movieIndex") Long movieIndex) {
        Optional<CustomList> listById = this.listService.findListById(listId);
        if (listById.isEmpty()) {
            return "redirect:/CustomList/" + listId;
        }

        CustomList customList = listById.get();
        List<Movie> movieSet = customList.getMovies();
        List<Movie> movieList = new ArrayList<>(movieSet);

        int moviesCount = movieList.size();
        int index = movieIndex.intValue();

        if (index < 0 || index > moviesCount) {
            return "redirect:/CustomList/" + listId;
        }

        movieList.remove(index);

        List<Movie> updatedSet = new ArrayList<>(movieList);
        customList.setMovies(updatedSet);

        this.listService.saveList(customList);

        return "redirect:/CustomList/" + listId;
    }

    @Transactional
    @GetMapping("/Lists/{username}")
    public String getListsByUsername(Model model,@PathVariable("username") String username) {
        List<CustomList> listsByUsername = this.listService.findListByUsername(username);

        return "lists";
    }
}
