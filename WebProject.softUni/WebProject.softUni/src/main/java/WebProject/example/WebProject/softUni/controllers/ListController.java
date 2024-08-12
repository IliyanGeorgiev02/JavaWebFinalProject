package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.*;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.model.enums.UserRoleEnum;
import WebProject.example.WebProject.softUni.services.*;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        redirectAttributes.addFlashAttribute("listData", listDto);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.listDto", bindingResult);
        return "redirect:/CreateList";
    }

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
        boolean alreadyInList = false;
        if (!customList.getMovies().contains(movieToUse)) {
            customList.getMovies().add(movieToUse);
            this.listService.saveList(customList);
        } else {
            alreadyInList = true;
        }

        if (alreadyInList) {
            redirectAttributes.addFlashAttribute("message", "The list already contains this movie");
        } else {
            redirectAttributes.addFlashAttribute("message", "Movie added to the list successfully");
        }

        return "redirect:/ListOfMovies";
    }



    @PostMapping("/CustomList/{listId}/like")
    public String likeList(@PathVariable("listId") Long listId, Model model) {
        this.listService.likeList(listId);
        return "redirect:/CustomList/" + listId;
    }

    @PostMapping("/CustomList/{listId}/dislike")
    public String dislikeList(@PathVariable("listId") Long listId, Model model) {
        this.listService.dislikeList(listId);
        return "redirect:/CustomList/" + listId;
    }
}
