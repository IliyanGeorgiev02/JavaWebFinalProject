package WebProject.example.WebProject.softUni.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingDto {
    @JsonProperty("Source")
    private String source;
    @JsonProperty("Value")
    private String value;

    // Getters and Setters

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return source+" "+value;
    }
}
