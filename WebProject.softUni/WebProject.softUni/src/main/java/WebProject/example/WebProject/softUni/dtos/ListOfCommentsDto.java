package WebProject.example.WebProject.softUni.dtos;

import java.util.List;

public class ListOfCommentsDto {
    private List<CommentsDto> comments;

    public List<CommentsDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentsDto> comments) {
        this.comments = comments;
    }
}
