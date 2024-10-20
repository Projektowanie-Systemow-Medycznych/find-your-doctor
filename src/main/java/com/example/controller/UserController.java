package com.example.controller;

import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/login")
    public String showUserLoginPage() {
        return "user/user-login";
    }

    @PostMapping("/login")
    public String processUserLogin(@RequestParam("login") String login,
                                   @RequestParam("password") String password,
                                   Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctor") != null) {
            model.addAttribute("error", "You are logged in as a doctor. Please log out from the doctor account.");
            return "user/user-login";
        }
        if (session.getAttribute("loggedInUser") != null) {
            model.addAttribute("error", "You are already logged in as a user. Please log out first.");
            return "user/user-login";
        }
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/user-register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            model.addAttribute("error", "Login already taken");
            return "user/user-register";
        }
        userRepository.save(user);
        return "redirect:/user/login";
    }

    @GetMapping("/reservation")
    public String showUserReservation(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        List<Reservation> reservations  = reservationRepository.findByUser(loggedInUser);

        YearMonth currentYearMonth = YearMonth.now();
        int currentYear = currentYearMonth.getYear();
        int currentMonth = currentYearMonth.getMonthValue() - 1;

        List<String> reservationDates = reservations.stream()
                .map(reservation -> reservation.getDatetime().toLocalDate().toString())
                .collect(Collectors.toList());

        model.addAttribute("reservations", reservationDates);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("user", loggedInUser);
        return "user/user-reservation";
    }
}
