package webproject.example.webproject.softuni.services;
import webproject.example.webproject.softuni.dtos.CommentsDto;
import webproject.example.webproject.softuni.dtos.ListOfCommentsDto;
import webproject.example.webproject.softuni.model.Comment;
import webproject.example.webproject.softuni.model.Like;
import webproject.example.webproject.softuni.model.Review;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;
    private final LikeService likeService;

    public CommentsService(CommentRepository commentRepository, LikeService likeService) {
        this.commentRepository = commentRepository;
        this.likeService = likeService;
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
}
