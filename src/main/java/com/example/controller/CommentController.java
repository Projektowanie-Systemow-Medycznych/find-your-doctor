package com.example.controller;

import com.example.model.Comment;
import com.example.model.Doctor;
import com.example.model.User;
import com.example.repository.CommentRepository;
import com.example.repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/add")
    public String showAddCommentForm(@RequestParam("doctor_id") UUID doctorId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        Comment comment = new Comment();
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        comment.setDoctor(doctor.get());
        model.addAttribute("comment", comment);
        return "user/add-comment";
    }

    @PostMapping("/add")
    public String addComment(@ModelAttribute("comment") Comment comment, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "user/add-comment";
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        comment.setUser(loggedInUser);
        commentRepository.save(comment);

        return "redirect:/";
    }
}
