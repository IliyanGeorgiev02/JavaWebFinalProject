package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddCommentDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.services.CommentsService;
import WebProject.example.WebProject.softUni.services.ListService;
import WebProject.example.WebProject.softUni.services.UserHelperService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.Optional;

@Controller
public class CommentsController {

    private final ModelMapper modelMapper;
    private final CommentsService commentsService;
    private final UserHelperService userHelperService;
    private final ListService listService;

    public CommentsController(ModelMapper modelMapper, CommentsService commentsService, UserHelperService userHelperService, ListService listService) {
        this.modelMapper = modelMapper;
        this.commentsService = commentsService;
        this.userHelperService = userHelperService;
        this.listService = listService;
    }

    @PostMapping("/CustomList/{id}")
    public String addCommentToList(@PathVariable("id") Long id,
                                   @ModelAttribute("addCommentDto") AddCommentDto addCommentDto) {
        Optional<CustomList> listById = this.listService.findListById(id);
        if (listById.isEmpty()) {
            return "redirect:/error";
        }
        CustomList customList = listById.get();
        Comment mappedComment = this.modelMapper.map(addCommentDto, Comment.class);

        mappedComment.setLikes(0);
        mappedComment.setCustomList(customList);
        mappedComment.setUser(userHelperService.getUser());

        this.commentsService.addComment(mappedComment);
        return "redirect:/CustomList/" + id;
    }

    @PostMapping("Home/Comments/{commentId}/like")
    public String likeHomeComment(@PathVariable("commentId") Long commentId) {
        this.commentsService.likeComment(commentId);
        return "redirect:/home";
    }

    @PostMapping("Home/Comments/{commentId}/dislike")
    public String dislikeHomeComment(@PathVariable("id") Long commentId) {
        this.commentsService.dislikeComment(commentId);
        return "redirect:/home";
    }

    @PostMapping("List/Comments/{commentId}/{listId}/like")
    public String likeListComment(@PathVariable("commentId") Long commentId, @PathVariable("listId") Long id) {
        this.commentsService.likeComment(commentId);
        return "redirect:/CustomList/" + id;
    }

    @PostMapping("List/Comments/{commentId}/{listId}/dislike")
    public String dislikeListComment(@PathVariable("commentId") Long commentId, @PathVariable("listId") Long id) {
        this.commentsService.dislikeComment(commentId);
        return "redirect:/CustomList/" + id;
    }

}
