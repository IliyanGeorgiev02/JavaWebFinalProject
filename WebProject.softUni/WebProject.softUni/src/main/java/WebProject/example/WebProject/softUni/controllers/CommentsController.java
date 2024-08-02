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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/{id}/like")
    public String likeComment(@PathVariable("id") Long commentId) {
        Long customListId = commentsService.likeComment(commentId);
        if (customListId != null) {
            return "redirect:/CustomList/" + customListId;
        } else {
            return"redirect:/CustomList/" + customListId;
        }
    }

    @PostMapping("/{id}/dislike")
    public String dislikeComment(@PathVariable("id") Long commentId) {
        Long customListId = commentsService.dislikeComment(commentId);
        if (customListId != null) {
            return "redirect:/CustomList/" + customListId;
        } else {
            return "redirect:/CustomList/" + customListId;
        }
    }

}
