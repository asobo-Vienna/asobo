package at.msm.asobo.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String register(Model model) {
        return "register"; // This maps to src/main/resources/templates/register.html
    }
}
