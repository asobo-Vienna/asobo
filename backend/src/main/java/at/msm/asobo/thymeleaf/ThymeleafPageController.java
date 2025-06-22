package at.msm.asobo.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ThymeleafPageController {
    @GetMapping
    public String home(Model model) {
        model.addAttribute("name", "Edmund Sackbauer");
        return "index";
    }

    @GetMapping("/{page}")
    public String renderPage(@PathVariable String page, Model model) {
        return page; // returns templates/{page}.html
    }

    @GetMapping("/users/{id}")
    public String userProfile(@PathVariable String id, Model model) {
        return "user-profile"; // maps to templates/user-profile.html
    }

    @GetMapping("/events/{id}")
    public String renderEventPage() {
        return "event-page";
    }
}
