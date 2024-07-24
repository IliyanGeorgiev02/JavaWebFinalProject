package WebProject.example.WebProject.softUni.dtos;

import WebProject.example.WebProject.softUni.validation.annotation.UniqueEmail;
import WebProject.example.WebProject.softUni.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterUserDto {
    @NotEmpty(message = "Username must not be empty")
    @Size(min = 3, max = 30, message = "the length of your username must be between 3 and 30 characters")
    @UniqueUsername
    private String username;
    @NotEmpty(message = "Password must not be empty")
    @Size(min = 3, max = 30, message = "the length of your password must be between 3 and 30 characters")
    private String password;
    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    @UniqueEmail(message = "Email is already in use")
    private String email;
    @Size(min = 2, max = 30, message = "the length of your first name must be between 2 and 30 characters")
    @NotEmpty(message = "First name must not be empty")
    private String firstName;
    @Size(min = 2, max = 30, message = "the length of your last name must be between 3 and 30 characters")
    @NotEmpty(message = "Last name must not be empty")
    private String lastName;

    public RegisterUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
