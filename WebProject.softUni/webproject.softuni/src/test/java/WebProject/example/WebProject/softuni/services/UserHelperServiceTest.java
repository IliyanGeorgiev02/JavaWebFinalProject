package webproject.example.webproject.softuni.services;

import webproject.example.webproject.softuni.dtos.UserProfileDto;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserHelperServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserHelperService userHelperService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUser_whenUserExists_thenReturnsUser() {
        User user = new User();
        user.setUsername("testUser");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User result = userHelperService.getUser();

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUser_whenUserDoesNotExist_thenReturnsNull() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        User result = userHelperService.getUser();

        assertNull(result);
    }

    @Test
    void updateUser_whenUserExistsAndProfilePictureProvided_thenUpdatesUser() {
        User user = new User();
        user.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setProfilePicture(file);

        when(userHelperService.getUser()).thenReturn(user);
        when(cloudinaryService.upload(file, "")).thenReturn("uploadedPath");

        userHelperService.updateUser(userProfileDto);

        assertEquals("uploadedPath", user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_whenUserExistsAndNoProfilePicture_thenUpdatesUserWithoutProfilePicture() {
        User user = new User();
        user.setId(1L);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setProfilePicture(mock(MultipartFile.class));
        when(userProfileDto.getProfilePicture().isEmpty()).thenReturn(true);

        when(userHelperService.getUser()).thenReturn(user);

        userHelperService.updateUser(userProfileDto);

        assertNull(user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_whenUserNotFound_thenNoUpdate() {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setProfilePicture(mock(MultipartFile.class));

        when(userHelperService.getUser()).thenReturn(null);

        userHelperService.updateUser(userProfileDto);

        verify(userRepository, never()).save(any(User.class));
    }

}
