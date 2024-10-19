package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showUserLoginPage() {
        return "user/user-login";
    }

    @PostMapping("/login")
    public String processUserLogin(@RequestParam("login") String login,
                                   @RequestParam("password") String password,
                                   Model model, HttpSession session) {
        User user = userRepository.findByLogin(login).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid login or password");
            return "user/user-login";
        }
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loggedInUser);
        return "user/user-profile";
    }

    @GetMapping("/edit-profile")
    public String editForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loggedInUser);
        return "user/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateUserProfile(@ModelAttribute("user") User user, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        loggedInUser.setName(user.getName());
        loggedInUser.setLogin(user.getLogin());
        loggedInUser.setPassword(user.getPassword());
        userRepository.save(loggedInUser);
        session.setAttribute("loggedInUser", loggedInUser);
        return "redirect:/user/profile";
    }
}
