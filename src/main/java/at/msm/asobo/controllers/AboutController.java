package at.msm.asobo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("aboutText", "TODO");
        return "about"; // This maps to src/main/resources/templates/about.html
    }
}
