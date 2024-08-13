package webproject.example.webproject.softuni.services;

import webproject.example.webproject.softuni.dtos.CommentsDto;
import webproject.example.webproject.softuni.dtos.ListOfCommentsDto;
import webproject.example.webproject.softuni.model.Comment;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentsServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentsService commentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_whenCalled_thenSavesComment() {
        Comment comment = new Comment();
        when(commentRepository.saveAndFlush(comment)).thenReturn(comment);
        commentsService.addComment(comment);
        verify(commentRepository, times(1)).saveAndFlush(comment);
    }

    @Test
    void likeComment_whenCommentExists_thenIncreasesLikes() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setLikes(5);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentsService.likeComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
        assertEquals(6, comment.getLikes(), "The number of likes should be incremented by 1");
    }

    @Test
    void likeComment_whenCommentDoesNotExist_thenDoesNothing() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        commentsService.likeComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void dislikeComment_whenCommentExistsAndLikesGreaterThanZero_thenDecreasesLikes() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setLikes(5);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentsService.dislikeComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
        assertEquals(4, comment.getLikes(), "The number of likes should be decremented by 1");
    }

    @Test
    void dislikeComment_whenCommentExistsAndLikesZero_thenDoesNotDecreaseLikesBelowZero() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setLikes(0);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentsService.dislikeComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
        assertEquals(0, comment.getLikes(), "The number of likes should not go below zero");
    }

    @Test
    void dislikeComment_whenCommentDoesNotExist_thenDoesNothing() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        commentsService.dislikeComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void findAllCommentsInReview_whenCommentsExist_thenReturnsComments() {
        Long reviewId = 1L;
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findAllByReviewId(reviewId)).thenReturn(comments);

        List<Comment> result = commentsService.findAllCommentsInReview(reviewId);

        assertEquals(comments, result);
        verify(commentRepository, times(1)).findAllByReviewId(reviewId);
    }

    @Test
    void findCommentById_whenCommentExists_thenReturnsComment() {
        Long commentId = 1L;
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Optional<Comment> result = commentsService.findCommentById(commentId);

        assertTrue(result.isPresent());
        assertEquals(comment, result.get());
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void findCommentById_whenCommentDoesNotExist_thenReturnsEmptyOptional() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        Optional<Comment> result = commentsService.findCommentById(commentId);

        assertFalse(result.isPresent());
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void saveComment_whenCalled_thenSavesComment() {
        Comment comment = new Comment();
        when(commentRepository.save(comment)).thenReturn(comment);

        commentsService.saveComment(comment);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void findByReviewId_whenCommentsExist_thenReturnsComments() {
        long reviewId = 1L;
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findAllByReviewId(reviewId)).thenReturn(comments);

        List<Comment> result = commentsService.findByReviewId(reviewId);

        assertEquals(comments, result);
        verify(commentRepository, times(1)).findAllByReviewId(reviewId);
    }

    @Test
    void mapCommentsToListOfCommentsDto_whenCommentsExist_thenReturnsListOfCommentsDto() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Comment 1");
        comment1.setLikes(10);
        User user1 = new User();
        user1.setUsername("user1");
        comment1.setUser(user1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Comment 2");
        comment2.setLikes(5);
        User user2 = new User();
        user2.setUsername("user2");
        comment2.setUser(user2);

        List<Comment> comments = List.of(comment1, comment2);

        CommentsDto dto1 = new CommentsDto();
        dto1.setId(1L);
        dto1.setText("Comment 1");
        dto1.setLikes(10);
        dto1.setUsername("user1");

        CommentsDto dto2 = new CommentsDto();
        dto2.setId(2L);
        dto2.setText("Comment 2");
        dto2.setLikes(5);
        dto2.setUsername("user2");

        ListOfCommentsDto expectedListOfCommentsDto = new ListOfCommentsDto();
        expectedListOfCommentsDto.setComments(List.of(dto1, dto2));

        ListOfCommentsDto result = commentsService.mapCommentsToListOfCommentsDto(comments);

        assertEquals(expectedListOfCommentsDto, result);
    }

    @Test
    void findCommentByListId_whenCommentsExist_thenReturnsComments() {
        long listId = 1L;
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findAllByListId(listId)).thenReturn(comments);

        List<Comment> result = commentsService.findCommentByListId(listId);

        assertEquals(comments, result);
        verify(commentRepository, times(1)).findAllByListId(listId);
    }

}
