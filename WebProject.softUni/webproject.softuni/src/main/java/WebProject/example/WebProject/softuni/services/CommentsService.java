package webproject.example.webproject.softuni.services;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.AddCommentDto;
import webproject.example.webproject.softuni.dtos.CommentsDto;
import webproject.example.webproject.softuni.dtos.ListOfCommentsDto;
import webproject.example.webproject.softuni.dtos.ReviewFullInfoDto;
import webproject.example.webproject.softuni.model.Comment;
import webproject.example.webproject.softuni.model.Like;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final ReviewClient reviewClient;
    private final UserHelperService userHelperService;

    public CommentsService(CommentRepository commentRepository, ModelMapper modelMapper, ReviewClient reviewClient, UserHelperService userHelperService) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.reviewClient = reviewClient;
        this.userHelperService = userHelperService;
    }

    public void addComment(Comment mappedComment) {
        this.commentRepository.saveAndFlush(mappedComment);
    }


    public void likeComment(Long commentId, User user) {
        Optional<Comment> byId = this.commentRepository.findById(commentId);
        if (byId.isPresent()) {
            Comment comment = byId.get();
            boolean alreadyLiked = comment.getLikes().stream()
                    .anyMatch(like -> like.getLiked().equals(user));
            if (!alreadyLiked) {
                Like like = new Like();
                like.setLiked(user);
                like.setComment(comment);
                comment.getLikes().add(like);
                this.commentRepository.save(comment);
            }
        }
    }

    public void dislikeComment(Long commentId, User user) {
        Optional<Comment> byId = this.commentRepository.findById(commentId);
        if (byId.isPresent()) {
            Comment comment = byId.get();
            Optional<Like> likeToRemove = comment.getLikes().stream()
                    .filter(like -> like.getLiked().equals(user))
                    .findFirst();
            if (likeToRemove.isPresent()) {
                comment.getLikes().remove(likeToRemove.get());
                this.commentRepository.save(comment);
            }
        }
    }

    public List<Comment> findAllCommentsInReview(Long reviewId) {
        return this.commentRepository.findAllByReviewId(reviewId);
    }

    public Optional<Comment> findCommentById(Long id) {
        return this.commentRepository.findById(id);
    }

    public void saveComment(Comment comment) {
        this.commentRepository.save(comment);
    }


    public List<Comment> findByReviewId(long id) {
        return commentRepository.findAllByReviewId(id);
    }

    public ListOfCommentsDto mapCommentsToListOfCommentsDto(List<Comment> comments) {
        List<CommentsDto> commentsDtos = comments.stream()
                .map(this::convertToCommentsDto)
                .collect(Collectors.toList());

        ListOfCommentsDto listOfCommentsDto = new ListOfCommentsDto();
        listOfCommentsDto.setComments(commentsDtos);

        return listOfCommentsDto;
    }

    private CommentsDto convertToCommentsDto(Comment comment) {
        CommentsDto commentsDto = new CommentsDto();
        commentsDto.setText(comment.getText());
        commentsDto.setLikes(comment.getLikes().size());
        commentsDto.setId(comment.getId());
        User user = comment.getUser();
        if (user != null) {
            commentsDto.setUsername(user.getUsername());
        }
        return commentsDto;
    }

    public List<Comment> findCommentByListId(long id) {
        return this.commentRepository.findAllByListId(id);
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> byId = this.commentRepository.findById(commentId);
        byId.ifPresent(this.commentRepository::delete);
    }

    @PostMapping("/Review/{reviewId}")
    public String postComment(@ModelAttribute("addCommentDto") AddCommentDto addCommentDto, @PathVariable("reviewId") long id) {
        Optional<ReviewFullInfoDto> reviewById = this.reviewClient.getReviewById(id);
        if (reviewById.isEmpty()) {
            return "redirect:/home";
        }
        Comment mappedComment = this.modelMapper.map(addCommentDto, Comment.class);
        mappedComment.setLikes(new HashSet<>());
        mappedComment.setReviewId(id);
        mappedComment.setUser(userHelperService.getUser());
        addComment(mappedComment);
        return "redirect:/Review/" + id;
    }
}
