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

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UserHelperService {
    private static final String ROLE_PREFIX = "ROLE_";
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserHelperService.class);

    public UserHelperService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        if (user != null) {
            logger.info(user.getUsername());
            logger.info(userProfileDto.getProfilePicUrl());
            if (!userProfileDto.getProfilePicUrl().isBlank()) {
                user.setProfilePicture(userProfileDto.getProfilePicUrl());
            }
            if (!userProfileDto.getUsername().isBlank()) {
                user.setUsername(userProfileDto.getUsername());
            }
            if (!userProfileDto.getFirstName().isBlank()) {
                user.setFirstName(userProfileDto.getFirstName());
            }
            if (!userProfileDto.getLastName().isBlank()) {
                user.setLastName(userProfileDto.getLastName());
            }
            if (!userProfileDto.getBio().isBlank()) {
                user.setBio(userProfileDto.getBio());
            }
            userRepository.save(user);
            logger.info(user.getUsername());
        } else {
            logger.warn("User not found");
        }
    }


    private void updateField(String newValue, Consumer<String> setter) {
        Optional.ofNullable(newValue)
                .filter(value -> !value.isBlank())
                .ifPresent(setter);
    }
}
