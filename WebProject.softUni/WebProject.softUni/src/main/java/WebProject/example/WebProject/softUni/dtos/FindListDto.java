package WebProject.example.WebProject.softUni.dtos;

import jakarta.validation.constraints.NotEmpty;

public class FindListDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotEmpty
    private String username;

    public FindListDto() {
    }

    public FindListDto(String title, String description, String username) {
        this.title = title;
        this.description = description;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
