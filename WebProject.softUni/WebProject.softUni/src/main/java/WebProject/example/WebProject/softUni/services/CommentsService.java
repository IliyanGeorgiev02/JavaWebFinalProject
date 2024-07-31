package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.model.Comment;
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

    public List<Comment> findAllCommentsInList(long id) {
       return this.commentRepository.findAllByCustomListId(id);
    }

    public Long likeComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
            return comment.getCustomList().getId();  // Return the CustomList ID
        }
        return null;
    }

    public Long dislikeComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setLikes(comment.getLikes() - 1);
            commentRepository.save(comment);
            return comment.getCustomList().getId();  // Return the CustomList ID
        }
        return null;
    }
}
