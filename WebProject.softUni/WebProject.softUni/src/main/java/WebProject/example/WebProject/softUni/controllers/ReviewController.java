package WebProject.example.WebProject.softUni.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {

    @GetMapping("/AddReview")
    public String getAddReview(Model model) {
        return "AddReview";
    }

   // @GetMapping("/AddReview")
   /// public String getAddReview() {
    //    return "AddReview";
  //  }
}
