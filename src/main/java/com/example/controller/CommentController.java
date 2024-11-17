package com.example.controller;

import com.example.model.Comment;
import com.example.model.Doctor;
import com.example.model.EmailSender;
import com.example.model.User;
import com.example.repository.CommentRepository;
import com.example.repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    EmailSender emailSender;

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

    @GetMapping("/report")
    public String reportComment(@RequestParam("commentId") UUID commentId, Model model, HttpSession session) throws Exception {
        Optional<Comment> comment = commentRepository.findById(commentId);
        String message = comment.get().getCommentText() + "\n" +
                "Reporting doctor: " + comment.get().getDoctor().getName() +
                "\n Reported User: " + comment.get().getUser().getName();


        //emailSender.sendMail("Report Comment", message);

        Doctor loggedInDoctor = (Doctor) session.getAttribute("loggedInDoctor");
        if (loggedInDoctor == null) {
            return "redirect:/doctor/login";
        }
        List<Comment> comments = commentRepository.findByDoctorOrderByTimestampDesc(loggedInDoctor);
        model.addAttribute("comments", comments);
        model.addAttribute("doctor", loggedInDoctor);
        model.addAttribute("error", "Email to administration has been send!");
        return "doctor/doctor-profile";
    }
}
