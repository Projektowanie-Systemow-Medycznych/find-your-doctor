package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showUserLoginPage() {
        return "user-login";
    }

    @PostMapping("/login")
    public String processUserLogin(@RequestParam("login") String login,
                                   @RequestParam("password") String password,
                                   Model model) {
        User user = userRepository.findByLogin(login).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            return "user-home";
        } else {
            model.addAttribute("error", "Invalid login or password");
            return "user-login";
        }
    }
}

