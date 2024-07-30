package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.controllers.MovieController;
import WebProject.example.WebProject.softUni.dtos.RegisterUserDto;
import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.model.enums.UserRoleEnum;
import WebProject.example.WebProject.softUni.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserHelperService userHelperService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    public UserService(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, UserHelperService userHelperService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userHelperService = userHelperService;
    }

    public void registerUser(RegisterUserDto registerUserDto) {
        User mappedUser = this.modelMapper.map(registerUserDto, User.class);
        if (this.userRepository.count() == 0) {
            mappedUser.setRole(UserRoleEnum.ADMIN);
        } else {
            mappedUser.setRole(UserRoleEnum.USER);
        }
        mappedUser.setPassword(this.passwordEncoder.encode(mappedUser.getPassword()));
        this.userRepository.save(mappedUser);
    }

    public boolean isUsernameUnique(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public boolean isEmailUnique(String email) {
        return !userRepository.existsByEmail(email);
    }

    public UserProfileDto getProfileData() {
        return modelMapper.map(userHelperService.getUser(), UserProfileDto.class);
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(this.userHelperService.getUser());
    }

    public User updateUser(User mappedUser) {
        return this.userRepository.save(mappedUser);
    }

    @Transactional
    public List<CustomList> getAllLists(User user) {
        List<CustomList> lists = this.userRepository.findAllByUserId(user.getId());
        logger.info("Retrieved lists: " + lists.toString());
        return lists;
    }
}
