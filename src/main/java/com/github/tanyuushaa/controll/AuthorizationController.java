package com.github.tanyuushaa.controll;

import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.User;
import com.github.tanyuushaa.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorizationController {

    private final UserService userService;

    @Autowired
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser != null) {
            return "redirect:/products";
        }
        if (model.getAttribute("user") == null) {
            model.addAttribute("user", new User());
        }
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute User user, Model model) {
        Response<User> responseUser = userService.addUser(user);
        if (!responseUser.isOkay()) {
            model.addAttribute("errors", responseUser.getErrorMessage());
            model.addAttribute("user", responseUser.getObject());
            return registration(model, null);
        }
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser != null) {
            return "redirect:/products";
        }
        if (model.getAttribute("user") == null) {
            model.addAttribute("user", new User());
        }
        return "login";
    }

    @PostMapping("/login-processing")
    public String loginUser(@ModelAttribute User user, Model model) {
        Response<User> loginResponse = userService.letUserLogIn(user);
        if (!loginResponse.isOkay()) {
            model.addAttribute("errors", loginResponse.getErrorMessage());
            model.addAttribute("user", loginResponse.getObject());
            return login(model, null);
        }
        return "redirect:/";
    }
}
