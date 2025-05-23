package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.AddCommentDto;
import webproject.example.webproject.softuni.dtos.ReviewFullInfoDto;
import webproject.example.webproject.softuni.model.Comment;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.services.CommentsService;
import webproject.example.webproject.softuni.services.ListService;
import webproject.example.webproject.softuni.services.UserHelperService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.HashSet;
import java.util.Optional;

@Controller
public class CommentsController {

    private final ModelMapper modelMapper;
    private final CommentsService commentsService;
    private final UserHelperService userHelperService;
    private final ListService listService;
    private final ReviewClient reviewClient;

    public CommentsController(ModelMapper modelMapper, CommentsService commentsService, UserHelperService userHelperService, ListService listService, ReviewClient reviewClient) {
        this.modelMapper = modelMapper;
        this.commentsService = commentsService;
        this.userHelperService = userHelperService;
        this.listService = listService;
        this.reviewClient = reviewClient;
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

        mappedComment.setLikes(new HashSet<>());
        mappedComment.setCustomList(customList);
        mappedComment.setUser(userHelperService.getUser());

        this.commentsService.addComment(mappedComment);
        return "redirect:/CustomList/" + id;
    }

    @PostMapping("/Review/AddComment/{reviewId}")
    public String addCommentToReview(@ModelAttribute("addCommentDto") AddCommentDto addCommentDto, @PathVariable("reviewId") long id) {
        Optional<ReviewFullInfoDto> reviewById = this.reviewClient.getReviewById(id);
        if (reviewById.isEmpty()) {
            return "redirect:/home";
        }
        Comment mappedComment = this.modelMapper.map(addCommentDto, Comment.class);
        mappedComment.setLikes(new HashSet<>());
        mappedComment.setReviewId(id);
        mappedComment.setUser(userHelperService.getUser());
        Comment comment = this.commentsService.addComment(mappedComment);
        boolean commentAdded = this.reviewClient.postComment(id, comment.getId());
        if (!commentAdded) {
            return "redirect:/Review/" + id;
        }
        return "redirect:/Review/" + id;
    }


    @Transactional
    @PostMapping("Home/Comments/{commentId}/like")
    public String likeHomeComment(@PathVariable("commentId") Long commentId) {
        this.commentsService.likeComment(commentId, userHelperService.getUser());
        return "redirect:/home";
    }

    @Transactional
    @PostMapping("Home/Comments/{commentId}/dislike")
    public String dislikeHomeComment(@PathVariable("commentId") Long commentId) {
        this.commentsService.dislikeComment(commentId, userHelperService.getUser());
        return "redirect:/home";
    }

    @Transactional
    @PostMapping("List/Comments/{commentId}/{listId}/like")
    public String likeListComment(@PathVariable("commentId") Long commentId, @PathVariable("listId") Long id) {
        this.commentsService.likeComment(commentId, userHelperService.getUser());
        return "redirect:/CustomList/" + id;
    }

    @Transactional
    @PostMapping("List/Comments/{commentId}/{listId}/dislike")
    public String dislikeListComment(@PathVariable("commentId") Long commentId, @PathVariable("listId") Long id) {
        this.commentsService.dislikeComment(commentId, userHelperService.getUser());
        return "redirect:/CustomList/" + id;
    }

    @Transactional
    @PostMapping("Review/Comments/{commentId}/{reviewId}/like")
    public String likeReviewComment(@PathVariable("reviewId") Long reviewId, @PathVariable("commentId") Long commentId) {
        this.commentsService.likeComment(commentId, userHelperService.getUser());
        return "redirect:/Review/" + reviewId;
    }

    @Transactional
    @PostMapping("Review/Comments/{commentId}/{reviewId}/dislike")
    public String dislikeReviewComment(@PathVariable("reviewId") Long reviewId, @PathVariable("commentId") Long commentId) {
        this.commentsService.dislikeComment(commentId, userHelperService.getUser());
        return "redirect:/Review/" + reviewId;
    }

    @DeleteMapping("/CustomList/Delete/Comments/{commentId}/{listId}")
    public String deleteCommentList(@PathVariable("commentId") Long commentId, @PathVariable("listId") Long id) {
        this.commentsService.deleteComment(commentId);
        return "redirect:/CustomList/" + id;
    }

    @DeleteMapping("/Review/Delete/Comments/{commentId}/{reviewId}")
    public String deleteCommentReview(@PathVariable("commentId") Long commentId, @PathVariable("reviewId") Long id) {
        this.commentsService.deleteComment(commentId);
        return "redirect:/Review/" + id;
    }


}
