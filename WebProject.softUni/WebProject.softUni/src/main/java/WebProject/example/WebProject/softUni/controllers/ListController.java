package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddCommentDto;
import WebProject.example.WebProject.softUni.dtos.CreateListDto;
import WebProject.example.WebProject.softUni.dtos.FindListDto;
import WebProject.example.WebProject.softUni.dtos.ListDataDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.services.CommentsService;
import WebProject.example.WebProject.softUni.services.ListService;
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

    public ListController(ListService listService, CommentsService commentsService, ModelMapper modelMapper) {
        this.listService = listService;
        this.commentsService = commentsService;
        this.modelMapper = modelMapper;
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
        List<Comment> allCommentsInList = commentsService.findAllCommentsInList(customList.get().getId());
        model.addAttribute("commentsData", allCommentsInList);
        model.addAttribute("listData", customList.get());
        model.addAttribute("addCommentDto", new AddCommentDto());
        return "CustomList";
    }


    @PostMapping("/CreateList")
    public String createList(@Valid CreateListDto listDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (!bindingResult.hasErrors()) {
            this.listService.addList(listDto);
            ListDataDto mappedList = modelMapper.map(listDto, ListDataDto.class);
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
}
