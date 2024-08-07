package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;

    public CommentsService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void addComment(Comment mappedComment) {
        this.commentRepository.saveAndFlush(mappedComment);
    }


    public Long likeComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
            return comment.getCustomList().getId();
        }
        return null;
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
}
