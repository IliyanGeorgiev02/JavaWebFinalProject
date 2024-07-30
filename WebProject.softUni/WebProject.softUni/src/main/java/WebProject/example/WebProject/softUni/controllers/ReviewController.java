package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.MovieResponseDto;
import WebProject.example.WebProject.softUni.dtos.MovieReviewInfoDto;
import WebProject.example.WebProject.softUni.dtos.MovieSearchDto;
import WebProject.example.WebProject.softUni.services.OmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class ReviewController {
    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    public ReviewController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @PostMapping("/AddReview")
    public String addReview(@RequestParam("title") String title,
                            @RequestParam("year") String year,
                            Model model) {
        MovieReviewInfoDto movieResponseDto = new MovieReviewInfoDto();
        movieResponseDto.setMovieTitle(title);
        movieResponseDto.setReleaseYear(year);
        model.addAttribute("movieDetails", movieResponseDto);
        return "AddReview";
    }
}
