package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.CommentsDto;
import WebProject.example.WebProject.softUni.dtos.ListOfCommentsDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;

    public CommentsService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void addComment(Comment mappedComment) {
        this.commentRepository.saveAndFlush(mappedComment);
    }


    public void likeComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
        }
    }

    public void dislikeComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getLikes() - 1 >= 0) {
                comment.setLikes(comment.getLikes() - 1);
            }
            commentRepository.save(comment);
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

    public void dislikeReview(Long commentId) {

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
        commentsDto.setLikes(comment.getLikes());
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
}
