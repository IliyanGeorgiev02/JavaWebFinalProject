package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddCommentDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.services.CommentsService;
import WebProject.example.WebProject.softUni.services.ListService;
import WebProject.example.WebProject.softUni.services.UserHelperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentsControllerIt {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentsService commentsService;

    @MockBean
    private UserHelperService userHelperService;

    @MockBean
    private ListService listService;

    @Autowired
    private ObjectMapper objectMapper;  // For JSON serialization/deserialization

    @Test
    public void testAddCommentToList_Success() throws Exception {
        // Arrange
        Long listId = 1L;
        AddCommentDto addCommentDto = new AddCommentDto();
        addCommentDto.setText("Great list!");

        CustomList customList = new CustomList();
        customList.setId(listId);

        when(listService.findListById(listId)).thenReturn(Optional.of(customList));
        when(userHelperService.getUser()).thenReturn(new User()); // Mock the user
        // No need to mock CommentsService as we're testing the controller here

        // Act & Assert
        mockMvc.perform(post("/CustomList/{id}", listId)
                        .flashAttr("addCommentDto", addCommentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/CustomList/" + listId));

        verify(commentsService).addComment(any(Comment.class)); // Verify that addComment was called
    }

    @Test
    public void testAddCommentToList_ListNotFound() throws Exception {
        // Arrange
        Long listId = 1L;
        AddCommentDto addCommentDto = new AddCommentDto();
        addCommentDto.setText("Great list!");

        when(listService.findListById(listId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/CustomList/{id}", listId)
                        .flashAttr("addCommentDto", addCommentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));

        verifyNoInteractions(commentsService); // Verify that no interaction with commentsService occurs
    }
}
