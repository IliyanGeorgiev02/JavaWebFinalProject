package webproject.example.webproject.softuni.dtos;

import jakarta.validation.constraints.NotBlank;

public class AddCommentDto {
    @NotBlank(message = "The comment must have text")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
