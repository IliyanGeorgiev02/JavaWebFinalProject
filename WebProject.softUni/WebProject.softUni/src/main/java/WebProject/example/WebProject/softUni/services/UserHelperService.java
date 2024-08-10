package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.UserProfileDto;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class UserHelperService {
    private static final String ROLE_PREFIX = "ROLE_";
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserHelperService.class);
    private final CloudinaryService cloudinaryService;

    public UserHelperService(UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public User getUser() {
        return userRepository.findByUsername(getUserDetails().getUsername())
                .orElse(null);
    }

    public boolean hasRole(String role) {
        return getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_PREFIX + role));
    }

    public UserDetails getUserDetails() {
        return (UserDetails) getAuthentication().getPrincipal();
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public void updateUser(UserProfileDto userProfileDto) {
        User user = getUser();
        if (user == null) {
            logger.warn("User not found");
            return;
        }
        String path="";
        userProfileDto.setId(user.getId());
        if (!userProfileDto.getProfilePicture().isEmpty()){
           path=this.cloudinaryService.upload(userProfileDto.getProfilePicture(),"");
           user.setProfilePicture(path);
        }
        updateFieldIfNotBlank(userProfileDto::getUsername, user::setUsername, "Username");
        updateFieldIfNotBlank(userProfileDto::getFirstName, user::setFirstName, "First Name");
        updateFieldIfNotBlank(userProfileDto::getLastName, user::setLastName, "Last Name");
        updateFieldIfNotBlank(userProfileDto::getBio, user::setBio, "Bio");
        userRepository.save(user);
    }

    private void updateFieldIfNotBlank(Supplier<String> newValueSupplier, Consumer<String> updateFunction, String fieldName) {
        String newValue = newValueSupplier.get();
        if (!newValue.isBlank()) {
            updateFunction.accept(newValue);
            logger.info("{} updated to: {}", fieldName, newValue);
        }
    }

}
