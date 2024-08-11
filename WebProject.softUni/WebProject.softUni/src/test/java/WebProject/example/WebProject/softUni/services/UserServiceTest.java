package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.RegisterUserDto;
import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.model.enums.UserRoleEnum;
import WebProject.example.WebProject.softUni.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHelperService userHelperService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_whenNoUsers_thenSetsAdminRoleAndSavesUser() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("user1");
        registerUserDto.setPassword("password");
        registerUserDto.setFirstName("John");
        registerUserDto.setLastName("Doe");
        registerUserDto.setEmail("john.doe@example.com");

        User mappedUser = new User();
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("encodedPassword");
        userService.registerUser(registerUserDto);
        verify(userRepository, times(1)).save(mappedUser);
        assertEquals(UserRoleEnum.ADMIN, mappedUser.getRole());
        assertEquals("encodedPassword", mappedUser.getPassword());
    }

    @Test
    void registerUser_whenUsersExist_thenSetsUserRoleAndSavesUser() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("user2");
        registerUserDto.setPassword("password");
        registerUserDto.setFirstName("Jane");
        registerUserDto.setLastName("Doe");
        registerUserDto.setEmail("jane.doe@example.com");

        User mappedUser = new User();
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("encodedPassword");
        userService.registerUser(registerUserDto);
        verify(userRepository, times(1)).save(mappedUser);
        assertEquals(UserRoleEnum.USER, mappedUser.getRole());
        assertEquals("encodedPassword", mappedUser.getPassword());
    }

    @Test
    void isUsernameUnique_whenUsernameNotExists_thenReturnsTrue() {
        String username = "uniqueUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        boolean result = userService.isUsernameUnique(username);
        assertTrue(result, "Username should be unique");
    }

    @Test
    void isUsernameUnique_whenUsernameExists_thenReturnsFalse() {
        String username = "existingUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        boolean result = userService.isUsernameUnique(username);
        assertFalse(result, "Username should not be unique");
    }

    @Test
    void isEmailUnique_whenEmailNotExists_thenReturnsTrue() {
        String email = "unique@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        boolean result = userService.isEmailUnique(email);
        assertTrue(result, "Email should be unique");
    }

    @Test
    void isEmailUnique_whenEmailExists_thenReturnsFalse() {
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        boolean result = userService.isEmailUnique(email);

        assertFalse(result, "Email should not be unique");
    }

    @Test
    void getProfileData_whenCalled_thenReturnsUserProfileDto() {
        User user = new User();
        UserProfileDto userProfileDto = new UserProfileDto();
        when(userHelperService.getUser()).thenReturn(user);
        when(modelMapper.map(user, UserProfileDto.class)).thenReturn(userProfileDto);
        UserProfileDto result = userService.getProfileData();
        assertNotNull(result, "Profile data should not be null");
        assertEquals(userProfileDto, result, "Profile data should match");
    }

    @Test
    void getUser_whenUserExists_thenReturnsOptionalOfUser() {
        User user = new User();
        when(userHelperService.getUser()).thenReturn(user);
        Optional<User> result = userService.getUser();
        assertTrue(result.isPresent(), "User should be present");
        assertEquals(user, result.get(), "Returned user should match");
    }

    @Test
    void updateUser_whenCalled_thenSavesUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.save(user)).thenReturn(user);
        User result = userService.updateUser(user);
        verify(userRepository, times(1)).save(user);
        assertEquals(user, result, "The saved user should be returned");
    }

    @Test
    void getAllLists_whenCalled_thenReturnsUserLists() {
        User user = new User();
        user.setId(1L);
        List<CustomList> customLists = new ArrayList<>();
        customLists.add(new CustomList());
        when(userRepository.findAllByUserId(user.getId())).thenReturn(customLists);
        List<CustomList> result = userService.getAllLists(user);
        verify(userRepository, times(1)).findAllByUserId(user.getId());
        assertEquals(customLists, result, "The lists should match the returned lists");
    }

    @Test
    void findAllReviewsByUser_whenCalled_thenReturnsUserReviews() {
        String username = "user123";
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review());
        when(userRepository.findAllReviewsByUser(username)).thenReturn(reviews);
        List<Review> result = userService.findAllReviewsByUser(username);
        verify(userRepository, times(1)).findAllReviewsByUser(username);
        assertEquals(reviews, result, "The reviews should match the returned reviews");
    }

    @Test
    void findUserById_whenUserExists_thenReturnsUser() {
        long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findUserById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertTrue(result.isPresent(), "User should be present");
        assertEquals(user, result.get(), "The returned user should match the expected user");
    }

    @Test
    void findUserById_whenUserDoesNotExist_thenReturnsEmptyOptional() {
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = userService.findUserById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertFalse(result.isPresent(), "User should not be present");
    }
}
