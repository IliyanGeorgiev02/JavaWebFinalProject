package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.model.enums.UserRoleEnum;
import WebProject.example.WebProject.softUni.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserHelperServiceIntegrationTest {
    @MockBean
    private UserHelperService userHelperService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(UserRoleEnum.USER);
        testUser.setBio("Test bio");

        userRepository.save(testUser);
    }

    @Test
    public void testUpdateUser_WithValidProfilePicture() {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(testUser.getId());
        userProfileDto.setUsername("updatedUser");
        userProfileDto.setFirstName("UpdatedFirstName");
        userProfileDto.setLastName("UpdatedLastName");
        userProfileDto.setBio("Updated bio");
        String uploadedImagePath = "http://cloudinary.com/testimage.jpg";
        MultipartFile profilePicture = new MockMultipartFile("profilePicture", "testimage.jpg", "image/jpeg", new byte[0]);
        userProfileDto.setProfilePicture(profilePicture);
        when(cloudinaryService.upload(any(MultipartFile.class), anyString())).thenReturn(uploadedImagePath);
        userHelperService.updateUser(userProfileDto);
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("UpdatedFirstName", updatedUser.getFirstName());
        assertEquals("UpdatedLastName", updatedUser.getLastName());
        assertEquals("Updated bio", updatedUser.getBio());
        assertEquals(uploadedImagePath, updatedUser.getProfilePicture());
    }

    @Test
    public void testUpdateUser_WithoutProfilePicture() {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(testUser.getId());
        userProfileDto.setUsername("updatedUserWithoutPic");
        userProfileDto.setFirstName("UpdatedFirstName");
        userProfileDto.setLastName("UpdatedLastName");
        userProfileDto.setBio("Updated bio");
        userProfileDto.setProfilePicture(null);
        userHelperService.updateUser(userProfileDto);
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updatedUserWithoutPic", updatedUser.getUsername());
        assertEquals("UpdatedFirstName", updatedUser.getFirstName());
        assertEquals("UpdatedLastName", updatedUser.getLastName());
        assertEquals("Updated bio", updatedUser.getBio());
        assertNull(updatedUser.getProfilePicture()); // Profile picture should not be updated
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(999L); // Non-existent user ID
        userProfileDto.setUsername("nonExistentUser");
        userHelperService.updateUser(userProfileDto);
        User unchangedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(unchangedUser);
        assertEquals("testuser", unchangedUser.getUsername());
    }
}
