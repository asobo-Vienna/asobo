package at.msm.asobo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// frontend
@Controller
@RequestMapping("/about")
public class AboutController {
    @GetMapping()
    public String about(Model model) {
        model.addAttribute("aboutText", "TODO");
        return "about"; // This maps to src/main/resources/templates/about.html
    }
}
