package webproject.example.webproject.softuni.dtos;

import webproject.example.webproject.softuni.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class UserProfileDto {
    private long id;

    private String profilePicUrl;


    private MultipartFile profilePicture;

    @UniqueUsername
    @Size(min = 3, max = 30, message = "the length of your username must be between 3 and 30 characters")
    private String username;

    @Size(min = 3, max = 30, message = "the length of your username must be between 3 and 30 characters")
    private String firstName;

    @Size(min = 3, max = 30, message = "the length of your username must be between 3 and 30 characters")
    private String lastName;

    private String bio;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }
}
